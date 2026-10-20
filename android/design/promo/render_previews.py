"""Render three native-UI product concepts into 1080x1920, 30fps MP4 previews."""
import argparse
from array import array
from functools import partial
import hashlib
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
import json
import math
import os
from pathlib import Path
import re
import shutil
import subprocess
import threading
import wave

ROOT = Path(__file__).resolve().parents[3]
SOURCE = Path(__file__).resolve().parent
FPS, DURATION = 30, 15
VARIANTS = {"A": ("A-hybrid", "真实操作＋产品动效"), "B": ("B-hero", "发布会感产品动效"), "C": ("C-demo", "直接功能演示")}


class QuietHandler(SimpleHTTPRequestHandler):
    def log_message(self, *_):
        pass

    def send_head(self):
        self.remaining = None
        path = Path(self.translate_path(self.path))
        if path.suffix != ".mp4" or not path.is_file():
            return super().send_head()
        # Chromium needs byte ranges to seek paused video; SimpleHTTPRequestHandler omits them.
        size = path.stat().st_size
        value = self.headers.get("Range")
        start, end = 0, size - 1
        if value:
            match = re.fullmatch(r"bytes=(\d+)-(\d*)", value)
            if not match or int(match[1]) >= size:
                self.send_error(416, "Unsupported byte range")
                return None
            start = int(match[1])
            end = min(int(match[2]), end) if match[2] else end
            if end < start:
                self.send_error(416, "Invalid byte range")
                return None
        self.send_response(206 if value else 200)
        self.send_header("Content-Type", "video/mp4")
        self.send_header("Accept-Ranges", "bytes")
        self.send_header("Content-Length", str(end - start + 1))
        if value:
            self.send_header("Content-Range", f"bytes {start}-{end}/{size}")
        self.end_headers()
        source = path.open("rb")
        source.seek(start)
        self.remaining = end - start + 1
        return source

    def copyfile(self, source, target):
        if self.remaining is None:
            return super().copyfile(source, target)
        while self.remaining:
            chunk = source.read(min(65536, self.remaining))
            if not chunk:
                break
            try:
                target.write(chunk)
            except (BrokenPipeError, ConnectionResetError, ConnectionAbortedError):
                return
            self.remaining -= len(chunk)


def score(path, variant):
    """A quiet original pad/pulse score with no external samples or voice models."""
    rate = 48000
    notes = [146.832, 174.614, 220.0, 261.626]
    bpm = {"A": 88, "B": 108, "C": 72}[variant]
    samples = array("h")
    for n in range(rate * DURATION):
        t = n / rate
        attack = min(1, t / .8) * min(1, (DURATION - t) / 1.1)
        pad = sum(math.sin(math.tau * f * t + i * .3) for i, f in enumerate(notes)) / 4
        beat = (t * bpm / 60) % 1
        pulse = math.sin(math.tau * notes[int(t * bpm / 60) % 4] * 2 * t) * math.exp(-beat * 13)
        bass = math.sin(math.tau * notes[0] * .5 * t) * math.exp(-beat * 7)
        sample = attack * (.065 * pad + (.045 if variant == "B" else .025) * pulse + .025 * bass)
        samples.append(round(sample * 32767))
    assert max(abs(v) for v in samples) < 32767
    with wave.open(str(path), "wb") as audio:
        audio.setnchannels(1)
        audio.setsampwidth(2)
        audio.setframerate(rate)
        audio.writeframes(samples.tobytes())


def prepare(output):
    assets = output / "assets"
    assets.mkdir(parents=True, exist_ok=True)
    if not (assets / "native-demo.mp4").is_file():
        raise RuntimeError("Run capture_native.py before rendering")
    shutil.copyfile(SOURCE / "preview.html", output / "preview.html")
    shutil.copyfile(ROOT / "android/app/src/main/res/drawable-nodpi/multigravity_mark.png", assets / "brand.png")
    if not (assets / "NotoSansCJKsc-Regular.otf").exists():
        shutil.copyfile(ROOT / ".verification/RaTeX/scripts/fonts/NotoSansCJKsc-Regular.otf", assets / "NotoSansCJKsc-Regular.otf")
    if not (assets / "FONT-LICENSE.txt").exists():
        license_text = (ROOT / ".verification/RaTeX/licenses/SIL-OFL-1.1.txt").read_text(encoding="utf-8-sig")
        (assets / "FONT-LICENSE.txt").write_text("Noto Sans CJK SC Regular\nCopyright 2014-2021 Adobe (http://www.adobe.com/).\n\n" + license_text, encoding="utf-8")


