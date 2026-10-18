# Third-party notices

These notices identify copied or bundled material. They do not replace the full licenses or describe every transitive Gradle/Go dependency.

| Component | Source and scope | License text |
| --- | --- | --- |
| antigravity-mobile | [GHSaiMo/antigravity-mobile](https://github.com/GHSaiMo/antigravity-mobile), the original app and gateway; Copyright (c) 2026 Antigravity Mobile Contributors | [MIT](android/docs/LICENSE.upstream-MIT) |
| RikkaHub highlighting | [rikkahub/rikkahub](https://github.com/rikkahub/rikkahub/tree/2267943a32d9208769b1ee199f3186d26066d1a3), extracted core, language definitions and token tests | [AGPL-3.0](android/highlight/LICENSE), [modification notice](android/highlight/NOTICE.md) |
| highlight.js | [highlight.js](https://github.com/highlightjs/highlight.js), highlighting rules and bundled legacy assets | [BSD-3-Clause](android/highlight/LICENSE.highlight-js), [asset license](android/app/src/main/assets/highlight/LICENSE) |
| MermaidKotlin | Native diagram parser and renderer; Copyright (c) 2025 Hugo Gonzalez | [MIT](android/mermaidkotlin/LICENSE) |
| KaTeX | [KaTeX](https://github.com/KaTeX/KaTeX), bundled legacy assets | [MIT](android/app/src/main/assets/katex/LICENSE) |
| Material Symbols | [Google Material Design icons](https://github.com/google/material-design-icons), bundled font | [Apache-2.0](android/app/src/main/assets/licenses/material_symbols_license.txt) |

The Android APK includes the highlighting component's notices and full license texts in its assets. The release gateway archive includes the upstream MIT text and a gateway-specific notice. Gradle dependencies are declared in each module's `build.gradle.kts`; Go dependencies are declared in `go.mod` and `go.sum`.

Keep the notices and applicable license texts when redistributing the affected material. Review the AGPL component and the licensing of GraviGo additions before publishing a combined Android binary. Source for a draft release must match its recorded commit and include the modifications and build instructions.
