# GraviGo — Remotion product shorts

## Complete product film

The original full film is preserved as **v1** in `.verification/gravigo-film-v3/versions/v1/`, including its MP4, viewing page, credits and checksums. Its source is Git commit `bdd787b`. **v2 removes only the floating native-detail cards** and preserves timing, phone motion, footage, copy and music. Select `GraviGo-FullFilm-V2` in Studio; `GraviGo-FullFilm` retains v1 behavior. The export and verification commands below now write v2 to `.verification/gravigo-film-v3/versions/v2/` and never overwrite the v1 archive.

The active delivery is **`GraviGo-FullFilm-V2`**, a **147.8-second, 1080×1920, 60fps** film. The owner requested a complete feature walkthrough on October 22 after rejecting the 18-second scope. Its 15 connected chapters live in `src/film/` and `FullFilm-Scenes` in Studio. The earlier shorts remain available as history.

The film follows connection, workspaces, streaming chat, workspace references and commands, image/file attachments, voice input, plan comments, code review, native reading, background tasks, queued messages, interactive decisions, revert previews, models and quotas, desktop state, account details, and Chinese/English settings. Sixteen native recordings use isolated demonstration data. They do not disclose the owner's account or pairing credentials.

Prepare the native test APK and run `../capture_features.py` with absolute ADB and FFmpeg paths. The opt-in instrumentation tests drive native animations frame by frame. Language switching uses `ProductLocaleCaptureTest` with Android's real frame clock, avoiding a conflict between system locale changes and Compose's test scheduler. Only the debug test-host Activity receives the manifest overlay; release behavior is unchanged. Recordings are cropped to the content viewport and normalized to 14-second, 60fps sources before editing.

```powershell
python prepare_film.py --ffprobe <absolute-ffprobe-path>
npm run lint
python verify_film_studio.py
./render_film.ps1 -BrowserExecutable <absolute-Chrome-Headless-Shell-path>
python verify_film.py --ffmpeg <absolute-ffmpeg-path> --ffprobe <absolute-ffprobe-path>
```

`prepare_film.py` checks successful native action tests and source-frame metadata before copying all footage into ignored `public/film/`. The film uses **Close Up** and **Tech House vibes** under their separate Mixkit music license, with a crossfade between sections. There is no narration.

The complete local MP4, poster, chapter navigation page `watch.html`, credits, and checksums are written to `.verification/gravigo-film-v3/versions/v2/` at the repository root. `verify_film.py` checks the actual file, decodes all 8,868 frames, samples every chapter and transition, and plays the complete video from beginning to end in Chromium. Native event-driven capture and authored 60fps motion graphics are different; source normalization does not create new native interaction detail. This workflow does not publish to any platform.

## Earlier short concepts

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
