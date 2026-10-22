"""Prepare the complete film's verified native footage and licensed music."""
import json
from pathlib import Path
import shutil
import subprocess
import argparse

ROOT = Path(__file__).resolve().parents[4]
PROJECT = Path(__file__).resolve().parent
FEATURES = ['pairing', 'workspace', 'stream', 'context', 'attachments', 'plan', 'diff', 'reading',
            'models', 'settings', 'tasks', 'voice', 'desktop', 'revert', 'interaction', 'rich']


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--ffprobe', required=True)
    parser.add_argument('--language', choices=['zh', 'en'], default='zh')
    args = parser.parse_args()
    destination = PROJECT / ('public/film-en' if args.language == 'en' else 'public/film')
    destination.mkdir(exist_ok=True)
    work = ROOT / '.verification/gravigo-film-v3'
    if args.language == 'en':
        work = work / 'english'
    native = work / 'native'
    manifest = []
    for feature in FEATURES:
        log = (native / feature / 'instrumentation.log').read_text(encoding='utf-8')
        assert 'OK (1 test)' in log and 'FAILURES' not in log, feature
        source = native / (feature + '.mp4')
        metadata = json.loads(subprocess.check_output([args.ffprobe, '-v', 'error', '-show_format', '-show_streams', '-of', 'json', str(source)]))
        video = metadata['streams'][0]
        assert video['width'] == 1080 and video['height'] == 2218 and float(metadata['format']['duration']) >= 12, (feature, video)
        assert video['r_frame_rate'] == '60/1' and int(video['nb_frames']) == 840, feature
        shutil.copy2(source, destination / source.name)
        shutil.copy2(native / feature / 'after.png', destination / (feature + '-after.png'))
        manifest.append({'feature': feature, 'nativeActions': 'passed', 'duration': metadata['format']['duration'], 'source': str(source.relative_to(ROOT))})
    music = ROOT / '.verification/gravigo-promo-remotion-v2/music'
    shutil.copy2(music / 'mixkit-1167.mp3', destination / 'music-close-up.mp3')
    shutil.copy2(music / 'mixkit-130.mp3', destination / 'music-tech-house.mp3')
    (work / 'footage.json').write_text(json.dumps(manifest, ensure_ascii=False, indent=2), encoding='utf-8')
    print('16 native feature recordings checked and copied; two licensed music tracks prepared.')


if __name__ == '__main__':
    main()
