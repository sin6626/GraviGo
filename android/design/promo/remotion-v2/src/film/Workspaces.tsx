import { Interactive } from "remotion";
import { FeatureShot } from "./visuals";
const WorkspacesInner = () => (
  <FeatureShot
    src="workspace"
    title="项目和会话，"
    accent="从原处接上。"
    label="工作区 · 项目会话 · 新建任务"
    number="02"
    reverse
  >
    进入电脑上的工作区，选择已有会话，
    <br />
    也可以在项目里开启一个新任务。
  </FeatureShot>
);
export const Workspaces = Interactive.withSchema({
  Component: WorkspacesInner,
  componentName: "<Workspaces>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
