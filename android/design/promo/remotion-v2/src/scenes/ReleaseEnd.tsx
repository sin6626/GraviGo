import type { CSSProperties } from "react";
import {
  CanvasImage,
  Easing,
  Interactive,
  interpolate,
  staticFile,
  useCurrentFrame,
  useVideoConfig,
} from "remotion";
import { blue, font, hold, ink, lime, paper, Scope, Stage } from "../design";

type EndProps = { kind: "palm" | "orbit" | "editorial"; style?: CSSProperties };
const ReleaseEndInner = ({ kind, style }: EndProps) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const dark = kind === "orbit";
  return (
    <Stage
      style={{
        background: dark ? "#080a10" : kind === "editorial" ? lime : blue,
        color: dark || kind === "palm" ? paper : ink,
        ...style,
      }}
    >
      <CanvasImage
        name="最终品牌标志"
        premountFor={fps}
        src={staticFile("brand.png")}
        style={{
          position: "absolute",
          left: 76,
          top: 250,
          width: 240,
          height: 240,
          translate: interpolate(
            frame,
            [0, 1.1 * fps],
            ["0px 80px", "0px 0px"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
        }}
      />
      <Interactive.Div
        name="产品名称"
        style={{
          position: "absolute",
          left: 76,
          top: 610,
          fontFamily: font,
          fontSize: 170,
          fontWeight: kind === "editorial" ? 800 : 500,
          letterSpacing: -10,
          translate: interpolate(
            frame,
            [0, 1.1 * fps],
            ["0px 120px", "0px 0px"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
        }}
      >
        GraviGo
        <span
          style={{
            fontSize: 48,
            verticalAlign: "top",
            paddingLeft: 18,
            letterSpacing: -1,
          }}
        >
          1.0.0
        </span>
      </Interactive.Div>
      <Interactive.Div
        name="品牌标语"
        style={{
          position: "absolute",
          left: 84,
          top: 1000,
          fontSize: 66,
          lineHeight: 1.5,
          fontWeight: 400,
        }}
      >
        把Antigravity，
        <br />
        带在身边
      </Interactive.Div>
      <Interactive.Div
        name="下载入口"
        style={{
          position: "absolute",
          left: 84,
          top: 1450,
          width: 912,
          borderTop: dark ? "1px solid #434d62" : "1px solid #ffffff66",
          paddingTop: 40,
          fontSize: 44,
        }}
      >
        github.com/sin6626/GraviGo <span style={{ float: "right" }}>↗</span>
      </Interactive.Div>
      <Scope color={dark || kind === "palm" ? "#d1d8e7" : "#52602d"} />
    </Stage>
  );
};
export const ReleaseEnd = Interactive.withSchema({
  Component: ReleaseEndInner,
  componentName: "<ReleaseEnd>",
  schema: {
    kind: {
      type: "enum",
      default: "palm",
      description: "落版风格",
      variants: { palm: {}, orbit: {}, editorial: {} },
    },
  },
  wrapInSequence: true,
});
