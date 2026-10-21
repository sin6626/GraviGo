"""Prepare licensed stock-music excerpts for synchronized GraviGo videos only."""
import argparse
import hashlib
import json
from pathlib import Path
import subprocess
import urllib.request

ROOT = Path(__file__).resolve().parents[4]
PROJECT = Path(__file__).resolve().parent
TRACKS = [
    {"variant": "A", "name": "Close Up", "artist": "Michael Ramir C.", "id": 1167,
     "file": "a-close-up.wav", "offset": 8, "page": "https://mixkit.co/free-stock-music/tag/technology/"},
    {"variant": "B", "name": "Vastness", "artist": "Andrew Ev", "id": 184,
     "file": "b-vastness.wav", "offset": 24, "page": "https://mixkit.co/free-stock-music/electronica/"},
    {"variant": "C", "name": "Tech House vibes", "artist": "Alejandro Magaña (A. M.)", "id": 130,
     "file": "c-tech-house.wav", "offset": 16, "page": "https://mixkit.co/free-stock-music/electronica/"},
]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--ffmpeg", required=True)
    args = parser.parse_args()
    cache = ROOT / ".verification/gravigo-promo-remotion-v2/music"
    destination = PROJECT / "public/music"
    cache.mkdir(parents=True, exist_ok=True)
    destination.mkdir(parents=True, exist_ok=True)
    records = []
    for track in TRACKS:
        url = f"https://assets.mixkit.co/music/{track['id']}/{track['id']}.mp3"
        raw = cache / f"mixkit-{track['id']}.mp3"
        if not raw.exists():
            with urllib.request.urlopen(url, timeout=60) as response:
                raw.write_bytes(response.read())
        subprocess.run([args.ffmpeg, "-hide_banner", "-loglevel", "error", "-y",
            "-ss", str(track["offset"]), "-i", str(raw), "-t", "18", "-af",
            "loudnorm=I=-16:TP=-2:LRA=7,afade=t=in:st=0:d=0.18,afade=t=out:st=16.8:d=1.2",
            "-ar", "48000", "-ac", "2", "-c:a", "pcm_s16le", str(destination / track["file"])], check=True)
        records.append({**track, "url": url, "license": "Mixkit Stock Music Free License",
                        "licenseUrl": "https://mixkit.co/license/modal/musicFree/",
                        "sourceSha256": hashlib.sha256(raw.read_bytes()).hexdigest()})
        print(f"{track['variant']}: prepared {track['name']} with fade-in and fade-out", flush=True)
    (cache / "sources.json").write_text(json.dumps(records, ensure_ascii=False, indent=2), encoding="utf-8")


if __name__ == "__main__":
    main()
