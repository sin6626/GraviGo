import {
  CanvasImage,
  Interactive,
  interpolate,
  staticFile,
  useCurrentFrame,
  useVideoConfig,
} from "remotion";
import { hold } from "../design";
import { cobalt, ease, FilmBrand, FilmStage, NativePhone } from "./visuals";
import { useFilmCopy } from "./copy";

const OpeningInner = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const { english, t } = useFilmCopy();
  return (
    <FilmStage>
      <FilmBrand chapter="THE WORK GOES ON" />
      <div
        style={{
          position: "absolute",
          top: 290,
          left: 80,
          right: 80,
          fontSize: english ? 84 : 118,
          letterSpacing: -5,
          fontWeight: 650,
          lineHeight: 1.28,
          translate: interpolate(
            frame,
            [0, 1.2 * fps, 3 * fps, 5 * fps],
            ["0px 0px", "0px 0px", "0px 0px", "0px -80px"],
            { ...hold, easing: ease },
          ),
        }}
      >
        {t("电脑继续跑。")}
        <br />
        <span style={{ color: cobalt }}>{t("你接着走。")}</span>
      </div>
      <div
        style={{
          position: "absolute",
          left: 85,
          top: 645,
          fontSize: 40,
          color: "#637189",
          opacity: interpolate(frame, [0.8 * fps, 1.6 * fps], [0, 1], hold),
        }}
      >
        {t("Antigravity 的进度，手机上接着看。")}
      </div>
      <div
        style={{
          position: "absolute",
          left: 150,
          top: 845,
          width: 780,
          height: 780,
          borderRadius: "50%",
          border: "2px solid #345df429",
          scale: interpolate(frame, [0, 6 * fps], [0.65, 1.1], {
            ...hold,
            easing: ease,
            output: "perceptual-scale",
          }),
        }}
      />
      <CanvasImage
        premountFor={fps}
        src={staticFile("brand.png")}
        style={{
          position: "absolute",
          left: 340,
          top: 1000,
          width: 400,
          height: 400,
          translate: interpolate(
            frame,
            [0, 1.5 * fps, 4 * fps, 7 * fps],
            ["0px 180px", "0px 0px", "0px 0px", "280px -440px"],
            { ...hold, easing: ease },
          ),
          scale: interpolate(
            frame,
            [0, 1.5 * fps, 4 * fps, 7 * fps],
            [0.6, 1, 1, 0.3],
            { ...hold, easing: ease, output: "perceptual-scale" },
          ),
          rotate: `${Math.sin((frame / fps) * 0.6) * 8}deg`,
        }}
      />
      <NativePhone
        src="stream"
        width={520}
        style={{
          left: 280,
          top: 790,
          translate: interpolate(
            frame,
            [3.6 * fps, 6.1 * fps],
            ["0px 1200px", "0px 0px"],
            { ...hold, easing: ease },
          ),
          rotate: interpolate(
            frame,
            [3.6 * fps, 6.1 * fps, 9 * fps],
            ["14deg", "-3deg", "1deg"],
            { ...hold, easing: ease },
          ),
        }}
      />
    </FilmStage>
  );
};
export const Opening = Interactive.withSchema({
  Component: OpeningInner,
  componentName: "<Opening>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
