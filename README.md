# coding-like-Agent-Harness（v13 Coding Agent 多代理本地运行）
> 
> 项目版本：s13
> 定位：本地可运行的**多 Agent 编码智能体运行时**，集成定时 Cron 任务、后台 Shell 执行、任务依赖管理、Git Worktree 并行工作区、持久化记忆、上下文自动压缩、多团队成员 Agent 通信系统、权限安全闸门。
> 入口文件：本 Python 主文件，配套目录：`.tasks`、`.memory`、`.mailboxes`、`.worktrees`、`transcripts`、`tool_results`，技能目录skill存放`SKILL.md`技能文件。

## 1. 整体架构概述

S13 是一个单进程多线程的 Agent 运行时，包含**Lead 主代理 + 多个 Teammate 子代理**。

- Lead：主交互入口，接收用户 CLI 输入，管理任务、调度子代理、审批计划、全局权限校验、cron 定时调度。
- Teammate：独立工作的子代理，认领任务、执行文件 / 脚本操作，**修改文件或 bash 执行前需要提交 Plan 给 Lead 审批**，支持独立工作目录（Git worktree）。
核心模块划分：

1. Cron 定时任务模块：5 字段 cron 表达式调度，任务持久化保存，重启自动加载，定时触发 prompt 送入 agent 会话。
2. 后台 Shell 任务模块：异步执行长时间 bash 命令，不阻塞主 Agent 循环，任务完成后推送通知。
3. 任务管理系统 TaskStore：带依赖 DAG 的任务看板，支持创建任务、添加阻塞依赖、认领、完成，检测依赖循环，持久化 JSON 存储。
4. Git Worktree 并行工作区：为任务绑定独立 Git 分支 + 工作目录，多个子代理并行修改代码互不干扰。
5. 记忆系统 Memory：持久项目记忆，自动提取会话长期知识，支持记忆检索、索引、记忆合并压缩；区分临时会话信息与永久记忆。
6. 上下文压缩 ContextCompact：自动截断、持久化、摘要超大对话，解决 LLM 上下文窗口超限，分级压缩策略。
7. 消息总线 MessageBus：Agent 之间异步信箱通信（mailbox），支持消息投递、等待消息、协议握手（计划审批、关闭子代理）。
8. 工具系统 + 权限安全闸门：统一工具调用入口，三层安全校验（黑名单、破坏性命令检测、工作目录逃逸检测，高危操作交互式用户确认）。
9. 技能加载系统 SkillLoader：从 SKILL_DIR 读取`SKILL.md`，frontmatter 配置 + 正文指令，agent 按需加载技能。
10. 钩子 Hook 系统：在用户提交输入、工具调用前、工具调用后、会话终止四个节点注入回调日志与校验逻辑。

## 2. 模块详细说明

### 2.1 Cron 定时任务模块

作用：按照标准 5 字段 cron 表达式（分 时 日 月 周）定时向 Agent 投递预设 prompt。
核心数据结构 `CronJob`：

表格

| 字段 | 说明 |
| --- | --- |
| id | 任务唯一 ID，固定前缀`cron_`，随机 hex 生成 |
| cron | 5 位 cron 表达式 `* * * * *` |
| prompt | 定时触发时交给 Agent 执行的指令文本 |
| recurring | 是否循环执行：True = 周期性重复；False = 一次性任务，触发后自动删除 |
| durable | 是否持久化：True 写入磁盘`DURABLE_PATH`，程序重启自动加载；False 仅内存有效，退出丢失 |
| pending_delivery | 标记任务是否已经入等待执行队列，防止同一分钟重复触发 |
| last_fired | 上次触发时间标记 `YYYY-MM-DD HH:MM`，按分钟粒度防重复触发 |

关键逻辑：

1. `cron_matches()`：校验当前时间是否匹配 cron 表达式，支持`*`、`*/N`间隔、`,`多值、`-`区间；星期与日字段遵循 cron 规则：**二者为或关系**。
2. `validate_cron()`：严格校验 cron 表达式字段范围，提前拦截非法表达式。
3. `save_durable_jobs()`：原子写入持久化 JSON，先写临时 tmp 文件，`os.replace`保证写入崩溃安全，避免文件损坏。
4. `load_durable_jobs()`：程序启动自动加载磁盘保存的定时任务，校验合法性，恢复待执行队列。
5. 调度线程`cron_scheduler_loop`：每秒轮询时间，`poll_due_job`匹配到期任务，送入全局`cron_queue`队列。
6. `queue_processor_loop`消费队列，将定时任务包装成`[Scheduled] prompt`用户消息，送入主 agent_loop。
7. `acknowledge_cron_jobs`：任务处理完成后更新状态，一次性任务自动删除；循环任务重置 pending 标记等待下一轮。

