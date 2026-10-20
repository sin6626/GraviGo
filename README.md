# GraviGo

**简体中文** | [English](README.en.md)

**把Antigravity，带在身边**

GraviGo 是一个用于远程操控电脑端 Antigravity 的 Android 应用。通过电脑上运行的网关，你可以在手机上发送指令、查看任务进度，并审阅方案与代码变更。

这个项目源于一个直接的需求：现有的 Antigravity Remote 体验没有满足我们的使用习惯，因此我们基于[九哥（GHSaiMo）的 antigravity-mobile](https://github.com/GHSaiMo/antigravity-mobile) 项目，继续开发更适合手机操作的远程客户端。GraviGo 是独立的社区项目。

[![CI](https://github.com/sin6626/GraviGo/actions/workflows/ci.yml/badge.svg)](https://github.com/sin6626/GraviGo/actions/workflows/ci.yml)

## 可以做什么

- 浏览电脑上的工作区与会话，查看流式回复，发送消息和附件。
- 引用工作区文件、目录，选择 Antigravity 提供的命令。
- 使用 Android 原生界面阅读 Markdown、代码、公式、图表及关联文件。
- 审阅方案和代码变更，针对选中文字或代码行提交评论。
- 查看电脑端当前账号与模型额度。账号信息来自已配对的电脑，App 不提供单独的账号登录。
- 在“账号与设置 → 语言”中切换简体中文和英文。会话正文、文件内容及服务端名称保留原文。

语音转写依赖电脑端账号的能力与权限，部分账号可能返回权限不足，不能保证所有账号都可使用。

## 当前状态

App 与网关版本为 **1.0.0**，Android 版本号为 **5**。正式下载见 [GitHub Releases](https://github.com/sin6626/GraviGo/releases)；源码中的版本号不代表对应二进制已发布。

目前优先支持 Windows x64 电脑。网关在 CI 中也会进行 Linux 编译与测试，但这不代表 Linux 桌面集成已经完整验证。当前集成验证使用 Antigravity 2.19.1；桌面端接口更新后可能需要适配。

## 构建项目

需要准备：

- Android SDK 平台 37，SDK 包名为 `platforms;android-37.0`；Build Tools 36.0.0；使用 JDK 25 运行仓库内的 Gradle 9.7.1 Wrapper。App 字节码目标为 Java 17，最低支持 Android 8.0 / API 26。
- Go 1.26.9，用于构建网关。Web 静态资源已内嵌，不需要 Node.js 构建。
- Python 3，用于翻译生成与源码检查。

克隆仓库后，在 Android Studio 中打开 `android/` 目录。通过 IDE 设置 SDK 路径，或写入不提交到 Git 的 `android/local.properties`。在仓库根目录执行：

```powershell
.\android\gradlew.bat -p android testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest
go test ./...
go build -trimpath -o bin/gravigo-gateway.exe ./cmd/gateway
```

Linux 使用 `./android/gradlew -p android` 替代 Windows Wrapper 命令。

Debug APK 位于 `android/app/build/outputs/apk/debug/app-debug.apk`，包名为 `io.github.sin6626.gravigo.debug`。正式版包名为 `io.github.sin6626.gravigo`。

早期的 `com.antigravity.mobile` 是另一个独立安装的应用，其配对、草稿和私有存储不会自动迁移。如果仍需要旧数据，请保留原安装，并为新 GraviGo 单独配对。更换包名或签名不能直接覆盖原应用。

## 启动开发环境

先打开 Antigravity 和 Android 模拟器，再在仓库根目录运行：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\android\scripts\start-dev.ps1
```

此入口检查或启动 `58901` 模拟器网关，构建并保数据安装 Debug APK，然后从实际 APK 读取包名和入口 Activity 启动，避免混用 IDE 中的旧包名。已有健康网关会被复用，不会自动停服；端口空闲时构建新版网关。现有开发配对会继续使用，首次连接时填写 `http://10.0.2.2:58901` 并申请配对码。

只检查或启动网关时，运行 `android/scripts/start-emulator-gateway.ps1`。脚本复用已有管理网关的数据目录；新克隆使用项目内被 Git 忽略的 `.local/emulator-gateway/`。它不会设置开机自启，也不会重置配对。

## Android Studio 提示 Activity 不存在

如果点击运行后看到：

```text
Activity class {com.antigravity.mobile/io.github.sin6626.gravigo.MainActivity} does not exist
```

说明 IDE 把旧应用包名与新 Activity 类名组合在了一起。执行 **Sync Project with Gradle Files**，将 app 的 Build Variant 选为 **debug**，并在 **app** 运行配置中使用 **Launch: Default Activity**，然后重新运行。

开发版应启动 `io.github.sin6626.gravigo.debug` 包中的 `io.github.sin6626.gravigo.MainActivity`。不要通过卸载旧应用或清空数据解决此问题。正式包 `io.github.sin6626.gravigo` 需要单独的正式签名构建。

## 连接手机与电脑

1. 在 Windows 电脑上打开 Antigravity。
2. 在终端启动已构建的网关，并保持终端运行：

   ```powershell
   $env:CF_TUNNEL_ENABLED = '0'
   .\bin\gravigo-gateway.exe run -port 58900
   ```

3. 打开另一个终端，运行 `.\bin\gravigo-gateway.exe pair -port 58900`，从运行中的网关申请临时配对链接与二维码。
4. 安装并打开 Android App，扫描二维码或填写配对信息。真机使用电脑可达的局域网地址，Android 模拟器使用 `10.0.2.2:58900`；手机上的 `127.0.0.1` 指向手机自身。

真机与电脑需能互相访问。Windows 防火墙询问时，仅在可信专用网络上允许网关。使用自定义端口时，`run`、`pair`、`list`、`clear` 和手机端填写的端口应一致。

网关关闭或电脑重启后，手机连接也会中断，需要重新启动对应网关。网关 ZIP 的完整使用说明见[网关指南](android/docs/GATEWAY-README.md)。

请在可信网络中使用网关，或通过 Tailscale 等专用网络配合 HTTPS 访问。配对链接和设备令牌会授予电脑会话访问权限，不要公开。环境变量见 [`env.example`](env.example)；现有 `MULTIGRAVITY_*` 配置键与旧版深链协议继续兼容。旧安装中名为 `mgy` 的程序与新版 `gravigo-gateway.exe` 可能分别存在。

1.0.0 的已验收连接范围是局域网。自动 Cloudflare 穿透默认关闭，需要显式设置 `CF_TUNNEL_ENABLED=1`；上游域名在当前环境返回 1010/403，公网与远程推送不列入本版已验证能力。真机验收按项目所有者要求暂缓。

## CI 与发布准备

CI 执行 Android 各模块单测、lint、Debug APK 构建、仪器测试编译，以及 Windows 和 Linux 上的 Go 格式、vet、测试与构建检查。模拟器 UI 测试仍是本地发布验收项目，不会在每次 PR 中自动运行。Android 报告保留七天。

独立的 **Draft release** 工作流需要手动触发，仅允许 `main`。它重新检查选定提交，要求正式签名密钥并核对证书，再生成带校验和的发布草稿，不会直接公开发布。签名配置和发布验收见[发布指南](android/docs/releasing.md)。

翻译源文件位于 [`android/localization.json`](android/localization.json)。修改后执行：

```powershell
python android/scripts/localize_strings.py
python android/scripts/localize_strings.py --check
```

Lint 错误会阻断构建。由于 lint 32.3.1 的 `BidiSpoofing` 检测器在遍历本项目 Kotlin 注释时会卡住，该项由 `android/scripts/check_bidi.py` 直接检查源码中的双向控制字符；Gradle 会在 lint 分析前运行它，其余 lint 检查照常执行。

## 致谢与许可证

GraviGo 使用 GNU Affero General Public License 第 3 版，标识为 **AGPL-3.0-only**。Copyright (c) 2026 sin6626 and GraviGo contributors。完整文本和声明见 [LICENSE](LICENSE)、[NOTICE.md](NOTICE.md) 和 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。

感谢九哥（GHSaiMo）的 [antigravity-mobile](https://github.com/GHSaiMo/antigravity-mobile) 提供基础与启发。原项目代码保留 MIT 声明；RikkaHub 高亮模块使用 AGPL-3.0，其中 highlight.js 内容使用 BSD-3-Clause。其他第三方组件保留各自许可证与声明。

每个二进制版本必须标明对应源码提交。手动发布工作流会同时准备该提交的源码归档、APK、网关 ZIP 与校验和。仓库包含源码和构建说明；本机签名密钥、密码、配对凭据及构建缓存不纳入版本控制。