def review(output, records):
    cards = []
    for key, entry in records.items():
        filename, title = VARIANTS[key]
        cards.append(f'<article><h2>{key} · {title}</h2><video controls playsinline preload="metadata" poster="{filename}-poster.png" src="{filename}.mp4"></video><p>15 秒 · 1080 × 1920 · 30 fps · 原创配乐</p><a href="{filename}.mp4" download>下载这一版</a></article>')
    (output / "delivery/compare.html").write_text('''<!doctype html><html lang="zh-CN"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>GraviGo · 三版对比</title><style>*{box-sizing:border-box}body{margin:0;padding:40px;background:#0b0e17;color:#edf1ff;font:16px system-ui,sans-serif}h1{font-size:32px}header p{color:#a7b3ce}main{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:24px;max-width:1400px}article{background:#151c2d;border:1px solid #2d3851;border-radius:20px;padding:20px}h2{font-size:19px}video{width:100%;max-height:72vh;background:black;border-radius:12px}p{color:#9eacc8;font-size:14px}a{color:#b0c8ff}@media(max-width:850px){main{grid-template-columns:1fr}body{padding:20px}}</style><header><h1>GraviGo · 竖版宣发风格预览</h1><p>同一组原生 App 演示内容，比较三种剪辑与呈现方式。建议逐一播放并打开声音。</p></header><main>''' + "".join(cards) + '</main><footer><p>演示会话使用隔离数据。1.0.0 已验收连接范围为局域网；电脑与网关需保持运行。这三支为风格预览。</p></footer></html>', encoding="utf-8")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--ffmpeg", required=True)
    parser.add_argument("--variant", choices=["all", *VARIANTS], default="all")
    parser.add_argument("--stills", action="store_true", help="capture review frames without encoding full videos")
    args = parser.parse_args()
    output = ROOT / ".verification/gravigo-product-promo"
    temp = output / "tmp"
    temp.mkdir(parents=True, exist_ok=True)
    (output / "delivery").mkdir(exist_ok=True)
    for variable in ("TEMP", "TMP", "TMPDIR"):
        os.environ[variable] = str(temp)
    prepare(output)
    from playwright.sync_api import sync_playwright
    server = ThreadingHTTPServer(("127.0.0.1", 0), partial(QuietHandler, directory=str(output)))
    serving = threading.Thread(target=server.serve_forever, daemon=True)
    serving.start()
    records = {}
    try:
        with sync_playwright() as browser_api:
            browser = browser_api.chromium.launch(headless=True)
            try:
                page = browser.new_page(viewport={"width": 1080, "height": 1920}, device_scale_factor=1)
                page_errors = []
                page.on("pageerror", lambda error: page_errors.append(str(error)))
                page.goto(f"http://127.0.0.1:{server.server_port}/preview.html?render=1", wait_until="networkidle")
                ready = page.evaluate("() => window.ready")
                assert ready["width"] == 1080 and ready["height"] == 1920 and ready["nativeDuration"] >= 14
                for variant in VARIANTS if args.variant == "all" else [args.variant]:
                    filename, title = VARIANTS[variant]
                    for second in (1, 5, 8, 11, 14):
                        page.evaluate("([t,v]) => window.drawFrame(t,v)", [second, variant])
                        page.locator("canvas").screenshot(path=str(temp / f"{filename}-{second:02d}.png"))
                    shutil.copyfile(temp / f"{filename}-01.png", output / "delivery" / f"{filename}-poster.png")
                    if args.stills:
                        print(f"{variant}: review frames captured", flush=True)
                        continue
                    wav = temp / f"{filename}.wav"
                    score(wav, variant)
                    target = output / "delivery" / f"{filename}.mp4"
                    command = [args.ffmpeg, "-hide_banner", "-loglevel", "warning", "-y", "-f", "image2pipe", "-framerate", str(FPS),
                        "-vcodec", "mjpeg", "-i", "pipe:0", "-i", str(wav), "-c:v", "libx264", "-preset", "fast", "-crf", "18",
                        "-pix_fmt", "yuv420p", "-c:a", "aac", "-b:a", "160k", "-t", str(DURATION), "-movflags", "+faststart", str(target)]
                    with (temp / f"{filename}-encode.log").open("wb") as log:
                        encoder = subprocess.Popen(command, stdin=subprocess.PIPE, stderr=log, stdout=subprocess.DEVNULL)
                        try:
                            for frame in range(FPS * DURATION):
                                page.evaluate("([t,v]) => window.drawFrame(t,v)", [frame / FPS, variant])
                                encoder.stdin.write(page.locator("canvas").screenshot(type="jpeg", quality=94))
                                if frame % 90 == 0:
                                    print(f"{variant}: {frame // FPS}/{DURATION}s rendered", flush=True)
                            encoder.stdin.close()
                            if encoder.wait(timeout=90):
                                raise RuntimeError("Encoding failed; inspect " + str(temp / f"{filename}-encode.log"))
                        finally:
                            if encoder.poll() is None:
                                encoder.terminate()
                                encoder.wait(timeout=10)
                    subprocess.run([args.ffmpeg, "-hide_banner", "-loglevel", "error", "-i", str(target), "-f", "null", "-"], check=True)
                    records[variant] = {"file": target.name, "title": title, "width": 1080, "height": 1920,
                        "fps": FPS, "duration": DURATION, "bytes": target.stat().st_size,
                        "sha256": hashlib.sha256(target.read_bytes()).hexdigest(), "fullDecode": "passed"}
                    print(f"{variant}: encoded and full decode passed", flush=True)
                assert not page_errors, page_errors
            finally:
                browser.close()
    finally:
        server.shutdown()
        serving.join(timeout=5)
        server.server_close()
    if not args.stills:
        manifest = output / "delivery/manifest.json"
        if manifest.exists():
            records = {**json.loads(manifest.read_text(encoding="utf-8")), **records}
        manifest.write_text(json.dumps(records, ensure_ascii=False, indent=2), encoding="utf-8")
        review(output, records)


if __name__ == "__main__":
    main()
