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
  Brand,
  copySchema,
  type CopyProps,
  hold,
  Scope,
  Stage,
} from "../design";

const OrbitProofInner = ({ title, subtitle, style }: CopyProps) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return (
    <Stage
      style={{
        background: "#080a10",
        color: "#f4f5fa",
        perspective: 2000,
        ...style,
      }}
    >
      <Interactive.Div
        name="空间光"
        style={{
          position: "absolute",
          inset: 0,
          background:
            "radial-gradient(ellipse at 55% 65%, #354c7638, transparent 65%)",
        }}
      />
      <Brand
        color="#c6ccda"
        style={{ position: "absolute", left: 84, top: 112 }}
      />
      <Interactive.Div
        name="会话主张"
        from={0}
        durationInFrames={4.8 * fps}
        premountFor={fps}
        style={{
          position: "absolute",
          left: 86,
          top: 310,
          fontSize: 112,
          fontWeight: 400,
          letterSpacing: -5,
          lineHeight: 1.3,
        }}
      >
        {title}
        <br />
        <span style={{ color: "#879dc8" }}>不必守在桌前。</span>
      </Interactive.Div>
      <Interactive.Div
        name="方案主张"
        from={4.8 * fps}
        premountFor={fps}
        style={{
          position: "absolute",
          left: 86,
          top: 310,
          fontSize: 112,
          fontWeight: 400,
          letterSpacing: -5,
          lineHeight: 1.3,
        }}
      >
        {subtitle}
        <br />
        <span style={{ color: "#879dc8" }}>由你掌握。</span>
      </Interactive.Div>
      <Interactive.Div
        name="空间会话层"
        durationInFrames={5 * fps}
        premountFor={fps}
        style={{
          position: "absolute",
          left: 80,
          top: 760,
          width: 920,
          height: 820,
          borderRadius: 22,
          overflow: "hidden",
          background: "#fff",
          boxShadow: "0 85px 160px #0009",
          rotate: interpolate(
            frame,
            [0, 4.8 * fps],
            ["0 1 0 -22deg", "0 1 0 3deg"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
          translate: interpolate(
            frame,
            [0, 4.8 * fps],
            ["100px 80px -150px", "0px 0px 0px"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
        }}
      >
        <CanvasImage
          name="原生回复"
          premountFor={fps}
          src={staticFile("01-conversation.png")}
          style={{
            position: "absolute",
            width: 1000,
            height: 1777.78,
            left: -20,
            top: -230,
          }}
        />
      </Interactive.Div>
      <Interactive.Div
        name="空间方案层"
        from={4.8 * fps}
        premountFor={fps}
        style={{
          position: "absolute",
          left: 80,
          top: 740,
          width: 920,
          height: 900,
          borderRadius: 22,
          overflow: "hidden",
          background: "#fff",
          boxShadow: "0 70px 130px #0009",
          rotate: interpolate(
            frame,
            [4.8 * fps, 6.3 * fps, 10 * fps],
            ["0 1 0 20deg", "0 1 0 -3deg", "0 1 0 0deg"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
          translate: interpolate(
            frame,
            [4.8 * fps, 6.3 * fps],
            ["-100px 140px -100px", "0px 0px 0px"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
        }}
      >
        <CanvasImage
          name="原生方案内容"
          premountFor={fps}
          src={staticFile("04-plan.png")}
          style={{
            position: "absolute",
            width: 980,
            height: 1742.22,
            left: -20,
            top: -310,
          }}
        />
      </Interactive.Div>
      <Scope color="#6b768c" />
    </Stage>
  );
};
export const OrbitProof = Interactive.withSchema({
  Component: OrbitProofInner,
  componentName: "<OrbitProof>",
  schema: copySchema,
  wrapInSequence: true,
});
