"""配置模块：集中管理环境变量加载、Anthropic 客户端与路径常量。

从 v13_coder.py 提取而来。v13_coder.py 通过
``from config import client, MODEL, WORKDIR, ...`` 使用这些名字，
名称、取值来源与构建行为均与原单文件版本保持一致：

- 先执行 load_dotenv(override=True) 加载 .env（已有环境变量被覆盖）；
- 若设置了 ANTHROPIC_BASE_URL，则移除 ANTHROPIC_AUTH_TOKEN
  （原 v13_coder.py 第 26-28 行的 pop 逻辑，行为不变）；
- Anthropic 客户端仅以 base_url 参数构建，与原先完全一致。
"""

import os
from pathlib import Path

from anthropic import Anthropic
from dotenv import load_dotenv

load_dotenv(override=True)
if os.getenv("ANTHROPIC_BASE_URL"):
    os.environ.pop("ANTHROPIC_AUTH_TOKEN", None)

client = Anthropic(base_url=os.getenv("ANTHROPIC_BASE_URL"))

WORKDIR = Path.cwd()
SKILL_DIR = WORKDIR / "skills"
TRANSCRIPT_DIR = WORKDIR / ".transcripts"
TOOL_RESULT_DIR = WORKDIR / ".task_outputs" / ".tool-results"
MODEL = os.getenv("MODEL_ID")

# 定时任务持久化文件路径（定时任务逻辑仍在 v13_coder.py）
DURABLE_PATH = WORKDIR / ".scheduled_tasks.json"

__all__ = [
    "client",
    "WORKDIR",
    "SKILL_DIR",
    "TRANSCRIPT_DIR",
    "TOOL_RESULT_DIR",
    "MODEL",
    "DURABLE_PATH",
]