对外工具（Agent 可直接调用）：

- `run_schedule_cron(cron, prompt, recurring, durable)`：新建定时任务
- `run_list_crons()`：列出全部定时任务
- `run_cancel_cron(job_id)`：删除指定定时任务

### 2.2 后台 Shell 执行模块 BackgroundManager

解决问题：长时间运行 bash 命令（如编译、长时间脚本）阻塞 Agent 主循环，改为后台线程异步执行。

- `_run_bash_process`：封装 subprocess，Windows/Linux 跨平台进程终止；120s 超时限制，捕获 stdout+stderr，合并输出。
- `_stop_process_group`：跨平台终止进程，Windows 使用 terminate/kill；Linux 使用进程组 SIGTERM/SIGKILL。
- `atexit`注册退出钩子：程序退出时自动清理所有后台 shell 进程，防止僵尸进程。
- `should_run_background`：bash 工具入参`run_in_background: true`标记为后台任务。
后台任务完成后自动生成`<task_notification>`通知块，`inject_background_results`把通知注入会话消息，下一轮 Agent 读取结果。

### 2.3 任务管理系统 TaskStore（DAG 依赖任务）

文件存储：`WORKDIR/.tasks/task_xxxxxx.json`，每个任务单独 json 文件，使用`FileLock`文件锁多线程安全读写。
`Task`数据结构：

表格

| 字段 | 说明 |
| --- | --- |
| id | task_8 位随机 hex，任务唯一 id |
| subject | 任务标题（必填） |
| description | 详细任务描述 |
| status | 状态：`pending`待认领 / `in_progress`进行中 / `completed`已完成 |
| owner | 所有者：agent/teammate 名称；None = 未认领 |
| blockedBy | 依赖任务 ID 列表，代表**本任务被这些任务阻塞**，只有全部依赖 completed，任务才可以认领 |
| worktree | 绑定的 worktree 名称，可选，不为空代表任务使用独立 Git 工作目录 |

核心能力：

1. 创建任务：`create_task`生成唯一 task id，写入磁盘。
2. 依赖管理：`update_dependencies`添加阻塞依赖，自动**检测循环依赖**（A 依赖 B，B 依赖 A 直接报错）；不能依赖自身。
3. `can_start(task_id)`：判断任务是否就绪，遍历 blockedBy，检查所有依赖是否完成。
4. `claim_task`认领任务：
   - 仅 pending、无 owner、所有依赖完成的任务可以认领
   - 同一个 agent/teammate**同一时间只能持有一个 in_progress 任务**，不允许并行认领多个任务
   - 绑定 worktree 任务自动切换工具 cwd 到 worktree 目录
5. `complete_task`完成任务：标记为 completed；自动扫描所有 pending 任务，**解除依赖阻塞，输出 unblocked 任务列表**。
6. 工具集给 Agent 调用：`create_task`、`update_task`、`list_tasks`、`get_task`、`claim_task`、`complete_task`。

### 2.4 Git Worktree 并行工作区

基于 git worktree，给单个任务分配独立分支`wt/{name}`+ 独立文件夹，多个任务并行开发互不污染主工作目录。

- `create_worktree`工具：创建 worktree 分支与目录，绑定到指定 task_id。约束：任务必须 pending、无 owner、未绑定其他 worktree；worktree 名称有字符校验，禁止`..`逃逸。
- `task_worktree_cwd`：读取任务绑定的 worktree 路径，工具执行时自动切换 bash/read/write/edit 的工作目录。
- 安全校验：worktree 目录强制在`WORKDIR/.worktrees`内部，禁止跳出项目根目录。

> 
> 限制：worktree 仅隔离工作目录，不是安全沙箱；文件读写权限由操作系统控制。

### 2.5 记忆系统 Memory

目录：`WORKDIR/.memory/`，存储`*.md`带 YAML frontmatter 的记忆文档，`MEMORY.md`为自动维护的记忆索引目录。
记忆类型 MEMORY_TYPES：`user`用户偏好、`feedback`反馈信息、`project`项目稳定事实、`reference`外部参考资料。
每条记忆文档格式示例：

```
---
name: 记忆名称
type: project
description: 简短描述
---
记忆正文，长期保存的项目事实、用户偏好等
```

四大核心功能：

