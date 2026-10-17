# GraviGo 参考反重力图标的第二轮候选

2026-10-17，用户否定前两轮方向，并提供反重力图标作为参考。新的明确要求是不强行表现 G、Go 等字母文字，以有内涵的抽象产品符号为主。

## 本轮取舍

学习参考图的简洁有机轮廓、大留白、蓝色主体与少量暖色顶端、连续柔和渐变及向上悬浮的感觉。每次内置 imagegen 调用都实际传入 `style-reference.png`，该图片只作为风格参考，不是要求保留原标轮廓的编辑目标。

没有用程序绘图、SVG 或 CSS 制作图标；下列六张均为独立生成并保留原始像素的 1254×1254 PNG。

| 编号 | 图片 | 视觉方向 |
|---|---|---|
| A | A-weightless-arc.png | 宽矮、不对称悬弧 |
| B | B-floating-wave.png | 两条上下呼应的悬浮曲线 |
| C | C-cloud-ridge.png | 圆润云脊与下方开口 |
| D | D-airborne-gesture.png | 单一轻跃手势 |
| E | E-rising-ripple.png | 浮核与承托曲线之间的气隙 |
| F | F-converging-flow.png | 两股不对称曲线形成的上升通道 |

A、C 延续参考图的拱形家族，彼此较接近；B、D、E、F 探索不同轮廓。不要宣称每款都完成了独立品牌辨识度验证，是否采用仍由用户评审。

## 文件和验证

- `prompts.json` 保留完整提示词及参考角色，`review-manifest.json` 记录候选清单。
- `review-board.html` 为原图对比页，`review-small.html` 为小尺寸检查页，均由 Creative Production 插件自带渲染器生成，不改变原图。
- 同一 Creative Production 看板 `b5533367-ebda-4962-b4c7-1536a5101a00`，本轮项目 `gg2-a` 至 `gg2-f`，最终 revision 26。
- 全部图片已逐张目视检查，无额外文字和水印，并做原图解码、唯一性与对比链接验证。无头浏览器的小尺寸呈现截图位于项目忽略目录 `.verification/2026-10-17-icon-reference/`。

仍为白背景候选，主体目视宽约 53%～75%。最终选择后需处理透明前景、安全区、monochrome 图标、Android 蒙版与启动屏验证。没有替换应用图标、重装应用或更改账号数据；包名、签名、仓库迁移也未在本轮执行。
