import { Interactive } from "remotion";
import { FeatureShot } from "./visuals";
const ModelsInner = () => (
  <FeatureShot
    src="models"
    title="用哪个模型，"
    accent="还剩多少额度。"
    label="模型选择 · 额度与恢复时间"
    number="11"
    lens={{ y: 1180, label: "额度与恢复时间，一眼可查", start: 5, end: 9 }}
  >
    按当前任务选择可用模型。
    <br />
    查看共享额度和恢复时间，再安排下一步。
  </FeatureShot>
);
export const Models = Interactive.withSchema({
  Component: ModelsInner,
  componentName: "<Models>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
