> 本文保留归藏上游说明。煎饼使TV 分支的公开包名是 `io.github.jhcina.yitv`，版本、照片导入和实测范围以 [分支 README](../README.md)、[照片指南](PHOTOS.md)、[兼容记录](YI-COMPATIBILITY.md) 为准。下文上游包名、下载地址及其验证记录不能直接当作本分支结果；不要照抄上游包名设置开机或 Home。

# 内置壁纸与许可

仅打包用户在 2026-10-20 截图中选中的五张 Unsplash 照片。逐张原页面已核对作者与「Free to use under the Unsplash License」标记；没有包含其余 Wallhaven 图片。

图片遵循 [Unsplash License](https://unsplash.com/license)，不属于项目代码的 MIT 授权。可免费复制、修改及分发用于本桌面；不得将未作显著修改的照片单独出售，或汇编成与 Unsplash 相似或竞争的服务。

| 预览 | 桌面名称 | 摄影师 | 原始页面 |
| --- | --- | --- | --- |
| ![雾中雪岭](../app/src/main/assets/wallpapers/bundled/rSYsP5TlEAk-thumb.jpg) | 雾中雪岭 | [Arina Avdzhian](https://unsplash.com/@yallwacky) | [Unsplash](https://unsplash.com/photos/a-black-and-white-photo-of-a-snow-covered-mountain-rSYsP5TlEAk) |
| ![雨林山路](../app/src/main/assets/wallpapers/bundled/hqzyUL3M21k-thumb.jpg) | 雨林山路 | [Geio Tischler](https://unsplash.com/@oww) | [Unsplash](https://unsplash.com/photos/a-winding-road-in-the-middle-of-a-forest-hqzyUL3M21k) |
| ![热带绿叶](../app/src/main/assets/wallpapers/bundled/4axNezh03UA-thumb.jpg) | 热带绿叶 | [Łukasz Rawa](https://unsplash.com/@lukasz_rawa) | [Unsplash](https://unsplash.com/photos/green-leaves-and-foliage-create-a-dark-and-lush-scene-4axNezh03UA) |
| ![山间彩虹](../app/src/main/assets/wallpapers/bundled/vwFvhJf6u_I-thumb.jpg) | 山间彩虹 | [Pascal Debrunner](https://unsplash.com/@debrupas) | [Unsplash](https://unsplash.com/photos/a-rainbow-arches-over-a-majestic-mountain-landscape-vwFvhJf6u_I) |
| ![流光花影](../app/src/main/assets/wallpapers/bundled/JtF_FvXVQQI-thumb.jpg) | 流光花影 | [Patrick Shaun](https://unsplash.com/@xiexianghua) | [Unsplash](https://unsplash.com/photos/a-blurry-photo-of-a-bunch-of-flowers-JtF_FvXVQQI) |

五张原图合计 18,776,212 bytes；显示副本与缩略图共 1,923,618 bytes，缩减 89.8%。

处理：显示副本最长边 1920 像素、JPEG quality 85；缩略图 320 × 180、quality 78；启用 optimize/progressive。原图不变，副本不写入 EXIF 或 GPS 信息。默认每 5 分钟顺序轮播，固定选择功能不变。

作者与来源也记录在 APK 内的 `assets/wallpapers/catalog.json`；资源大小、尺寸及校验值见 [媒体索引](../app/src/main/assets/wallpapers/bundled/MEDIA-INDEX.md)。


## 分支补充：许可复核与电视候选（2026-10-24）

默认图库实际为三张煎饼使原创航拍和上述五张 Unsplash 图片。原创照片按 public-defaults/NOTICE.md 的限定随项目分发授权，不是赠送版权；人物和私人照片不随公开项目分发。归藏代码的 MIT 许可不覆盖外部摄影作品，不能据此复制归藏的任意壁纸。

本次重新读取 Unsplash 官方 License，允许免费复制、修改、分发及商业使用；禁止原样出售图片，以及汇编为同类或竞争服务。本桌面少量附带背景与建立图库下载站是不同用途，后者应重新评估。照片授权不等于人物肖像、商标等其他权利自动获准，优先选无人、无商标自然风景。

五张原页面：雪岭、山路、彩虹和花影本次可打开；绿叶返回 429，沿用索引中 2026-10-20 的核对记录，不声称五张全部已重新在线核验。尚未发现本分支将未经核对的 Wallhaven 图片打包。现有五张无需因 fork 身份而一刀切删除，也不能承诺绝对零风险。

### 新候选：已核对页面许可，尚未下载、裁切或打包

| 方向 | 摄影师 / 来源 | 适配意图与待验项 |
| --- | --- | --- |
| 雾湖 | Valentin Blary，[原页](https://unsplash.com/photos/misty-lake-with-bare-trees-and-reflections-3KmSaZR4fro) | 冷灰、留白；需查看原图分辨率与 16:9 裁切，避免树干落在文字下 |
| 极简沙丘 | Alexander Psiuk，[原页](https://unsplash.com/photos/desert-dunes-stretch-toward-a-serene-clear-sky-cczug9CpFpU) | 暖沙色大色块；需实看亮背景下深色文字和焦点，不承诺照片主题会自动按局部亮度反色 |
| 海面俯拍 | Luke Stackpoole，[原页](https://unsplash.com/photos/aerial-ocean-waves-NEsS80iDVdw) | 青灰、自然纹理；需避免浪花高频细节干扰图标 |

以上原页面均标示 Unsplash License，而非 CC0，也不是“没有版权”。只列出来源和设计候选不等于已完成视觉筛选。若采纳再记录原图尺寸、许可、作者、处理方式和实际文件 SHA-256，加入 catalog 后由现有构建校验把关。不为增加数量扩大 APK。

可用 CC0 备选库：[StockSnap](https://stocksnap.io/license)。Poly Haven 的素材确为 CC0，但多数为 360° HDRI，须正确投影和色调映射，不能当普通横屏照片直接铺满。此次两张 Pixnio 沙丘的页面写 CC0，许可链接实际转向旧 Public Domain Certification，未纳入可打包清单。

电视筛选标准：先保证文字与焦点好认，再追求画面丰富；优先横向、少亮斑、两侧有安静区域。原图足够时只做保真处理，不能把放大尺寸当成真实细节增加。纯浅色主题用深色主体，深色/照片主题用黄色主体及已有背景遮罩；照片不做像素级自动反色。
