# GraviGo

**Bring Antigravity with you.**

GraviGo is an Android companion for Google Antigravity. Connect to a gateway on your computer to follow coding sessions, send instructions, and review plans and code changes from your phone.

[![CI](https://github.com/sin6626/GraviGo/actions/workflows/ci.yml/badge.svg)](https://github.com/sin6626/GraviGo/actions/workflows/ci.yml)

Inspired by and based on [GHSaiMo's antigravity-mobile](https://github.com/GHSaiMo/antigravity-mobile). GraviGo develops the Android experience and desktop gateway further. This is an independent community project, unaffiliated with Google.

## What you can do

- Browse desktop workspaces and conversations, follow streaming replies, and send messages with attachments.
- Reference workspace files and folders, and choose commands provided by Antigravity.
- Read Markdown, code, math, diagrams, and linked files with native Android views.
- Review plans and code changes, and submit comments tied to the selected text or lines.
- View the current desktop account and model quota. Account information comes from the paired desktop; GraviGo does not sign in to Google separately.
- Switch the app interface between Simplified Chinese and English in **Account & settings → Language**. Conversation content and server-provided names remain in their original language.

Voice transcription uses the desktop account's capabilities. Availability depends on its permissions; some accounts return an insufficient-scope error.

## Project status

The app and gateway are version **1.0.0**; the Android version code is **5**. The first public release is being prepared. A version number in the source does not mean a signed download has been published.

Windows x64 is the initial desktop target. The gateway is also compiled and tested on Linux in CI; this does not establish complete desktop integration support there. The current integration was exercised with Antigravity 2.19.1 and may need changes when its desktop interfaces change.

## Build

Requirements:

- Android SDK platform 37 (SDK package `platforms;android-37.0`), Build Tools 36.0.0, and JDK 25 to run the checked-in Gradle 9.7.1 wrapper. App bytecode targets Java 17; Android 8.0/API 26 or newer is required.
- Go 1.26.9 for the gateway. Its web assets are embedded; no Node.js build is required.

Clone this repository and open `android/` in Android Studio. Set your SDK location through Android Studio or an untracked `android/local.properties` file. From the repository root:

```powershell
.\android\gradlew.bat -p android testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest
go test ./...
go build -trimpath -o bin/gravigo-gateway.exe ./cmd/gateway
```

On Linux, use `./android/gradlew -p android` instead of the Windows wrapper. The debug APK is written to `android/app/build/outputs/apk/debug/app-debug.apk` and uses `io.github.sin6626.gravigo.debug`. Production uses `io.github.sin6626.gravigo`.

The former `com.antigravity.mobile` installation is a separate app. Its pairing, drafts, and private storage are not migrated automatically. Keep it installed if you need its data; pair the new GraviGo installation separately. Changing an app ID or signing certificate cannot update an existing Android installation in place.

## Connect your phone

1. Open Antigravity on your Windows computer.
2. Start the built gateway in a terminal. Keep this terminal running:

   ```powershell
   $env:CF_TUNNEL_ENABLED = '0'
   .\bin\gravigo-gateway.exe run -port 58900
   ```

3. In another terminal, run `.\bin\gravigo-gateway.exe pair`. It requests a fresh, short-lived pairing link and QR code from the running gateway. The app also calls this command `mgy pair`, the gateway's legacy CLI name; use the executable name you built or downloaded.
4. Install and open the Android debug APK, then scan the QR code or enter the pairing information. Use a reachable computer address: on the Android emulator, `10.0.2.2` refers to the host computer; `127.0.0.1` refers to the Android device itself.

Keep the gateway on a trusted network, or use a private network such as Tailscale with HTTPS where available. Pairing links and device tokens grant access to your desktop sessions; do not publish them. See [`env.example`](env.example) for the existing configuration keys. The `MULTIGRAVITY_*` keys and legacy deep-link schemes remain supported for compatibility.

## Checks and release preparation

CI runs Android unit tests, lint, debug builds, and instrumentation-test compilation, plus Go formatting, vet, tests, and builds on Windows and Linux. It does not run emulator UI tests on every pull request. Android reports are retained for seven days.

The separate **Draft release** workflow is manual and runs only on `main`. It rechecks the selected commit, requires a real signing key, verifies the certificate, and creates a draft with checksums. It does not publish a public release. See the [release guide](android/docs/releasing.md) for signing inputs and the remaining release checks.

Translations live in [`android/localization.json`](android/localization.json). After editing them, run:

```powershell
python android/scripts/localize_strings.py
python android/scripts/localize_strings.py --check
```

## Credits and licensing

The original antigravity-mobile code is MIT licensed. The code-highlighting module contains code from RikkaHub under AGPL-3.0 and highlight.js under BSD-3-Clause; the repository therefore must not be described as entirely MIT licensed. Other bundled components retain their own licenses.

Read [LICENSE](LICENSE) and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md). A single project-wide license for GraviGo additions has not been selected, and binary distribution remains pending a review of the combined licensing and source-distribution requirements.
