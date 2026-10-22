import { Composition, Folder, Series, useVideoConfig } from "remotion";
import { Audio } from "@remotion/media";
import { staticFile } from "remotion";
import { TransitionSeries, linearTiming } from "@remotion/transitions";
import { slide } from "@remotion/transitions/slide";
import { fade } from "@remotion/transitions/fade";
import { PalmOpen } from "./scenes/PalmOpen";
import { PalmProof } from "./scenes/PalmProof";
import { OrbitOpen } from "./scenes/OrbitOpen";
import { OrbitProof } from "./scenes/OrbitProof";
import { EditorialOpen } from "./scenes/EditorialOpen";
import { EditorialProof } from "./scenes/EditorialProof";
import { ReleaseEnd } from "./scenes/ReleaseEnd";
import { FullFilm } from "./film/FullFilm";
import { Opening } from "./film/Opening";
import { Connection } from "./film/Connection";
import { Workspaces } from "./film/Workspaces";
import { LiveChat } from "./film/LiveChat";
import { Context } from "./film/Context";
import { Attachments } from "./film/Attachments";
import { PlanReview } from "./film/PlanReview";
import { CodeReview } from "./film/CodeReview";
import { Reading } from "./film/Reading";
import { Tasks } from "./film/Tasks";
import { Decisions } from "./film/Decisions";
import { Models } from "./film/Models";
import { Desktop } from "./film/Desktop";
import { Settings } from "./film/Settings";
import { Closing } from "./film/Closing";

const PalmRelay = () => {
  const { fps } = useVideoConfig();
  return (
    <>
      <Audio
        name="配乐：Close Up"
        premountFor={fps}
        src={staticFile("music/a-close-up.wav")}
      />
      <TransitionSeries>
        <TransitionSeries.Sequence
          name="电脑继续跑，你接着走"
          durationInFrames={4 * fps}
          premountFor={fps}
        >
          <PalmOpen title="电脑继续跑。" subtitle="你接着走。" />
        </TransitionSeries.Sequence>
        <TransitionSeries.Transition
          presentation={slide({ direction: "from-bottom" })}
          timing={linearTiming({ durationInFrames: 0.5 * fps })}
        />
        <TransitionSeries.Sequence
          name="补充要求与审阅方案"
          durationInFrames={11 * fps}
          premountFor={fps}
        >
          <PalmProof title="补充一句，" subtitle="方案出来，" />
        </TransitionSeries.Sequence>
        <TransitionSeries.Transition
          presentation={slide({ direction: "from-right" })}
          timing={linearTiming({ durationInFrames: 0.5 * fps })}
        />
        <TransitionSeries.Sequence
          name="掌心接力品牌落版"
          durationInFrames={4 * fps}
          premountFor={fps}
        >
          <ReleaseEnd kind="palm" />
        </TransitionSeries.Sequence>
      </TransitionSeries>
    </>
  );
};

const WeightlessOrbit = () => {
  const { fps } = useVideoConfig();
  return (
    <>
      <Audio
        name="配乐：Vastness"
        premountFor={fps}
        src={staticFile("music/b-vastness.wav")}
      />
      <TransitionSeries>
        <TransitionSeries.Sequence
          name="悬浮晶核与品牌主张"
          durationInFrames={5 * fps}
          premountFor={fps}
        >
          <OrbitOpen title="让开发，" subtitle="轻一点。" />
        </TransitionSeries.Sequence>
        <TransitionSeries.Transition
          presentation={fade()}
          timing={linearTiming({ durationInFrames: 0.5 * fps })}
        />
        <TransitionSeries.Sequence
          name="进入会话与方案"
          durationInFrames={10 * fps}
          premountFor={fps}
        >
          <OrbitProof title="跟进，" subtitle="每一步，" />
        </TransitionSeries.Sequence>
        <TransitionSeries.Transition
          presentation={fade()}
          timing={linearTiming({ durationInFrames: 0.5 * fps })}
        />
        <TransitionSeries.Sequence
          name="失重轨道品牌落版"
          durationInFrames={4 * fps}
          premountFor={fps}
        >
          <ReleaseEnd kind="orbit" />
        </TransitionSeries.Sequence>
      </TransitionSeries>
    </>
  );
};

const StopWaiting = () => {
  const { fps } = useVideoConfig();
  return (
    <>
      <Audio
        name="配乐：Tech House vibes"
        premountFor={fps}
        src={staticFile("music/c-tech-house.wav")}
      />
      <Series>
        <Series.Sequence
          name="AI在写，你还在等"
          durationInFrames={3 * fps}
          premountFor={fps}
        >
          <EditorialOpen title="AI在写。" subtitle="你还在等？" />
        </Series.Sequence>
        <Series.Sequence
          name="看进度、补要求、审方案"
          durationInFrames={12 * fps}
          premountFor={fps}
        >
          <EditorialProof title="再补一句。" subtitle="然后审方案。" />
        </Series.Sequence>
        <Series.Sequence
          name="别守着电脑品牌落版"
          durationInFrames={3 * fps}
          premountFor={fps}
        >
          <ReleaseEnd kind="editorial" />
        </Series.Sequence>
      </Series>
    </>
  );
};

