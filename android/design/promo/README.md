# GraviGo product promo previews

The current delivery contains three Chinese, 15-second concepts for Douyin and Xiaohongshu. All use 1080×1920 video at 30fps, H.264/AAC, the approved GraviGo icon, original synthesized music, and native Android UI rendered with isolated demonstration data.

| Concept | Treatment |
| --- | --- |
| A — hybrid | A practical scenario followed by native operation and restrained product motion |
| B — hero | Brand icon, large type, and a paced product reveal |
| C — demo | A light background, numbered steps, and close views of the native interaction |

Open `.verification/gravigo-product-promo/delivery/compare.html` from the project root to play them side by side. MP4s, posters, and `manifest.json` are in that same directory. Generated assets and media remain Git-ignored. The full 60–75 second film and voiceover are not part of these style previews.

## Capture

`ProductPromoCaptureTest` is opt-in. It runs only when `promoRun` is provided, uses the existing local fake gateway, and namespaces preferences and cache files. It never connects to the owner's paired desktop. The capture controller starts Android screen recording only after the isolated demo is ready. Four screenshots and a cropped native recording become the shared footage.

Build the instrumentation APK using the existing project Gradle cache and JDK, then run:

```powershell
python android/design/promo/capture_native.py --adb <absolute-adb-path> --ffmpeg <absolute-ffmpeg-path>
```

Use an FFmpeg build with `libx264` and AAC. The current render uses a project-local executable from imageio-ffmpeg 0.6.0. Existing Fingerling tools are not modified.

## Render and verify

The installed Python Playwright package and its Chromium render deterministic Canvas frames. Browser profiles and temporary files stay inside the project. The local asset server supports HTTP byte ranges because paused MP4 seeking otherwise sticks on the first frame in Chromium. Every requested media time is checked.

```powershell
python android/design/promo/render_previews.py --ffmpeg <absolute-ffmpeg-path> --stills
python android/design/promo/render_previews.py --ffmpeg <absolute-ffmpeg-path>
python android/design/promo/verify_previews.py --ffmpeg <absolute-ffmpeg-path> --ffprobe <absolute-ffprobe-path>
```

`--variant A`, `B`, or `C` rerenders one concept. The prepared assets include the Noto Sans CJK SC Regular font and its Adobe copyright / SIL Open Font License. Keep those notices with the production assets. Initial font preparation reuses the verified local font in `.verification/RaTeX/scripts/fonts/`; subsequent renders reuse the copied asset.

Verification reads each actual MP4, checks its checksum, resolution, codec, frame count, duration, audio peak, black frames, seeking, and playback to the end in Chromium. Rendering also performs a full FFmpeg decode. Browsers, encoders, and the local HTTP server close at the end of each script.

The demo preserves the product's current scope: desktop and gateway remain running, and the verified 1.0.0 connection baseline is LAN. It does not show private account details, access tokens, private code, or unverified public-network / Mac desktop claims.
