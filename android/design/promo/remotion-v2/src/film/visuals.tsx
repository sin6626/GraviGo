import type { CSSProperties, ReactNode } from "react";
import { createContext, useContext } from "react";
import { Video } from "@remotion/media";
import {
  AbsoluteFill,
  CanvasImage,
  Easing,
  Interactive,
  interpolate,
  staticFile,
  useCurrentFrame,
  useVideoConfig,
} from "remotion";
import type { TransitionPresentationComponentProps } from "@remotion/transitions";
import { font, hold } from "../design";

export const cream = "#f5f3ee";
export const DetailLensesContext = createContext(true);
export const navy = "#101e36";
export const cobalt = "#345df4";
export const ease = Easing.bezier(0.22, 1, 0.36, 1);
export const glide = Easing.bezier(0.65, 0, 0.35, 1);

export const FilmStage = ({
  children,
  dark = false,
  style,
}: {
  children: ReactNode;
  dark?: boolean;
  style?: CSSProperties;
}) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return (
    <AbsoluteFill
      style={{
        background: dark ? navy : cream,
        color: dark ? cream : navy,
        fontFamily: font,
        overflow: "hidden",
        ...style,
      }}
    >
      <div
        style={{
          position: "absolute",
          left: -390,
          top: 520,
          width: 1850,
          height: 950,
          borderRadius: "50%",
          border: `2px solid ${dark ? "#6d8dce30" : "#345df41a"}`,
          rotate: `${-28 + (frame / fps) * 0.55}deg`,
        }}
      />
      <div
        style={{
          position: "absolute",
          left: -300,
          top: 650,
          width: 1660,
          height: 900,
          borderRadius: "50%",
          border: `1px solid ${dark ? "#6d8dce20" : "#345df416"}`,
          rotate: `${26 - (frame / fps) * 0.35}deg`,
        }}
      />
      <div
        style={{
          position: "absolute",
          left: 800,
          top: 900,
          width: 280,
          height: 280,
          background: dark ? "#6b9df414" : "#345df40c",
          borderRadius: "50%",
          translate: `${Math.sin((frame / fps) * 0.6) * 70}px ${Math.cos((frame / fps) * 0.4) * 80}px`,
        }}
      />
      {children}
    </AbsoluteFill>
  );
};

export const FilmBrand = ({
  dark = false,
  chapter = "PRODUCT FILM",
}: {
  dark?: boolean;
  chapter?: string;
}) => {
  const { fps } = useVideoConfig();
  return (
    <>
      <div
        style={{
          position: "absolute",
          left: 80,
          top: 90,
          display: "flex",
          gap: 12,
          alignItems: "center",
          fontSize: 37,
          fontWeight: 650,
          letterSpacing: -1,
        }}
      >
        <CanvasImage
          src={staticFile("brand.png")}
          premountFor={fps}
          style={{ width: 58, height: 58 }}
        />
        GraviGo
      </div>
      <div
        style={{
          position: "absolute",
          right: 82,
          top: 109,
          fontSize: 22,
          letterSpacing: 3,
          color: dark ? "#95a9c8" : "#7b879a",
        }}
      >
        {chapter}
      </div>
    </>
  );
};

export const FilmTitle = ({
  title,
  accent,
  dark = false,
  label,
}: {
  title: string;
  accent: string;
  dark?: boolean;
  label: string;
}) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return (
    <Interactive.Div
      name="章节主张"
      style={{
        position: "absolute",
        left: 82,
        right: 78,
        top: 200,
        translate: interpolate(
          frame,
          [0, 1.15 * fps],
          ["0px 90px", "0px 0px"],
          { ...hold, easing: ease },
        ),
        opacity: interpolate(frame, [0, 0.55 * fps], [0, 1], hold),
      }}
    >
      <div
        style={{
          color: dark ? "#a1b6d8" : "#64748e",
          fontSize: 28,
          letterSpacing: 2,
          marginBottom: 23,
        }}
      >
        {label}
      </div>
      <div
        style={{
          fontSize: 91,
          fontWeight: 650,
          lineHeight: 1.22,
          letterSpacing: -4,
        }}
      >
        {title}
        <br />
        <span style={{ color: dark ? "#9bb8ff" : cobalt }}>{accent}</span>
      </div>
    </Interactive.Div>
  );
};

export const NativePhone = ({
  src,
  trim = 0,
  style,
  width = 550,
}: {
  src: string;
  trim?: number;
  style?: CSSProperties;
  width?: number;
}) => {
  const { fps } = useVideoConfig();
  return (
    <Interactive.Div
      name="原生Android录屏"
      style={{
        position: "absolute",
        width,
        height: width * 2.054 + 28,
        padding: 10,
        background: "#202630",
        border: "3px solid #5f6773",
        borderRadius: 57,
        boxShadow: "0 40px 75px #06142930, 0 4px 9px #06142935",
        overflow: "hidden",
        ...style,
      }}
    >
      <div
        style={{
          width: "100%",
          height: "100%",
          borderRadius: 43,
          overflow: "hidden",
          background: "white",
        }}
      >
        <Video
          muted
          src={staticFile(`film/${src}.mp4`)}
          trimBefore={trim * fps}
          premountFor={fps}
          objectFit="cover"
          style={{ width: "100%", height: "100%" }}
        />
      </div>
    </Interactive.Div>
  );
};

