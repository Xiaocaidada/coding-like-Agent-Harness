import ast
import json
import re
import subprocess
from pathlib import Path

import yaml
from anthropic import Anthropic
from dotenv import load_dotenv
import os
load_dotenv(override=True)
if os.getenv("ANTHROPIC_BASE_URL"):
    os.environ.pop("ANTHROPIC_AUTH_TOKEN",None)

client=Anthropic(base_url=os.getenv("ANTHROPIC_BASE_URL"))
WORKDIR=Path.cwd()
SKILL_DIR=WORKDIR / "skills"
MODEL=os.getenv("MODEL_ID")


class SkillLoader:

    def __init__(self,SKILL_DIR:Path):
        self.skill_dir=SKILL_DIR
        self.skills:dict[str,dict[str,str]] ={}
        self.load()

    @staticmethod
    def parse_frontmatter(self,content:str)->tuple[dict,str]:
        lines=content.splitlines(keepends=True)
        if not lines or lines[0].rstrip("\r\n")!='---':
            return {},content
        closing_index=next((index for index, line in enumerate(lines[1:],start=1) if line.rstrip("\r\n")=='---'),None)
        if not closing_index:
            return {},content
        frontmatter="".join(lines[1:closing_index])
        body="".join(lines[closing_index+1:]).strip()
        try:
            metadata=yaml.safe_load(frontmatter) or {}
        except yaml.YAMLError:
            metadata={}
        if not isinstance(metadata,dict):
            metadata={}
        return metadata,body


    def load(self):
        self.skills.clear()
        if not self.skill_dir.exists():
            return
        root_dir=self.skill_dir.resolve()
        for manifest in sorted(self.skill_dir.glob("*/SKILL.md")):
            if not manifest.is_file() or not manifest.resolve().is_relative_to(root_dir):
                continue
            content=manifest.read_text(encoding="utf-8")
            metadata,body=self.parse_frontmatter(self,content)
            row_name=metadata.get("name")
            name=row_name.strip()if isinstance(row_name,str) else ""
            name=name or manifest.parent.name
            raw_description=metadata.get("description")
            description=raw_description.strip() if isinstance(raw_description,str) else ""
            description=description or body.split("\n",1)[0]
            description=" ".join(str(description).lstrip("# ").split())
            self.skills[name]={
                "name":name,
                "description":description,
                "content":content
            }
    def show_skills(self):
        if not self.skills:
            return "(no skills found)"
        return "\n".join(f"-{skill.get("name","")}: {skill.get("description","")}" for skill in self.skills.values())

    def load_skill(self,name:str)->str:
        skill=self.skills.get(name)
        if skill:
            return skill.get("content")
        available=", ".join(self.skills) or "none"
        return f"Error: Unknown skill '{name}'. Available: {available}"

skill_loader=SkillLoader(SKILL_DIR)

def bash(command:str)-> str:
    dangerous=["rm -rf /","sudo","shutdown","reboot","> /dev/"]
    if any(d in command  for d in dangerous):
        return f"{command} 是危险指令，禁止使用"
    try:
        resp=subprocess.run(command,shell=True,cwd=os.getcwd(),capture_output=True,text=True,errors="replace",timeout=120)
        out=(resp.stdout+resp.stderr).strip()
        return out[:50000] if out else "(no output)"
    except subprocess.TimeoutExpired:
        return "Error: Timeout (120s)"
    except (FileNotFoundError,OSError) as e:
        return f"Error: {e}"


def safe_path(p:str)->Path:
    path=(WORKDIR / p).resolve()
    if not path.is_relative_to(WORKDIR):
        raise ValueError(f"Path escapes workspace: {p}")
    return path



def run_read(path:str,limit:int | None=None)->str:
    try:
        lines=safe_path(path).read_text(encoding="utf-8").splitlines()
        if limit and limit<len(lines):
            lines=lines[:limit]+[f"... ({len(lines)-limit}) more lines"]
        return "\n".join(lines)
    except Exception as e:
        return f"Error: {e}"

def run_write(path:str,content:str)->str:
    try:
        file_path=safe_path(path)
        file_path.parent.mkdir(parents=True,exist_ok=True)
        file_path.write_text(content,encoding="utf-8")
        return f"Wrote {len(content)} bytes to {path}"
    except Exception as e:
        return f"Error: {e}"

def run_edit(path:str,old_text:str,new_text:str)->str:
    try:
        file_path=safe_path(path)
        text=file_path.read_text(encoding="utf-8")
        if old_text not in text:
            return f"Error: text not in {path}"
        file_path.write_text(text.replace(old_text,new_text,1),encoding="utf-8")
        return f"Edited {path}"
    except Exception as e:
        return f"Error: {e}"