export const RemotionRoot = () => (
  <>
    <Composition
      id="GraviGo-FullFilm-EN"
      component={FullFilm}
      width={1080}
      height={1920}
      fps={60}
      durationInFrames={8868}
      defaultProps={{ showDetailLenses: false, language: "en" }}
    />
    <Composition
      id="GraviGo-FullFilm-V2"
      component={FullFilm}
      width={1080}
      height={1920}
      fps={60}
      durationInFrames={8868}
      defaultProps={{ showDetailLenses: false }}
    />
    <Composition
      id="GraviGo-FullFilm"
      component={FullFilm}
      width={1080}
      height={1920}
      fps={60}
      durationInFrames={8868}
    />
    <Folder name="FullFilm-Scenes">
      <Composition
        id="Film-Opening"
        component={Opening}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={540}
      />
      <Composition
        id="Film-Connection"
        component={Connection}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={600}
      />
      <Composition
        id="Film-Workspaces"
        component={Workspaces}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={600}
      />
      <Composition
        id="Film-LiveChat"
        component={LiveChat}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={720}
      />
      <Composition
        id="Film-Context"
        component={Context}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={600}
      />
      <Composition
        id="Film-Attachments"
        component={Attachments}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={720}
      />
      <Composition
        id="Film-PlanReview"
        component={PlanReview}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={720}
      />
      <Composition
        id="Film-CodeReview"
        component={CodeReview}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={720}
      />
      <Composition
        id="Film-Reading"
        component={Reading}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={720}
      />
      <Composition
        id="Film-Tasks"
        component={Tasks}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={600}
      />
      <Composition
        id="Film-Decisions"
        component={Decisions}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={600}
      />
      <Composition
        id="Film-Models"
        component={Models}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={660}
      />
      <Composition
        id="Film-Desktop"
        component={Desktop}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={540}
      />
      <Composition
        id="Film-Settings"
        component={Settings}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={600}
      />
      <Composition
        id="Film-Closing"
        component={Closing}
        width={1080}
        height={1920}
        fps={60}
        durationInFrames={600}
      />
    </Folder>
    <Composition
      id="A-PalmRelay"
      component={PalmRelay}
      width={1080}
      height={1920}
      fps={30}
      durationInFrames={540}
    />
    <Composition
      id="B-WeightlessOrbit"
      component={WeightlessOrbit}
      width={1080}
      height={1920}
      fps={30}
      durationInFrames={540}
    />
    <Composition
      id="C-StopWaiting"
      component={StopWaiting}
      width={1080}
      height={1920}
      fps={30}
      durationInFrames={540}
    />
    <Folder name="Scenes">
      <Composition
        id="Palm-Opening"
        component={PalmOpen}
        width={1080}
        height={1920}
        fps={30}
        durationInFrames={120}
        defaultProps={{ title: "电脑继续跑。", subtitle: "你接着走。" }}
      />
      <Composition
        id="Palm-Product"
        component={PalmProof}
        width={1080}
        height={1920}
        fps={30}
        durationInFrames={330}
        defaultProps={{ title: "补充一句，", subtitle: "方案出来，" }}
      />
      <Composition
        id="Orbit-Opening"
        component={OrbitOpen}
        width={1080}
        height={1920}
        fps={30}
        durationInFrames={150}
        defaultProps={{ title: "让开发，", subtitle: "轻一点。" }}
      />
      <Composition
        id="Orbit-Product"
        component={OrbitProof}
        width={1080}
        height={1920}
        fps={30}
        durationInFrames={300}
        defaultProps={{ title: "跟进，", subtitle: "每一步，" }}
      />
      <Composition
        id="Editorial-Opening"
        component={EditorialOpen}
        width={1080}
        height={1920}
        fps={30}
        durationInFrames={90}
        defaultProps={{ title: "AI在写。", subtitle: "你还在等？" }}
      />
      <Composition
        id="Editorial-Product"
        component={EditorialProof}
        width={1080}
        height={1920}
        fps={30}
        durationInFrames={360}
        defaultProps={{ title: "再补一句。", subtitle: "然后审方案。" }}
      />
      <Composition
        id="Brand-End"
        component={ReleaseEnd}
        width={1080}
        height={1920}
        fps={30}
        durationInFrames={120}
        defaultProps={{ kind: "palm" }}
      />
    </Folder>
  </>
);
