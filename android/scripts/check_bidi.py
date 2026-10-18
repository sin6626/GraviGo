"""Reject bidirectional control characters in Android app source without UAST."""
from pathlib import Path
import re

SOURCE = Path(__file__).resolve().parents[1] / "app/src"
CONTROLS = set(range(0x202A, 0x202F)) | set(range(0x2066, 0x206A))


def controls(text):
    for index, character in enumerate(text):
        if ord(character) in CONTROLS:
            yield index, ord(character)
    for match in re.finditer(r"\\u([0-9a-fA-F]{4})", text):
        value = int(match[1], 16)
        if value in CONTROLS:
            yield match.start(), value


def main():
    assert list(controls("English 中文")) == []
    for value in CONTROLS:
        assert list(controls("// " + chr(value))) == [(3, value)]
        assert list(controls("\\u" + format(value, "04x"))) == [(0, value)]
    failures = []
    for path in sorted(SOURCE.rglob("*")):
        if path.suffix not in {".kt", ".java", ".kts"}:
            continue
        text = path.read_text(encoding="utf-8")
        for index, value in controls(text):
            line = text.count("\n", 0, index) + 1
            failures.append(f"{path.relative_to(SOURCE)}:{line}: bidi control U+{value:04X}")
    if failures:
        raise SystemExit("\n".join(failures))
    print("Android source bidi-control check passed")


if __name__ == "__main__":
    main()