def run_glob(pattern: str)-> str:
    import glob as g
    try:
        matches=sorted({
            match for match in g.glob(pathname=pattern,root_dir=WORKDIR,recursive=True)
                if (WORKDIR / match).resolve().is_relative_to(WORKDIR)
        })
        shown=matches[:200]
        if len(shown)>200:
            shown.append("... (more matches omitted; narrow the pattern)")
        return "\n".join(shown) if shown else "(no matches)"
    except Exception as e:
        return f"Error: {e}"



#闸门1————拒绝危险指令
DENY_LIST=["rm -rf /", "sudo", "shutdown", "reboot",
    "mkfs", "dd if=", "> /dev/sda",]

def deny_list_check(command:str)->str | None:
    if any( d_item in command for d_item in DENY_LIST) :
        return f"Error: {command} is deny! ! !"
    return None

#闸门2————自定义规则
DESTRUCTIVE_COMMAND_WORD=re.compile(r"(?i)(?:^|[;&|()\n])\s*(?:rm|del)(?=\s|$|[;&|()])")
def contains_destructive_command(command :str)->bool:
    return bool(DESTRUCTIVE_COMMAND_WORD.search(command))

PERMISSION_RULES=[
    {
        "tools":["run_read","run_write","run_edit","run_glob"],
        "check": lambda args: not (WORKDIR / args.get("path","")).resolve().is_relative_to(WORKDIR),
        "message":"Acess outside workspace"
    },
    {
        "tools":{"bash"},
        "check": lambda args: contains_destructive_command(args.get("command","str")) or any(
           kw in args.get("command","")  for kw in ["rm ", "> /etc/", "chmod 777"]
        ),
        "message":"Potentially destructive command"
    }

]

def check_rules(tool:str,args:dict)-> str | None :
    for rule in PERMISSION_RULES:
        if tool in rule['tools'] and rule['check'](args):
            return rule['message']
    return None


#闸门3————规则命中后等待用户输入
def ask_user(tool_name:str ,args:dict,mes:str)-> str:
    print(f"\n ⚠  {mes}")
    print(f"Tool name: {tool_name},args: {args}")
    choice=input("Allow? [y/N] ").strip().lower()
    return "allow" if choice in ("y","yes") else "deny"

#三道闸门放在一起，进行工具调用之前
def check_permission(block)->bool:
    tool_name=block.name
    args=block.input
    reason=deny_list_check(args.get("command","str"))
    if reason:
        print(f"\n ⛔ {reason}  ")
        return False
    reason=check_rules(tool_name,args)
    if reason:
        ask_resp=ask_user(tool_name,args,reason)
        return ask_resp=='allow'
    return True



BASE_TOOL=[{
    "name":"bash",
    "description":"运行shell指令",
    "input_schema":{
        "type":"object",
        "properties":{
            "command":{"type":"string"}
        },
    "required":["command"]
    }
},{
    "name":"run_read",
    "description":"传入path，读取该path下的文件",
    "input_schema":{
        "type":"object",
        "properties":{
            "path":{"type":"string"},
            "limit":{"type":"integer"}
        },
        "required":["path"]
    }
},{
    "name":"run_write",
    "description":"向指定path下写content",
    "input_schema":{
        "type":"object",
        "properties":{
            "path":{"type":"string"},
            "content":{"type":"string"}
        },
        "required":["path","content"]
    }
},{
    "name":"run_edit",
    "description":"编译指定path下的内容",
    "input_schema":{
        "type":"object",
        "properties":{
            "path":{"type":"string"},
            "old_text":{"type":"string"},
            "new_text":{"type":"string"}
        },
        "required":["path","old_text","new_text"]
    }
},{
    "name":"run_glob",
    "description":"根据指定pattern通配符进行查询",
    "input_schema":{
        "type":"object",
        "properties":{
            "pattern":{"type":"string"}
        },
        "required":["pattern"]
    }
},{
    "name":"todo_write",
    "description":"为你当前的会话创建一个任务列表(todo list)，不提供编程功能",
    "input_schema":{
        "type":"object",
        "properties":{
            "todos":{
                "type":"array",
                "maxItems":20,
                "items":{
                    "type":"object",
                    "properties":{
                        "content":{"type":"string","minLength":1},
                        "status":{"type":"string","enum":["pending","in_progress","completed"]}
                    },
                    "required":["content","status"]
                }
            }
        },
        "required":["todos"]
    }
},{
    "name":"load_skill",
    "description":"Load the full SKILL.md content by skill name.",
    "input_schema":{
        "type":"object",
        "properties":{
            "name":{"type":"string"},
        },
        "required":["name"]
    }
}
]
BASE_TOOL_HANDLER={"bash":bash,"run_read":run_read,"run_write":run_write,"run_edit":run_edit,"run_glob":run_glob,"load_skill":skill_loader.load_skill}

