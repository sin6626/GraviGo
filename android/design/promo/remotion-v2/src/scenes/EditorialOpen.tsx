import {
  Easing,
  Interactive,
  interpolate,
  useCurrentFrame,
  useVideoConfig,
} from "remotion";
import {
  Brand,
  copySchema,
  type CopyProps,
  hold,
  ink,
  lime,
  Scope,
  Stage,
} from "../design";

const EditorialOpenInner = ({ title, subtitle, style }: CopyProps) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return (
    <Stage style={{ background: lime, ...style }}>
      <Brand style={{ position: "absolute", left: 84, top: 112 }} />
      <Interactive.Div
        name="第一问"
        style={{
          position: "absolute",
          left: 104,
          top: 350,
          fontSize: 164,
          lineHeight: 1.2,
          fontWeight: 850,
          letterSpacing: -8,
          translate: interpolate(
            frame,
            [0, 0.6 * fps],
            ["-24px 0px", "0px 0px"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
        }}
      >
        {title}
      </Interactive.Div>
      <Interactive.Div
        name="第二问"
        style={{
          position: "absolute",
          left: 78,
          top: 580,
          fontSize: 144,
          lineHeight: 1.3,
          fontWeight: 850,
          letterSpacing: -8,
          translate: interpolate(
            frame,
            [0.3 * fps, 1 * fps],
            ["48px 0px", "0px 0px"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
          opacity: interpolate(frame, [0.3 * fps, 0.5 * fps], [0, 1], hold),
        }}
      >
        {subtitle}
      </Interactive.Div>
      <Interactive.Div
        name="横向切带"
        style={{
          position: "absolute",
          left: -130,
          top: 1060,
          width: 1360,
          padding: "60px 220px",
          color: lime,
          background: ink,
          fontSize: 84,
          fontWeight: 700,
          rotate: interpolate(
            frame,
            [0.7 * fps, 1.4 * fps],
            ["-14deg", "-7deg"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
          translate: interpolate(
            frame,
            [0.7 * fps, 1.4 * fps],
            ["0px 220px", "0px 0px"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
        }}
      >
        离开工位，接上进度。
      </Interactive.Div>
      <Interactive.Div
        name="产品说明"
        style={{
          position: "absolute",
          left: 86,
          top: 1460,
          width: 850,
          fontSize: 48,
          lineHeight: 1.6,
          fontWeight: 500,
          opacity: interpolate(frame, [1.2 * fps, 1.6 * fps], [0, 1], hold),
        }}
      >
        用 Android 手机，
        <br />
        跟进电脑上的 Antigravity。
      </Interactive.Div>
      <Scope color="#59652b" />
    </Stage>
  );
};
export const EditorialOpen = Interactive.withSchema({
  Component: EditorialOpenInner,
  componentName: "<EditorialOpen>",
  schema: copySchema,
  wrapInSequence: true,
});
