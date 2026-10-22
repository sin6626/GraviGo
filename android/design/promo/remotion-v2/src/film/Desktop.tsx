import { Interactive } from "remotion";
import { FeatureShot } from "./visuals";
const DesktopInner = () => (
  <FeatureShot
    src="desktop"
    title="电脑端的状态，"
    accent="也有地方可查。"
    label="权限 · 审批策略 · MCP连接"
    number="12"
    reverse
  >
    查看电脑端有效权限与工具连接状态。
    <br />
    需要认证的服务，回到电脑端处理。
  </FeatureShot>
);
export const Desktop = Interactive.withSchema({
  Component: DesktopInner,
  componentName: "<Desktop>",
  schema: {},
  wrapInSequence: true,
  layout: "absolute-fill",
});
