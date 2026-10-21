import { Video } from "@remotion/media";
import {
  CanvasImage,
  Easing,
  Interactive,
  interpolate,
  Sequence,
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

const PalmProofInner = ({ title, subtitle, style }: CopyProps) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return (
    <Stage style={style}>
      <Brand style={{ position: "absolute", left: 84, top: 112 }} />
      <Interactive.Div
        name="蓝色侧标"
        style={{
          position: "absolute",
          left: 86,
          top: 270,
          width: 72,
          height: 8,
          background: blue,
        }}
      />
      <Interactive.Div
        name="输入标题"
        durationInFrames={5 * fps}
        premountFor={fps}
        style={{
          position: "absolute",
          left: 80,
          top: 330,
          fontSize: 112,
          fontWeight: 550,
          lineHeight: 1.23,
          letterSpacing: -5,
          translate: interpolate(
            frame,
            [0, 0.8 * fps],
            ["0px 55px", "0px 0px"],
            { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
          ),
        }}
      >
        {title}
        <br />
        <span style={{ color: blue }}>继续推进。</span>
      </Interactive.Div>
      <Interactive.Div
        name="审阅标题"
        from={5 * fps}
        premountFor={fps}
        style={{
          position: "absolute",
          left: 80,
          top: 330,
          fontSize: 112,
          fontWeight: 550,
          lineHeight: 1.23,
          letterSpacing: -5,
        }}
      >
        {subtitle}
        <br />
        <span style={{ color: blue }}>先审再继续。</span>
      </Interactive.Div>
      <Sequence
        name="原生输入演示"
        durationInFrames={5 * fps}
        premountFor={fps}
      >
        <Interactive.Div
          name="输入近景"
          style={{
            position: "absolute",
            left: 68,
            top: 960,
            width: 944,
            height: 360,
            borderRadius: 28,
            overflow: "hidden",
            background: "white",
            boxShadow: "0 24px 90px #23314e12",
          }}
        >
          <Video
            name="补充要求的原生操作"
            premountFor={fps}
            src={staticFile("native-demo.mp4")}
            trimBefore={2 * fps}
            muted
            style={{
              position: "absolute",
              width: 1010,
              height: 1795.56,
              left: -25,
              top: -1500,
            }}
          />
        </Interactive.Div>
        <Interactive.Div
          name="输入说明"
          style={{
            position: "absolute",
            left: 86,
            top: 740,
            fontSize: 46,
            color: "#69717b",
          }}
        >
          补充要求，送回电脑上的当前会话。
        </Interactive.Div>
      </Sequence>
      <Sequence name="原生方案阅读" from={5 * fps} premountFor={fps}>
        <Interactive.Div
          name="方案阅读近景"
          style={{
            position: "absolute",
            left: 68,
            top: 740,
            width: 944,
            height: 900,
            borderRadius: 28,
            overflow: "hidden",
            background: "white",
            boxShadow: "0 24px 90px #23314e12",
            translate: interpolate(
              frame,
              [5 * fps, 5.8 * fps],
              ["0px 100px", "0px 0px"],
              { ...hold, easing: Easing.bezier(0.16, 1, 0.3, 1) },
            ),
          }}
        >
          <CanvasImage
            name="真实方案预览"
            premountFor={fps}
            src={staticFile("04-plan.png")}
            style={{
              position: "absolute",
              width: 1000,
              height: 1777.78,
              left: -28,
              top: -310,
            }}
          />
        </Interactive.Div>
      </Sequence>
      <Scope />
    </Stage>
  );
};
export const PalmProof = Interactive.withSchema({
  Component: PalmProofInner,
  componentName: "<PalmProof>",
  schema: copySchema,
  wrapInSequence: true,
});
