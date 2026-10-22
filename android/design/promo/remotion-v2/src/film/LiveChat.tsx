import { Interactive } from "remotion";
import { FeatureShot } from "./visuals";
const LiveChatInner = () => (
  <FeatureShot
    src="stream"
    title="想到什么，"
    accent="就补充一句。"
    label="发送要求 · 跟进流式回复"
    number="03"
    lens={{ y: 700, label: "要求送达，回复接着来", start: 4, end: 8 }}
  >
    补充要求发送到电脑上的当前会话。
    <br />
    回复逐步出现，阅读和执行都接着走。
  </FeatureShot>
);
export const LiveChat = Interactive.withSchema({
  Component: LiveChatInner,
  componentName: "<LiveChat>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
