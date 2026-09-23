import subprocess

from anthropic import Anthropic
from dotenv import load_dotenv
import os
load_dotenv(override=True)
if os.getenv("ANTHROPIC_BASE_URL"):
    os.environ.pop("ANTHROPIC_AUTH_TOKEN",None)

client=Anthropic(base_url=os.getenv("ANTHROPIC_BASE_URL"))
MODEL=os.getenv("MODEL_ID")
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
}]
SYSTEM=f"你是一位工作在:{os.getcwd()}目录编程助理，根据用户的操作需求生成Bash来解决任务，不需要解释"

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

# loop循环
def agent_loop(messages:list):
    while True:
        resp=client.messages.create(
            model=MODEL
            ,tools=tools
            ,system=SYSTEM
            ,max_tokens=8000
            ,messages=messages)
        messages.append({"role":"assistant","content":resp.content})
        tool_calls=[block for block in resp.content if block.type=="tool_use"]
        if not tool_calls:
            return
        results=[]
        for block in tool_calls:
            print(f">>assistant:f{block.input["command"]}")
            tool_result=bash(block.input["command"])
            results.append({
                "type":"tool_result",
                "tool_use_id":block.id,
                "content":tool_result
            })
        messages.append({"role":"user","content":results})

if __name__ == "__main__":
    print("v1: Agent Loop Test")
    print("你好，我是您的编程助手，有什么需要问题尽管问我哦！")
    history=[]
    while True:
        try:
            user_mes=input(">>>")
        except (EOFError,KeyboardInterrupt):
            break
        if user_mes.strip().lower() in ("exit","q","quit"):
            break
        history.append({"role":"user","content":user_mes})
        agent_loop(history)
        resp=history[-1]["content"]
        if isinstance(resp,list):
            for block in resp:
                if getattr(block,"type",None)=="text":
                    print(block.text)
        print()














