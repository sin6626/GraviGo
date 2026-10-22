"""Record real Android feature flows against an isolated local fixture gateway."""
import argparse
import json
import os
from pathlib import Path
import subprocess
import time

ROOT = Path(__file__).resolve().parents[3]
FEATURES = ['pairing', 'workspace', 'stream', 'context', 'attachments', 'plan', 'diff', 'reading',
            'models', 'settings', 'tasks', 'voice', 'desktop', 'revert', 'interaction', 'rich']


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--adb', required=True)
    parser.add_argument('--ffmpeg', required=True)
    parser.add_argument('--feature', choices=FEATURES, action='append')
    args = parser.parse_args()
    output = ROOT / '.verification/gravigo-film-v3'
    assets = output / 'native'
    assets.mkdir(parents=True, exist_ok=True)
    adb = [args.adb, '-s', 'emulator-5554']

    def call(*command):
        return subprocess.run([*adb, *command], check=True, capture_output=True, text=True)

    call('install', '-r', str(ROOT / 'android/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk'))
    run = time.strftime('%Y%m%d%H%M%S')
    remote = f'/sdcard/Android/data/io.github.sin6626.gravigo.debug/files/film-{run}'
    call('shell', 'mkdir', '-p', remote)
    call('push', str(ROOT / 'android/design/promo/remotion-v2/public/brand.png'), remote + '/brand.png')
    requirements = output / 'requirements.txt'
    requirements.write_text('GraviGo demo: preserve input, show retry, verify interactions.\n', encoding='utf-8')
    call('push', str(requirements), remote + '/requirements.txt')
    failures = []
    try:
        for feature in args.feature or FEATURES:
            folder = assets / feature
            folder.mkdir(exist_ok=True)
            target = remote + '/' + feature
            log_path = folder / 'instrumentation.log'
            with log_path.open('w', encoding='utf-8') as log:
                test = subprocess.Popen([*adb, 'shell', 'am', 'instrument', '-w', '-e', 'class',
                    'io.github.sin6626.gravigo.ui.demo.' + ('ProductLocaleCaptureTest' if feature == 'settings' else 'ProductFeatureCaptureTest'), '-e', 'promoRun', run,
                    '-e', 'promoFeature', feature,
                    'io.github.sin6626.gravigo.debug.test/androidx.test.runner.AndroidJUnitRunner'], stdout=log, stderr=log)
                recorder = None
                try:
                    deadline = time.monotonic() + 75
                    while subprocess.run([*adb, 'shell', 'test', '-f', target + '/ready'], capture_output=True).returncode:
                        if test.poll() is not None or time.monotonic() > deadline:
                            raise RuntimeError(feature + ': native flow did not become ready; see ' + str(log_path))
                        time.sleep(.3)
                    recording = remote + '/' + feature + '.mp4'
                    recorder = subprocess.Popen([*adb, 'shell', 'screenrecord', '--time-limit', '14', '--bit-rate', '16000000', recording], stdout=log, stderr=log)
                    time.sleep(.4)
                    call('shell', 'touch', target + '/go')
                    recorder.wait(timeout=25)
                    call('shell', 'touch', target + '/ack')
                    test.wait(timeout=35)
                    log.flush()
                    report = log_path.read_text(encoding='utf-8')
                    if 'OK (1 test)' not in report or 'FAILURES' in report:
                        raise RuntimeError(feature + ': native action check failed; see ' + str(log_path))
                    for name in ['before.png', 'after.png', 'bounds.json', 'done']:
                        call('pull', target + '/' + name, str(folder / name))
                    raw = folder / 'full.mp4'
                    call('pull', recording, str(raw))
                    bounds = json.loads((folder / 'bounds.json').read_text())
                    bounds['width'] -= bounds['width'] % 2
                    bounds['height'] -= bounds['height'] % 2
                    crop = ':'.join(str(bounds[key]) for key in ['width', 'height', 'x', 'y'])
                    subprocess.run([args.ffmpeg, '-hide_banner', '-loglevel', 'error', '-y', '-i', str(raw),
                        '-vf', 'crop=' + crop + ':exact=1,fps=60,tpad=stop_mode=clone:stop_duration=14', '-t', '14', '-an', '-c:v', 'libx264', '-crf', '17', '-preset', 'fast',
                        '-pix_fmt', 'yuv420p', '-movflags', '+faststart', str(assets / (feature + '.mp4'))], check=True)
                    print(feature + ': native actions checked and footage saved', flush=True)
                except Exception as error:
                    failures.append(feature)
                    print(str(error), flush=True)
                finally:
                    for process in [recorder, test]:
                        if process is not None and process.poll() is None:
                            process.terminate(); process.wait(timeout=10)
    finally:
        call('shell', 'am', 'start', '-n', 'io.github.sin6626.gravigo.debug/io.github.sin6626.gravigo.MainActivity')
    if failures:
        raise RuntimeError('Native footage needs repair: ' + ', '.join(failures))


if __name__ == '__main__':
    main()
