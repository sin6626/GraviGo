"""Package a gateway for Windows or macOS with source identity and full notices."""
import argparse
import hashlib
import json
from pathlib import Path
import subprocess
import io
import tarfile
import zipfile

ROOT = Path(__file__).resolve().parents[2]
TOOL_ROOT = ROOT


def command(*args):
    return subprocess.check_output(args, cwd=ROOT, text=True, encoding="utf-8").strip()


def main():
    global ROOT
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--binary", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)
    parser.add_argument("--target", choices=("windows-amd64", "darwin-amd64", "darwin-arm64"), default="windows-amd64")
    parser.add_argument("--source-root", type=Path, default=ROOT,
                        help="clean source checkout used to build the binary")
    args = parser.parse_args()
    ROOT = args.source_root.resolve()
    binary = args.binary.resolve()
    if not binary.is_file():
        parser.error("gateway binary does not exist")
    metadata = command("go", "version", "-m", str(binary))
    target_os, target_arch = args.target.split("-")
    executable = "gravigo-gateway.exe" if target_os == "windows" else "gravigo-gateway"
    for expected in (f"GOOS={target_os}", f"GOARCH={target_arch}", "CGO_ENABLED=0"):
        if expected not in metadata:
            parser.error("unexpected gateway build: missing " + expected)
    if command("git", "status", "--porcelain", "--untracked-files=all"):
        parser.error("commit source changes before packaging a release candidate")
    revision = command("git", "rev-parse", "HEAD")
    if "vcs.revision=" + revision not in metadata or "vcs.modified=false" not in metadata:
        parser.error("binary does not match the clean source commit")
    files = {
        executable: binary,
        "README.md": TOOL_ROOT / "android/docs/GATEWAY-README.md",
        "LICENSE": ROOT / "LICENSE",
        "NOTICE.md": ROOT / "NOTICE.md",
        "THIRD_PARTY_NOTICES.md": ROOT / "THIRD_PARTY_NOTICES.md",
        "licenses/antigravity-mobile-MIT.txt": ROOT / "android/docs/LICENSE.upstream-MIT",
        "licenses/Go-LICENSE.txt": Path(command("go", "env", "GOROOT")) / "LICENSE",
    }
    for notice in sorted((ROOT / "web/licenses").iterdir()):
        if notice.is_file():
            files[f"licenses/web/{notice.name}"] = notice
    for module, name in (("github.com/gorilla/websocket", "gorilla-websocket"),
                         ("github.com/skip2/go-qrcode", "skip2-go-qrcode"),
                         ("github.com/pires/go-proxyproto", "pires-go-proxyproto")):
        details = json.loads(command("go", "list", "-m", "-json", module))
        directory = Path(details["Dir"])
        files[f"licenses/{name}-LICENSE.txt"] = directory / "LICENSE"
        if (directory / "NOTICE").is_file():
            files[f"licenses/{name}-NOTICE.txt"] = directory / "NOTICE"
    source = (f"GraviGo Gateway 1.0.0\nSource commit: {revision}\n"
              f"Source: https://github.com/sin6626/GraviGo/tree/{revision}\n"
              "License: AGPL-3.0-only; component notices retained.\n").encode()
    contents = {name: path.read_bytes() for name, path in files.items()}
    contents["SOURCE.txt"] = source
    args.output.parent.mkdir(parents=True, exist_ok=True)
    if target_os == "windows":
        with zipfile.ZipFile(args.output, "w", compression=zipfile.ZIP_DEFLATED) as archive:
            for name, data in sorted(contents.items()):
                archive.writestr(name, data)
        with zipfile.ZipFile(args.output) as archive:
            assert set(archive.namelist()) == set(contents)
            assert archive.testzip() is None
            for name, data in contents.items():
                assert archive.read(name) == data, name
    else:
        with tarfile.open(args.output, "w:gz") as archive:
            for name, data in sorted(contents.items()):
                entry = tarfile.TarInfo(name)
                entry.size = len(data)
                entry.mode = 0o755 if name == executable else 0o644
                archive.addfile(entry, io.BytesIO(data))
        with tarfile.open(args.output, "r:gz") as archive:
            assert set(archive.getnames()) == set(contents)
            assert archive.getmember(executable).mode == 0o755
            for name, data in contents.items():
                assert archive.extractfile(name).read() == data, name
    print(json.dumps({"archive": str(args.output), "files": len(contents), "sourceCommit": revision,
                      "sha256": hashlib.sha256(args.output.read_bytes()).hexdigest()}))


if __name__ == "__main__":
    main()
