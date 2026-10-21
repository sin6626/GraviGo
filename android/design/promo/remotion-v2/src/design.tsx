import type { CSSProperties, ReactNode } from "react";
import { loadFont } from "@remotion/fonts";
import { CanvasImage, Interactive, staticFile, useVideoConfig } from "remotion";

await Promise.all([
  loadFont({
    family: "Noto Sans SC",
    url: staticFile("NotoSansSC.ttf"),
    weight: "100 900",
  }),
  loadFont({
    family: "Manrope",
    url: staticFile("Manrope.ttf"),
    weight: "200 800",
  }),
]);

export const font = 'Manrope, "Noto Sans SC", sans-serif';
export const paper = "#f7f6f2";
export const ink = "#171b21";
export const blue = "#2155f5";
export const lime = "#ddff57";
export const hold = {
  extrapolateLeft: "clamp",
  extrapolateRight: "clamp",
} as const;
export type CopyProps = {
  title: string;
  subtitle: string;
  style?: CSSProperties;
};
export const copySchema = {
  title: { type: "text-content", default: "", description: "主标题" },
  subtitle: { type: "text-content", default: "", description: "副标题" },
} as const;

export const Stage = ({
  children,
  style,
}: {
  children: ReactNode;
  style?: CSSProperties;
}) => (
  <Interactive.Div
    style={{
      position: "absolute",
      inset: 0,
      overflow: "hidden",
      fontFamily: font,
      background: paper,
      color: ink,
      ...style,
    }}
  >
    {children}
  </Interactive.Div>
);

export const Brand = ({
  color = ink,
  style,
}: {
  color?: string;
  style?: CSSProperties;
}) => {
  const { fps } = useVideoConfig();
  return (
    <Interactive.Div
      name="GraviGo"
      style={{
        display: "flex",
        alignItems: "center",
        gap: 16,
        fontSize: 45,
        fontWeight: 650,
        letterSpacing: -1.6,
        ...style,
      }}
    >
      <CanvasImage
        name="品牌图标"
        premountFor={fps}
        src={staticFile("brand.png")}
        style={{ width: 64, height: 64 }}
      />
      <span style={{ color }}>GraviGo</span>
    </Interactive.Div>
  );
};

export const Scope = ({ color = "#656d77" }: { color?: string }) => (
  <Interactive.Div
    name="连接范围"
    style={{
      position: "absolute",
      left: 84,
      right: 84,
      bottom: 170,
      color,
      fontSize: 26,
      fontWeight: 450,
    }}
  >
    局域网连接 · 电脑与网关需保持运行
  </Interactive.Div>
);
