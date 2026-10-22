import { Easing, Interactive, useVideoConfig } from "remotion";
import { TransitionSeries, linearTiming } from "@remotion/transitions";
import { slide } from "@remotion/transitions/slide";
import { FeatureShot } from "./visuals";
const ReadingInner = () => {
  const { fps } = useVideoConfig();
  return (
    <TransitionSeries>
      <TransitionSeries.Sequence
        name="代码与自动换行"
        durationInFrames={5.4 * fps}
        premountFor={fps}
      >
        <FeatureShot
          src="reading"
          title="长代码，"
          accent="也能顺着读。"
          label="原生文件阅读 · 高亮 · 自动换行"
          number="08"
          reverse
        >
          手机原生阅读关联文件。
          <br />
          按需要切换换行，不改变文件原文。
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
        name="表格公式与图表"
        durationInFrames={7.2 * fps}
        premountFor={fps}
      >
        <FeatureShot
          src="rich"
          trim={3}
          title="文字之外，"
          accent="图表也清楚。"
          label="Markdown · 表格 · 公式 · Mermaid"
          number="08"
          lens={{
            y: 400,
            label: "代码、公式与图表，原生阅读",
            start: 1.8,
            end: 5.8,
          }}
        >
          方案里的结构与关系，直接在手机上读。
          <br />
          文件支持复制、保存和系统分享。
        </FeatureShot>
      </TransitionSeries.Sequence>
    </TransitionSeries>
  );
};
export const Reading = Interactive.withSchema({
  Component: ReadingInner,
  componentName: "<Reading>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
