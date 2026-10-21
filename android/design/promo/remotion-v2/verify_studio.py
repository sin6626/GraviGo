"""Check actual Remotion Studio compositions through its published WebMCP controls."""
import json
import os
from pathlib import Path
from playwright.sync_api import sync_playwright

ROOT = Path(__file__).resolve().parents[4]
OUTPUT = ROOT / ".verification/gravigo-promo-remotion-v2"
for variable in ("TEMP", "TMP", "TMPDIR"):
    os.environ[variable] = str(OUTPUT / "tmp")

BRIDGE = """
window.studioTools = new Map();
Object.defineProperty(document, 'modelContext', {value: {
  registerTool(tool, options) {
    window.studioTools.set(tool.name, tool);
    options?.signal?.addEventListener('abort', () => {
      if (window.studioTools.get(tool.name) === tool) window.studioTools.delete(tool.name);
    });
    return Promise.resolve();
  }
}});
"""

def main():
    results = []
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        try:
            page = browser.new_page(viewport={"width": 1500, "height": 1100})
            page.add_init_script(BRIDGE)
            errors = []
            page.on("pageerror", lambda error: errors.append(str(error)))
            page.goto('http://localhost:4317/Palm-Product', wait_until='domcontentloaded')
            page.wait_for_function("window.studioTools?.has('select_composition')")

            def tool(name, args=None):
                return page.evaluate("([name,args]) => window.studioTools.get(name).execute(args)", [name, args or {}])

            def select(name):
                tool('select_composition', {'compositionName': name})
                page.wait_for_function("async name => (await window.studioTools.get('get_playback_state').execute({})).currentContent?.compositionId === name", arg=name)
                page.wait_for_timeout(250)

            for name in ['Palm-Opening', 'Palm-Product', 'Orbit-Opening', 'Orbit-Product', 'Editorial-Opening', 'Editorial-Product', 'Brand-End']:
                select(name)
                tool('seek_to_frame', {'frame': 35})
                page.wait_for_timeout(180)
                assert tool('get_current_error') is None, name
            for name in ['A-PalmRelay', 'B-WeightlessOrbit', 'C-StopWaiting']:
                select(name)
                for frame in [0, 80, 180, 330, 500, 539]:
                    tool('seek_to_frame', {'frame': frame})
                    page.wait_for_timeout(200)
                    assert tool('get_playback_state')['currentFrame'] == frame, (name, frame)
                    assert tool('get_current_error') is None, (name, frame)
                tool('seek_to_frame', {'frame': 80})
                tool('play')
                page.wait_for_timeout(800)
                tool('pause')
                state = tool('get_playback_state')
                assert state['currentFrame'] > 80 and not state['playing'], state
                page.screenshot(path=str(OUTPUT / f'{name}-studio.png'))
                results.append({'composition': name, 'frameCount': 540, 'sceneChecks': 'passed', 'seekChecks': 'passed', 'playPause': 'passed'})
                print(name + ': independent scenes, seeking and playback passed', flush=True)
            assert not errors, errors
        finally:
            browser.close()
    (OUTPUT / 'studio-verification.json').write_text(json.dumps(results, indent=2), encoding='utf-8')

if __name__ == '__main__':
    main()
