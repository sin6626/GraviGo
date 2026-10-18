# GraviGo 英文文档、App 双语与首次发布计划

日期：2026-10-18。状态：待审阅，仅计划，尚未实施。

## 1. 本轮已明确的范围

- README 与 GitHub 仓库 About 使用英文。
- App 内提供「简体中文 / English」切换，覆盖应用界面，不只翻译关于页。
- 建立 GitHub Actions CI，并准备 GraviGo 1.0.0 的可审阅发布流程。
- 本轮只交付这份计划，不创建实际 README、工作流、签名密钥或 Release，不修改 App，不推送远程。
- 计划用中文便于审阅；未来面向外部读者的 README、GitHub About、构建步骤和 Release Notes 用英文。Agent.md 与既有内部开发记录不属于本次全文翻译范围。

## 2. 已核验现状

| 项目 | 当前情况 | 实施时处理 |
|---|---|---|
| 仓库 | `sin6626/AntigravityMobile-Android`，默认分支 main | GraviGo 仓库改名尚未执行；定稿后统一链接 |
| GitHub 页面 | README 请求 404；About description 为空、topics 为空；未看到 `.github`；Releases 为空 | 补完整入口，不沿用原作者的下载地址作为本项目下载入口 |
| 代码同步 | 核验时本地比已知远端 main 领先 14 个提交 | 实施后的工作流必须随相应源码进入远端，不能拿旧 main 的结果证明当前版本可发布 |
| 忽略规则 | 根目录 `/*` 未放行 README、LICENSE、`.github` | 精确放行计划新增文件，继续忽略本地缓存、证据和密钥 |
| Android | 1.0.0 / versionCode 5；minSdk 26；compileSdk 37；原包名 `com.antigravity.mobile` | 保持整数版本号递增，正式包名在首次发布前定稿 |
| 发布签名 | release 显式使用 debug signingConfig | 建立稳定正式签名，缺配置时失败，不自动退回 debug |
| Gradle | 9.7.1；仅跟踪 wrapper properties，缺 gradlew、gradlew.bat、wrapper JAR；本机 daemon 使用 JDK 25 | 补齐可核验 Wrapper 和版本配置，验证干净环境构建 |
| Lint | `abortOnError=false`，`checkReleaseBuilds=false` | 修复现有 errors 后启用真实失败检查，不让红色问题得到绿色 CI |
| 多语言 | 仅有中文默认资源；44 个 Kotlin 文件含汉字，包括注释、日志和界面文字 | 按用户可见字符串分类迁移，不能把 44 当成页面数或翻译数量 |
| Activity / Theme | ComponentActivity + 系统 Material Theme | 语言兼容层涉及 Activity 与主题，须单独验证 |
| Go 网关 | Go 1.26；CLI 和 Makefile 仍标 1.0.3；有 Windows 路径与 WebSocket 测试失败的历史记录 | 实施时重现并修正，统一发布版本；不能跳过失败假装绿灯 |
| Web | Go embed 打包静态资源 | 首版不添加没有实际构建需求的 Node/npm CI |
| 根许可证 | 当前未跟踪根 LICENSE；上游 LICENSE 为 MIT | 保留来源，先厘清本项目各部分的许可范围 |
| 高亮模块 | `android/highlight/LICENSE` 与 NOTICE 明确包含源自 RikkaHub 的 AGPL v3 代码 | 发布前明确对应分发与源码安排；不能直接给整个项目挂 MIT 徽章 |

上述远端信息通过 Codex GitHub 插件只读核验，未使用本机 GitHub CLI。历史测试失败不等于本轮已重现；本轮没有执行全量测试或云端构建。

## 3. 推荐实施顺序

### 阶段 A：确定正式发布身份与许可范围

1. 确认仓库最终名称，建议 `sin6626/GraviGo`，先核验是否可用。同步 README、App 内项目链接、issue 链接及后续徽章地址。
2. 确认最终 applicationId / namespace，建议评估 `io.github.sin6626.gravigo`。清点 Kotlin 包路径、Manifest、FileProvider authority、深链接、测试、推送配置与脚本，逐项迁移，不全仓无差别替换。
3. 更换包名会形成新应用身份。保留旧应用，不先卸载；拟定草稿与设置导入方式及重新配对步骤，验证后再迁移。Android Keystore 中的旧应用凭据不能当作普通 XML 直接跨包复制。
4. 建立来源和许可证清单：上游 antigravity-mobile 的 MIT、highlight / RikkaHub 的 AGPL、highlight.js、Mermaid、KaTeX、字体等。保留既有完整许可文本和修改说明，再确定根 LICENSE 的适用范围及第三方 notices。若现有模块的许可安排不适合预期分发方式，单独评估替换，不擅自删功能或改许可证标签。
5. 准备属于本项目的正式 keystore、alias、密码与证书指纹，并离线备份。密钥与密码不进入 Git 或聊天正文。明确新签名与已安装测试包的升级关系。

