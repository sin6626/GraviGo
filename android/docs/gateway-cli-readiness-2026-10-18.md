# Gateway CLI readiness — 2026-10-18

The original inspection below records the initial findings. The owner subsequently selected AGPL v3 and authorized the fixes described in the follow-up section. Publishing remains paused: no release, tag, signing-secret configuration, or deployment replacement is authorized by these fixes.

## Implementation follow-up

- `clear -port PORT <device-id>` now selects the requested gateway, returns a nonzero exit status on failure, and never changes offline credentials after an HTTP rejection. Offline persistence errors are reported.
- Pairing links preserve a separate `lan_port` for custom-port gateways behind HTTPS tunnels. Terminal output and Android use that port; older links remain compatible.
- Product-facing CLI instructions use GraviGo. Existing configuration keys, data paths, and pairing schemes remain compatible.
- The Windows ZIP packager includes quick-start instructions, the complete project license, retained notices, Go dependency/runtime licenses, and exact source identity. It rejects a dirty worktree or a binary from another commit, then verifies every archived byte. The manual release workflow also archives the matching source and checksums.
- The project now uses AGPL-3.0-only, retaining the original MIT and component notices. Android includes license texts and a bilingual license entry; the gateway Web/PWA settings offer the public source link.
- Local Go tests and vet, Android module tests/lint/builds, and the API 37 language and large-font settings tests passed. An isolated real gateway passed health/readiness, pairing, replay rejection, device listing, authenticated workspace reads, selected-port revocation, and missing-device failure checks. Existing gateways and app pairing data were preserved.

Clean-commit packaging and remote CI are final verification steps; the sections below remain historical evidence from the initial inspection.

## Candidate verified

- Source commit: `90917b2abef2e53b14e59645aaf32f8acdf63f7d`.
- Version command: `GraviGo Gateway (mgy) 1.0.0`.
- Build: Go 1.26.9, Windows amd64, `CGO_ENABLED=0`, `-trimpath`, `-ldflags="-s -w -X main.Version=1.0.0"`.
- Candidate SHA-256: `6f545985d2590d9941f884505c4a30eb9ac7b7646889939105e4cccdaf28b9cb`.
- The deployed legacy `bin/mgy.exe` still reports `Multigravity (mgy) 1.0.3`. It must not be copied into a 1.0.0 release as if it were the rebuilt candidate.

The candidate and verification artifacts are local, ignored files under `.verification/2026-10-18-gateway-readiness/`. The existing gateways on ports 58900 and 58901 remained running. Test gateway processes exited and their temporary listening ports closed.

## Checks completed

| Check | Result |
| --- | --- |
| `go vet ./...` | Passed |
| `go test -count=1 -timeout=10m ./...` | Passed on this Windows host |
| Version and help commands | Passed |
| Start candidate on an isolated loopback port | Passed; `/healthz` returned 200 |
| Discover the running Antigravity desktop | Passed; `/readyz` returned 200 |
| Deny workspace reads without authentication | Passed |
| `pair` prints a code and terminal QR output | Passed |
| Register an isolated test device with that code | Passed |
| Reject reuse of the one-time pairing code | Passed; returned 401 |
| `list` shows the isolated test device | Passed |
| Authenticated workspace read | Passed; returned 200 |
| `clear <device-id>` with the correct environment-selected port | Passed; the revoked device lost access |

The probe used a fresh auth store and generated test credentials. It did not pair the user's real app again, send conversation messages, switch desktop accounts, start a public tunnel, or enable notifications.

## Issues to resolve before calling the CLI release-ready

1. **Inconsistent port selection for `clear`.** `pair` and `list` parse `-port`; `runClearCmd` treats its first argument as a device ID and reads only `MULTIGRAVITY_PORT`. For example, `clear -port 58901 <id>` does not select port 58901. See `cmd/gateway/main.go`, `runClearCmd`.
2. **Failure can report a successful exit status.** An isolated call to `clear readiness-probe-missing-device` printed a failure message but exited with code 0. Scripts cannot use the exit code to tell whether revocation succeeded. See the offline fallback in `runClearCmd`.
3. **Custom-port LAN pairing links.** `FormatPairingQRCode` and `PrintRawPairingQRCode` build their displayed LAN URI with port 58900. This is correct for the default local port but can point at the wrong gateway when a custom local port is selected. The cloud-to-LAN case must retain its separate local port handling when this is fixed. This finding is from source inspection; the runtime probe's loopback banner did not include a LAN URI.
4. **Legacy branding remains in help and pairing/device output.** Version output uses GraviGo, while pairing banners, device lists, command examples, and some descriptions still say Multigravity or `mgy`. Keep compatibility keys and URI schemes; update the product-facing instructions deliberately.
5. **The ZIP needs usable instructions and complete notices.** The current draft workflow packages an executable and license overview files. It does not include a gateway quick-start README or complete dependency license texts. The final archive should explain how to start and keep the gateway running, run `pair` from a second terminal, select an address the phone can reach, and stop the process. Retain applicable upstream and dependency notices.

## Limits

This inspection verified Windows x64 against the currently running Antigravity installation. It did not validate macOS, Linux desktop discovery, Windows ARM, a clean Windows installation, public Cloudflare routing, voice-account permissions, notification delivery, or Authenticode signing. Cross-platform Go CI from the previous implementation is separate evidence, not a substitute for those runtime checks.

The RikkaHub AGPL component is in the Android highlighting module. The Go gateway is built from `cmd/gateway`, `internal`, and embedded `web` assets; Android module licensing and gateway dependency notices must be assessed for their actual scope. The owner subsequently chose AGPL v3; see the implementation follow-up above.
