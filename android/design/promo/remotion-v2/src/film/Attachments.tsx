import { Easing, Interactive, useVideoConfig } from "remotion";
import { TransitionSeries, linearTiming } from "@remotion/transitions";
import { slide } from "@remotion/transitions/slide";
import { FeatureShot } from "./visuals";
const AttachmentsInner = () => {
  const { fps } = useVideoConfig();
  return (
    <TransitionSeries>
      <TransitionSeries.Sequence
        name="图片与文件附件"
        durationInFrames={6.4 * fps}
        premountFor={fps}
      >
        <FeatureShot
          src="attachments"
          title="图、文件，"
          accent="都能一起发。"
          label="附件 · 草稿保留"
          number="05"
          lens={{ y: 1500, label: "原生附件输入区", start: 1.4, end: 5.5 }}
        >
          把参考图和文件加入要求。
          <br />
          草稿、附件与当前会话保持在一起。
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
        name="语音转写界面"
        durationInFrames={6.2 * fps}
        premountFor={fps}
      >
        <FeatureShot
          src="voice"
          title="不方便打字，"
          accent="就直接说出来。"
          label="语音输入 · 转成可编辑草稿"
          number="05"
          reverse
          lens={{
            y: 1820,
            label: "语音转成草稿，还能继续编辑",
            start: 1,
            end: 5,
          }}
        >
          转写后可以继续编辑。
          <br />
          此能力取决于电脑端账号权限。
        </FeatureShot>
      </TransitionSeries.Sequence>
    </TransitionSeries>
  );
};
export const Attachments = Interactive.withSchema({
  Component: AttachmentsInner,
  componentName: "<Attachments>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
