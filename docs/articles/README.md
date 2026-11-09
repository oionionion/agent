# PaiCLI 教程与面试题

> 2026-11-09：本地文章文件名统一为 `codecli-` 前缀；原文内容、作者署名及上游网站地址保留，当前启动命令以仓库根 README 为准。
> 当前交互入口补充：`/tools` 查看实际注册的内置/MCP工具，`/mcp` 查看MCP最新状态；启动界面显示块状CC标识及服务状态。

这里是 PaiCLI 配套文章的唯一源。javabetter.cn 上 `/sidebar/itwanger/paicli/` 下的页面是副本，改动先在这里完成，再用 `tools/sync-articles-to-javabetter.sh` 同步过去，线上 URL 保持不变。

文章 frontmatter 沿用 VuePress 格式（title / shortTitle / description / tag / category / author / date），图片统一走 CDN 链接，两边可以直接复用同一份 Markdown。改文章时以当前源码为准，涉及的源码事实优先引用 `AGENTS.md` 和对应类。

## 从零实现系列

| 文章 | 主题 |
|---|---|
| [build-agent-from-scratch.md](build-agent-from-scratch.md) | 第 1 期：ReAct 循环与工具注册 |
| [build-agent-p2-plan-execute.md](build-agent-p2-plan-execute.md) | 第 2 期：Plan-and-Execute 与 DAG 调度 |
| [build-agent-p3-memory.md](build-agent-p3-memory.md) | 第 3 期：短期记忆、长期记忆与上下文压缩 |
| [build-agent-p4-rag.md](build-agent-p4-rag.md) | 第 4 期：RAG 代码检索 |
| [codecli-multi-agent.md](codecli-multi-agent.md) | 第 5 期：Multi-Agent 协作 |
| [codecli-hitl.md](codecli-hitl.md) | 第 6 期：HITL 人工审批 |
| [codecli-async-parallel.md](codecli-async-parallel.md) | 第 7 期：异步与并行工具调用 |
| [codecli-multi-model.md](codecli-multi-model.md) | 第 8 期：多模型切换 |
| [codecli-websearch-webfetch.md](codecli-websearch-webfetch.md) | 第 9 期：联网能力 |
| [codecli-mcp.md](codecli-mcp.md) | 第 10 期：MCP 协议核心 |
| [codecli-mcp-advanced.md](codecli-mcp-advanced.md) | 第 11 期：MCP 高级能力 |
| [codecli-chrome-devtools-mcp.md](codecli-chrome-devtools-mcp.md) | 第 13 期：Chrome DevTools MCP |
| [codecli-cdp-session-reuse.md](codecli-cdp-session-reuse.md) | 第 14 期：CDP 会话复用 |
| [codecli-skill-system.md](codecli-skill-system.md) | 第 15 期：Skill 系统 |
| [codecli-image-input.md](codecli-image-input.md) | 第 21 期：图片输入 |
| [codecli-agentbench.md](codecli-agentbench.md) | 自建评测集：28 道题、证据检查与 LLM-as-a-Judge |

## 面试题

| 文章 | 主题 |
|---|---|
| [codecli-interview-agent-core.md](codecli-interview-agent-core.md) | Agent 核心架构 |
| [codecli-interview-memory-context.md](codecli-interview-memory-context.md) | 记忆与上下文 |
| [codecli-interview-tool-security.md](codecli-interview-tool-security.md) | 工具与安全 |
| [codecli-interview-prompt-skill.md](codecli-interview-prompt-skill.md) | Prompt 与 Skill |
| [codecli-interview-multi-model.md](codecli-interview-multi-model.md) | 多模型与成本 |
| [codecli-interview-mcp.md](codecli-interview-mcp.md) | MCP 协议与生态 |
| [codecli-interview-productization.md](codecli-interview-productization.md) | 产品化工程 |

## 其他

| 文章 | 主题 |
|---|---|
| [codecli-xuexiluxian.md](codecli-xuexiluxian.md) | 学习路线 |
| [codecli-go-launch.md](codecli-go-launch.md) | Go 版上线 |
| [codecli-python-launch.md](codecli-python-launch.md) | Python 版上线 |
| [codecli-ts-launch.md](codecli-ts-launch.md) | TypeScript 版上线 |
