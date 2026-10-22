import { Interactive } from "remotion";
import { FeatureShot } from "./visuals";
const SettingsInner = () => (
  <FeatureShot
    src="settings"
    title="你的账号，"
    accent="你的阅读习惯。"
    label="电脑账号资料 · 中文 / English"
    number="13"
    lens={{ y: 840, label: "中文和 English，按习惯选择", start: 3.8, end: 8 }}
  >
    账号资料来自已配对电脑，无需另开账号。
    <br />
    随时切换中英文界面和文件换行偏好。
  </FeatureShot>
);
export const Settings = Interactive.withSchema({
  Component: SettingsInner,
  componentName: "<Settings>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
