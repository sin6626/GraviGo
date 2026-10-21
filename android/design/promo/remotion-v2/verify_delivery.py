"""Inspect and play the actual three Remotion MP4 deliveries, then write a comparison page."""
import argparse
from functools import partial
import hashlib
from http.server import ThreadingHTTPServer
import json
import os
from pathlib import Path
import re
import subprocess
import sys
import threading

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from render_previews import QuietHandler

ROOT = Path(__file__).resolve().parents[4]
CLIPS = [
    ("A", "掌心接力", "GraviGo-A-PalmRelay-1080x1920.mp4"),
    ("B", "失重轨道", "GraviGo-B-WeightlessOrbit-1080x1920.mp4"),
    ("C", "别守着电脑", "GraviGo-C-StopWaiting-1080x1920.mp4"),
]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--ffmpeg", required=True)
    parser.add_argument("--ffprobe", required=True)
    args = parser.parse_args()
    output = ROOT / ".verification/gravigo-promo-remotion-v2"
    delivery = output / "delivery"
    for key in ("TEMP", "TMP", "TMPDIR"):
        os.environ[key] = str(output / "tmp")
    records, cards = [], []
    for variant, title, filename in CLIPS:
        file = delivery / filename
        metadata = json.loads(subprocess.check_output([args.ffprobe, "-v", "error", "-show_format", "-show_streams", "-of", "json", str(file)], text=True))
        video = next(s for s in metadata["streams"] if s["codec_type"] == "video")
        audio = next(s for s in metadata["streams"] if s["codec_type"] == "audio")
        assert (video["width"], video["height"], video["r_frame_rate"], int(video["nb_frames"])) == (1080, 1920, "30/1", 540)
        assert video["codec_name"] == "h264" and video["pix_fmt"] == "yuv420p"
        assert video["color_space"] == "bt709" and video["color_range"] == "tv"
        assert audio["codec_name"] == "aac" and audio["sample_rate"] == "48000" and audio["channels"] == 2
        assert abs(float(metadata["format"]["duration"]) - 18) < .06
        scan = subprocess.run([args.ffmpeg, "-hide_banner", "-i", str(file), "-vf", "blackdetect=d=0.03:pix_th=0.02:pic_th=0.99", "-af", "volumedetect", "-f", "null", "-"], capture_output=True, text=True, check=True)
        assert "black_start:" not in scan.stderr, filename
        peak = float(re.search(r"max_volume: ([\-\d.]+) dB", scan.stderr)[1])
        assert -30 < peak < -.1, (filename, peak)
        for second in (0, 1, 6, 11, 17):
            subprocess.run([args.ffmpeg, "-hide_banner", "-loglevel", "error", "-y", "-ss", str(second), "-i", str(file),
                            "-frames:v", "1", str(output / f"{variant}-export-{second:02d}.png")], check=True)
        poster = f"{variant}-poster.jpg"
        subprocess.run([args.ffmpeg, "-hide_banner", "-loglevel", "error", "-y", "-ss", "1.2", "-i", str(file),
                        "-frames:v", "1", "-q:v", "2", str(delivery / poster)], check=True)
        records.append({"variant": variant, "title": title, "file": filename, "duration": 18, "frames": 540,
                        "width": 1080, "height": 1920, "fps": 30, "video": "H.264/yuv420p", "audio": "AAC/stereo/48kHz",
                        "bytes": file.stat().st_size, "sha256": hashlib.sha256(file.read_bytes()).hexdigest(),
                        "fullDecode": "passed", "blackFrames": 0, "audioPeakDb": peak})
        cards.append(f'<article><h2>{variant} · {title}</h2><video controls playsinline preload="metadata" poster="{poster}" src="{filename}"></video><p>18 秒 · 1080 × 1920 · 30 fps · 带配乐</p><a download href="{filename}">下载这一版</a></article>')
        print(f"{variant}: actual MP4 metadata, full decode, black-frame and audio checks passed", flush=True)
    credits = ["GraviGo Remotion V2 — music and asset credits", "", "Music: Mixkit Stock Music Free License",
               "https://mixkit.co/license/modal/musicFree/", "Source and license checked on 2026-10-21.",
               "These clips use excerpts synchronized to video. Original music files are not included in this delivery.",
               "Keep use within the license's web/social-video scope; the music is not licensed here for TV, radio, CDs, DVDs or video games.", ""]
    for source in json.loads((output / "music/sources.json").read_text(encoding="utf-8")):
        credits += [f"{source['variant']}: {source['name']} — {source['artist']}", source['page'], f"Mixkit track ID: {source['id']}", ""]
    credits += ["Product: GraviGo, an independent Android remote client for Antigravity.",
                "Based on GHSaiMo's antigravity-mobile: https://github.com/GHSaiMo/antigravity-mobile",
                "Source: https://github.com/sin6626/GraviGo", "Native UI: isolated demonstration conversations; no private account data.",
                "Fonts: Noto Sans SC and Manrope, SIL Open Font License.", "No narration. Verified product connectivity baseline: LAN; desktop and gateway remain running."]
    (delivery / "CREDITS.txt").write_text("\n".join(credits), encoding="utf-8-sig")
    html = '''<!doctype html><html lang="zh-CN"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>GraviGo · Remotion 三版成片</title><style>*{box-sizing:border-box}body{margin:0;padding:40px;background:#101216;color:#eef1f7;font:16px system-ui,sans-serif}h1{font-size:32px}header p{color:#adb5c4}main{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:24px;max-width:1500px}article{padding:20px;border:1px solid #303741;border-radius:16px;background:#1a1e25}h2{font-size:22px}video{width:100%;max-height:74vh;background:#080a10;border-radius:8px}p{font-size:14px;color:#adb5c4}a{color:#a7c6ff}@media(max-width:850px){main{grid-template-columns:1fr}body{padding:20px}}</style><header><h1>GraviGo · Remotion 三版成片</h1><p>请打开声音，逐一比较画面与配乐。播放一支时，其他视频会暂停。</p></header><main>'''
    html += "".join(cards) + '''</main><footer><p>三支均为18秒竖版短片。<a href="CREDITS.txt">素材来源与使用说明</a></p></footer><script>document.querySelectorAll('video').forEach(v=>v.addEventListener('play',()=>document.querySelectorAll('video').forEach(o=>{if(o!==v)o.pause()})))</script></html>'''
    (delivery / "compare.html").write_text(html, encoding="utf-8")
    from playwright.sync_api import sync_playwright
    server = ThreadingHTTPServer(("127.0.0.1", 0), partial(QuietHandler, directory=str(delivery)))
    worker = threading.Thread(target=server.serve_forever, daemon=True)
    worker.start()
    try:
        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            try:
                page = browser.new_page(viewport={"width": 1500, "height": 1150})
                page_errors = []
                page.on("pageerror", lambda error: page_errors.append(str(error)))
                page.goto(f"http://127.0.0.1:{server.server_port}/compare.html", wait_until="networkidle")
                for index, record in enumerate(records):
                    state = page.evaluate("""async index => {
                      const video=document.querySelectorAll('video')[index];video.muted=true;
                      if(video.readyState<2)await new Promise((resolve,reject)=>{video.addEventListener('loadeddata',resolve,{once:true});video.addEventListener('error',reject,{once:true})});
                      for(const time of [1,6,11]){video.currentTime=time;await new Promise(resolve=>video.addEventListener('seeked',resolve,{once:true}));if(Math.abs(video.currentTime-time)>.1)throw Error('Seek failed')}
                      video.currentTime=17.6;await video.play();await new Promise(resolve=>video.addEventListener('ended',resolve,{once:true}));
                      return {ended:video.ended,duration:video.duration,error:video.error};
                    }""", index)
                    assert state["ended"] and not state["error"] and abs(state["duration"] - 18) < .06
                    record["browserPlayback"] = "passed"
                    print(record["variant"] + ": browser seeking and playback to the end passed", flush=True)
                assert not page_errors, page_errors
                page.screenshot(path=str(output / "delivery-browser.png"), full_page=True)
            finally:
                browser.close()
    finally:
        server.shutdown()
        worker.join(timeout=5)
        server.server_close()
    (delivery / "manifest.json").write_text(json.dumps(records, ensure_ascii=False, indent=2), encoding="utf-8")


if __name__ == "__main__":
    main()