验收：项目身份、来源说明、许可范围和正式签名方案可明确描述；没有默认卸载、清数据或假设旧凭据自然继承。

### 阶段 B：让项目能从干净 clone 构建

1. 精确调整 `.gitignore`，放行 README、所需 LICENSE / notices、`.github/` 和必要文档。继续忽略 `.verification`、本机 SDK 配置、缓存与签名资料。
2. 通过 Gradle 正规任务补全 `gradlew`、`gradlew.bat`、`gradle-wrapper.jar`，提交脚本执行位和发行 ZIP checksum，并校验 Wrapper。
3. CI 显式准备 JDK、Gradle、Go 与 Android SDK，不引用本机 `C:` / `G:` / `H:` 路径。初始建议对齐 Gradle 9.7.1、JDK 25 和字节码目标 17；最终以干净环境实测为准，不为接 CI 顺便升级依赖。
4. 用 sdkmanager 确认 compileSdk 37 所需精确平台包和 Build Tools 可安装，然后固定配置。缺包时明确失败，不静默改装 preview 或降低 compileSdk。
5. 固定并记录 Go 1.26 的实际补丁版本，确保 go.mod、README 与 CI 一致。Go 静态 Web 资源随源码构建，不添加多余 npm 安装步骤。
6. 复查并修复已有 Go 路径/换行断言及 WebSocket 偶发用例；执行 `go test ./...` 和 `go vet ./...`，不使用 continue-on-error 掩盖。
7. 将 lint 改为 errors 会失败的检查，先处理存量错误，不要求首版消除所有普通 warning。

验收：没有本机缓存、绝对路径或私有配置的干净 clone 能完成 Android 与网关构建。

### 阶段 C：接入普通 CI

首版建议保留两个工作流文件：`ci.yml` 与 `release.yml`。CI 的作用是每次代码变动自动发现构建和测试问题；它不代表已完成真机或真实 Antigravity 端到端验收。

| 检查 | 触发 | 内容 | 产物 |
|---|---|---|---|
| Android | PR 到 main、push main、可复用调用 | Wrapper、构建 Debug、编译 AndroidTest、三个 Android 模块 JVM 测试、lintDebug、资源翻译检查 | 测试 APK、测试报告、Lint 报告 |
| Go | 同上，Ubuntu 与 Windows | gofmt 检查、go vet、全量 go test、网关构建 | 测试日志、可选构建产物 |
| 发布前 UI | 语言实现后及发布前手动验收 | 中英文、旧版/新版 Android、开屏、账号、配对、草稿、附件、方案与 diff 阅读 | 隔离测试截图、回归记录 |

具体 Gradle task 在实施时核对，至少包含 app 测试与高亮/Mermaid 模块测试，不只编译 APK。普通 CI 产物明确标记为 debug 测试包；runner 临时 debug 证书不承诺可以覆盖本机现有测试包。

CI 配置要求：

- 默认 `contents: read`，不读取正式签名 secrets；fork PR 也应能跑完整普通 CI。
- 不使用 pull_request_target 来执行 PR 提供的 Gradle / Go 代码。
- 使用受维护的官方 actions，实施时核对版本并固定完整 commit SHA。使用 setup-gradle 的 Wrapper 校验与缓存，不叠加重复 Gradle 缓存。
- PR 缓存只读，main 更新缓存；设置合理的超时、产物保留期与 concurrency，同一 PR 新提交取消旧检查。
- 首版不做复杂 paths 过滤，避免必需检查未触发而一直 pending。
- 第一轮真实运行成功后，将 Android 与 Go 检查加入 main 的必需检查；设置是否允许管理员绕过由仓库规则明确记录，不假装“写了 YAML 就已保护分支”。

验收：一个正常 PR 能通过；一个受控的构建/测试错误会使检查失败；fork PR 没有正式 secrets 也能运行；远端 Actions 的结果对应实际提交。

### 阶段 D：App 内中英文切换

**用户体验：**

- 在「账号与设置」增加 Language / 语言入口，提供「简体中文」与「English」两个明确选项。
- 切换后界面更新，重启后保留选择。已有安装默认保持中文；新安装按系统语言选择中文或英文，用户手动选择后以该选择为准。
- 账号、配对、当前会话、输入草稿、附件和本地阅读偏好保留。语言切换引发 Activity 重建时，不重复开屏、重发消息或丢失进行中的状态。
- 本地菜单、设置、按钮、空态、错误提示、无障碍描述、通知和关于介绍均纳入翻译。
- AI 回复、用户输入、模型与套餐原名、代码、文件正文、网关返回的原始错误不做自动翻译。本地错误外壳可以翻译，原始技术详情保留。

