# GraviGo Gateway 1.0.0 for Windows and macOS

GraviGo is an independent Android remote client for Antigravity. This gateway lets the app control Antigravity sessions on your computer. Keep it running while you use the phone app.

## Windows installation

In PowerShell on Windows x64, run:

```powershell
irm https://raw.githubusercontent.com/sin6626/GraviGo/main/android/scripts/install-gateway.ps1 | iex
```

The installer resolves the latest stable release once, downloads its gateway ZIP, checks SHA-256, and extracts all files into `%LOCALAPPDATA%\GraviGo\Gateway\<version>`. No administrator access is required. It prints the installed directory and the startup and pairing commands. Existing versions are verified without overwriting files. Pairing data, PATH, and startup settings are preserved.

To choose another directory, download the [installer](https://raw.githubusercontent.com/sin6626/GraviGo/main/android/scripts/install-gateway.ps1), inspect it, then run `powershell -NoProfile -ExecutionPolicy Bypass -File .\install-gateway.ps1 -InstallDir 'D:\GraviGo'`. If an existing version is incomplete or has changed, the installer stops; choose a different installation directory to keep the existing files.

For a manual installation, download the complete ZIP from [Releases](https://github.com/sin6626/GraviGo/releases/latest), check `SHA256SUMS.txt`, and extract it to a folder you control. Do not run the executable from inside the archive.

## macOS installation

Run in Terminal on Apple Silicon or Intel:

```bash
curl -fsSL https://raw.githubusercontent.com/sin6626/GraviGo/main/android/scripts/install-gateway-macos.sh | bash
```

The script uses macOS's built-in Bash, curl, tar, and shasum. It selects the native architecture, including from a Rosetta terminal, resolves the latest stable release, and verifies its package against `SHA256SUMS-macos.txt`. All notices are installed under `~/Library/Application Support/GraviGo/Gateway/<version>-<architecture>`. It prints commands with the actual installation path. Existing versions are verified without overwriting files. Set `GRAVIGO_INSTALL_DIR` to choose another installation root; if existing files differ, choose a different root to preserve them.

For manual installation, choose `darwin-arm64.tar.gz` for Apple Silicon or `darwin-amd64.tar.gz` for Intel from [Releases](https://github.com/sin6626/GraviGo/releases/latest), verify with `shasum -a 256` against `SHA256SUMS-macos.txt`, and extract the complete archive. In the extracted directory:

```bash
./gravigo-gateway run -port 58900
# Keep it running; in a second terminal in the same directory:
./gravigo-gateway pair -port 58900
```

Open Antigravity before pairing. Use the computer's reachable LAN address on your phone. Existing configuration and pairing data stay in `~/.multigravity`; no `sudo`, Homebrew, startup service, or PATH change is needed. Allow incoming connections on a trusted network if macOS prompts.

Both Mac architectures run native Go tests and executable checks in CI. The complete connection flow against a real Mac Antigravity desktop is not yet accepted. Packages do not have a Developer ID signature or Apple notarization. If blocked, follow [Apple's Privacy & Security instructions](https://support.apple.com/en-us/102445). The installer does not change Gatekeeper or remove quarantine attributes.

## Connect on your local network

1. Open Antigravity on your computer.
2. Open PowerShell in the extracted folder and start the gateway:

   ```powershell
   $env:CF_TUNNEL_ENABLED = '0'
   .\gravigo-gateway.exe run -port 58900
   ```

3. Keep that terminal running. Open a second PowerShell window in the same folder:

   ```powershell
   .\gravigo-gateway.exe pair -port 58900
   ```

4. Open GraviGo on your phone and scan the QR code, or enter the pairing code and the displayed computer address. Pairing codes expire after five minutes and can be used only once. Both devices must be able to reach the selected address. Allow the gateway through Windows Firewall on your trusted private network when Windows prompts.

The Android emulator uses `10.0.2.2:58900` to reach the host computer. A physical phone uses the computer's reachable LAN address, not `127.0.0.1`. For a custom port, use that same port in `run`, `pair`, `list`, and `clear`.

## Manage the gateway

```powershell
.\gravigo-gateway.exe version
.\gravigo-gateway.exe help
.\gravigo-gateway.exe list -port 58900
.\gravigo-gateway.exe clear -port 58900 <device-id>
```

Place flags before the device ID. `clear -port 58900 all` revokes every paired device for that gateway; use it only when you intend to pair them again. A failed `clear` command returns a nonzero exit status. If the selected local gateway cannot be connected to, `clear` can update its local credential store offline; a running gateway's HTTP errors never trigger that fallback.

To stop the gateway, press **Ctrl+C** in its running terminal. Closing it or restarting Windows stops phone access until you start it again. The archive does not install a Windows service or configure automatic startup.

## Existing configurations and remote access

The default configuration and pairing files remain under `%USERPROFILE%\.multigravity`. Existing `MULTIGRAVITY_*` environment keys and `agy://` / `multigravity://` pairing schemes remain compatible. The release executable is named `gravigo-gateway.exe`; an older executable named `mgy` may still exist separately on your PATH.

GraviGo 1.0.0 defaults to local-network access. Automatic Cloudflare tunneling is disabled unless you explicitly set `CF_TUNNEL_ENABLED=1`. The upstream public dispatcher/domain returned Cloudflare 1010/403 in this release environment, and public routing is outside the verified release baseline. Configure and verify your own private network or supported HTTPS tunnel before remote use. Do not expose the plain HTTP gateway directly to an untrusted network.

## Source and licenses

GraviGo uses AGPL-3.0-only. Preserve LICENSE, NOTICE.md, THIRD_PARTY_NOTICES.md, and the complete dependency license texts in `licenses/` when redistributing this archive. `SOURCE.txt` identifies the exact source commit and its URL. The matching source archive and build instructions accompany a release at https://github.com/sin6626/GraviGo.

Packages cover Windows x64 and macOS Apple Silicon / Intel. The Windows executable is not Authenticode-signed. Keep pairing links, device tokens, and administrator credentials private. On macOS, the management commands above use `./gravigo-gateway` instead of `.\gravigo-gateway.exe`.
