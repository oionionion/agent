# CodeCLI 名称、目录与启动验证（2026-11-09）

## 启动标识与工具可见性补充（2026-11-10）

按用户反馈，把简略的 `[CC]` 替换为绿色块状双C标识；宽终端左右排列、窄终端上下排列并折行。恢复 Browser、Image、Memory、RAG 功能提示。启动屏从实际工具注册表计算内置/MCP工具数，列出每个MCP服务的就绪、启动中、失败、禁用或待确认状态；`/tools`可随时查看当前注册工具，`/mcp`和`/mcp logs <name>`查看实时服务与日志。

只读核对Windows用户目录：18个内置工具仍在；用户级`C:\Users\86138\.paicli\mcp.json`仍配置并启用`chrome-devtools`。本轮未修改该文件、启动真实浏览器MCP或调用真实模型；配置存在不等于该服务已经连接。前一轮缩短的是首屏功能提示，不是删除实际工具实现。

验证：9组定向测试168项、MCP配置/注册/管理回归28项，均0失败/错误/跳过；离线clean package成功，新jar元信息和隔离本地命令通过。用本地stdio测试服务完成真实进程握手/发现/注册，120列和40列下`/tools`、`/mcp`都能显示18内置+1测试MCP工具，失败/禁用/待确认状态正确；模型调用计数0。测试服务不代表用户chrome-devtools已连接，未跑全量或Native AgentBench。

结构化证据见[本轮验证记录](validation/codecli-startup-tools-2026-11-10.json)，本地原始日志/测试驱动在`.codex/better-harness/codecli-startup-tools-2026-11-09/`（会话从11月9日跨至10日）。日常启动产物仍为`target/codecli-1.0-SNAPSHOT.jar`，更新后须退出旧进程再启动新jar。

## 本轮范围

对外名称与当前简历统一为 **CodeCLI — 本地代码助手**：终端横幅、帮助、导出、TUI、浏览器操作提示、微信启动用法、模型身份和 Maven 产物同步更新，产物为 `target/codecli-1.0-SNAPSHOT.jar`。`/init` 优先识别 CodeCLI，仍兼容识别 OpsPilot 与 PaiCLI。

本地学习工作目录迁到 `E:\obsidian\agent\项目\codecli-main`，实际源码位于其 `codecli-main/` 子目录；IDEA 项目名称、模块文件和 Maven/VCS 路径一起更新。外层三份参考材料改用 `CodeCLI-` 文件名前缀，独立知识库目录及原有笔记名称不改，避免破坏 Obsidian 链接。

仓库内 23 篇文章、AgentBench 说明和 5 个历史图片资源的文件名由 `paicli-` 统一为 `codecli-`，本地链接随之调整。文章正文、图片内容、原作者说明与外部原文网址不批量改写，旧图片不是本轮启动截图；`docs/reference/upstream-readme.md` 与冻结评测资料保持原样。

## 兼容边界

- 保留 Java 包 `com.paicli`、内部 `PaiCli*` 类名、`PAICLI_*` 环境变量、`paicli.*` 系统属性、`.paicli` 数据目录与 `PAI.md`，不搬动用户配置/记忆/会话数据。
- 不修改任务调度、审批、安全策略、模型接口或评测语义；不把原有功能重新登记为本人新增实现。
- GitHub 仍使用原仓库 `oionionion/agent`，不擅自重命名远端仓库或改写历史。
- 当前会话占用旧外层目录，直接重命名失败后已将全部内容迁入新目录；旧目录仅残留空壳，待关闭占用程序后可删除。没有强行终止用户程序。

## 验证

最终结果见[结构化验证记录](validation/codecli-rename-2026-11-09.json)。环境为 Windows Microsoft JDK 17.0.15、Maven 3.9.9，WSL 直接调用 Windows JVM，使用已缓存依赖。

| 检查 | 结果 |
| --- | --- |
| 改名前 9 组定向回归 | 161 项，0 失败/错误/跳过 |
| 改后相同 9 组（新增 2 项 CodeCLI 初始化测试） | 163 项，0 失败/错误/跳过 |
| `mvn -o clean package -DskipTests` | 成功；旧 PaiCLI/OpsPilot jar 已清理 |
| 实际新 jar `--version`、`--help`、`-h` | 均退出 0；显示 CodeCLI，未创建配置 |
| `wechat --help` | 退出 0；展示 CodeCLI jar，不绑定账号或启动微信服务 |
| 隔离交互启动 | `/context`、`/memory`、`/init`、`/exit` 全程退出 0；新横幅、模型配置读取及 CodeCLI 项目识别通过 |
| 本地文件链接 | 检查 25 处指向改名文件的 Markdown 链接，0 处失效 |
| 历史图片、原 README | 5 张图片逐字节不变；原 README SHA-256 与上轮一致 |

定向测试命令：

```bash
mvn -o test -DskipTests=false \
  -Dtest=MainInputNormalizationTest,CliCommandParserTest,ProjectMemoryInitializerTest,PromptAssemblerTest,SkillBuiltinExtractorTest,PlainRendererTest,LanternaRendererTest,BrowserGuardTest,PaiCliCompleterTest
```

交互检查使用独立工作目录/用户主目录、虚拟凭据、`127.0.0.1:9` 模型地址和空 MCP 配置；只发送以上本地命令，确认没有继承真实 API 凭据，未发送模型任务。jar SHA-256 为 `0f8ff7eebda4c0ada631ff9c7dc5cfdb3aded2fe31082dc04c28abee973ceedf`。

