#!/bin/bash
# Compatible with the Bash 3.2 shipped with macOS; no Homebrew or Python required.
gravigo_install() (
    set -euo pipefail
    [ "$(uname -s)" = Darwin ] || { echo 'This installer supports macOS only.' >&2; exit 1; }
    arch=$(uname -m)
    if [ "$(sysctl -in sysctl.proc_translated 2>/dev/null || true)" = 1 ]; then arch=arm64; fi
    case "$arch" in
        arm64) target=arm64 ;;
        x86_64) target=amd64 ;;
        *) echo "Unsupported Mac architecture: $arch" >&2; exit 1 ;;
    esac
    release_url=$(curl --fail --silent --show-error --location --head --output /dev/null --write-out '%{url_effective}' https://github.com/sin6626/GraviGo/releases/latest)
    if [[ ! "$release_url" =~ ^https://github\.com/sin6626/GraviGo/releases/tag/v([0-9]+\.[0-9]+\.[0-9]+)$ ]]; then
        echo 'Could not resolve a supported stable release.' >&2; exit 1
    fi
    version=${BASH_REMATCH[1]}
    asset="GraviGo-Gateway-$version-darwin-$target.tar.gz"
    base="https://github.com/sin6626/GraviGo/releases/download/v$version"
    root=${GRAVIGO_INSTALL_DIR:-"$HOME/Library/Application Support/GraviGo/Gateway"}
    mkdir -p "$root"
    root=$(cd "$root" && pwd -P)
    destination="$root/$version-$target"
    archive=$(mktemp "$root/.download-archive.XXXXXX")
    sums=$(mktemp "$root/.download-checksums.XXXXXX")
    trap 'rm -f -- "$archive"; rm -f -- "$sums"' EXIT
    curl --fail --silent --show-error --location "$base/SHA256SUMS-macos.txt" --output "$sums"
    expected=$(awk -v file="$asset" '$2 == file {print $1}' "$sums")
    if [[ ! "$expected" =~ ^[0-9a-fA-F]{64}$ ]]; then
        echo 'Missing or ambiguous macOS gateway SHA-256 checksum.' >&2; exit 1
    fi
    echo "Downloading GraviGo Gateway $version for $arch..."
    curl --fail --silent --show-error --location "$base/$asset" --output "$archive"
    actual=$(shasum -a 256 "$archive" | awk '{print $1}')
    [ "$actual" = "$expected" ] || { echo 'Gateway SHA-256 mismatch. Nothing was installed.' >&2; exit 1; }

    entries=$(tar -tzf "$archive")
    while IFS= read -r entry; do
        case "$entry" in
            ''|/*|..|../*|*/../*|*/..|*\\*) echo "Unsafe archive entry: $entry" >&2; exit 1 ;;
        esac
    done <<< "$entries"
    # Only regular files and directories may be extracted, never links or devices.
    if ! tar -tvzf "$archive" | awk 'substr($0,1,1) !~ /[-d]/ {bad=1} END {exit bad}'; then
        echo 'Unsupported archive entry type.' >&2; exit 1
    fi
    for required in gravigo-gateway README.md LICENSE NOTICE.md THIRD_PARTY_NOTICES.md SOURCE.txt; do
        grep -Fxq "$required" <<< "$entries" || { echo "Incomplete gateway archive: $required" >&2; exit 1; }
    done
    grep -q '^licenses/' <<< "$entries" || { echo 'Missing dependency licenses.' >&2; exit 1; }
    if [ -e "$destination" ]; then
        while IFS= read -r entry; do
            case "$entry" in */) continue ;; esac
            [ -f "$destination/$entry" ] || { echo "Incomplete installation; choose another GRAVIGO_INSTALL_DIR: $destination" >&2; exit 1; }
            expected_file=$(tar -xzOf "$archive" "$entry" | shasum -a 256 | awk '{print $1}')
            actual_file=$(shasum -a 256 "$destination/$entry" | awk '{print $1}')
            [ "$expected_file" = "$actual_file" ] || { echo "Existing installation differs; choose another GRAVIGO_INSTALL_DIR: $destination" >&2; exit 1; }
        done <<< "$entries"
        [ -x "$destination/gravigo-gateway" ] || { echo 'Existing gateway is not executable; choose another GRAVIGO_INSTALL_DIR.' >&2; exit 1; }
        echo 'Existing installation verified; no files replaced.'
    else
        mkdir "$destination"
        tar -xzf "$archive" -C "$destination"
        chmod 755 "$destination/gravigo-gateway"
    fi
    echo "Installed GraviGo Gateway $version in $destination"
    echo 'Open Antigravity, then run:'
    printf 'cd %q\n' "$destination"
    echo './gravigo-gateway run -port 58900'
    echo 'In another terminal in that folder: ./gravigo-gateway pair -port 58900'
    echo 'Keep the gateway terminal open while using the app. Existing pairing data is preserved.'
)
gravigo_install
