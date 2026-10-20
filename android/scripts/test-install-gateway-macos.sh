#!/bin/bash
set -euo pipefail
scripts=$(cd "$(dirname "$0")" && pwd -P)
mkdir -p "$scripts/../../.verification"
root=$(mktemp -d "$scripts/../../.verification/macos-installer-test.XXXXXX")
export GRAVIGO_TEST_ROOT="$root"
mkdir "$root/fixture"
for name in gravigo-gateway README.md LICENSE NOTICE.md THIRD_PARTY_NOTICES.md SOURCE.txt; do
    printf 'fixture: %s\n' "$name" > "$root/fixture/$name"
done
chmod 755 "$root/fixture/gravigo-gateway"
mkdir "$root/fixture/licenses"
echo license > "$root/fixture/licenses/test.txt"
tar -czf "$root/fixture.tar.gz" -C "$root/fixture" gravigo-gateway README.md LICENSE NOTICE.md THIRD_PARTY_NOTICES.md SOURCE.txt licenses/
export GRAVIGO_TEST_ARCH=arm64 GRAVIGO_TEST_TRANSLATED=0 GRAVIGO_TEST_MODE=valid
uname() { case "$1" in -s) echo Darwin ;; -m) echo "$GRAVIGO_TEST_ARCH" ;; esac; }
sysctl() { echo "$GRAVIGO_TEST_TRANSLATED"; }
curl() {
    local output='' url='' arg
    while [ "$#" -gt 0 ]; do
        arg=$1; shift
        case "$arg" in
            --output) output=$1; shift ;;
            --write-out) shift ;;
            https:*) url=$arg ;;
        esac
    done
    if [[ "$url" = */releases/latest ]]; then echo 'https://github.com/sin6626/GraviGo/releases/tag/v1.0.0'; return; fi
    if [[ "$url" = */SHA256SUMS-macos.txt ]]; then
        if [ "$GRAVIGO_TEST_MODE" = missing ]; then echo '' > "$output"; return; fi
        local hash
        hash=$(shasum -a 256 "$GRAVIGO_TEST_ROOT/fixture.tar.gz" | awk '{print $1}')
        if [ "$GRAVIGO_TEST_MODE" = corrupt ]; then hash=$(printf '%064d' 0); fi
        printf '%s  GraviGo-Gateway-1.0.0-darwin-arm64.tar.gz\n%s  GraviGo-Gateway-1.0.0-darwin-amd64.tar.gz\n' "$hash" "$hash" > "$output"
    else
        cp "$GRAVIGO_TEST_ROOT/fixture.tar.gz" "$output"
    fi
}
export -f uname sysctl curl
install() { GRAVIGO_INSTALL_DIR="$1" bash "$scripts/install-gateway-macos.sh"; }
expect_failure() {
    if install "$1" > "$root/failure.log" 2>&1; then echo 'Expected installer failure.' >&2; exit 1; fi
    grep -Fq "$2" "$root/failure.log" || { cat "$root/failure.log"; exit 1; }
}
install "$root/install with spaces"
exe="$root/install with spaces/1.0.0-arm64/gravigo-gateway"
[ -x "$exe" ]
install "$root/install with spaces" > "$root/repeat.log"
grep -Fq 'no files replaced' "$root/repeat.log"
export GRAVIGO_TEST_ARCH=x86_64
install "$root/intel"
[ -x "$root/intel/1.0.0-amd64/gravigo-gateway" ]
export GRAVIGO_TEST_TRANSLATED=1
install "$root/rosetta"
[ -x "$root/rosetta/1.0.0-arm64/gravigo-gateway" ]
export GRAVIGO_TEST_TRANSLATED=0 GRAVIGO_TEST_ARCH=arm64 GRAVIGO_TEST_MODE=corrupt
expect_failure "$root/corrupt" 'SHA-256 mismatch'
[ ! -e "$root/corrupt/1.0.0-arm64" ]
export GRAVIGO_TEST_MODE=missing
expect_failure "$root/missing" 'Missing or ambiguous'
export GRAVIGO_TEST_MODE=valid
echo 'user data' > "$exe"
expect_failure "$root/install with spaces" 'Existing installation differs'
grep -Fxq 'user data' "$exe"
ln -s /tmp "$root/fixture/escape"
tar -czf "$root/fixture.tar.gz" -C "$root/fixture" gravigo-gateway README.md LICENSE NOTICE.md THIRD_PARTY_NOTICES.md SOURCE.txt licenses/ escape
expect_failure "$root/symlink" 'Unsupported archive entry type'
[ ! -e "$root/symlink/1.0.0-arm64" ]
if find "$root" -name '.download-*' | grep -q .; then echo 'Downloads were not cleaned up.' >&2; exit 1; fi
echo 'PASS: Intel, Apple Silicon, Rosetta, repeat, checksum rejection, missing checksum, overwrite protection, symlink rejection, cleanup.'
