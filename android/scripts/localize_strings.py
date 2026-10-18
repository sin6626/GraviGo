"""Inventory Kotlin UI messages and generate checked Android translation resources."""
from __future__ import annotations

import argparse
import hashlib
import json
import re
from pathlib import Path
from xml.sax.saxutils import escape

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "android/app/src/main/java"
CATALOG = ROOT / "android/localization.json"
HAN = re.compile(r"[\u3400-\u9fff]")


def comment_end(code: str, start: int) -> int:
    if code.startswith("//", start):
        pos = code.find("\n", start)
        return len(code) if pos < 0 else pos
    depth, pos = 1, start + 2
    while pos < len(code) and depth:
        if code.startswith("/*", pos):
            depth += 1
            pos += 2
        elif code.startswith("*/", pos):
            depth -= 1
            pos += 2
        else:
            pos += 1
    return pos


def quoted_end(code: str, start: int) -> int:
    quote = code[start]
    pos = start + 1
    while pos < len(code):
        if code[pos] == "\\":
            pos += 2
        elif code[pos] == quote:
            return pos + 1
        else:
            pos += 1
    raise ValueError("Unterminated quoted literal")


def expression_end(code: str, start: int) -> int:
    depth, pos = 1, start
    while pos < len(code):
        if code.startswith(("//", "/*"), pos):
            pos = comment_end(code, pos)
        elif code.startswith('"""', pos):
            end = code.find('"""', pos + 3)
            if end < 0:
                raise ValueError("Unterminated raw string")
            pos = end + 3
        elif code[pos] in "\"'":
            if code[pos] == '"':
                pos, _ = string_parts(code, pos)
            else:
                pos = quoted_end(code, pos)
        elif code[pos] == "{":
            depth += 1
            pos += 1
        elif code[pos] == "}":
            depth -= 1
            if depth == 0:
                return pos
            pos += 1
        else:
            pos += 1
    raise ValueError("Unterminated interpolation")


def string_parts(code: str, start: int) -> tuple[int, list[tuple[str, str]]]:
    raw = code.startswith('"""', start)
    quote = '"""' if raw else '"'
    pos, text_start, parts = start + len(quote), start + len(quote), []
    while pos < len(code):
        if not raw and code[pos] == "\\":
            pos += 2
        elif code.startswith(quote, pos):
            parts.append(("text", code[text_start:pos]))
            return pos + len(quote), parts
        elif code.startswith("${", pos):
            parts.append(("text", code[text_start:pos]))
            end = expression_end(code, pos + 2)
            parts.append(("expr", code[pos + 2:end]))
            pos = text_start = end + 1
        elif code[pos] == "$" and (match := re.match(r"[A-Za-z_][A-Za-z_0-9]*", code[pos + 1:])):
            parts.append(("text", code[text_start:pos]))
            parts.append(("expr", match.group()))
            pos = text_start = pos + len(match.group()) + 1
        else:
            pos += 1
    raise ValueError("Unterminated string")


def decode(text: str, raw: bool = False) -> str:
    if raw:
        return text
    def unescape(match: re.Match) -> str:
        value = match.group()[1:]
        if value.startswith("u"):
            return chr(int(value[1:], 16))
        return {"n": "\n", "r": "\r", "t": "\t", "b": "\b", "\\": "\\", '"': '"', "'": "'", "$": "$"}[value]
    return re.sub(r"\\(?:u[0-9a-fA-F]{4}|[nrtb\\\"'$])", unescape, text)


def strings(code: str):
    pos = 0
    while pos < len(code):
        if code.startswith(("//", "/*"), pos):
            pos = comment_end(code, pos)
        elif code[pos] == "'":
            pos = quoted_end(code, pos)
        elif code[pos] == '"':
            end, parts = string_parts(code, pos)
            raw = code.startswith('"""', pos)
            arg = 0
            template = ""
            for kind, value in parts:
                if kind == "text":
                    template += decode(value, raw).replace("%", "%%")
                else:
                    arg += 1
                    template += f"%{arg}$s"
            if HAN.search(template):
                if not arg:
                    template = template.replace("%%", "%")
                yield pos, end, template, parts, raw
            # Interpolated expressions may have their own localizable literals.
            for kind, value in parts:
                if kind == "expr":
                    offset = code.find(value, pos + (3 if raw else 1), end)
                    for a, b, text, nested_parts, nested_raw in strings(value):
                        yield offset + a, offset + b, text, nested_parts, nested_raw
            pos = end
        else:
            pos += 1


