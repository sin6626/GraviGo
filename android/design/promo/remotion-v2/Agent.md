# GraviGo Remotion V2

- 用户已否定第一轮Canvas／FFmpeg三份预览；本项目是2026-10-21按remotion-create技能创建的新工程，不恢复旧构图作为默认方向。
- 三份为A-PalmRelay《掌心接力》、B-WeightlessOrbit《失重轨道》、C-StopWaiting《别守着电脑》，均1080×1920、30fps、540帧。当前是无声的交互视觉草案，音乐、旁白和最终MP4尚未制作。
- Studio为http://localhost:4317，先打开预览再改视频，迭代时保持服务运行。主合成和场景均有独立JSX注册，时间、文字、关键帧保持可在Studio编辑。不要把独立场景折叠成一个循环模板。
- Remotion依赖全部锁定4.0.537；由官方create-video脚手架生成。先npm run assets准备素材；public为Git忽略的原生演示素材、正式图标、变量字体与原始OFL声明。不得替换为伪造App界面或披露真实账号、会话、配对凭据。
- 缓存、Chrome用户目录、关键帧和检查报告放在项目根.verification/gravigo-promo-remotion-v2/。避免C盘临时目录。Studio保留运行供用户预览，临时Python验收与浏览器检查结束后应退出。
- npm run lint包含ESLint和TypeScript。python verify_studio.py通过Studio提供的WebMCP接口检查7个独立场景、三份主合成的寻帧／错误状态／播放暂停，结果和截图写入忽略目录。关键帧使用Remotion still与已安装的Chrome Headless Shell；普通Chrome启动器在本机可能spawn UNKNOWN，不硬编码该路径到工程配置。
- 宣传范围沿用1.0.0局域网基线：电脑与网关保持运行，GraviGo是独立社区的Antigravity安卓远程客户端。用户标语精确为“把Antigravity，带在身边”。
