# 煎饼使 TV · Yi TV

把电视首页留给常用应用和自己的照片。以 [op7418/guizang-tv](https://github.com/op7418/guizang-tv) 为底座的家庭照片桌面分支，保留归藏的大字、壁纸预览和遥控器交互，新增本机相册与看照片模式。

**[下载预览版](https://github.com/jhcina/yi-tv/releases) · [照片导入](docs/PHOTOS.md) · [兼容与恢复](docs/YI-COMPATIBILITY.md) · [来源与许可](NOTICE.md)**

当前为 **0.5.0-yi.preview.1**，适合愿意反馈的用户。它是 Android 电视桌面 App；安装不会刷写电视固件，也不会删除原桌面。

![MFTR0 实机桌面截图](docs/screenshots/home.png)

> 本人电视直出截图；不是客厅现场实拍。

## 这个 fork 改了什么

| 场景 | 归藏已有的基础 | 本分支新增 |
| --- | --- | --- |
| 换自己的照片 | 源码构建时打包照片，设置中预览与选择 | 从系统文件选择器导入；缺少选择器的电视可用电脑导入，无需重新编译 |
| 保留私人照片 | 打包素材由构建者管理 | 默认壁纸与“我的壁纸”分开存储、分开轮播，私人照片不进入源码和 APK |
| 在客厅看照片 | 照片作为桌面背景 | “看照片”收起桌面文字；左右换图，确定/返回回桌面；竖幅导入保留完整画面 |
| 用遥控器辨认焦点 | 文字高亮与毛玻璃 | 黄色圆角纸片、左上光源、右下阴影；另绘 App 图标 |
| 第一次安装 | 开发者预设入口 | 根据本机已安装应用生成初始入口；以后更新保留用户编辑 |
| 小米 S Pro 75 | 上游已有其他小米型号的适配 | 增加 MFTR0 指定固件配置与可选 Home 返回；不把参数套给其他品牌 |

本分支没有独创归藏已有的设置、Dock、增删排序、壁纸预览、天气或 Codex 实验；这些仍属于上游贡献。看照片不是系统屏保，不会接管其他应用或禁止电视休眠。

## 先试用

Android 8.0 / API 26 起可安装，其他电视仍须实测。下载签名预览 APK，通过电视支持的方式安装；已启用 ADB 的用户可执行：

```sh
adb -s <电视设备标识> install -r yi-tv-0.5.0-yi.preview.1.apk
```

打开“煎饼使 TV”。初次使用不要求开启无障碍或修改默认桌面。公开包名 `io.github.jhcina.yitv`，与归藏原版及此前私人试用包分开，设置和私人照片不会自动迁移。预览版后续更新需使用同一签名；自行构建的 Debug 包不保证能覆盖公开包，勿为解决签名冲突直接卸载有数据的版本。

- 设置 → 首页内容：添加、排序、隐藏；隐藏不会卸载应用。
- 设置 → 背景 → 我的壁纸 → 导入照片：使用电视的系统文件选择器。
- 设置 → 看照片：收起文字，左右换图，确定或返回退出；有菜单键的遥控器也可从首页按菜单进入。
- 设置 → 背景 → 默认壁纸：分享桌面截图前请在这里选择公开照片，并关闭可能含私人信息的天气/Codex 浮层；“我的壁纸”不是加密相册。
- 设置 → 电视与启动：查看原桌面与 Home 设置。Home 返回服务默认关闭，只在已适配的小米固件上工作，须本人从系统设置开启；不读取屏幕节点、不过滤遥控器按键。

天气与 Codex 延续上游，默认关闭；基础桌面及本机照片不要求账号。实验功能的上游说明见 [原版 README 存档](docs/UPSTREAM-README.md)，其中下载链接、包名和验证结果属于上游，不代表本 fork 的测试结果。

## 构建

JDK 17、Gradle 8.9 或兼容版本、Android SDK Platform 34 / Build Tools 34.0.0。配置 Android SDK 后：

```sh
gradle :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
python -m pip install 'Pillow>=11,<13'
python -m unittest discover -s scripts -p 'test_*.py'
```

输出 `app/build/outputs/apk/debug/app-debug.apk`。Windows 中文目录如遇工具链路径问题，可将工程映射到空闲盘符，并将 SDK 与 Gradle 缓存放在 ASCII 路径。不要改占用中的盘符。

正式签名仍使用上游的环境变量接口 `GUIZANG_KEYSTORE`、`GUIZANG_STORE_PASSWORD`、`GUIZANG_KEY_ALIAS`、`GUIZANG_KEY_PASSWORD`；密钥在仓库外。`gradle :app:assembleRelease` 未配置签名时仅输出未签名包，不能当作可安装发布包。

构建前会核对公开图片清单与 SHA-256，并拒绝清单外位图；照片导入不使用 `assets/wallpapers/local/`。旧 `prepare_wallpapers.py` 的构建内嵌流程不适用于本 fork，改用 [本机照片导入](docs/PHOTOS.md)。CI 构建的是 Debug 测试产物，Releases 才提供维护者签名预览包。

## 来源与素材

直接 fork 自归藏 main `312c01119a327f402e68818670a6542f1e643e56`，保留完整 Git 历史、MIT 与第三方通知；归藏的基础来源包括 cleanYourTV、CleanFrame 等，见 [NOTICE](NOTICE.md)。本分支由煎饼使提出产品取舍，并由 Codex 协助实现；不是上游官方版本。

代码采用 MIT。三张本人航拍署名“煎饼使”，按[壁纸再分发许可](app/src/main/assets/wallpapers/public-defaults/NOTICE.md)随项目使用；其余五张照片保留 Unsplash 来源和许可，均不因代码 MIT 而变成 MIT 素材。人物照片与 AI 重建室内图不在公开分发中。

反馈时请提供机型、固件和复现步骤，去掉账号、网络地址、序列号与私人照片。当前已知验证范围见 [兼容性记录](docs/YI-COMPATIBILITY.md)。
