# GraviGo — Remotion product concepts

Three independent 1080×1920, 30fps, 18-second visual studies for Douyin and Xiaohongshu:

| Composition | Direction |
| --- | --- |
| `A-PalmRelay` | Warm paper, cobalt blue, readable native UI, and directional handoff transitions |
| `B-WeightlessOrbit` | Dark space, restrained orbit imagery, layered perspective, and a floating brand mark |
| `C-StopWaiting` | Acid lime, bold editorial typography, match cuts, and direct native-operation proof |

These replace the first-round concepts. They are silent Studio drafts for reviewing art direction and motion; no final MP4 or soundtrack is included in this round. See [the Chinese director's plan](../ART-DIRECTION-V2.zh-CN.md).

## Project setup

```powershell
npm ci
npm run assets
npm run dev
```

Open [Remotion Studio](http://localhost:4317). The three main compositions appear first; `Scenes` contains connected compositions for independently editing each scene. Main registrations, scene instances, timing, copy, and keyframes are authored as separate editable JSX nodes.

`prepare-assets.mjs` copies approved branding and isolated Android demo footage from `.verification/gravigo-product-promo/assets/` into Git-ignored `public/`. It caches variable Noto Sans SC and Manrope fonts from Google Fonts and preserves their original SIL OFL notices. Regenerate missing native recordings using the opt-in capture workflow in the parent directory.

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
