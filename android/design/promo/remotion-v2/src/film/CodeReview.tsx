import { Interactive } from "remotion";
import { FeatureShot } from "./visuals";
const CodeReviewInner = () => (
  <FeatureShot
    src="diff"
    title="代码改了哪里，"
    accent="看清再反馈。"
    label="代码差异 · 行级评论"
    number="07"
    dark
    reverse
    lens={{ y: 460, label: "新增与删除，一眼看清", start: 1.5, end: 5.2 }}
  >
    查看本次文件变更，定位到具体代码行。
    <br />
    评论随位置一起提交，减少来回解释。
  </FeatureShot>
);
export const CodeReview = Interactive.withSchema({
  Component: CodeReviewInner,
  componentName: "<CodeReview>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
