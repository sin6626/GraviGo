import { createContext, useContext } from "react";

export type FilmLanguage = "zh" | "en";
export const FilmLanguageContext = createContext<FilmLanguage>("zh");

const englishCopy: Record<string, string> = {
  "电脑继续跑。": "Your PC keeps going.",
  "你接着走。": "So can you.",
  "Antigravity 的进度，手机上接着看。": "Follow Antigravity from your phone.",
  "电脑保持运行。": "Keep your PC running.",
  "手机接上现场。": "Connect from your phone.",
  从一条连接开始: "Start with a connection",
  电脑端: "Desktop",
  启动网关: "Start gateway",
  扫码或填码: "Scan or paste code",
  继续当前任务: "Continue the task",
  提供一行安装入口: "One-line installers",
  "当前连接范围为局域网。": "LAN connections are supported.",
  "电脑与网关需保持运行。": "Keep your PC and gateway running.",
  "项目和会话，": "Projects and chats.",
  "从原处接上。": "Pick up where you left off.",
  "工作区 · 项目会话 · 新建任务": "Workspaces · Chats · New tasks",
  "进入电脑上的工作区，选择已有会话，": "Open a workspace or an existing chat.",
  "也可以在项目里开启一个新任务。": "Start a new task inside the same project.",
  "想到什么，": "Have another thought?",
  "就补充一句。": "Send it straight back.",
  "发送要求 · 跟进流式回复": "Send instructions · Follow streaming replies",
  "补充要求发送到电脑上的当前会话。":
    "Send instructions to the desktop session.",
  "回复逐步出现，阅读和执行都接着走。": "Follow replies as they appear.",
  "把上下文，": "Bring the context.",
  "一起带过去。": "Files and commands too.",
  "@ 文件引用 · / 命令 · 规划方式": "@ Files · / Commands · Planning",
  "引用工作区文件，选择电脑端提供的命令。":
    "Reference workspace files and commands.",
  "补充具体上下文，让这一次要求更清楚。":
    "Give the next instruction more context.",
  "图、文件，": "Images and files.",
  "都能一起发。": "Send them together.",
  "附件 · 草稿保留": "Attachments · Saved drafts",
  "把参考图和文件加入要求。": "Attach reference images and files.",
  "草稿、附件与当前会话保持在一起。":
    "Drafts and attachments stay with the chat.",
  "不方便打字，": "Can't type right now?",
  "就直接说出来。": "Say it instead.",
  "语音输入 · 转成可编辑草稿": "Voice input · Editable transcripts",
  "转写后可以继续编辑。": "Edit the transcript before sending.",
  "此能力取决于电脑端账号权限。": "Requires support from your desktop account.",
  "方案出来，": "The plan is ready.",
  "先审再继续。": "Review before it runs.",
  "原生方案阅读 · 选段评论": "Read plans · Comment on passages",
  "阅读实现方案，选中需要修改的段落。":
    "Read the plan and select a passage to change.",
  "把反馈交回电脑，再决定是否继续执行。":
    "Send feedback before the next step.",
  "代码改了哪里，": "See what changed.",
  "看清再反馈。": "Comment by line.",
  "代码差异 · 行级评论": "Code diffs · Line comments",
  "查看本次文件变更，定位到具体代码行。":
    "Review changes down to the code line.",
  "评论随位置一起提交，减少来回解释。":
    "Comments include the exact code location.",
  "长代码，": "Long code.",
  "也能顺着读。": "Easy to read.",
  "原生文件阅读 · 高亮 · 自动换行": "Native reading · Highlighting · Wrapping",
  "手机原生阅读关联文件。": "Read linked files in native Android views.",
  "按需要切换换行，不改变文件原文。":
    "Toggle wrapping without changing the source.",
  "文字之外，": "Beyond plain text.",
  "图表也清楚。": "Charts make it clear.",
  "Markdown · 表格 · 公式 · Mermaid": "Markdown · Tables · Math · Mermaid",
  "方案里的结构与关系，直接在手机上读。":
    "Read structure and relationships on the go.",
  "文件支持复制、保存和系统分享。": "Copy, save or share the original file.",
  "任务在运行，": "The task keeps running.",
  "进度握在手里。": "Stay on top of progress.",
  "后台任务 · 排队要求 · 即时补充": "Background tasks · Queues · Updates",
  "查看正在执行的任务和排队消息。": "View running tasks and queued messages.",
  "需要时立即补充，或停止指定后台任务。":
    "Send an update now or stop a specific task.",
  "需要你决定时，": "Your decision is needed.",
  "手机上直接回应。": "Respond from your phone.",
  "交互选项 · 补充说明": "Interactive choices · Your own notes",
  "选择要保留的体验，补上自己的要求。":
    "Choose what to keep and add your own notes.",
  "决策继续留在当前工作流里。": "Keep decisions in the current workflow.",
  "想回到前一步，": "Need to go back?",
  "先确认回退范围。": "Choose what to revert.",
  "消息回退 · 文件改动选项": "Revert messages · Restore file changes",
  "回退之前，先确认是否恢复文件。": "Choose whether to restore file changes.",
  "会话与工作区改动，由你选择。":
    "Revert the chat, workspace changes, or both.",
  "用哪个模型，": "Choose your model.",
  "还剩多少额度。": "Check your quota.",
  "模型选择 · 额度与恢复时间": "Model selection · Quota · Reset times",
  "按当前任务选择可用模型。": "Choose an available model for the task.",
  "查看共享额度和恢复时间，再安排下一步。":
    "Check shared quota and the reset time.",
  "电脑端的状态，": "Check the desktop.",
  "也有地方可查。": "Permissions and tools.",
  "权限 · 审批策略 · MCP连接": "Permissions · Approval policies · MCP",
  "查看电脑端有效权限与工具连接状态。":
    "View effective permissions and tool status.",
  "需要认证的服务，回到电脑端处理。":
    "Complete required sign-ins on the desktop.",
  "你的账号，": "Your account.",
  "你的阅读习惯。": "Your reading preferences.",
  "电脑账号资料 · 中文 / English": "Desktop account · Chinese / English",
  "账号资料来自已配对电脑，无需另开账号。":
    "Account details come from the paired desktop.",
  "随时切换中英文界面和文件换行偏好。":
    "Choose your language and line wrapping.",
  "把Antigravity，带在身边": "Bring Antigravity with you.",
  "独立社区项目 · Android 原生界面":
    "Independent community app · Native Android",
  "Windows / macOS 网关 · AGPL v3 开源": "Windows / macOS gateways · AGPL v3",
  "基于九哥 GHSaiMo 的 antigravity-mobile":
    "Based on GHSaiMo's antigravity-mobile",
  "演示采用隔离示例数据 · 局域网连接": "Isolated demo data · LAN connections",
  电脑与网关需保持运行: "Keep your PC and gateway running",
};

export const useFilmCopy = () => {
  const english = useContext(FilmLanguageContext) === "en";
  return {
    english,
    t: (value: string) => {
      if (!english || !/[\u4e00-\u9fff]/.test(value)) return value;
      const translated = englishCopy[value.trim()];
      if (!translated) throw new Error(`Missing film translation: ${value}`);
      return translated;
    },
  };
};
