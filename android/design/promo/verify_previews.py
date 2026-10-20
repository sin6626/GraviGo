"""Verify the three delivered MP4s, including browser playback and media metadata."""
import argparse
from functools import partial
from http.server import ThreadingHTTPServer
import hashlib
import json
import os
import re
import subprocess
import threading
from render_previews import QuietHandler, ROOT


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--ffmpeg", required=True)
    parser.add_argument("--ffprobe", required=True)
    args = parser.parse_args()
    root = ROOT / ".verification/gravigo-product-promo"
    delivery = root / "delivery"
    records = json.loads((delivery / "manifest.json").read_text(encoding="utf-8"))
    assert set(records) == {"A", "B", "C"}
    for name in ("TEMP", "TMP", "TMPDIR"):
        os.environ[name] = str(root / "tmp")
    from playwright.sync_api import sync_playwright
    server = ThreadingHTTPServer(("127.0.0.1", 0), partial(QuietHandler, directory=str(delivery)))
    thread = threading.Thread(target=server.serve_forever, daemon=True)
    thread.start()
    try:
        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            try:
                page = browser.new_page(viewport={"width": 1440, "height": 1150})
                errors = []
                page.on("pageerror", lambda error: errors.append(str(error)))
                page.goto(f"http://127.0.0.1:{server.server_port}/compare.html", wait_until="networkidle")
                for index, (variant, entry) in enumerate(records.items()):
                    file = delivery / entry["file"]
                    assert hashlib.sha256(file.read_bytes()).hexdigest() == entry["sha256"]
                    metadata = json.loads(subprocess.check_output([args.ffprobe, "-v", "error", "-show_format", "-show_streams", "-of", "json", str(file)], text=True))
                    video = next(s for s in metadata["streams"] if s["codec_type"] == "video")
                    audio = next(s for s in metadata["streams"] if s["codec_type"] == "audio")
                    assert (video["width"], video["height"], video["r_frame_rate"], int(video["nb_frames"])) == (1080, 1920, "30/1", 450)
                    assert video["codec_name"] == "h264" and audio["codec_name"] == "aac" and audio["sample_rate"] == "48000"
                    assert abs(float(metadata["format"]["duration"]) - 15) < .05
                    scan = subprocess.run([args.ffmpeg, "-hide_banner", "-i", str(file), "-vf", "blackdetect=d=0.03:pix_th=0.02:pic_th=0.99", "-af", "volumedetect", "-f", "null", "-"], capture_output=True, text=True, check=True)
                    assert "black_start:" not in scan.stderr
                    peak = float(re.search(r"max_volume: ([\-\d.]+) dB", scan.stderr)[1])
                    assert -40 < peak < -1
                    playback = page.evaluate("""async index => {
                      const video=document.querySelectorAll('video')[index];video.muted=true;
                      if(video.readyState<2)await new Promise((resolve,reject)=>{video.addEventListener('loadeddata',resolve,{once:true});video.addEventListener('error',reject,{once:true})});
                      for(const time of [1,7,11]){video.currentTime=time;await new Promise(resolve=>video.addEventListener('seeked',resolve,{once:true}));if(Math.abs(video.currentTime-time)>.1)throw Error('Preview did not seek')}
                      video.currentTime=14.6;await video.play();await new Promise(resolve=>video.addEventListener('ended',resolve,{once:true}));
                      return {duration:video.duration,ended:video.ended,error:video.error};
                    }""", index)
                    assert playback["ended"] and not playback["error"] and abs(playback["duration"] - 15) < .05
                    entry["verification"] = {"actualMetadata": "passed", "blackFrames": 0, "audioPeakDb": peak,
                                             "browserSeekAndPlayback": "passed"}
                    print(f"{variant}: 450 frames, 15s, H.264/AAC, no black frames, browser playback passed", flush=True)
                assert not errors, errors
                page.screenshot(path=str(root / "tmp/comparison-browser.png"), full_page=True)
            finally:
                browser.close()
    finally:
        server.shutdown()
        thread.join(timeout=5)
        server.server_close()
    (delivery / "manifest.json").write_text(json.dumps(records, ensure_ascii=False, indent=2), encoding="utf-8")


if __name__ == "__main__":
    main()
