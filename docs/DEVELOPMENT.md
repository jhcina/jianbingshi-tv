# 开发与构建


直接 fork 自归藏 `312c01119a327f402e68818670a6542f1e643e56`，保留历史、MIT 和第三方通知；基础来源包括 CleanYourTV、CleanFrame 等，具体见 NOTICE。项目由煎饼使提出产品取舍，Codex 协助实现，不代表上游或小米官方。

JDK 17、Gradle 8.9 或兼容版本、Android SDK Platform 34 / Build Tools 34.0.0：

```sh
gradle :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
python -m pip install 'Pillow>=11,<13'
python -m unittest discover -s scripts -p 'test_*.py'
```

构建前校验默认图片 catalog 与 SHA-256，拒绝清单外位图。Debug 构建产物不是维护者签名的公开下载；Release 签名使用 `GUIZANG_KEYSTORE`、`GUIZANG_STORE_PASSWORD`、`GUIZANG_KEY_ALIAS`、`GUIZANG_KEY_PASSWORD`，密钥留在仓库外。Windows 中文路径遇工具链限制时可用空闲盘符映射，不修改已占用盘符。

每次公开版本仍需检查签名、来源、私密素材和源码对应关系；构建通过不能替代设备、长期待机或主观音画验收。反馈请写型号、固件和复现步骤，不公开账号、网络地址、序列号或私人照片。