1. **写入记忆 write_memory_file**：创建 md 记忆文件，自动重建 MEMORY.md 索引。
2. **召回记忆 select_relevant_memories /load_memories**：基于当前对话，优先调用 LLM 挑选相关记忆；LLM 调用失败降级为关键词检索；加载记忆到 system prompt 作为背景知识，有字符上限。
3. **自动提取记忆 extract_memories**：每轮对话结束后，LLM 从对话提取**持久有效知识**，过滤临时状态、工具输出、一次性指令；识别临时标记（this session、本次会话等），不保存临时信息；自动去重，不重复写入相同记忆。
4. **记忆合并压缩 consolidate_memories**：当记忆文件数量超过`CONSOLIDATE_THRESHOLD`阈值，LLM 读取全部记忆，合并重复、过时信息，精简记忆集；操作前快照备份，失败自动回滚防止记忆丢失。

> 
> 记忆区分：persistent 长期跨会话保存；current_task 仅当前任务临时信息，不会存入持久记忆。

### 2.6 上下文压缩模块 ContextCompact

解决 LLM 对话上下文 token 超限，**多层分级压缩策略**，按顺序执行：

1. tool_result_budget：对超大单条工具输出做持久化，将长文本保存到`tool_results`文件，会话内替换为预览 + 文件路径。
2. snip_compact：消息数量过多时，把中间一大段消息归档保存到`transcripts`目录 jsonl 文件，会话替换为归档标记`[N messages archived at path]`，保留头部 + 最新消息。
3. micro_compact：压缩旧的、已经被 agent 读取过的 tool_result，替换为持久文件引用，保留最近 K 条完整工具结果。
4. fit_tool_results：继续裁剪工具输出预览长度，控制总字符预算。
5. compact_history /reactive_compact：兜底策略，将历史对话写入归档文件，LLM 生成事实摘要，用摘要消息替换全部旧会话；保留最新 N 条消息。

所有归档文件存储在`TRANSCRIPT_DIR`，可随时读取完整历史。

> 
> 压缩不会丢失原始对话数据，只是在送入 LLM 的消息列表里做精简。

### 2.7 多 Agent 消息总线 MessageBus

实现 Lead 和 Teammate 之间异步通信，基于文件信箱：`WORKDIR/.mailboxes/{agent_name}.jsonl`，每个 agent 独立信箱文件。
核心特性：

- send：写入消息到目标 agent 信箱，condition 条件变量唤醒等待中的 agent。
- read_inbox：一次性读取并清空当前 agent 所有消息。
- wait_for_messages：阻塞等待消息，带超时，空闲 teammate 在这个位置休眠。
协议内置两种标准化消息交互：

1. Plan 审批流程：Teammate 提交 plan → 发送 plan_approval_request 给 Lead；Lead 调用`review_plan`批准 / 拒绝，下发 plan_approval_response；Teammate 收到响应，gate 闸门控制是否允许执行修改文件 /bash。**未审批 plan 时，禁止 teammate 执行 bash、write_file、edit_file**。
2. Shutdown 关闭协议：Lead 发送 shutdown_request，Teammate 确认，清理任务认领状态，退出线程。

Teammate 生命周期：
`idle`空闲 → 自动扫描未认领就绪任务，claim_task 认领 → `working`工作中 → 提交 plan 后进入`waiting_approval`等待审批 → 完成任务释放认领，回到 idle；Lead 可随时请求 shutdown 终止。

团队工具：`spawn_teammate`、`list_teammates`、`send_message`、`request_shutdown`、`request_plan`、`review_plan`。

### 2.8 工具系统 & 三层安全权限闸门

所有工具统一注册到`TOOL_HANDLERS`字典，工具定义 JSON schema，传给 LLM 作为 function calling 工具描述。
工具分组：

- BASE_TOOLS：基础文件 + bash 工具（bash, read_file, write_file, edit_file, glob, load_skill, compact_context）
- TASK_TOOLS：任务 DAG 管理工具
- TEAM_TOOLS：多团队管理、worktree 工具
- TEAMMATE_TOOLS：子代理可用工具集合（不含团队管理）

三层安全校验 PreToolUse 钩子 `pre_tool_permission_check`：

1. 黑名单 DENY_LIST：直接拦截高危指令`rm -rf /`、shutdown、reboot、mkfs 等，直接拒绝。
2. 破坏性命令正则检测：匹配`rm / del`等删除命令；匹配`chmod 777`、直接写设备等高危操作。
3. 工作空间逃逸检测：read/write/edit 文件路径，解析绝对路径，**必须在 WORKDIR 根目录内部**，跳出工作目录直接拦截。

