"""Capture a native, isolated GraviGo demo session on the running emulator."""
import argparse
import json
from pathlib import Path
import subprocess
import time

ROOT = Path(__file__).resolve().parents[3]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--adb", required=True)
    parser.add_argument("--ffmpeg", required=True)
    args = parser.parse_args()
    output = ROOT / ".verification/gravigo-product-promo"
    assets = output / "assets"
    assets.mkdir(parents=True, exist_ok=True)
    adb = [args.adb, "-s", "emulator-5554"]

    def call(*command, **kwargs):
        return subprocess.run([*adb, *command], check=True, capture_output=True, text=True, **kwargs)

    call("install", "-r", str(ROOT / "android/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"))
    run = "preview" + time.strftime("%Y%m%d%H%M%S")
    remote = f"/sdcard/Android/data/io.github.sin6626.gravigo.debug/files/promo-{run}"
    log_path = output / "capture-instrumentation.log"
    with log_path.open("w", encoding="utf-8") as log:
        test = subprocess.Popen([*adb, "shell", "am", "instrument", "-w", "-e", "class",
            "io.github.sin6626.gravigo.ui.demo.ProductPromoCaptureTest", "-e", "promoRun", run,
            "io.github.sin6626.gravigo.debug.test/androidx.test.runner.AndroidJUnitRunner"], stdout=log, stderr=log)
        recorder = None
        try:
            deadline = time.monotonic() + 70
            while subprocess.run([*adb, "shell", "test", "-f", remote + "/ready"], capture_output=True).returncode:
                if test.poll() is not None or time.monotonic() > deadline:
                    raise RuntimeError("Native capture did not become ready; inspect " + str(log_path))
                time.sleep(0.5)
            print("Native demo ready; recording only isolated demo content.", flush=True)
            video = f"/sdcard/{run}.mp4"
            recorder = subprocess.Popen([*adb, "shell", "screenrecord", "--time-limit", "18", "--bit-rate", "12000000", video],
                                        stdout=subprocess.DEVNULL, stderr=log)
            time.sleep(0.5)
            call("shell", "touch", remote + "/go")
            recorder.wait(timeout=30)
            test.wait(timeout=50)
            log.flush()
            result = log_path.read_text(encoding="utf-8")
            if "OK (1 test)" not in result or "FAILURES" in result:
                raise RuntimeError("Native capture test failed; inspect " + str(log_path))
            for name in ["01-conversation.png", "02-instruction.png", "03-plan-ready.png", "04-plan.png", "bounds.json"]:
                call("pull", remote + "/" + name, str(assets / name))
            raw = output / "native-full.mp4"
            call("pull", video, str(raw))
            bounds = json.loads((assets / "bounds.json").read_text())
            crop = ":".join(str(bounds[key]) for key in ("width", "height", "x", "y"))
            subprocess.run([args.ffmpeg, "-hide_banner", "-loglevel", "error", "-y", "-i", str(raw),
                "-t", "16", "-vf", "crop=" + crop + ":exact=1", "-an", "-c:v", "libx264", "-preset", "fast",
                "-crf", "18", "-pix_fmt", "yuv420p", "-movflags", "+faststart", str(assets / "native-demo.mp4")], check=True)
            # The plan lives in a dialog window, outside the Compose root capture.
            subprocess.run([args.ffmpeg, "-hide_banner", "-loglevel", "error", "-y", "-ss", "11", "-i",
                str(assets / "native-demo.mp4"), "-frames:v", "1", str(assets / "04-plan.png")], check=True)
            print("Native sequence and four clean screen captures saved in " + str(assets), flush=True)
        finally:
            for process in (recorder, test):
                if process is not None and process.poll() is None:
                    process.terminate()
                    process.wait(timeout=10)


if __name__ == "__main__":
    main()
