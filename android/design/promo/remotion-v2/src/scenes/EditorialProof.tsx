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
  Brand,
  copySchema,
  type CopyProps,
  hold,
  ink,
  lime,
  Scope,
  Stage,
} from "../design";

const EditorialProofInner = ({ title, subtitle, style }: CopyProps) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return (
    <Stage style={{ background: "#f2f2ed", ...style }}>
      <Interactive.Div
        name="上方色面"
        style={{
          position: "absolute",
          left: 0,
          top: 0,
          right: 0,
          height: 535,
          background: lime,
        }}
      />
      <Brand style={{ position: "absolute", left: 84, top: 112 }} />
      <Interactive.Div
        name="进度标题"
        durationInFrames={3.5 * fps}
        premountFor={fps}
        style={{
          position: "absolute",
          left: 78,
          top: 290,
          fontSize: 132,
          fontWeight: 800,
          letterSpacing: -7,
        }}
      >
        先看进度。
      </Interactive.Div>
      <Interactive.Div
        name="要求标题"
        from={3.5 * fps}
        durationInFrames={3 * fps}
        premountFor={fps}
        style={{
          position: "absolute",
          left: 78,
          top: 290,
          fontSize: 132,
          fontWeight: 800,
          letterSpacing: -7,
        }}
      >
        {title}
      </Interactive.Div>
      <Interactive.Div
        name="方案标题"
        from={6.5 * fps}
        premountFor={fps}
        style={{
          position: "absolute",
          left: 78,
          top: 290,
          fontSize: 132,
          fontWeight: 800,
          letterSpacing: -7,
        }}
      >
        {subtitle}
      </Interactive.Div>
      <Sequence
        name="会话到输入的原生操作"
        durationInFrames={6.5 * fps}
        premountFor={fps}
      >
        <Interactive.Div
          name="功能操作近景"
          style={{
            position: "absolute",
            left: 60,
            top: 620,
            width: 960,
            height: 980,
            background: "#fff",
            overflow: "hidden",
            border: `2px solid ${ink}`,
          }}
        >
          <Video
            name="原生任务与输入"
            premountFor={fps}
            src={staticFile("native-demo.mp4")}
            muted
            style={{
              position: "absolute",
              left: -20,
              top: interpolate(
                frame,
                [0, 3 * fps, 4.1 * fps],
                [-240, -240, -830],
                { ...hold, easing: Easing.bezier(0.2, 0.8, 0.2, 1) },
              ),
              width: 1010,
              height: 1795.56,
            }}
          />
        </Interactive.Div>
      </Sequence>
      <Interactive.Div
        name="方案内容近景"
        from={6.5 * fps}
        premountFor={fps}
        style={{
          position: "absolute",
          left: 60,
          top: 620,
          width: 960,
          height: 940,
          background: "#fff",
          overflow: "hidden",
          border: `2px solid ${ink}`,
        }}
      >
        <CanvasImage
          name="原生方案正文"
          premountFor={fps}
          src={staticFile("04-plan.png")}
          style={{
            position: "absolute",
            left: -20,
            top: -320,
            width: 1010,
            height: 1795.56,
          }}
        />
      </Interactive.Div>
      <Scope color="#68705d" />
    </Stage>
  );
};
export const EditorialProof = Interactive.withSchema({
  Component: EditorialProofInner,
  componentName: "<EditorialProof>",
  schema: copySchema,
  wrapInSequence: true,
});
