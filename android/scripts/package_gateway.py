"""Package an existing Windows x64 gateway with source identity and full notices."""
import argparse
import hashlib
import json
from pathlib import Path
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[2]


def command(*args):
    return subprocess.check_output(args, cwd=ROOT, text=True, encoding="utf-8").strip()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--binary", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)
    args = parser.parse_args()
    binary = args.binary.resolve()
    if not binary.is_file():
        parser.error("gateway binary does not exist")
    metadata = command("go", "version", "-m", str(binary))
    for expected in ("GOOS=windows", "GOARCH=amd64", "CGO_ENABLED=0"):
        if expected not in metadata:
            parser.error("unexpected gateway build: missing " + expected)
    if command("git", "status", "--porcelain", "--untracked-files=all"):
        parser.error("commit source changes before packaging a release candidate")
    revision = command("git", "rev-parse", "HEAD")
    if "vcs.revision=" + revision not in metadata or "vcs.modified=false" not in metadata:
        parser.error("binary does not match the clean source commit")
    files = {
        "gravigo-gateway.exe": binary,
        "README.md": ROOT / "android/docs/GATEWAY-README.md",
        "LICENSE": ROOT / "LICENSE",
        "NOTICE.md": ROOT / "NOTICE.md",
        "THIRD_PARTY_NOTICES.md": ROOT / "THIRD_PARTY_NOTICES.md",
        "licenses/antigravity-mobile-MIT.txt": ROOT / "android/docs/LICENSE.upstream-MIT",
        "licenses/Go-LICENSE.txt": Path(command("go", "env", "GOROOT")) / "LICENSE",
    }
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
    with zipfile.ZipFile(args.output, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        for name, data in sorted(contents.items()):
            archive.writestr(name, data)
    with zipfile.ZipFile(args.output) as archive:
        assert set(archive.namelist()) == set(contents)
        assert archive.testzip() is None
        for name, data in contents.items():
            assert archive.read(name) == data, name
    print(json.dumps({"archive": str(args.output), "files": len(contents), "sourceCommit": revision,
                      "sha256": hashlib.sha256(args.output.read_bytes()).hexdigest()}))


if __name__ == "__main__":
    main()