**实现方式：**

1. 建立完整英文默认资源 `values/strings.xml` 与简体中文 `values-zh-rCN/strings.xml`；图标名称、品牌名和协议枚举按需标为不可翻译。先迁移再检查缺项，不只增加一个语言开关。
2. 迁移 Compose 中的硬编码文字，使用 stringResource / pluralStringResource，使用参数与复数资源处理动态文本。ViewModel / service 中的本地错误改为可本地化的消息类型或资源引用，在展示处解析，避免固定语言字符串残留在状态中。
3. 首选 AndroidX 的应用语言 API 与 Android 系统语言设置同步。当前 MainActivity 是 ComponentActivity；按官方要求，使用 AppCompatDelegate 支持 Android 12 及更早版本时，需迁移为 AppCompatActivity，并采用兼容主题。先做这个小范围验证，再批量迁移文案。
4. 配置支持的 locales、持久化与 Android 13+ 系统 App Language 入口。实际采用手工 LocaleConfig 或 AGP 自动生成中的一种，避免两套重复配置。
5. 保留现有开屏、状态栏、返回、二维码入口、权限请求和 Compose 生命周期行为。
6. 更新依赖中文文案定位的测试。优先用稳定语义或资源定位；有意检查语言内容的用例分别断言中文与英文。

验收：至少覆盖一个 Android 13 以下版本及当前模拟器，两种语言、大字号与长文本；覆盖切换、旋转/重建、进程恢复、草稿/附件、配对、文件阅读、错误重试和开屏。不能以关于页已翻译代替整 App 双语完成。

### 阶段 E：补齐英文 README 与 GitHub About

README 建议包含：

1. 产品名、现有正式图标、英文定位与下载入口。
2. 手机与电脑网关的关系，以及必须在电脑运行 Antigravity 的前提。
3. 已实现功能、支持平台与已知限制。
4. 下载 APK 和匹配网关、启动网关、获取配对码、连接手机的最短步骤。
5. 同一局域网、Android 模拟器、远程访问各自的连接方式。模拟器地址以真实网关端口为准，不让 `10.0.2.2` 与电脑 `localhost` 混淆。
6. 源码构建命令与要求，由干净 clone 实测；不引用缺失的 scripts 或本机绝对路径。
7. 配对、鉴权与脱敏日志说明；截图使用隔离测试内容，避免真实邮箱、会话和地址。
8. 贡献、反馈、来源、许可范围和第三方声明。

不照搬上游对 iOS、macOS、Linux、语音或通知的支持承诺。语音目前存在账号 scopes 限制，FCM 等配置也须核验；没有验证成功的能力明确标注限制。现有静态 Web 页面不等于已有原生 iOS 客户端。

**英文首页文案草案：**

> # GraviGo
>
> Bring Antigravity with you.
>
> GraviGo is an Android companion for Google Antigravity. Connect to your desktop gateway to follow coding sessions, send instructions, and review plans and code changes from your phone.
>
> Based on GHSaiMo's antigravity-mobile project, with changes to the Android app and desktop gateway. See the credits and license notices for details.

**GitHub About 草案：**

> An Android companion for Google Antigravity. Follow coding sessions, review changes, and stay connected to your desktop.

建议 topics：`android`、`kotlin`、`jetpack-compose`、`antigravity`、`go`、`developer-tools`。没有独立官网时，Website 先留空，不填原作者官网。仓库实际更名后再生成徽章和固定链接。

验收：公开文案为英文，所有下载/源码/来源链接指向正确项目，功能与构建步骤可以复现，不把待实现项写成已实现。

### 阶段 F：配置手动发布与正式签名

**发布范围建议：** 首发提供 Android 签名 APK，以及经当前真实桌面链路验证的 Windows x64 网关。macOS / Linux 的运行支持在独立验证后加入；交叉编译成功不能当作端到端可用证明。暂不配置 Google Play 自动上传。

**发布流程：**

