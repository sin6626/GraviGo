"""Verify, inspect and play the actual 147.8-second GraviGo product film."""
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
CHAPTERS = [
    ('Opening', '电脑继续跑', 9), ('Connection', '连接电脑与网关', 10), ('Workspaces', '工作区与项目会话', 10),
    ('LiveChat', '发送要求与流式回复', 12), ('Context', '文件引用与命令', 10), ('Attachments', '附件与语音', 12),
    ('PlanReview', '方案阅读与评论', 12), ('CodeReview', '代码差异与行评论', 12), ('Reading', '原生文件与图表', 12),
    ('Tasks', '后台任务与排队', 10), ('Decisions', '交互选择与回退', 10), ('Models', '模型与额度', 11),
    ('Desktop', '电脑权限与工具', 9), ('Settings', '账号与中英文', 10), ('Closing', '下载与致谢', 10),
]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--ffmpeg', required=True)
    parser.add_argument('--ffprobe', required=True)
    args = parser.parse_args()
    work = ROOT / '.verification/gravigo-film-v3'
    delivery = work / 'delivery'
    frames = work / 'review-frames'
    frames.mkdir(exist_ok=True)
    for key in ['TEMP', 'TMP', 'TMPDIR']:
        os.environ[key] = str(work / 'tmp')
    filename = 'GraviGo-Product-Film-1080x1920-60fps.mp4'
    movie = delivery / filename
    metadata = json.loads(subprocess.check_output([args.ffprobe, '-v', 'error', '-show_format', '-show_streams', '-of', 'json', str(movie)]))
    video = next(s for s in metadata['streams'] if s['codec_type'] == 'video')
    audio = next(s for s in metadata['streams'] if s['codec_type'] == 'audio')
    assert (video['width'], video['height'], video['r_frame_rate'], int(video['nb_frames'])) == (1080, 1920, '60/1', 8868), video
    assert video['codec_name'] == 'h264' and video['pix_fmt'] == 'yuv420p' and video['color_space'] == 'bt709' and video['color_range'] == 'tv'
    assert audio['codec_name'] == 'aac' and audio['sample_rate'] == '48000' and audio['channels'] == 2
    assert abs(float(metadata['format']['duration']) - 147.8) < .08
    scan = subprocess.run([args.ffmpeg, '-hide_banner', '-i', str(movie), '-vf', 'blackdetect=d=0.015:pix_th=0.02:pic_th=0.99',
                           '-af', 'silencedetect=noise=-50dB:d=0.5,volumedetect', '-f', 'null', '-'], capture_output=True, text=True, check=True)
    (work / 'full-decode.log').write_text(scan.stderr, encoding='utf-8')
    assert 'black_start:' not in scan.stderr, 'Unexpected black flash'
    peak = float(re.search(r'max_volume: ([\-\d.]+) dB', scan.stderr)[1])
    assert -20 < peak < -.1, peak
    silence_starts = [float(value) for value in re.findall(r'silence_start: ([\d.]+)', scan.stderr)]
    assert all(value < .3 or value > 147 for value in silence_starts), silence_starts
    print('Actual MP4: 8868 frames, 60fps, BT.709, stereo AAC and full decode passed.', flush=True)

    def frame_at(second, name):
        subprocess.run([args.ffmpeg, '-hide_banner', '-loglevel', 'error', '-y', '-ss', str(second), '-i', str(movie), '-frames:v', '1', str(frames / name)], check=True)

    cursor, chapters = 0, []
    for index, (key, title, duration) in enumerate(CHAPTERS):
        chapters.append({'scene': key, 'title': title, 'start': round(cursor, 3), 'duration': duration})
        for offset in [2, min(6.5, duration - 1)]:
            frame_at(cursor + offset, f'{index:02d}-{key}-{offset}.jpg')
        if index:
            for delta in [-.1, .2, .4, .6, .9]:
                frame_at(cursor + delta, f'transition-{index:02d}-{delta}.jpg')
        cursor += duration - .8 if index < len(CHAPTERS) - 1 else duration
    assert abs(cursor - 147.8) < .001
    frame_at(0, 'first-frame.jpg')
    frame_at(147.7, 'last-frame.jpg')
    subprocess.run([args.ffmpeg, '-hide_banner', '-loglevel', 'error', '-y', '-ss', '6', '-i', str(movie), '-frames:v', '1', '-q:v', '2', str(delivery / 'poster.jpg')], check=True)
    (delivery / 'chapters.json').write_text(json.dumps(chapters, ensure_ascii=False, indent=2), encoding='utf-8')
    credits = '''GraviGo product film · 2026-10-22
147.8 seconds / 1080 × 1920 / 60 fps / music, no narration

Music: Close Up — Michael Ramir C. (Mixkit track 1167)
https://mixkit.co/free-stock-music/tag/technology/
Music: Tech House vibes — Alejandro Magaña (A. M.) (Mixkit track 130)
https://mixkit.co/free-stock-music/electronica/
Music terms: https://mixkit.co/license/modal/musicFree/
Licensed for synchronized web/social videos. Music retains its own license; no standalone music is distributed.
TV, radio, video games, CDs and DVDs are outside this music license's scope.

Native UI: real GraviGo Android components recorded with isolated synthetic demo data and a test gateway.
The recording includes controlled test-host animation ticks; the film's motion graphics are authored at 60fps.
No real account, private conversation, pairing secret or signing key appears in the footage.
Voice transcription is an interface demonstration and depends on desktop-account permissions.
Product connectivity baseline: LAN; desktop and gateway must remain running.
Windows and macOS gateway packages exist; Mac desktop end-to-end connectivity is not claimed as verified.

GraviGo: https://github.com/sin6626/GraviGo
Based on GHSaiMo's antigravity-mobile: https://github.com/GHSaiMo/antigravity-mobile
Source code: AGPL-3.0-only; dependencies and music retain separate terms.
Fonts: Noto Sans SC and Manrope, SIL Open Font License.
The owner's supplied reference was used for timing and motion analysis only; its footage was not copied.
'''
    (delivery / 'CREDITS.txt').write_text(credits, encoding='utf-8-sig')
    links = ''.join(f'<button data-time="{chapter["start"]}">{chapter["title"]}</button>' for chapter in chapters)
    html = f'''<!doctype html><html lang="zh-CN"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>GraviGo 完整产品片</title><style>*{{box-sizing:border-box}}body{{margin:0;background:#101e36;color:#f5f3ee;font:16px system-ui;padding:36px}}main{{display:flex;gap:40px;max-width:1200px;margin:auto}}video{{width:min(52vw,500px);height:88vh;object-fit:contain;background:#07101f;border-radius:18px}}aside{{max-width:500px}}h1{{font-size:32px}}p{{line-height:1.8;color:#bacbe4}}button{{display:block;background:transparent;color:#c8d7ef;border:1px solid #405577;border-radius:8px;padding:10px 15px;margin:8px 0;cursor:pointer}}a{{color:#a7c4ff}}@media(max-width:750px){{main{{display:block}}video{{width:100%;height:75vh}}}}</style><main><video src="{filename}" poster="poster.jpg" controls playsinline preload="metadata"></video><aside><h1>GraviGo · 把Antigravity，带在身边</h1><p>2分27.8秒 · 1080×1920 · 60fps<br>请打开声音。下方按钮可跳到对应功能。</p>{links}<p><a download href="{filename}">下载完整 MP4</a> · <a href="CREDITS.txt">来源与使用说明</a></p></aside></main><script>const v=document.querySelector('video');document.querySelectorAll('button').forEach(b=>b.onclick=()=>{{v.currentTime=+b.dataset.time;v.play()}})</script></html>'''
    (delivery / 'watch.html').write_text(html, encoding='utf-8')
    from playwright.sync_api import sync_playwright
    server = ThreadingHTTPServer(('127.0.0.1', 0), partial(QuietHandler, directory=str(delivery)))
    thread = threading.Thread(target=server.serve_forever, daemon=True)
    thread.start()
    try:
        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            try:
                page = browser.new_page(viewport={'width': 1350, 'height': 1100})
                errors = []
                page.on('pageerror', lambda e: errors.append(str(e)))
                page.goto(f'http://127.0.0.1:{server.server_port}/watch.html', wait_until='networkidle')
                page.set_default_timeout(190000)
                page.evaluate("""async () => {const v=document.querySelector('video');v.muted=true;if(v.readyState<2)await new Promise(r=>v.onloadeddata=r);for(const t of [10,35,75,115,140]){v.currentTime=t;await new Promise(r=>v.addEventListener('seeked',r,{once:true}));if(Math.abs(v.currentTime-t)>.1)throw Error('seek failed')}v.currentTime=0;await v.play()}""")
                for target in [25, 50, 75, 100, 125]:
                    page.wait_for_function('t=>document.querySelector("video").currentTime>=t', arg=target, timeout=40000)
                    print(f'Actual MP4 continuous playback reached {target}s.', flush=True)
                page.wait_for_function('document.querySelector("video").ended', timeout=40000)
                state = page.evaluate('({ended:document.querySelector("video").ended,error:document.querySelector("video").error,duration:document.querySelector("video").duration})')
                assert state['ended'] and not state['error'] and not errors, (state, errors)
                page.screenshot(path=str(work / 'watch-browser.png'), full_page=True)
            finally:
                browser.close()
    finally:
        server.shutdown(); thread.join(timeout=5); server.server_close()
    manifest = {'file': filename, 'duration': 147.8, 'frames': 8868, 'fps': 60, 'width': 1080, 'height': 1920,
                'sha256': hashlib.sha256(movie.read_bytes()).hexdigest(), 'bytes': movie.stat().st_size,
                'video': 'H.264/yuv420p/BT.709/limited', 'audio': 'AAC/stereo/48kHz', 'audioPeakDb': peak,
                'fullDecode': 'passed', 'blackFrames': 0, 'audioContinuity': 'passed', 'continuousBrowserPlayback': 'passed', 'chapters': chapters}
    (delivery / 'manifest.json').write_text(json.dumps(manifest, ensure_ascii=False, indent=2), encoding='utf-8')
    print('Full-length playback to the end passed; delivery and review frames are ready.', flush=True)


if __name__ == '__main__':
    main()
