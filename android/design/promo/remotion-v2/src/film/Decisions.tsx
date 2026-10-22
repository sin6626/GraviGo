import { Easing, Interactive, useVideoConfig } from "remotion";
import { TransitionSeries, linearTiming } from "@remotion/transitions";
import { slide } from "@remotion/transitions/slide";
import { FeatureShot } from "./visuals";
const DecisionsInner = () => {
  const { fps } = useVideoConfig();
  return (
    <TransitionSeries>
      <TransitionSeries.Sequence
        name="交互选择"
        durationInFrames={6.4 * fps}
        premountFor={fps}
      >
        <FeatureShot
          src="interaction"
          title="需要你决定时，"
          accent="手机上直接回应。"
          label="交互选项 · 补充说明"
          number="10"
          dark
          reverse
        >
          选择要保留的体验，补上自己的要求。
          <br />
          决策继续留在当前工作流里。
        </FeatureShot>
      </TransitionSeries.Sequence>
      <TransitionSeries.Transition
        presentation={slide({ direction: "from-right" })}
        timing={linearTiming({
          durationInFrames: 0.6 * fps,
          easing: Easing.bezier(0.65, 0, 0.35, 1),
        })}
      />
      <TransitionSeries.Sequence
        name="回退预览"
        durationInFrames={4.2 * fps}
        premountFor={fps}
      >
        <FeatureShot
          src="revert"
          title="想回到前一步，"
          accent="先确认回退范围。"
          label="消息回退 · 文件改动选项"
          number="10"
          dark
        >
          回退之前，先确认是否恢复文件。
          <br />
          会话与工作区改动，由你选择。
        </FeatureShot>
      </TransitionSeries.Sequence>
    </TransitionSeries>
  );
};
export const Decisions = Interactive.withSchema({
  Component: DecisionsInner,
  componentName: "<Decisions>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
