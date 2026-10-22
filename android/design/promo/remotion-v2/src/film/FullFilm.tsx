import { Audio } from "@remotion/media";
import { Easing, interpolate, staticFile, useVideoConfig } from "remotion";
import { linearTiming, TransitionSeries } from "@remotion/transitions";
import { slide } from "@remotion/transitions/slide";
import { Opening } from "./Opening";
import { Connection } from "./Connection";
import { Workspaces } from "./Workspaces";
import { LiveChat } from "./LiveChat";
import { Context } from "./Context";
import { Attachments } from "./Attachments";
import { PlanReview } from "./PlanReview";
import { CodeReview } from "./CodeReview";
import { Reading } from "./Reading";
import { Tasks } from "./Tasks";
import { Decisions } from "./Decisions";
import { Models } from "./Models";
import { Desktop } from "./Desktop";
import { Settings } from "./Settings";
import { Closing } from "./Closing";
import { DetailLensesContext, orbitReveal } from "./visuals";
import { hold } from "../design";
import { FilmLanguageContext, type FilmLanguage } from "./copy";

export const FullFilm = ({
  showDetailLenses = true,
  language = "zh",
}: {
  showDetailLenses?: boolean;
  language?: FilmLanguage;
}) => {
  const { fps } = useVideoConfig();
  return (
    <FilmLanguageContext.Provider value={language}>
      <DetailLensesContext.Provider value={showDetailLenses}>
        <Audio
          name="配乐上篇：Close Up"
          src={staticFile(
            `${language === "en" ? "film-en" : "film"}/music-close-up.mp3`,
          )}
          premountFor={fps}
          durationInFrames={94 * fps}
          volume={(f) =>
            interpolate(
              f,
              [0, 2 * fps, 90 * fps, 94 * fps],
              [0, 0.74, 0.74, 0],
              hold,
            )
          }
        />
        <Audio
          name="配乐下篇：Tech House vibes"
          src={staticFile(
            `${language === "en" ? "film-en" : "film"}/music-tech-house.mp3`,
          )}
          from={90 * fps}
          trimBefore={12 * fps}
          premountFor={fps}
          durationInFrames={57.8 * fps}
          volume={(f) =>
            interpolate(
              f,
              [0, 4 * fps, 54 * fps, 57.8 * fps],
              [0, 0.64, 0.64, 0],
              hold,
            )
          }
        />
        <TransitionSeries>
          <TransitionSeries.Sequence
            name="开场：电脑继续跑"
            durationInFrames={9 * fps}
            premountFor={fps}
          >
            <Opening />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={orbitReveal()}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.inOut(Easing.cubic),
            })}
          />
          <TransitionSeries.Sequence
            name="01 连接电脑与网关"
            durationInFrames={10 * fps}
            premountFor={fps}
          >
            <Connection />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={slide({ direction: "from-right" })}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.bezier(0.65, 0, 0.35, 1),
            })}
          />
          <TransitionSeries.Sequence
            name="02 工作区与项目会话"
            durationInFrames={10 * fps}
            premountFor={fps}
          >
            <Workspaces />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={slide({ direction: "from-bottom" })}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.bezier(0.65, 0, 0.35, 1),
            })}
          />
          <TransitionSeries.Sequence
            name="03 发送要求与流式回复"
            durationInFrames={12 * fps}
            premountFor={fps}
          >
            <LiveChat />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={orbitReveal()}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.inOut(Easing.cubic),
            })}
          />
          <TransitionSeries.Sequence
            name="04 文件引用与命令"
            durationInFrames={10 * fps}
            premountFor={fps}
          >
            <Context />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={slide({ direction: "from-right" })}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.bezier(0.65, 0, 0.35, 1),
            })}
          />
          <TransitionSeries.Sequence
            name="05 附件与语音输入"
            durationInFrames={12 * fps}
            premountFor={fps}
          >
            <Attachments />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={slide({ direction: "from-bottom" })}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.bezier(0.65, 0, 0.35, 1),
            })}
          />
          <TransitionSeries.Sequence
            name="06 方案阅读与评论"
            durationInFrames={12 * fps}
            premountFor={fps}
          >
            <PlanReview />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={orbitReveal()}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.inOut(Easing.cubic),
            })}
          />
          <TransitionSeries.Sequence
            name="07 代码差异与行评论"
            durationInFrames={12 * fps}
            premountFor={fps}
          >
            <CodeReview />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={slide({ direction: "from-right" })}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.bezier(0.65, 0, 0.35, 1),
            })}
          />
          <TransitionSeries.Sequence
            name="08 文件代码与富文本阅读"
            durationInFrames={12 * fps}
            premountFor={fps}
          >
            <Reading />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={slide({ direction: "from-bottom" })}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.bezier(0.65, 0, 0.35, 1),
            })}
          />
          <TransitionSeries.Sequence
            name="09 后台任务与排队消息"
            durationInFrames={10 * fps}
            premountFor={fps}
          >
            <Tasks />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={orbitReveal()}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.inOut(Easing.cubic),
            })}
          />
          <TransitionSeries.Sequence
            name="10 交互选择与回退预览"
            durationInFrames={10 * fps}
            premountFor={fps}
          >
            <Decisions />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={slide({ direction: "from-right" })}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.bezier(0.65, 0, 0.35, 1),
            })}
          />
          <TransitionSeries.Sequence
            name="11 模型选择与额度"
            durationInFrames={11 * fps}
            premountFor={fps}
          >
            <Models />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={slide({ direction: "from-bottom" })}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.bezier(0.65, 0, 0.35, 1),
            })}
          />
          <TransitionSeries.Sequence
            name="12 电脑权限与工具状态"
            durationInFrames={9 * fps}
            premountFor={fps}
          >
            <Desktop />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={orbitReveal()}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.inOut(Easing.cubic),
            })}
          />
          <TransitionSeries.Sequence
            name="13 账号资料与双语"
            durationInFrames={10 * fps}
            premountFor={fps}
          >
            <Settings />
          </TransitionSeries.Sequence>
          <TransitionSeries.Transition
            presentation={orbitReveal()}
            timing={linearTiming({
              durationInFrames: 0.8 * fps,
              easing: Easing.inOut(Easing.cubic),
            })}
          />
          <TransitionSeries.Sequence
            name="结尾：把Antigravity带在身边"
            durationInFrames={10 * fps}
            premountFor={fps}
          >
            <Closing />
          </TransitionSeries.Sequence>
        </TransitionSeries>
      </DetailLensesContext.Provider>
    </FilmLanguageContext.Provider>
  );
};