命中高危规则后交互式弹窗询问用户`Allow? [y/N]`，用户拒绝则直接终止本次工具调用；子代理后台执行时非交互模式，直接拒绝高危操作。

### 2.9 技能加载 SkillLoader

技能目录`SKILL_DIR`下每个子文件夹存放`SKILL.md`，采用 YAML frontmatter + markdown 正文。

```
---
name: 技能名称
description: 简短描述
---
技能详细指令，Agent调用load_skill读取全部内容，加载进会话上下文。
```

启动时自动扫描全部 SKILL.md，建立技能目录；Agent 调用`load_skill(name)`读取完整技能内容。

### 2.10 钩子系统 HOOKS_REGISTER

事件钩子，支持扩展注入自定义逻辑：

1. `UserPromptSubmit`：用户输入提交触发（日志打印）
2. `PreToolUse`：工具调用**之前**执行（日志打印、权限校验核心逻辑）
3. `PostToolUse`：工具调用**之后**执行（输出日志）
4. `Stop`：Agent 会话结束退出时触发

### 2.11 主循环 agent_loop 与 CLI 入口

1. `wait_for_cli_event`：CLI 交互，同时监听用户键盘输入 和 MessageBus 团队消息唤醒事件。
2. agent_loop 主轮次：
   - 加载相关记忆，构建 system 提示词
   - 消费 cron 队列，注入定时任务消息
   - 调用 context_compact 自动压缩会话
   - 注入后台任务通知
   - 调用 LLM，接收 tool_use 工具调用
   - 循环执行工具、追加 tool_result，直到无工具调用（LLM 输出纯文本，本轮结束）
   - 会话结束自动提取记忆，执行记忆合并
3. 主程序启动：`start_runtime_threads()`启动 cron 调度线程、cron 队列消费线程；循环等待 CLI 事件。

## 3. 目录结构说明

```
WORKDIR/
├── .tasks/                # Task任务持久化目录，每个任务task_xxxx.json
│   └── .lock              # FileLock任务存储锁文件
├── .memory/               # 记忆存储目录
│   ├── MEMORY.md          # 记忆索引（自动维护，不要手动修改）
│   └── *.md               # 单条记忆文档，yaml frontmatter + markdown正文
├── .mailboxes/            # Agent消息信箱，每个agent一个jsonl文件
├── .worktrees/            # Git worktree并行工作目录
├── transcripts/           # 上下文归档，保存超长对话jsonl归档文件
├── tool_results/          # 超大工具输出持久化文本文件
├── skill/
│   └── SKILL.md           # 技能定义文件
```

## 4. 核心运行约束与安全说明

1. 所有文件操作强制沙箱在 WORKDIR 内，禁止读取 / 写入项目外文件；worktree 也限制在`.worktrees`内部。
2. Teammate 执行破坏性 bash / 文件修改**必须提交 Plan 并由 Lead 审批**，未审批会被闸门拦截。
3. Cron 任务持久化使用原子文件替换，防止程序崩溃导致 json 损坏。
4. 文件锁：TaskStore 使用 FileLock，多线程读写任务文件不会发生竞争损坏。
5. 后台进程退出自动清理，防止长时间僵尸 shell 进程。
6. 记忆压缩、任务操作均有快照 + 回滚机制，避免数据丢失。

## 5. 工具清单（Agent 可调用）

### 基础工具

`bash`、`read_file`、`write_file`、`edit_file`、`glob`、`load_skill`、`compact_context`

### Cron 定时工具

`run_schedule_cron`、`run_list_crons`、`run_cancel_cron`

### 任务 DAG 工具

`create_task`、`update_task`、`list_tasks`、`get_task`、`claim_task`、`complete_task`

### 多团队管理工具

`spawn_teammate`、`list_teammates`、`send_message`、`request_shutdown`、`request_plan`、`review_plan`、`create_worktree`

## 6. 典型使用流程示例

1. 用户 CLI 输入，触发 Lead agent_loop
2. Lead 调用`create_task`创建多个任务，`update_task`添加依赖
3. Lead 调用`spawn_teammate`生成子代理，分配任务
4. Teammate 自动 claim_task 认领就绪任务
5. Teammate 准备修改代码，调用`submit_plan`提交方案，进入等待审批状态
6. Lead 调用`review_plan`批准计划，闸门放行
7. Teammate 执行 bash、文件编辑，完成后`complete_task`标记任务完成，自动解锁下游任务
8. 可通过`run_schedule_cron`设置定时 prompt，到时间自动触发 Agent 执行
9. 对话过程中自动保存记忆；会话过长自动压缩上下文；长时间 bash 放到后台异步执行。