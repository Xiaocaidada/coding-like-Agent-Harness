import ast
import json
import re
import subprocess
from pathlib import Path

from anthropic import Anthropic
from dotenv import load_dotenv
import os
load_dotenv(override=True)
if os.getenv("ANTHROPIC_BASE_URL"):
    os.environ.pop("ANTHROPIC_AUTH_TOKEN",None)

client=Anthropic(base_url=os.getenv("ANTHROPIC_BASE_URL"))
WORKDIR=Path.cwd()
MODEL=os.getenv("MODEL_ID")


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



tools=[{
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
}
]
TOOL_HANDLER={"bash":bash,"run_read":run_read,"run_write":run_write,"run_edit":run_edit,"run_glob":run_glob}

SYSTEM=f"你是一位工作在:{os.getcwd()}目录编程助理，根据用户的操作需求生成Bash来解决任务，不需要解释"



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
    print(f">>> PreToolUse( pre_tool_log )：打印日志：工具名称: {block.name},工具参数: {block.input}" )

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
    print(f">>> PostToolUse( post_tool_log )：打印输出结果: {out}")

def userPromptSubmit_log_print(inp:str):
    print(f">>> UserPromptSubmit ( UserPromptSubmit_log_print ) ：用户输入:{inp.strip()}")

def stop_print_context(messages):
    print(f">>> Stop( stop_print_context )：{messages}")



class TodoManager:
    def __init__(self):
        self.tasks=[]


    def update_tasks(self,new_tasks: list | str ) -> str:
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
    print(f">>>todos: {output}")
    return "\n".join(output)

TOOL_HANDLER["todo_write"]=todo_write

register_hook("UserPromptSubmit",userPromptSubmit_log_print)
register_hook("PreToolUse",pre_tool_log)
register_hook("PreToolUse",pre_tool_permission_check)
register_hook("PostToolUse",post_tool_log)
register_hook("Stop",stop_print_context)

# loop循环
def agent_loop(messages:list):
    rounds_since_todo=0
    while True:
        print("------------------------------------------")
        resp=client.messages.create(
            model=MODEL
            ,tools=tools
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
            new_block=trigger_hooks("PreToolUse",block)
            if new_block:
                results.append({"type":"tool_result","tool_use_id":block.id,"content":str(new_block)})
                continue
            handler=TOOL_HANDLER.get(block.name)
            out=handler(**block.input) if handler else f"Unknown: {block.name}"
            trigger_hooks("PostToolUse",out)
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
    print("v5: Agent Todo Test")
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