def key(text: str) -> str:
    return "msg_" + hashlib.sha256(text.encode()).hexdigest()[:12]


def inventory() -> list[dict]:
    result = []
    for path in sorted(SOURCE.rglob("*.kt")):
        if path.name == "TestMessages.kt":
            continue
        code = path.read_text(encoding="utf-8")
        for start, end, text, parts, raw in strings(code):
            line = code.count("\n", 0, start) + 1
            context = code[code.rfind("\n", 0, start) + 1:code.find("\n", end) if "\n" in code[end:] else len(code)].strip()
            result.append({"key": key(text), "source": text, "file": path.relative_to(ROOT).as_posix(), "line": line,
                           "context": context, "args": [v for k, v in parts if k == "expr"], "raw": raw})
    return result


def android_text(value: str) -> str:
    return '"' + escape(value.replace("\\", "\\\\").replace("\n", r"\n").replace("\t", r"\t")
                        .replace('"', r'\"').replace("'", r"\'")) + '"'


def generated_files(catalog: dict) -> dict[Path, str]:
    import xml.etree.ElementTree as ET
    messages = catalog["messages"]
    output = {}
    for language, directory in (("en", "values"), ("zh", "values-zh-rCN")):
        lines = ['<?xml version="1.0" encoding="utf-8"?>', '<!-- Generated by scripts/localize_strings.py. Edit localization.json. -->', '<resources>']
        for name, translations in sorted(messages.items()):
            english, chinese = translations["en"], translations["zh"]
            assert not HAN.search(english), f"Untranslated English: {name}"
            assert sorted(re.findall(r"%\d+\$[sd]", english)) == sorted(re.findall(r"%\d+\$[sd]", chinese)), name
            text = translations[language]
            formatted = ' formatted="false"' if '%' in text and not re.search(r"%\d+\$[sd]", text) else ''
            lines.append(f'    <string name="{name}"{formatted}>{android_text(text)}</string>')
        lines.append('</resources>')
        content = '\n'.join(lines) + '\n'
        ET.fromstring(content)
        output[ROOT / f"android/app/src/main/res/{directory}/strings.xml"] = content
    # Unit tests have no Android application. Keep their default Chinese messages deterministic.
    entries = '\n'.join(f'    R.string.{name} to ' + json.dumps(t['zh'], ensure_ascii=False).replace('$', r'\$') + ',' for name, t in sorted(messages.items()))
    output[SOURCE / 'io/github/sin6626/gravigo/i18n/TestMessages.kt'] = '''// Generated by scripts/localize_strings.py. Edit localization.json.
package io.github.sin6626.gravigo.i18n

import io.github.sin6626.gravigo.R

internal val testMessages = mapOf(
''' + entries.replace('\\u', '\\u') + '\n)\n'
    return output


def generate(check: bool):
    catalog = json.loads(CATALOG.read_text(encoding='utf-8'))
    for path, expected in generated_files(catalog).items():
        if check:
            assert path.read_text(encoding='utf-8') == expected, f"Run python android/scripts/localize_strings.py: {path}"
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(expected, encoding='utf-8', newline='\n')
    print(f'{len(catalog["messages"])} English/Chinese resources checked')


def self_test():
    sample = '// "注释"\nval a = "文件 ${if (n == 1) "一" else "多"}：$name"\nval b = "价格\\$5"'
    found = list(strings(sample))
    assert [x[2] for x in found] == ["文件 %1$s：%2$s", "一", "多", "价格$5"]
    assert found[0][3][1] == ("expr", 'if (n == 1) "一" else "多"')
    assert decode(r"a\n\"b\"") == 'a\n"b"'
    assert list(strings('/* nested /* "中" */ */ val s="English"')) == []
    assert list(strings('val s="""原文 $name"""'))[0][2] == "原文 %1$s"
    assert key("文件") == key("文件")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--inventory", type=Path)
    parser.add_argument("--self-test", action="store_true")
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    self_test()
    if args.inventory:
        items = inventory()
        args.inventory.parent.mkdir(parents=True, exist_ok=True)
        args.inventory.write_text(json.dumps(items, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        print(f"{len(items)} literals; {len({x['source'] for x in items})} unique templates")
    if args.self_test:
        print("Kotlin literal inventory self-checks passed")
    if not args.inventory:
        generate(args.check)


if __name__ == "__main__":
    main()