SYSTEM=(
    f"You are a coding agent at {WORKDIR}. "
    "Use task for focused explroation or a self-contained subtask."
    f"Skills available:\n{skill_loader.show_skills()}\n\n"
    "Use load_skill to read the full instructions when a skill applies."
)
SUB_SYSTEM = (
    f"You are a coding agent at {WORKDIR}. "
    "Complete the given task, then return a concise final answer."
    f"Skills available:\n{skill_loader.show_skills()}\n\n"
    "Use load_skill to read the full instructions when a skill applies."
)



HOOKS_REGISTER={
    "UserPromptSubmit":[],
    "PreToolUse":[],
    "PostToolUse":[],
    "Stop":[]
}
def register_hook(event:str,callable):
    HOOKS_REGISTER[event].append(callable)

def trigger_hooks(event:str,*args) :
    for callable in HOOKS_REGISTER[event]:
        result=callable(*args)
        if result:
            return result
    return None

def pre_tool_log(block):
    print(f">>> PreToolUse( pre_tool_log )：调用工具名称: {block.name},工具参数: {block.input}" )

def pre_tool_permission_check(block) -> str | None:
    tool_name=block.name
    args=block.input
    reason=deny_list_check(args.get("command",""))
    if reason:
        print(f">>> PreToolUse( pre_tool_permission_check )： \n ⛔ {reason}  ")
        return "Permission denied"
    reason=check_rules(tool_name,args)
    if reason:
        ask_resp=ask_user(tool_name,args,reason)
        if ask_resp!="allow":
            text=f"\n ⛔ Permission denied by user(ask_resp={ask_resp})"
            print(">>> PreToolUse( pre_tool_permission_check )："+text)
            return text
    return None

def post_tool_log(out):
    print(f">>> PostToolUse( post_tool_log )：工具输出结果: {out}")

def userPromptSubmit_log_print(inp:str):
    print(f">>> UserPromptSubmit ( UserPromptSubmit_log_print ) ：用户输入:{inp.strip()}")

def stop_print_context(messages):
    print(f">>> Stop( stop_print_context )：")



class TodoManager:
    def __init__(self):
        self.tasks=[]
    def update_tasks(self,new_tasks: list | str) -> str:
        if isinstance(new_tasks,str):
            try:
                new_tasks=json.loads(new_tasks)
            except json.JSONDecodeError:
                try:
                    new_tasks=ast.literal_eval(new_tasks)
                except (SyntaxError,ValueError) as e:
                    raise ValueError("todos must be a list or JSON array string")
        if not isinstance(new_tasks,list):
            raise ValueError("todos must be a list")
        if len(new_tasks)> 20:
            raise ValueError("Max 20 todos allowed")

        validated=[]
        in_progress_count=0
        for index,task in enumerate(new_tasks):
            if not isinstance(task,dict):
                raise ValueError(f"todos[{index}] be a dict")
            content=str(task.get("content","")).strip()
            status=str(task.get("status","")).strip()
            if not content:
                raise ValueError(f"todos[{index}] requires content")
            if not status:
                raise ValueError(f"todos[{index}] requires status")
            if status=="in_progress":
                in_progress_count+=1
            validated.append(task)
        if in_progress_count>1:
            raise ValueError("Only one todo can be in_progress at a time")
        self.tasks=validated
        return self.render()

    def render(self)->str:
        if not self.tasks:
            return "No todos"
        lines=[]
        for task in self.tasks:
            marker={
                "pending":"[ ]",
                "in_progress":"[>]",
                "completed":"[x]"
            }[task["status"]]
            lines.append(f"{marker} {task['content']}")
        done=sum( task['status']=="completed" for task in self.tasks)
        lines.append(f"\n{done}/{len(self.tasks)} completed")
        return "\n".join(lines)


todo_manager=TodoManager()
def todo_write(todos: list| str)-> str:
    try:
        output=todo_manager.update_tasks(todos)
    except ValueError as e:
        return f"Error: {e}"
    # print(f">>>todos: {output}")
    return output

BASE_TOOL_HANDLER["todo_write"]=todo_write

register_hook("UserPromptSubmit",userPromptSubmit_log_print)
register_hook("PreToolUse",pre_tool_log)
register_hook("PreToolUse",pre_tool_permission_check)
register_hook("PostToolUse",post_tool_log)
register_hook("Stop",stop_print_context)

