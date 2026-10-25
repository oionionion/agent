# OpsPilot — 研发运维智能体

面向研发与科研任务的 Java 终端智能体，围绕代码定位与修改、脚本执行、日志排查和构建验证，组织多步任务并约束工具操作。

## 核心能力

- **执行与规划**：ReAct 串联模型决策与工具反馈；Plan-and-Execute 校验任务依赖，按 DAG 调度，支持计划审阅与有界重规划。
- **工具协同与受控执行**：Function Calling 按调用 ID 对应结果，批内白名单只读工具并行，副作用/未知调用串行；通过参数检查、路径/命令策略、人工审批与审计约束执行。
- **Memory 与上下文**：管理项目规则与长期事实，结合工具结果外置、旧结果清理和历史摘要控制上下文增长。
- **MCP 接入**：支持 stdio 本机子进程和 Streamable HTTP 服务，串联初始化、发现、注册、调用及结果回填。

## 快速开始

需要 Java 17+ 和 Maven。先配置一个可用模型，再启动交互终端：

```bash
cp .env.example .env
# 编辑 .env，填写实际模型连接配置；不要提交密钥。
mvn clean package
java -jar target/opspilot-1.0-SNAPSHOT.jar
```

仅查看版本或帮助，无需 API Key，不加载用户配置、不启动 MCP、不调用模型：

```bash
java -jar target/opspilot-1.0-SNAPSHOT.jar --version
java -jar target/opspilot-1.0-SNAPSHOT.jar --help
```

默认构建跳过测试。源码修改后请重新打包，不要继续使用旧 jar。

## 一个任务流程

> 检查实验脚本的报错日志，结合代码与配置给出处理计划；确认后再执行指定修改和验证，最后汇总改动与未解决项。

```text
理解任务 → 定位代码/配置/日志 → 制定计划 → 策略检查与必要审批
         → 修改或执行工具 → 分析反馈 → 汇总结果
```

可用于代码维护、实验脚本排错和日志分析；需要执行修改时，可使用规划审阅与 ask 审批控制操作范围。

## 常用交互命令

| 命令 | 用途 |
| --- | --- |
| `/plan <任务>` | 进入本次任务的规划、审阅与执行流程 |
| `/mode auto` / `/mode ask` | 切换工具审批预设 |
| `/mode plan` | 让后续普通任务默认进入规划；不是第三档审批 |
| `/mcp` | 查看 MCP 服务状态与工具 |
| `/context` | 查看上下文预算与治理状态 |
| `/memory` / `/save` | 查看长期事实或显式保存稳定约定 |
| `/export` / `/resume` | 导出当前会话或恢复历史会话 |
| `/exit` | 退出交互终端 |

## MCP：按工具部署位置选择传输

- **stdio**：启动本机工具进程，通过标准输入/输出通信。
- **Streamable HTTP**：连接配置的 HTTP 服务，可用于本机、内网等获准端点；不是只面向公网。
- 两者都保留。工具是否联网、能访问什么数据，由服务权限及执行环境约束，不由传输名称保证。

配置仍读取用户级 `~/.paicli/mcp.json` 和项目级 `.paicli/mcp.json`；项目级服务需按现有信任流程确认。

## 内网部署边界

面向源码、科研数据和日志需在内网处理的环境，模型与工具应使用获准端点，并在运行环境中配置访问策略。

部署时要分别确认：

1. 模型推理端点、认证连接和请求数据是否全部在获准边界内。
2. MCP 目标服务、业务数据源及工具自身的出站访问是否受控。
3. 构建依赖、浏览器和其他可选组件是否需要外部连接；原有默认 MCP 模板可能使用 `npx` 获取依赖，内网使用前需调整。
4. 文件/命令检查、人工审批与审计不是容器或网络防火墙，不能替代运行环境的隔离策略。

## 构建与定向验证

```bash
# 离线指使用已经缓存的 Maven 依赖，并非项目运行自动离线。
mvn -o test -DskipTests=false \
  -Dtest=MainInputNormalizationTest,CliCommandParserTest,ProjectMemoryInitializerTest,PromptAssemblerTest,SkillBuiltinExtractorTest,PlainRendererTest,LanternaRendererTest

# 终端/TUI 冒烟
mvn -o test -Pphase16-smoke -DskipTests=false

# 打包
mvn -o package -DskipTests
```

定向回归、打包与启动检查的结果，以及当前环境下既有的界面测试失败，见[验证记录](docs/opspilot-branding.md#验证)。

## 兼容与目录

- 构建产物为 `opspilot-1.0-SNAPSHOT.jar`，主入口为 `com.paicli.cli.Main`。
- 配置沿用系统属性 `paicli.*`、环境变量 `PAICLI_*`、配置目录 `.paicli` 及项目规则 `PAI.md`。
- `.env`、本地配置与 `target/` 不提交到版本库。

## 文档与来源

- [品牌迁移与验证记录](docs/opspilot-branding.md)
- [实现参考](docs/agents-reference.md)，模块文档位于 `docs/phase-*.md`。
- 代码基础来自 PaiCLI（沉默王二），作者说明与原始文档见[原 README 存档](docs/reference/upstream-readme.md)。
