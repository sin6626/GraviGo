import {
  CanvasImage,
  Easing,
  Interactive,
  interpolate,
  staticFile,
  useCurrentFrame,
  useVideoConfig,
} from "remotion";
import {
  blue,
  Brand,
  copySchema,
  type CopyProps,
  hold,
  Scope,
  Stage,
} from "../design";

const PalmOpenInner = ({ title, subtitle, style }: CopyProps) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return (
    <Stage style={style}>
      <Brand style={{ position: "absolute", left: 84, top: 112 }} />
      <Interactive.Div
        name="开场主标题"
        style={{
          position: "absolute",
          left: 80,
          top: 300,
          fontSize: 118,
          fontWeight: 500,
          letterSpacing: -5,
          lineHeight: 1.28,
          translate: interpolate(
            frame,
            [0, 1.1 * fps],
            ["0px 48px", "0px 0px"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
        }}
      >
        {title}
        <br />
        <span style={{ color: blue, fontWeight: 650 }}>{subtitle}</span>
      </Interactive.Div>
      <Interactive.Div
        name="开场说明"
        style={{
          position: "absolute",
          left: 86,
          top: 660,
          fontSize: 45,
          fontWeight: 400,
          color: "#69717b",
          opacity: interpolate(frame, [0.4 * fps, 1.1 * fps], [0, 1], hold),
        }}
      >
        Antigravity 的进度，手机上接着看。
      </Interactive.Div>
      <Interactive.Div
        name="会话接力轨迹"
        style={{
          position: "absolute",
          left: 112,
          top: 790,
          width: 2,
          height: 790,
          background: "#d9dee9",
          scale: interpolate(frame, [0, 2 * fps], [0, 1], {
            ...hold,
            output: "perceptual-scale",
          }),
          transformOrigin: "top",
        }}
      />
      <Interactive.Div
        name="当前会话"
        style={{
          position: "absolute",
          left: 84,
          top: 830,
          width: 912,
          height: 695,
          borderRadius: 24,
          overflow: "hidden",
          background: "white",
          boxShadow: "0 32px 85px #23314e15",
          translate: interpolate(
            frame,
            [0, 1.4 * fps],
            ["0px 120px", "0px 0px"],
            { ...hold, easing: Easing.bezier(0.12, 0.85, 0.2, 1) },
          ),
        }}
      >
        <CanvasImage
          name="原生会话内容"
          premountFor={fps}
          src={staticFile("01-conversation.png")}
          style={{
            position: "absolute",
            width: 1000,
            height: 1777.78,
            left: -32,
            top: -245,
          }}
        />
      </Interactive.Div>
      <Interactive.Div
        name="接力注解"
        style={{
          position: "absolute",
          left: 86,
          top: 1590,
          fontSize: 44,
          color: blue,
          opacity: interpolate(frame, [1 * fps, 1.8 * fps], [0, 1], hold),
        }}
      >
        从电脑，接到手边。<span style={{ paddingLeft: 30 }}>↗</span>
      </Interactive.Div>
      <Scope />
    </Stage>
  );
};
export const PalmOpen = Interactive.withSchema({
  Component: PalmOpenInner,
  componentName: "<PalmOpen>",
  schema: copySchema,
  wrapInSequence: true,
});
