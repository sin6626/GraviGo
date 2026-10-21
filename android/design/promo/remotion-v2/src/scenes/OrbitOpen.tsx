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

const OrbitOpenInner = ({ title, subtitle, style }: CopyProps) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return (
    <Stage style={{ background: "#080a10", color: "#f4f5fa", ...style }}>
      <Interactive.Div
        name="空间照明"
        style={{
          position: "absolute",
          left: -150,
          right: -150,
          top: 500,
          height: 1250,
          background: "radial-gradient(ellipse, #344a773c 0%, transparent 65%)",
          scale: interpolate(frame, [0, 5 * fps], [1.1, 1.35], {
            ...hold,
            output: "perceptual-scale",
          }),
        }}
      />
      <Brand
        color="#c6ccda"
        style={{ position: "absolute", left: 84, top: 112 }}
      />
      <Interactive.Div
        name="品牌主张"
        style={{
          position: "absolute",
          left: 82,
          top: 320,
          fontSize: 142,
          fontWeight: 350,
          lineHeight: 1.18,
          letterSpacing: -8,
          translate: interpolate(
            frame,
            [0, 1.5 * fps],
            ["0px 65px", "0px 0px"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
          opacity: interpolate(frame, [0, 1.1 * fps], [0, 1], hold),
        }}
      >
        {title}
        <br />
        <span style={{ color: "#8da9ee" }}>{subtitle}</span>
      </Interactive.Div>
      <Interactive.Div
        name="轨道与晶核"
        style={{
          position: "absolute",
          left: 70,
          top: 790,
          width: 940,
          height: 780,
          perspective: 1800,
          translate: interpolate(
            frame,
            [0, 5 * fps],
            ["0px 80px", "0px -40px"],
            { ...hold, easing: Easing.bezier(0.2, 0.6, 0.3, 1) },
          ),
        }}
      >
        <Interactive.Div
          name="主轨道"
          style={{
            position: "absolute",
            left: 25,
            top: 135,
            width: 900,
            height: 410,
            border: "2px solid #8298be4d",
            borderRadius: "50%",
            rotate: interpolate(
              frame,
              [0, 5 * fps],
              ["-28deg", "-10deg"],
              hold,
            ),
          }}
        />
        <Interactive.Div
          name="背面轨道"
          style={{
            position: "absolute",
            left: 160,
            top: -40,
            width: 610,
            height: 850,
            border: "1px solid #aebed32e",
            borderRadius: "50%",
            rotate: interpolate(frame, [0, 5 * fps], ["38deg", "16deg"], hold),
          }}
        />
        <CanvasImage
          name="悬浮品牌晶核"
          premountFor={fps}
          src={staticFile("brand.png")}
          style={{
            position: "absolute",
            left: 30,
            top: -120,
            width: 900,
            height: 900,
            filter: "drop-shadow(0 60px 90px #7395d926)",
            scale: interpolate(frame, [0, 5 * fps], [0.78, 1.04], {
              ...hold,
              output: "perceptual-scale",
            }),
            rotate: interpolate(frame, [0, 5 * fps], ["-12deg", "7deg"], hold),
          }}
        />
      </Interactive.Div>
      <Interactive.Div
        name="空间说明"
        style={{
          position: "absolute",
          left: 84,
          top: 1600,
          fontSize: 46,
          color: "#8995ad",
        }}
      >
        Antigravity，从桌面延伸到掌心。
      </Interactive.Div>
      <Scope color="#6b768c" />
    </Stage>
  );
};
export const OrbitOpen = Interactive.withSchema({
  Component: OrbitOpenInner,
  componentName: "<OrbitOpen>",
  schema: copySchema,
  wrapInSequence: true,
});
