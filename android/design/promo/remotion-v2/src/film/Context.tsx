import { Interactive } from "remotion";
import { FeatureShot } from "./visuals";
const ContextInner = () => (
  <FeatureShot
    src="context"
    title="把上下文，"
    accent="一起带过去。"
    label="@ 文件引用 · / 命令 · 规划方式"
    number="04"
    dark
    reverse
    lens={{ y: 1770, label: "文件、命令和要求放在一起", start: 2, end: 7 }}
  >
    引用工作区文件，选择电脑端提供的命令。
    <br />
    补充具体上下文，让这一次要求更清楚。
  </FeatureShot>
);
export const Context = Interactive.withSchema({
  Component: ContextInner,
  componentName: "<Context>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