第一次改后定向测试有一项仍期待旧 `[OP]` 横幅；同步为 `[CC]` 后全部重跑通过。最初 PowerShell 启动包装未正确启动/返回输出，验证改用直接 Windows JVM，不把旧测试报告或空输出当作通过。原始日志保存在本地忽略目录 `.codex/better-harness/codecli-rename-2026-11-09/`。

本轮不调用真实模型、不启动外部 MCP/浏览器、不跑 Docker 或 Native AgentBench；没有重跑 `phase16-smoke` 或全量测试，因此不声称它们通过。

回退标签：`backup/pre-codecli-rename-2026-11-09`，指向改名前提交 `5ef27c311138d09bc4fdfebc10829ccade229b27`。原分支和历史保留，不用强制重置覆盖后续工作。

---

# 历史记录：2026-10-25 OpsPilot 外部品牌迁移

## 范围

本轮只统一项目对外标识：Maven artifact/name、终端启动横幅、帮助/退出/导出、可选 TUI 标题与正文身份、模型身份提示、README、示例中的 jar 路径。

新增 `--version` 与 `--help` / `-h`，在读取配置与建立外部连接之前返回；用于无模型的启动检查，不证明交互任务或内网部署已验收。

## 保持不变

`com.paicli`、内部类名、主入口、`PAICLI_*`、`paicli.*`、`.paicli`、`PAI.md`、API 鉴权头和内部协议标记不迁移；历史教程/评测材料与资源署名不批量改写。保留原始 README 的作者说明和内容，存档于 `reference/upstream-readme.md`。

`/init` 识别 OpsPilot，同时保留 PaiCLI 项目识别兼容。没有修改任务调度、权限语义、数据存储或自动增加联网能力。

## 回退点

改动前提交：`6b823bebac74d2e558bbabe16ac973f3e5fb4def`。

本地标签：`backup/pre-opspilot-surface-2026-10-25`。

工作分支：`codex/opspilot-surface-2026-10-25`。

回退时先检查/备份当前未提交内容，再在独立工作区检出标签；不要用强制重置覆盖新工作。已有 `main` 不改写历史。

## 验证

命令、统计、启动输出与最终 jar 哈希见[结构化检查记录](validation/opspilot-surface-2026-10-25.json)。

环境：Windows JDK 17.0.15、Maven 3.9.9，从 WSL 调用 Windows JVM；Maven 使用本机已缓存依赖（`-o`）。

| 检查 | 改动前 | 改动后 |
| --- | --- | --- |
| 七组定向回归 | 127 项通过，0 失败/错误/跳过 | 131 项通过，0 失败/错误/跳过 |
| 浏览器策略与命令补全 | 本轮未单独跑基线 | 30 项通过，0 失败/错误/跳过 |
| `phase16-smoke` 界面测试 | 140 项，2 失败、1 错误 | 142 项，2 失败、1 错误；失败名单相同 |
| Maven 打包 | 不单独作为测试结论 | `package -DskipTests` 成功，产物 `target/opspilot-1.0-SNAPSHOT.jar` |
| 实际 jar 启动 | 未运行旧 jar | `--help`、`--version`、隔离配置启动并 `/exit` 均退出 0 |

七组定向回归的命令：

```bash
mvn -o test -DskipTests=false \
  -Dtest=MainInputNormalizationTest,CliCommandParserTest,ProjectMemoryInitializerTest,PromptAssemblerTest,SkillBuiltinExtractorTest,PlainRendererTest,LanternaRendererTest
mvn -o test -DskipTests=false -Dtest=BrowserGuardTest,PaiCliCompleterTest
mvn -o test -Pphase16-smoke -DskipTests=false
mvn -o package -DskipTests
```

界面测试在改动前标签的独立工作区与改动后工作区分别执行；三项均为原有 `InlineRendererTest`：

- `streamUsesPrintAboveWhenLineReaderIsReading`：期望 LF、实际 CRLF 的换行差异。
- `firstCodeBlockSharingLineWithAnswerMarkerStillFolds`：首个代码块折叠断言失败。
- `promptApprovalDelegatesToFallback`：模拟终端读取器导致 `NullPointerException`。

因此不能将 `phase16-smoke` 或全量测试称为通过；本轮没有修改这部分渲染逻辑，也没有为了品牌迁移修复它们。

启动检查使用临时工作目录和独立的 `user.home`、虚拟凭据、本机不可用端点、空 MCP 服务配置，仅发送 `/exit`；未发送模型任务，未启动浏览器或真实 MCP 服务。`--help` 与 `--version` 在空主目录下不创建配置文件。微信仅检查 `wechat --help`，不绑定账号或启动服务。

首次交互检查因 WSL 的自定义环境变量未传入 Windows JVM，返回“未找到可用的 API Key”；随后改为临时 `config.json` 配置，启动与退出通过。此项为验证脚本环境修正，没有改动程序的配置读取逻辑。

原始 README 存档逐字节核对一致，SHA-256 为 `7e74c9c96637ababfc01fa18bdef6d4e36a4b26f974d878de8618fe756abffbb`。`git diff --check` 通过。

本轮不调用真实模型，不运行 Docker 或 Native AgentBench；内网模型部署与数据出域限制不在本轮验证范围。
