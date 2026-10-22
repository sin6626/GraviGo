import {
  Interactive,
  interpolate,
  useCurrentFrame,
  useVideoConfig,
} from "remotion";
import { hold } from "../design";
import {
  Caption,
  cobalt,
  ease,
  FilmBrand,
  FilmStage,
  FilmTitle,
  NativePhone,
} from "./visuals";

const ConnectionInner = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return (
    <FilmStage>
      <FilmBrand chapter="01 / CONNECT" />
      <FilmTitle
        title="电脑保持运行。"
        accent="手机接上现场。"
        label="从一条连接开始"
      />
      <svg
        width="1080"
        height="400"
        style={{ position: "absolute", top: 485, left: 0 }}
      >
        <path
          d="M180 100 C350 100 330 240 540 240 S730 100 900 100"
          fill="none"
          stroke="#c1cdee"
          strokeWidth="3"
        />
        <path
          d="M180 100 C350 100 330 240 540 240 S730 100 900 100"
          fill="none"
          stroke={cobalt}
          strokeWidth="5"
          strokeDasharray="1000"
          strokeDashoffset={interpolate(
            frame,
            [0.5 * fps, 3.5 * fps],
            [1000, 0],
            { ...hold, easing: ease },
          )}
        />
      </svg>
      <div
        style={{
          position: "absolute",
          left: 78,
          right: 78,
          top: 540,
          display: "flex",
          justifyContent: "space-between",
          fontSize: 27,
          fontWeight: 650,
        }}
      >
        <span>Antigravity</span>
        <span style={{ color: cobalt }}>Gateway</span>
        <span>GraviGo</span>
      </div>
      <div
        style={{
          position: "absolute",
          left: 100,
          top: 725,
          fontSize: 26,
          color: "#72809a",
        }}
      >
        电脑端
      </div>
      <NativePhone
        src="pairing"
        width={470}
        style={{
          left: 455,
          top: 680,
          rotate: "4deg",
          translate: interpolate(
            frame,
            [0, 1.8 * fps],
            ["480px 350px", "0px 0px"],
            { ...hold, easing: ease },
          ),
        }}
      />
      <div
        style={{
          position: "absolute",
          left: 85,
          top: 930,
          width: 350,
          fontSize: 43,
          lineHeight: 1.7,
          fontWeight: 550,
          translate: interpolate(
            frame,
            [2 * fps, 3.5 * fps],
            ["-80px 0px", "0px 0px"],
            { ...hold, easing: ease },
          ),
          opacity: interpolate(frame, [2 * fps, 3 * fps], [0, 1], hold),
        }}
      >
        启动网关
        <br />
        扫码或填码
        <br />
        <span style={{ color: cobalt }}>继续当前任务</span>
      </div>
      <div
        style={{
          position: "absolute",
          left: 85,
          top: 1400,
          width: 320,
          paddingTop: 28,
          borderTop: "1px solid #bac6e0",
          fontSize: 28,
          lineHeight: 1.6,
          color: "#6a7890",
        }}
      >
        Windows / macOS
        <br />
        提供一行安装入口
      </div>
      <Caption>
        当前连接范围为局域网。
        <br />
        电脑与网关需保持运行。
      </Caption>
    </FilmStage>
  );
};
export const Connection = Interactive.withSchema({
  Component: ConnectionInner,
  componentName: "<Connection>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
