# Preparing a GraviGo release

The release workflow creates a **draft**, not a public release. No production signing key is included in this repository. Version 1.0.0 uses Android version code 5.

## Before the first binary distribution

Resolve the licensing of GraviGo additions and review the RikkaHub AGPL-3.0 component, the combined app, and the corresponding source to distribute. Keep the existing attribution and full license texts. See [LICENSE](../../LICENSE) and [third-party notices](../../THIRD_PARTY_NOTICES.md). Publishing source does not, by itself, select a license for previously unlicensed additions.

The production ID is `io.github.sin6626.gravigo`. Debug builds use `io.github.sin6626.gravigo.debug`. The old `com.antigravity.mobile` app cannot be upgraded in place to either ID. Keep the old installation to preserve its private storage; pair the new installation separately. Do not uninstall the old app to solve a package conflict without first considering its data.

## Configure signing

Create and back up a production Android signing key in a location you control. Do not paste its passwords into issues, chat, source files, or workflow inputs. Keep an offline backup: future Android updates require the same signing identity.

For a local release build, provide these environment variables in the shell that runs Gradle:

| Variable | Value |
| --- | --- |
| `ANDROID_KEYSTORE_PATH` | Absolute path to the keystore file |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore password |
| `ANDROID_KEY_ALIAS` | Signing-key alias |
| `ANDROID_KEY_PASSWORD` | Key password |

Run `.\android\gradlew.bat -p android :app:assembleRelease`. Missing inputs or a missing keystore fail the build. There is no debug-signing fallback. Signing files and local key-property files are ignored by Git; verify this before adding any new filename.

For GitHub Actions, create a repository environment named **release**, restrict it to `main`, and configure an approval reviewer if your repository plan supports it. Store:

| Environment secret | Value |
| --- | --- |
| `ANDROID_KEYSTORE_BASE64` | Base64 encoding of the keystore file |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore password |
| `ANDROID_KEY_ALIAS` | Signing-key alias |
| `ANDROID_KEY_PASSWORD` | Key password |

Set the environment variable `ANDROID_SIGNING_CERT_SHA256` to the expected production certificate SHA-256 fingerprint. Obtain it with `keytool -list -v -keystore <path> -alias <alias>` and compare it with your backed-up key. The fingerprint is not a password. Base64 is an encoding, not encryption; treat the encoded keystore as secret.

## Run the workflow

1. Ensure the commit is on `main`, its CI checks pass, and the version is consistent in the app, gateway, and workflow.
2. In GitHub Actions, choose **Draft release**, select `main`, and run it. It invokes CI again for that same commit before accessing signing secrets.
3. Verify that the draft points to the intended commit. The workflow refuses an existing `v1.0.0` tag and does not replace a published release. If an earlier draft exists, inspect its commit and assets before removing or retrying it.
4. Download the APK, Windows x64 gateway archive, and `SHA256SUMS.txt` from the draft. Verify the checksums and the APK's certificate with Android `apksigner verify --verbose --print-certs`.
5. Install on a test device, start the gateway against a running Antigravity desktop, pair, and verify conversation reading, message sending, attachments, plan comments, diff comments, and both interface languages. Confirm drafts and attachments survive language switching and app restart. Use a test conversation for write operations.
6. Review the source and license materials for this exact commit. Publish the draft only after the release owner explicitly approves public distribution.

The workflow produces `GraviGo-1.0.0.apk`, `GraviGo-Gateway-1.0.0-windows-amd64.zip`, and `SHA256SUMS.txt`. The gateway is not Authenticode-signed. No Google Play upload is configured.

## CI scope

CI runs Android unit tests in all modules, lint, a debug APK build, and instrumentation-test compilation. It also checks generated translations and runs Go formatting, vet, tests, and builds on Windows and Linux. Emulator UI tests remain a local release check.

Protect `main` with the `android` and both `go` matrix checks after the first successful Actions run. Branch-protection changes require repository administration access and are separate from committing workflow files. Keep action references pinned to full commits when updating them.
