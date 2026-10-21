# GraviGo — Remotion product shorts

Three independent 1080×1920, 30fps, 18-second visual studies for Douyin and Xiaohongshu:

| Composition | Direction |
| --- | --- |
| `A-PalmRelay` | Warm paper, cobalt blue, readable native UI, and directional handoff transitions |
| `B-WeightlessOrbit` | Dark space, restrained orbit imagery, layered perspective, and a floating brand mark |
| `C-StopWaiting` | Acid lime, bold editorial typography, match cuts, and direct native-operation proof |

These replace the rejected first-round concepts. Each direction now has an 18-second MP4 with its own licensed soundtrack, native product footage, and brand ending. There is no narration. See [the Chinese director's plan](../ART-DIRECTION-V2.zh-CN.md).

The local delivery is `.verification/gravigo-promo-remotion-v2/delivery/` at the repository root. Open `compare.html` to compare all three videos; `CREDITS.txt` records asset sources and music terms, and `manifest.json` records actual-file checks and SHA-256 hashes. Media stays Git-ignored. These are short films; a 60–75 second cut is not included.

## Project setup

```powershell
npm ci
npm run assets
python prepare_music.py --ffmpeg <absolute-ffmpeg-path>
npm run dev
```

Open [Remotion Studio](http://localhost:4317). The three main compositions appear first; `Scenes` contains connected compositions for independently editing each scene. Main registrations, scene instances, timing, copy, and keyframes are authored as separate editable JSX nodes.

`prepare-assets.mjs` copies approved branding and isolated Android demo footage from `.verification/gravigo-product-promo/assets/` into Git-ignored `public/`. It caches variable Noto Sans SC and Manrope fonts from Google Fonts and preserves their original SIL OFL notices. Regenerate missing native recordings using the opt-in capture workflow in the parent directory.

`prepare_music.py` downloads three Mixkit tracks into a private project-local cache, selects 18-second excerpts, normalizes loudness, and adds beginning/end fades. A uses **Close Up** by Michael Ramir C.; B uses **Vastness** by Andrew Ev; C uses **Tech House vibes** by Alejandro Magaña (A. M.). The [Mixkit Stock Music Free License](https://mixkit.co/license/modal/musicFree/) applies to the music independently of the source-code license. Keep these excerpts synchronized to video; do not distribute the cached tracks as standalone music.

## Export

```powershell
./render_delivery.ps1 -BrowserExecutable <absolute-Chrome-Headless-Shell-path>
python verify_delivery.py --ffmpeg <absolute-ffmpeg-path> --ffprobe <absolute-ffprobe-path>
```

The renderer exports all three compositions sequentially as H.264/yuv420p with BT.709 limited-range color, AAC stereo audio at 48 kHz, and CRF 18. `verify_delivery.py` checks each actual MP4's dimensions, frame count, duration, color, audio, full decode, black frames, and browser seeking/playback. It also creates the comparison page, posters, credits, and manifest. FFmpeg must support H.264/AAC decoding and the `blackdetect`/`volumedetect` filters; music preparation also needs `loudnorm`.

## Project verification

```powershell
npm run lint
python verify_studio.py
```

The Python check uses the installed Playwright package and adapts Studio's published WebMCP registration API inside a disposable browser. It checks composition selection, standalone scenes, frame seeking, error inspection, playback, and pause without editing source. Reports and screenshots go to `.verification/gravigo-promo-remotion-v2/`.

Use project-local npm cache and temporary directories on this Windows workspace. Keyframes can be captured with Remotion's `still` command and an installed Chrome Headless Shell using `--browser-executable`; keep that machine-specific path out of project configuration. Keep Studio running while reviewing the directions.

The footage uses synthetic demonstration conversations and preserves actual native app UI. It contains no private account details or pairing credentials. The product's verified connection baseline remains LAN with desktop and gateway running. Project source follows the repository's AGPL-3.0-only license; third-party tools and fonts retain their own terms.

## Original scaffold reference

<p align="center">
  <a href="https://github.com/remotion-dev/logo">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://github.com/remotion-dev/logo/raw/main/animated-logo-banner-dark.apng">
      <img alt="Animated Remotion Logo" src="https://github.com/remotion-dev/logo/raw/main/animated-logo-banner-light.gif">
    </picture>
  </a>
</p>

Welcome to your Remotion project!

## Commands

**Install Dependencies**

```console
npm i --loglevel=error
```

**Start Preview**

```console
npm run dev
```

**Render video**

```console
npx remotion render
```

**Upgrade Remotion**

```console
npx remotion upgrade
```

## Docs

Get started with Remotion by reading the [fundamentals page](https://www.remotion.dev/docs/the-fundamentals).

## Help

We provide help on our [Discord server](https://discord.gg/6VzzNDwUwV).

## Issues

Found an issue with Remotion? [File an issue here](https://github.com/remotion-dev/remotion/issues/new).

## License

Note that for some entities a company license is needed. [Read the terms here](https://github.com/remotion-dev/remotion/blob/main/LICENSE.md).
