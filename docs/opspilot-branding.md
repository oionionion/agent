# OpsPilot 外部品牌迁移（2026-10-25）

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
