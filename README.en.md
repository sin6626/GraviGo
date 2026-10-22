# GraviGo

[简体中文](README.md) | **English**

**Bring Antigravity with you.**

GraviGo is an independent Android remote client for Antigravity. Connect through a gateway running on your computer to control desktop sessions, send instructions, follow task progress, and review plans and code changes from your phone.

[![CI](https://github.com/sin6626/GraviGo/actions/workflows/ci.yml/badge.svg)](https://github.com/sin6626/GraviGo/actions/workflows/ci.yml)

GraviGo grew out of a practical need: the existing Antigravity Remote experience did not fit how we wanted to work from a phone. Inspired by and based on [Jiuge (GHSaiMo)'s antigravity-mobile](https://github.com/GHSaiMo/antigravity-mobile), it develops the Android remote experience and desktop gateway further. This is an independent community project.

## Product film

Full walkthrough · 2 min 27.8 sec. Turn on sound.

https://github.com/user-attachments/assets/6df80b11-0367-4075-95c5-6bfb31a43a83

[Video production and music credits](android/design/promo/remotion-v2/README.md).

## What you can do

- Browse desktop workspaces and conversations, follow streaming replies, and send messages with attachments.
- Reference workspace files and folders, and choose commands provided by Antigravity.
- Read Markdown, code, math, diagrams, and linked files with native Android views.
- Review plans and code changes, and submit comments tied to the selected text or lines.
- View the current desktop account and model quota. Account information comes from the paired desktop; GraviGo does not provide a separate account sign-in.
- Switch the app interface between Simplified Chinese and English in **Account & settings → Language**. Conversation content and server-provided names remain in their original language.

Voice transcription uses the desktop account's capabilities. Availability depends on its permissions; some accounts return an insufficient-scope error.

## Project status

The app and gateway are version **1.0.0**; the Android version code is **5**. Published downloads are listed on [GitHub Releases](https://github.com/sin6626/GraviGo/releases). A version number in the source does not confirm a published signed download.

Gateway packages are available for Windows x64 and macOS on Apple Silicon and Intel. Native macOS runners test and build both architectures and check the installer; the full connection flow with a real Antigravity desktop has not been accepted on a Mac. Linux is compiled and tested without a packaged installer. The Windows integration was exercised with Antigravity 2.19.1 and may need changes when its desktop interfaces change.

## Install the gateway

### Windows

Run this one-line command in PowerShell on Windows x64. Administrator access is not required:

```powershell
irm https://raw.githubusercontent.com/sin6626/GraviGo/main/android/scripts/install-gateway.ps1 | iex
```

The installer downloads the latest stable release, verifies its SHA-256 checksum, and extracts the gateway and complete licenses into `%LOCALAPPDATA%\GraviGo\Gateway\<version>`. It prints the commands to start the gateway and pair your phone. Open Antigravity first and keep the gateway terminal running. The installer preserves pairing data and does not change PATH or configure automatic startup.

Running it again verifies the installed version without replacing existing files. You can also download and extract the complete ZIP from [Releases](https://github.com/sin6626/GraviGo/releases/latest). See the [gateway guide](android/docs/GATEWAY-README.md) for details.

### macOS

In Terminal, run this command. It selects the Apple Silicon or Intel package automatically; Homebrew, Python, and `sudo` are not required:

```bash
curl -fsSL https://raw.githubusercontent.com/sin6626/GraviGo/main/android/scripts/install-gateway-macos.sh | bash
```

The installer verifies SHA-256, installs the complete gateway into `~/Library/Application Support/GraviGo/Gateway/<version>-<architecture>`, and prints startup and pairing commands. Open Antigravity and run `./gravigo-gateway run -port 58900` in the printed directory. In another terminal in that directory, run `./gravigo-gateway pair -port 58900`. Pairing data is preserved; automatic startup is not configured.

Mac packages do not have an Apple Developer ID signature or notarization. If macOS blocks execution, follow [Apple's instructions](https://support.apple.com/en-us/102445) in Privacy & Security. The installer does not disable Gatekeeper or remove quarantine attributes.

## Build

Requirements:

- Android SDK platform 37 (SDK package `platforms;android-37.0`), Build Tools 36.0.0, and JDK 25 to run the checked-in Gradle 9.7.1 wrapper. App bytecode targets Java 17; Android 8.0/API 26 or newer is required.
- Go 1.26.9 for the gateway. Its web assets are embedded; no Node.js build is required.
- Python 3 for the translation and source checks.

Clone this repository and open `android/` in Android Studio. Set your SDK location through Android Studio or an untracked `android/local.properties` file. From the repository root:

```powershell
.\android\gradlew.bat -p android testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest
go test ./...
go build -trimpath -o bin/gravigo-gateway.exe ./cmd/gateway
```

On Linux, use `./android/gradlew -p android` instead of the Windows wrapper. The debug APK is written to `android/app/build/outputs/apk/debug/app-debug.apk` and uses `io.github.sin6626.gravigo.debug`. Production uses `io.github.sin6626.gravigo`.

The earlier `com.antigravity.mobile` installation is a separate app. Its pairing, drafts, and private storage are not migrated automatically. Keep it installed if you need its data; pair the new GraviGo installation separately. Changing an app ID or signing certificate cannot update an existing Android installation in place.

## Start the development environment

Open Antigravity and the Android emulator, then run from the repository root:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\android\scripts\start-dev.ps1
```

This entry point checks or starts the emulator gateway on port `58901`, builds and installs the debug APK while preserving app data, and reads the package and launcher activity from that APK. It avoids mixing old IDE package metadata with a renamed activity. A healthy existing gateway is reused; when the port is free, the script builds the current gateway source. Existing development pairing is retained. For a first pairing, use `http://10.0.2.2:58901` and request a pairing code.

To check or start only the gateway, run `android/scripts/start-emulator-gateway.ps1`. It preserves the existing management gateway's data directory; a fresh clone uses ignored project-local `.local/emulator-gateway/` storage. It does not install a startup service or reset pairing.

## Fix an Android Studio launch error

If Run reports `Activity class {com.antigravity.mobile/io.github.sin6626.gravigo.MainActivity} does not exist`, Android Studio is using the old application ID with the renamed activity. Run **Sync Project with Gradle Files**, select the app's **debug** build variant, and run the **app** configuration with **Launch: Default Activity**. The installed development package is `io.github.sin6626.gravigo.debug`; its launcher activity is `io.github.sin6626.gravigo.MainActivity`.

Do not uninstall the earlier app or clear its storage to fix this error. After the sync, Android Studio must launch the new package. The production package `io.github.sin6626.gravigo` belongs to a separately signed release build.

## Connect your phone

1. Open Antigravity on your Windows computer.
2. Start the built gateway in a terminal. Keep this terminal running:

   ```powershell
   $env:CF_TUNNEL_ENABLED = '0'
   .\bin\gravigo-gateway.exe run -port 58900
   ```

3. In another terminal, run `.\bin\gravigo-gateway.exe pair -port 58900`. It requests a fresh, short-lived pairing link and QR code from the running gateway. The release executable is named `gravigo-gateway.exe`; older installations named `mgy` keep their existing configuration keys and pairing schemes.
4. Install and open the Android APK from Releases or the debug APK built above, then scan the QR code or enter the pairing information. Use a reachable computer address: on the Android emulator, `10.0.2.2` refers to the host computer; `127.0.0.1` refers to the Android device itself.

Both devices must be able to reach the selected address. Allow the gateway through Windows Firewall only on a trusted private network. For a custom port, use the same port in `run`, `pair`, `list`, `clear`, and the app.

Closing the gateway or restarting the computer stops phone access until you start that gateway again. See the [gateway guide](android/docs/GATEWAY-README.md) for the extracted ZIP instructions.

Keep the gateway on a trusted network, or use a private network such as Tailscale with HTTPS where available. Pairing links and device tokens grant access to your desktop sessions; do not publish them. See [`env.example`](env.example) for the existing configuration keys. The `MULTIGRAVITY_*` keys and legacy deep-link schemes remain supported for compatibility.

The verified 1.0.0 connection baseline is local networking. Automatic Cloudflare tunneling is disabled unless explicitly enabled with `CF_TUNNEL_ENABLED=1`. The upstream domain returned 1010/403 in this environment; public routing and remote push delivery are not claimed as validated features. Physical-device acceptance is deferred at the owner's request.

## Checks and release preparation

CI runs Android unit tests, lint, debug builds, and instrumentation-test compilation, plus Go formatting, vet, tests, and builds on Windows and Linux. It does not run emulator UI tests on every pull request. Android reports are retained for seven days.

The separate **Draft release** workflow is manual and runs only on `main`. It rechecks the selected commit, requires a real signing key, verifies the certificate, and creates a draft with checksums. It does not publish a public release. See the [release guide](android/docs/releasing.md) for signing inputs and the remaining release checks.

Translations live in [`android/localization.json`](android/localization.json). After editing them, run:

```powershell
python android/scripts/localize_strings.py
python android/scripts/localize_strings.py --check
```

Lint remains blocking on errors. A direct source check rejects bidirectional control characters because lint 32.3.1's `BidiSpoofing` detector hangs while walking this app's Kotlin comments. Gradle runs `android/scripts/check_bidi.py` before lint analysis; the remaining lint checks run normally.

## Credits and licensing

GraviGo is licensed under the GNU Affero General Public License, version 3 (AGPL-3.0-only). Copyright (c) 2026 sin6626 and GraviGo contributors. See [LICENSE](LICENSE), [NOTICE.md](NOTICE.md), and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

The original antigravity-mobile code retains its MIT notice. RikkaHub highlighting is AGPL-3.0, with highlight.js material under BSD-3-Clause. Other third-party components retain their individual licenses and notices.

Release assets must identify their exact source commit. The manual draft workflow includes a source archive generated from that commit, alongside the APK, gateway ZIP, and checksums. The repository includes the source and build instructions; local signing keys, passwords, pairing credentials, and build caches are kept outside tracked source.
