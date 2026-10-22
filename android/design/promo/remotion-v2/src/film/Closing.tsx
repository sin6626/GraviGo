import {
  CanvasImage,
  Interactive,
  interpolate,
  staticFile,
  useCurrentFrame,
  useVideoConfig,
} from "remotion";
import { hold } from "../design";
import { cobalt, ease, FilmBrand, FilmStage } from "./visuals";

const ClosingInner = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return (
    <FilmStage dark>
      <FilmBrand dark chapter="KEEP THE WORK MOVING" />
      <CanvasImage
        premountFor={fps}
        src={staticFile("brand.png")}
        style={{
          position: "absolute",
          left: 330,
          top: 300,
          width: 420,
          height: 420,
          translate: interpolate(
            frame,
            [0, 2 * fps],
            ["0px 180px", "0px 0px"],
            { ...hold, easing: ease },
          ),
          rotate: `${Math.sin((frame / fps) * 0.5) * 5}deg`,
        }}
      />
      <div
        style={{
          position: "absolute",
          left: 80,
          right: 80,
          top: 790,
          textAlign: "center",
          fontSize: 155,
          fontWeight: 550,
          letterSpacing: -8,
        }}
      >
        GraviGo
        <span style={{ fontSize: 32, letterSpacing: 0, paddingLeft: 18 }}>
          1.0.0
        </span>
      </div>
      <div
        style={{
          position: "absolute",
          left: 80,
          right: 80,
          top: 1060,
          textAlign: "center",
          fontSize: 66,
          lineHeight: 1.6,
        }}
      >
        把Antigravity，带在身边
      </div>
      <div
        style={{
          position: "absolute",
          top: 1285,
          left: 130,
          right: 130,
          border: "1px solid #55709b",
          borderRadius: 28,
          background: cobalt,
          padding: "32px 24px",
          textAlign: "center",
          fontSize: 40,
        }}
      >
        github.com/sin6626/GraviGo ↗
      </div>
      <div
        style={{
          position: "absolute",
          left: 90,
          right: 90,
          top: 1480,
          textAlign: "center",
          fontSize: 32,
          lineHeight: 1.75,
          color: "#b0c1db",
        }}
      >
        独立社区项目 · Android 原生界面
        <br />
        Windows / macOS 网关 · AGPL v3 开源
      </div>
      <div
        style={{
          position: "absolute",
          left: 90,
          right: 90,
          top: 1680,
          textAlign: "center",
          fontSize: 26,
          lineHeight: 1.7,
          color: "#8da1bf",
          opacity: interpolate(frame, [2 * fps, 3 * fps], [0, 1], hold),
        }}
      >
        基于九哥 GHSaiMo 的 antigravity-mobile
        <br />
        演示采用隔离示例数据 · 局域网连接
        <br />
        电脑与网关需保持运行
      </div>
    </FilmStage>
  );
};
export const Closing = Interactive.withSchema({
  Component: ClosingInner,
  componentName: "<Closing>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
