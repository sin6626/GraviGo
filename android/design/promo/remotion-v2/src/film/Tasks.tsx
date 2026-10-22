import { Interactive } from "remotion";
import { FeatureShot } from "./visuals";
const TasksInner = () => (
  <FeatureShot
    src="tasks"
    title="任务在运行，"
    accent="进度握在手里。"
    label="后台任务 · 排队要求 · 即时补充"
    number="09"
    lens={{ y: 1590, label: "下一条要求，排队或立即发送", start: 2, end: 7 }}
  >
    查看正在执行的任务和排队消息。
    <br />
    需要时立即补充，或停止指定后台任务。
  </FeatureShot>
);
export const Tasks = Interactive.withSchema({
  Component: TasksInner,
  componentName: "<Tasks>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
