# GraviGo Remotion V2

- 完整片最终导出与第二次实际文件验收已完成：45,882,946字节，SHA-256=45ea34de0da0b6075a5eebbe87c01d436d2b0ffb11e2e121556ce1c8e932802e。实际逐章图修掉手机压标题与Gateway遮挡后重渲染，最终完整解码/零黑帧/音轨连续/从0正常速度播至结束通过，元数据与哈希见delivery/manifest.json。不能拿首次47.5MB导出代替最终45.9MB文件。
- 2026-10-22用户明确要求完整两分多钟，18秒不满足。主交付改为GraviGo-FullFilm：147.8秒/1080×1920/60fps/8868帧，15个独立文件和FullFilm-Scenes注册，不能再只交短片。导演方案../FILM-V3.zh-CN.md，参考视频仅作分析，不拷贝其媒体。
- 新16项原生功能素材在根.verification/gravigo-film-v3/native，含配对、项目、流式发送、引用/命令、附件、方案、diff、代码/富文本、模型、语言、任务、语音、电脑状态、回退和交互选择。每项均有仪器动作断言和隔离FakeGateway/UUID配置/缓存。普通动作在录制期间手动推进Compose测试时钟，状态等待也驱动时钟；locale专用类不使用ComposeTestRule，走真实系统帧钟和可访问节点精确点击。原生VFR统一为14秒/60fps/840帧，不冒充实际交互原本全为60fps。
- prepare_film.py核验16项才复制public/film；render_film.ps1导出完整片；verify_film_studio.py检查全部独立章节及主时间线；verify_film.py完整解码、颜色/音频/黑帧、章节与邻接转场抽帧，并以1倍速从0播放到147.8秒结束。完整片输出在.verification/gravigo-film-v3/delivery，包含MP4/watch.html/海报/章节/许可/校验和。
- 主镜头及内部子段使用重叠滑移/圆形遮罩和曲线缓动，手机含轻微透视，焦点卡片取自实际原生after截图。不得重画App界面。Caption字号44px，底部保留约100px；放大素材保留左右内边距，防止切掉文字。
- 完整片音乐为Close Up和Tech House vibes，分别有独立Audio节点，章节间交叉淡化；音乐单独遵循Mixkit许可，不是原创或AGPL。没有旁白，不发布到外部。最终只有在实际MP4与完整播放验收通过后才可发完成提醒。
- 用户已否定第一轮Canvas／FFmpeg三份预览；本项目是2026-10-21按remotion-create技能创建的新工程，不恢复旧构图作为默认方向。
- 三份为A-PalmRelay《掌心接力》、B-WeightlessOrbit《失重轨道》、C-StopWaiting《别守着电脑》，均1080×1920、30fps、540帧。用户2026-10-21要求制作，当前已加入三首不同配乐并导出18秒短片；没有旁白，也不是60～75秒长版。
- Studio为http://localhost:4317，先打开预览再改视频，迭代时保持服务运行。主合成和场景均有独立JSX注册，时间、文字、关键帧保持可在Studio编辑。不要把独立场景折叠成一个循环模板。
- Remotion依赖全部锁定4.0.537；由官方create-video脚手架生成。先npm run assets准备素材；public为Git忽略的原生演示素材、正式图标、变量字体与原始OFL声明。不得替换为伪造App界面或披露真实账号、会话、配对凭据。
- 缓存、Chrome用户目录、关键帧和检查报告放在项目根.verification/gravigo-promo-remotion-v2/。避免C盘临时目录。Studio保留运行供用户预览，临时Python验收与浏览器检查结束后应退出。
- npm run lint包含ESLint和TypeScript。python verify_studio.py通过Studio提供的WebMCP接口检查7个独立场景、三份主合成的寻帧／错误状态／播放暂停，结果和截图写入忽略目录。关键帧使用Remotion still与已安装的Chrome Headless Shell；普通Chrome启动器在本机可能spawn UNKNOWN，不硬编码该路径到工程配置。
- 宣传范围沿用1.0.0局域网基线：电脑与网关保持运行，GraviGo是独立社区的Antigravity安卓远程客户端。用户标语精确为“把Antigravity，带在身边”。
- prepare_music.py下载Mixkit配乐并生成18秒/48kHz/立体声片段，含响度处理与淡入淡出。A为Close Up/Michael Ramir C.，B为Vastness/Andrew Ev，C为Tech House vibes/Alejandro Magaña。音乐遵循Mixkit单独许可，不是AGPL或原创；原始音乐仅留忽略缓存，不单独分发。
- render_delivery.ps1使用指定浏览器逐个渲染，显式BT.709有限范围避免默认全范围输出差异。verify_delivery.py对实际MP4作完整解码、颜色/帧数/音频/黑帧和浏览器播放检查，并生成delivery/compare.html、海报、CREDITS.txt与manifest.json；目录在根.verification/gravigo-promo-remotion-v2/。新增脚本不触碰App、模拟器或网关。