1. `release.yml` 只允许从可信 main 手动触发，不让每次 push 自动生成公开版本。
2. 固定本次运行对应的 commit SHA，在这个提交上复用/重新运行 CI。不能查询“最近一次绿色结果”就发布另一个提交。
3. 校验目标 tag、versionName、versionCode、网关版本与发布说明一致；1.0.0 可保留当前 code 5，后续升级递增。网关仍写 1.0.3 的地方需同步处理。
4. 普通检查完成后，签名 job 才读取正式密钥。建议 GitHub Secrets 为 `ANDROID_KEYSTORE_BASE64`、`ANDROID_KEYSTORE_PASSWORD`、`ANDROID_KEY_ALIAS`、`ANDROID_KEY_PASSWORD`；预期签名证书 SHA-256 可作为非秘密发布校验值保存。
5. 在本次可信 checkout 的隔离临时目录恢复密钥文件，不提交、不缓存、不上传；只清理明确创建的文件，不引入批量删除脚本。缺少签名配置时立即失败，不能回退到 debug。
6. 校验 APK 签名和证书指纹，生成 SHA256SUMS。建议产物命名为 `GraviGo-1.0.0.apk`、`GraviGo-Gateway-1.0.0-windows-amd64.zip`、`SHA256SUMS.txt`。
7. 使用 GitHub Releases API 创建英文 draft Release，指向同一个提交。发布 job 才需要 `contents: write`；不需要个人 PAT 或不必要的权限。
8. 已有同名 tag 时检查其最终提交；同名公开 Release 不覆盖。重跑已有 draft 时先核对提交和产物，避免把不同构建混在一起。
9. 从 draft 下载包，完成安装、网关启动、配对、聊天读取/发送、文件与方案/代码变更审阅的真实验收。确认许可证材料、源码与校验和匹配，然后才公开发布。

验收：缺密钥不会产生冒充正式版的 APK；失败的 CI 不生成发布包；draft 的签名、版本、tag 与源码提交可互相核对。不能把 draft 创建成功表述成已经正式发布。

## 4. 预计涉及的文件

| 范围 | 预计文件 |
|---|---|
| 英文文档 | 新 README.md、LICENSE / 第三方 notices（许可范围确认后）、必要发布指南与 Release Notes 草稿 |
| GitHub 元信息 | About description、topics、仓库名与最终链接；这是仓库设置，不是 about.md |
| 构建基础 | .gitignore、Gradle Wrapper、必要 JVM / SDK 配置、lint 配置 |
| App 双语 | strings.xml、values-zh-rCN、locale 配置、MainActivity、主题、语言入口和存储、UI 与本地错误展示、相关测试 |
| 发布身份 | applicationId / namespace、Manifest、Provider / 深链接与调用方、签名配置、版本信息 |
| CI | .github/workflows/ci.yml、.github/workflows/release.yml |
| 网关 | 版本注入、必要的已知失败测试修正、经验证的构建/使用说明 |

不修改真实账号、不重置配对、不上传本机环境文件，不把旧视频目录清理混进这次发布准备。远端维护先使用 Codex GitHub 插件；插件不支持的设置操作在实施时明确说明所需入口，不暗中使用本机 GitHub CLI。

## 5. 完成标准与当前未决项

完成这份计划不代表项目已具备发布条件。后续实施按以下顺序推进：

1. 身份、许可证范围、数据迁移和正式签名方案定稿。
2. 干净 clone 能构建，基础 CI 在远端实际跑通。
3. 中文/英文 App 界面与必要的平台回归完成。
4. 英文 README / GitHub About 与实际功能一致，最终 CI 及分支必需检查生效。
5. 对同一提交生成正式签名包和英文 draft Release。
6. 实际安装验收后，按用户的明确发布指令公开 1.0.0。

尚待实施确认：最终仓库/包名；AGPL 模块分发与源码安排；密钥归属与备份；旧包数据迁移；首发网关平台范围；SDK 37 精确安装包及云端构建；已有测试/Lint 的实际错误；App 全量翻译清单。

首版暂不引入自动版本号、Google Play 自动发布、所有 CPU/系统打包、每个 PR 云模拟器矩阵或无实际构建需求的前端流水线。

## 6. 核验依据

- [当前 GitHub 仓库](https://github.com/sin6626/AntigravityMobile-Android)
- [原项目 MIT LICENSE](https://github.com/GHSaiMo/antigravity-mobile/blob/main/LICENSE)
- 本地 `android/highlight/LICENSE` 与 `android/highlight/NOTICE.md`，以及各依赖随附许可。
- [Android 应用语言 API 与 Compose / AppCompat 兼容要求](https://developer.android.com/guide/topics/resources/app-languages)
- [Android 正式签名](https://developer.android.com/studio/publish/app-signing)
- [Gradle Wrapper 与完整文件/校验](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
- [GitHub Actions 安全使用与权限](https://docs.github.com/en/actions/reference/security/secure-use)
- [GitHub 工作流复用](https://docs.github.com/en/actions/how-tos/reuse-automations/reuse-workflows)
- [GitHub 工作流并发控制](https://docs.github.com/en/actions/how-tos/write-workflows/choose-when-workflows-run/control-workflow-concurrency)
- [GitHub Release 草稿与发布](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository)

官方 action 版本、完整 SHA、Runner 与 SDK 准备的调研记录保存在本项目忽略目录 `.verification/2026-10-18-release-plan/ci-official-research.md`。实施时重新核对实际可用版本，不以该记录替代云端运行结果。
