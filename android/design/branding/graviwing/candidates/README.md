# GraviWing 图标候选

2026-10-17，用户确定品牌名为 GraviWing，要求生成多款贴合 Google Antigravity 移动伴侣用途的新图标，供挑选。

共 6 款独立生成的 1254×1254 PNG，使用内置 imagegen，经目视检查后保留原图，没有用 SVG、CSS 或程序绘图代替图像生成。完整提示词在 `prompts.json`，对比页为 `review-board.html`。Creative Production 看板 ID：`b5533367-ebda-4962-b4c7-1536a5101a00`。

| 编号 | 文件 | 方向 |
|---|---|---|
| A | A-swept-wing.png | 白底蓝紫单翼，强调向上飞行 |
| B | B-floating-wing.png | 深色悬浮翼与分离底座，强调反重力 |
| C | C-g-wing.png | 钴蓝底白色 G 翼，强调品牌字母和小尺寸识别 |
| D | D-paired-wings.png | 大小双翼呼应，表达电脑与手机接续 |
| E | E-folded-wing.png | 奶油白底暖橙折翼，探索更温暖的视觉方向 |
| F | F-orbital-wing.png | 深紫底青紫上升翼，强调飞行轨迹 |

所有图片均为带背景的候选图，没有替换现有应用资源。主体目视占比约 70%～78%，高于提示词要求的中央 64%；选择后需要制作透明前景、调整尺寸，并在实际 Android 自适应蒙版、小尺寸和开屏中验证。不能将本轮候选视为已经适配完成的正式图标。

`review-board.html` 由 Creative Production 插件自带的 `scripts/review_renderer.py` 根据 `review-manifest.json` 和 `review-options.json` 生成，未生成额外图片衍生物。对比页直接引用这六张原图，可独立在浏览器打开。

本轮只记录已确定品牌和图标候选；应用安装名、GitHub 仓库、包名、深链接、签名及相关配置的统一迁移仍待后续处理。
