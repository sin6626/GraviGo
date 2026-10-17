# GraviGo 图标候选

2026-10-17，用户将品牌名从 GraviWing 改为 GraviGo，并要求按 Google 产品风格重新生成整套图标。本目录为当前候选；GraviWing 旧候选作为历史方案保留，不再代表当前命名方向。

共 6 张独立生成的 1254×1254 PNG，使用内置 imagegen，未经程序绘图或图片编辑替代。方向为白底、友好圆角几何和蓝绿黄红配色，参考 [Google 视觉语言说明](https://design.google/library/evolving-google-identity)，采用新构形，没有直接复用官方产品标志。

| 编号 | 文件 | 方向 |
|---|---|---|
| A | A-go-monogram.png | 自定义圆角 G 与前进缺口 |
| B | B-antigravity-gate.png | 反重力门形与悬浮横梁 |
| C | C-device-handoff.png | 电脑与手机双端接续 |
| D | D-go-forward.png | 向前和向上跃迁 |
| E | E-floating-core.png | 浮空核心与分离托架 |
| F | F-rising-steps.png | 接力向上的圆润阶梯 |

完整提示词在 `prompts.json`；`review-board.html` 由 Creative Production 插件自带的 `scripts/review_renderer.py` 根据 `review-manifest.json`、`review-options.json` 生成，可直接打开比较原图。没有生成联系表或额外图片衍生物。

沿用同一个 Creative Production 看板 `b5533367-ebda-4962-b4c7-1536a5101a00`，本轮 item ID 为 `gg-a` 至 `gg-f`，最终 revision 18；旧 GraviWing 项仍保留为历史对比。

六款均为带白背景的候选。图片色面仍有轻微明度变化，不能宣称为严格纯色矢量；主体目视宽约 51%～75%，正式采用前需制作透明前景、安全区及 monochrome 版本，并验证 Android 自适应蒙版、小尺寸和开屏。当前没有覆盖应用图标。

应用显示名称同步为 GraviGo，沿用原包名与签名以保留现有安装数据；仓库、包名和正式签名的统一迁移尚未执行。
