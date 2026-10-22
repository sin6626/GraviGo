import { Interactive } from "remotion";
import { FeatureShot } from "./visuals";
const PlanReviewInner = () => (
  <FeatureShot
    src="plan"
    title="方案出来，"
    accent="先审再继续。"
    label="原生方案阅读 · 选段评论"
    number="06"
    lens={{ y: 1770, label: "反馈和原文位置，一起交回", start: 4, end: 8 }}
  >
    阅读实现方案，选中需要修改的段落。
    <br />
    把反馈交回电脑，再决定是否继续执行。
  </FeatureShot>
);
export const PlanReview = Interactive.withSchema({
  Component: PlanReviewInner,
  componentName: "<PlanReview>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
