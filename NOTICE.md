# 煎饼使TV 分支说明

直接衍生自 [op7418/guizang-tv](https://github.com/op7418/guizang-tv)，基线 `312c01119a327f402e68818670a6542f1e643e56`。保留上游 Git 历史、版权与所有以下通知。本分支为独立的家庭照片桌面试验，不代表归藏或小米官方。

本分支新增：本机照片导入与公开/私有图库分离、看照片模式、完整画面留边处理、黄色纸片焦点与新图标、首次入口发现及 MFTR0 指定固件适配。界面设置、Dock、预览、天气与 Codex 等基础来自归藏，不作为本分支独创宣传。

本机照片方向识别使用 AndroidX ExifInterface 1.4.2（Android Open Source Project，Apache License 2.0），未修改库源码。完整许可随 APK 保存于 `assets/licenses/ANDROIDX-APACHE-2.0.txt`，来源：https://developer.android.com/jetpack/androidx/releases/exifinterface 。AndroidX 的传递注解依赖按其 Apache 2.0 许可使用；JSpecify 1.0.0、Kotlin 标准库 1.7.10 及 JetBrains annotations 13.0 均按各自 Apache 2.0 许可使用；AndroidX annotation 1.8.1 是本库的传递依赖。

八张原创摄影署名为乙，适用限定的项目内壁纸使用许可，详见 `app/src/main/assets/wallpapers/public-defaults/NOTICE.md`；它们与七张 Unsplash 照片均不适用代码 MIT。人物、私人截图与 AI 重建室内图不分发。

---

# 来源与许可

- 初始工程参考与衍生基础：Gathin23/cleanyourtv，MIT License。
  - https://github.com/Gathin23/cleanyourtv
  - 本地定制基线：`69c93b66a75c70a9e386adff327b6a51c12addc5`
  - 原始版权：Copyright (c) 2026 Gathin，保留于根目录 LICENSE。
- 设计与设置交互参考：nthnclrk/CleanFrame，Nathan Clark。
  - https://github.com/nthnclrk/CleanFrame
  - 参考版本：`fa537d1892b71d2de2bf94c24cbff3812e9f70d5`
  - 参考其分组、首页预览、焦点与已应用值分离、确认应用和逐层返回的交互。
  - 本项目没有打包其 Tizen 代码、字体或照片。
  - 图标采用下述开源 SVG，不打包 Android 应用的原始图标。
- Codex 输入浮层、磨砂材质与细描边参考并适配 [Libraries.dev](https://github.com/Jakubantalik/Libraries.dev) 的公开 Web 实现（MIT，Copyright (c) 2026 Jakub Antalik），固定版本 `b0e5a8f1e659ac549e6035e42a750e73678ed67a`。来源与原生映射见 [适配记录](docs/BEAM-ANDROID.md)，MIT 通知在 `app/src/main/assets/licenses/LIBRARIES-DEV-MIT.txt` 并随 APK 打包；未引入 React/SwiftUI 运行时。
- 默认渐变、布局示意及本项目绘制代码采用 MIT 许可。
- 品牌 SVG：Simple Icons，CC0-1.0；工具 SVG：Phosphor Icons Fill（勾选与箭头为 Regular），MIT。原 SVG、精确提交来源与许可证位于 `app/src/main/assets/icons/`；Android VectorDrawable 是同一路径数据的格式转换。商标权不因图标的开源许可而转移。
- 使用 Android 系统字体，不打包 Outfit 字体。
- Android Gradle Plugin、Gradle 与 JUnit 是构建或测试依赖，各自适用其许可证；不代表被重新许可为 MIT。
- 各产品与商标归其权利人所有。本项目不隶属于小米、Sony、Apple 或哔哩哔哩。

上游 v0.1.1 起内置五张 Unsplash 照片；本分支本地候选新增两张经原页核对的照片，合计七张，适用 **Unsplash License**，不适用代码的 MIT 许可。逐张署名、原始链接和处理方式见 [壁纸说明](docs/WALLPAPERS.md)，许可通知也随 APK 打包。

用户自行加入的照片不因放入本工程而自动获得 MIT 许可。其他未授权公开的私人试用素材、实机诊断数据、签名密钥均不在公开源代码或官方 APK 中。

main 的可选天气使用 [Open-Meteo](https://open-meteo.com/) 数据（[CC BY 4.0](https://creativecommons.org/licenses/by/4.0/)），温度取整、天气码转换为中文及自绘矢量图标；城市搜索由 Open-Meteo / GeoNames 提供，IP 城市由 [IPWhois](https://ipwhois.io/) 提供。托管 API 的使用条款独立于本项目代码许可，详见[天气说明](docs/WEATHER.md)。


## 待机恢复实验参考（默认关闭）

参考 [pengchujin/aerial-tv](https://github.com/pengchujin/aerial-tv) 的 MFTR0 设备记录、RevivalListener 与 GuardSetup 的系统重新绑定恢复机制，参考版本 `70c18ff12ae04513f4ce2884c8554971ca8316d7`。按本分支 Java 结构实现默认关闭的最小候选；没有引入其账号同步、屏保、ADB 保持或按键过滤机制。Copyright (c) 2026 pengchujin，MIT 全文保存在 `app/src/main/assets/licenses/AERIAL-MIT.txt` 并随 APK 打包。上游同机型测试不是本分支验收；本分支在本人明确授权后有三次短时恢复样本，完整重启、长期待机仍待验收；新设备须本人另行开启。
