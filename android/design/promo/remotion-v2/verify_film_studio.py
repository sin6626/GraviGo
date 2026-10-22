"""Check the full film and every connected chapter in real Remotion Studio."""
import argparse
import json
import os
from pathlib import Path
from playwright.sync_api import sync_playwright
from verify_film import CHAPTERS

ROOT = Path(__file__).resolve().parents[4]
OUT = ROOT / '.verification/gravigo-film-v3'
for key in ['TEMP', 'TMP', 'TMPDIR']:
    os.environ[key] = str(OUT / 'tmp')
BRIDGE = """window.filmTools=new Map();Object.defineProperty(document,'modelContext',{value:{registerTool(t,o){window.filmTools.set(t.name,t);o?.signal?.addEventListener('abort',()=>window.filmTools.delete(t.name));return Promise.resolve()}}});"""


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--language', choices=['zh', 'en'], default='zh')
    args = parser.parse_args()
    english = args.language == 'en'
    records = []
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        try:
            page = browser.new_page(viewport={'width': 1600, 'height': 1100})
            errors = []
            page.on('pageerror', lambda error: errors.append(str(error)))
            page.add_init_script(BRIDGE)
            page.goto('http://localhost:4317/Film-Opening', wait_until='domcontentloaded')
            page.wait_for_function("window.filmTools?.has('select_composition')")

            def tool(name, args=None):
                return page.evaluate('([n,a])=>window.filmTools.get(n).execute(a)', [name, args or {}])

            def select(name):
                tool('select_composition', {'compositionName': name})
                page.wait_for_function("async n=>(await window.filmTools.get('get_playback_state').execute({})).currentContent?.compositionId===n", arg=name)

            for key, title, duration in ([] if english else CHAPTERS):
                name = 'Film-' + key
                select(name)
                for frame in [120, min(390, (duration - 1) * 60)]:
                    tool('seek_to_frame', {'frame': frame})
                    page.wait_for_timeout(280)
                    assert tool('get_playback_state')['currentFrame'] == frame
                    assert tool('get_current_error') is None, name
                records.append({'scene': name, 'standalonePreview': 'passed'})
            select('GraviGo-FullFilm-EN' if english else 'GraviGo-FullFilm')
            cursor = 0
            for key, _, duration in CHAPTERS:
                tool('seek_to_frame', {'frame': round((cursor + 2) * 60)})
                page.wait_for_timeout(220)
                assert tool('get_current_error') is None, key
                records.append({'scene': key, 'timelinePreview': 'passed'})
                cursor += duration - .8
            tool('seek_to_frame', {'frame': 3300})
            page.wait_for_function("async ()=>(await window.filmTools.get('get_playback_state').execute({})).currentFrame===3300", timeout=30000)
            tool('play')
            page.wait_for_function("async ()=>(await window.filmTools.get('get_playback_state').execute({})).currentFrame>3300", timeout=30000)
            tool('pause')
            assert tool('get_current_error') is None
            assert not errors, errors
            page.screenshot(path=str(OUT / ('full-film-studio-en.png' if english else 'full-film-studio.png')))
        finally:
            browser.close()
    (OUT / ('studio-checks-en.json' if english else 'studio-checks.json')).write_text(json.dumps(records, indent=2), encoding='utf-8')
    print('Chapter previews, full timeline seeking and playback passed.', flush=True)


if __name__ == '__main__':
    main()