def execute_tool(block,tool_handlers:dict)->str:
    blocked=trigger_hooks("PreToolUse",block)
    if blocked:
        return str(blocked)
    handler=tool_handlers.get(block.name)
    try:
        output=handler(**block.input) if handler else f"Unknown: {block.name}"
    except Exception as e:
        output=f"Error: {e}"
    trigger_hooks("PostToolUse",output)


    return str(output)

def extract_text(content)->str:
    if isinstance(content,str):
        return content
    return "\n".join(
       getattr(block,"text","") for block in content if getattr(block,"type","")=="text"
    )

SUB_TOOLS=list(BASE_TOOL)
SUB_TOOL_HANDLER=dict(BASE_TOOL_HANDLER)


def run_sub_agent_loop(prompt: str)-> str:
    print(">>> [sub_agent_loop start !]")
    loop_count=0
    messages=[{"role":"user","content":prompt}]
    for _ in range(30):
        resp=client.messages.create(
            model=MODEL,
            system=SUB_SYSTEM,
            messages=messages,
            tools=SUB_TOOLS,
            max_tokens=8000
        )
        messages.append({"role":"assistant","content":resp.content})
        tool_calls=[block for block in resp.content if block.type=="tool_use" ]
        if not tool_calls:
            force=trigger_hooks("Stop",messages)
            if force:
                messages.append({"role":"user","content":force})
                continue
            print(">>> [sub_agent_loop finished !]")
            return extract_text(resp.content)
        use_todo=False
        result=[]
        for tool_call in tool_calls:
            output=execute_tool(tool_call,SUB_TOOL_HANDLER)
            result.append({
                "type":"tool_result",
                "tool_use_id":tool_call.id,
                "content":output
            })
            if tool_call.name=="todo_write":
                use_todo=True
            loop_count=0 if use_todo else loop_count+1
            if loop_count>=3:
                result.append({
                    "type":"text",
                    "text": "<reminder>Update your todos.</reminder>"
                })
                loop_count=0

        messages.append({"role":"user","content":result})
    print(">>> [sub_agent_loop stopped !]")
    return "Subagent stopped after 30 turns without a final answer"

TASK_TOOL={
    "name":"run_sub_agent_loop",
    "description":"(执行复杂任务优先调用)Run a subagent with fresh conversation context and return its final text.(使用新的对话上下文运行一个子代理并且执行任务，并返回其最终文本。)",
    "input_schema":{
        "type": "object",
        "properties": {
            "prompt":{"type": "string","minLength":1}
        },
        "required": ["prompt"]
    }
}

MAIN_TOOLS=[*BASE_TOOL,TASK_TOOL]
MAIN_TOOL_HANDLER={**BASE_TOOL_HANDLER,"run_sub_agent_loop":run_sub_agent_loop}

# 父loop循环
def agent_loop(messages:list):
    rounds_since_todo=0
    while True:
        print("------------------------------------------")
        resp=client.messages.create(
            model=MODEL
            ,tools=MAIN_TOOLS
            ,system=SYSTEM
            ,max_tokens=8000
            ,messages=messages)
        messages.append({"role":"assistant","content":resp.content})
        tool_calls=[block for block in resp.content if block.type=="tool_use"]
        if not tool_calls:
            force=trigger_hooks("Stop",messages)
            if force:
                messages.append({"role":"user","content":force})
                continue
            return
        results=[]
        use_todo=False
        for block in tool_calls:
            out=execute_tool(block,MAIN_TOOL_HANDLER)
            if block.name=="todo_write":
                use_todo=True
            results.append({
                "type":"tool_result",
                "tool_use_id":block.id,
                "content":out
            })
            rounds_since_todo=0 if use_todo else rounds_since_todo+1
            if rounds_since_todo>=3:
                results.append({
                    "type":"text",
                    "text":"<reminder>Update your todos.</reminder>"
                })
                rounds_since_todo=0

        messages.append({"role":"user","content":results})


if __name__ == "__main__":
    print("v7: Agent Skills Test")
    print("你好，我是您的编程助手，有什么需要问题尽管问我哦！")
    history=[]
    while True:
        try:
            user_mes=input(">>>")
        except (EOFError,KeyboardInterrupt):
            break
        if user_mes.strip().lower() in ("exit","q","quit"):
            break
        trigger_hooks("UserPromptSubmit",user_mes)
        history.append({"role":"user","content":user_mes})
        agent_loop(history)
        resp=history[-1]["content"]
        if isinstance(resp,list):
            for block in resp:
                if getattr(block,"type",None)=="text":
                    print(block.text)
        print()