export const Caption = ({
  children,
  dark = false,
}: {
  children: ReactNode;
  dark?: boolean;
}) => (
  <div
    style={{
      position: "absolute",
      left: 82,
      right: 82,
      top: 1700,
      fontSize: 44,
      lineHeight: 1.35,
      color: dark ? "#bdcbe0" : "#5e6b7f",
    }}
  >
    {children}
  </div>
);

export const DetailLens = ({
  src,
  y,
  start = 3,
  end = 8,
  label,
}: {
  src: string;
  y: number;
  start?: number;
  end?: number;
  label: string;
}) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return (
    <Interactive.Div
      name="原生操作放大"
      style={{
        position: "absolute",
        left: 102,
        top: 1100,
        width: 876,
        height: 412,
        background: "white",
        borderRadius: 26,
        overflow: "hidden",
        boxShadow: "0 28px 95px #0c234a36",
        border: "1px solid #c9d5ec",
        opacity: interpolate(
          frame,
          [(start - 0.4) * fps, start * fps, end * fps, (end + 0.5) * fps],
          [0, 1, 1, 0],
          hold,
        ),
        translate: interpolate(
          frame,
          [
            (start - 0.4) * fps,
            (start + 0.6) * fps,
            end * fps,
            (end + 0.7) * fps,
          ],
          ["200px 90px", "0px 0px", "0px 0px", "-180px -60px"],
          { ...hold, easing: ease },
        ),
        scale: interpolate(
          frame,
          [(start - 0.4) * fps, (start + 0.6) * fps],
          [0.82, 1],
          { ...hold, easing: ease, output: "perceptual-scale" },
        ),
      }}
    >
      <div
        style={{
          position: "absolute",
          left: 0,
          right: 0,
          height: 62,
          background: "#edf2ff",
          zIndex: 1,
          padding: "15px 26px",
          color: cobalt,
          fontSize: 32,
          fontWeight: 600,
        }}
      >
        {label}
        <span style={{ float: "right" }}>↗</span>
      </div>
      <CanvasImage
        premountFor={fps}
        src={staticFile(`film/${src}-after.png`)}
        style={{
          position: "absolute",
          width: 828,
          height: 1701.4,
          left: 24,
          top: 72 - y * (828 / 1080),
        }}
      />
    </Interactive.Div>
  );
};

export const FeatureShot = ({
  src,
  title,
  accent,
  label,
  number,
  children,
  dark = false,
  lens,
  reverse = false,
  trim = 0,
}: {
  src: string;
  title: string;
  accent: string;
  label: string;
  number: string;
  children: ReactNode;
  dark?: boolean;
  lens?: { y: number; label: string; start?: number; end?: number };
  reverse?: boolean;
  trim?: number;
}) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const showDetailLenses = useContext(DetailLensesContext);
  return (
    <FilmStage dark={dark}>
      <FilmBrand dark={dark} chapter={`${number} / REMOTE WORKFLOW`} />
      <FilmTitle title={title} accent={accent} label={label} dark={dark} />
      <NativePhone
        src={src}
        trim={trim}
        style={{
          left: 265,
          top: 520,
          transform: `perspective(2200px) rotateY(${interpolate(frame, [0, 1.4 * fps, 5 * fps, 11 * fps], [reverse ? -16 : 16, 0, -3, 3], { ...hold, easing: ease })}deg)`,
          translate: interpolate(
            frame,
            [0, 1.4 * fps, 4 * fps, 8.8 * fps, 12 * fps],
            [
              reverse ? "-580px 260px" : "580px 260px",
              "0px 0px",
              reverse ? "85px 12px" : "-85px 12px",
              "0px -8px",
              "0px -20px",
            ],
            { ...hold, easing: ease },
          ),
          rotate: interpolate(
            frame,
            [0, 1.4 * fps, 5 * fps, 11 * fps],
            [reverse ? "-12deg" : "12deg", "-1.5deg", "1.5deg", "-1deg"],
            { ...hold, easing: ease },
          ),
          scale: interpolate(
            frame,
            [0, 1.4 * fps, 5 * fps, 11 * fps],
            [0.8, 1, 0.97, 1],
            { ...hold, easing: ease, output: "perceptual-scale" },
          ),
        }}
      />
      {showDetailLenses && lens ? (
        <DetailLens
          src={src}
          y={lens.y}
          label={lens.label}
          start={lens.start}
          end={lens.end}
        />
      ) : null}
      <Caption dark={dark}>{children}</Caption>
    </FilmStage>
  );
};

const OrbitReveal = ({
  children,
  presentationDirection,
  presentationProgress,
}: TransitionPresentationComponentProps<Record<string, never>>) => (
  <AbsoluteFill
    style={{
      clipPath:
        presentationDirection === "entering"
          ? `circle(${presentationProgress * 2400}px at 82% 72%)`
          : undefined,
      scale:
        presentationDirection === "exiting"
          ? 1 + presentationProgress * 0.045
          : 1,
    }}
  >
    {children}
  </AbsoluteFill>
);

export const orbitReveal = () => ({ component: OrbitReveal, props: {} });
