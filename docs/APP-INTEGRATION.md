# 应用整合与分发边界

核对日期：2026-10-23。本文记录已发现的授权条件与当前产品选择，不保证未核对应用、后续版本或节目内容的权利状态。

## 先分清三件事

| 做法 | 当前结论 | 发布条件 |
| --- | --- | --- |
| 融合归藏与 CleanYourTV 的桌面代码 | 两者目前代码使用 MIT，可以按其许可修改、融合和分发 | 保留版权和许可；标明来源和改动；字体、图标、照片及第三方依赖分别核对 |
| 桌面识别用户已安装的应用、提供启动入口 | 本分支已有此能力，不需要把第三方 APK 放进桌面 | 不冒充应用官方，不复制其节目、海报或商标素材作自己的品牌资产 |
| 把第三方 APK 打进公开下载包 | 不能因免费下载或托管在 GitHub 就默认获准 | 逐一确认二进制再分发权、完整适用条款、组件许可及服务条件；保持原签名并明确来源 |

GitHub 允许 fork 公开仓库，并不替作者授予任意再分发权。保留一个归藏 fork 的关系，同时在 NOTICE 记录其他代码来源即可，不需要伪造多个 fork 父项目。参考布局思想也不等于可以复制所有图像、字体与品牌资产。

来源：[GitHub 许可说明](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/licensing-a-repository)、[GitHub 服务条款](https://docs.github.com/en/site-policy/github-terms/github-terms-of-service)、[CleanYourTV LICENSE](https://github.com/Gathin23/cleanyourtv/blob/main/LICENSE)、[归藏 LICENSE](https://github.com/op7418/guizang-tv/blob/main/LICENSE)。

## 红果电视端的已知情况

[仓库](https://github.com/waligoraamodio288-rgb/hongguo-desktop-releases)是公开发行和反馈仓库，README 明确应用源码没有开源。[电视指南](https://github.com/waligoraamodio288-rgb/hongguo-desktop-releases/blob/main/TV.md)说明由渠道有数独立维护，与红果官方无隶属关系；不能在本项目中称其为官方客户端或开源应用。

核对版本为 [tv-v0.5.8 测试版](https://github.com/waligoraamodio288-rgb/hongguo-desktop-releases/releases/tag/tv-v0.5.8)。随包 ORIGIN-NOTICE 欢迎转载介绍并要求保留署名、原版链接，要求第三方镜像表明身份；尚未找到明确覆盖本项目所设想的 APK 捆绑、修改、商业分发等场景的完整授权。随包 Apache 2.0 文本对应 JNA 等组件，不代表整个闭源应用采用 Apache 2.0。现阶段不把它的 APK、DEX、海报或剧目资源提交到本仓库或公开安装包。

个人设备安装原版与公开捆绑分发分别处理。原版 ARM64 附件离线核对：包名 cn.hongguo.tv，versionCode 18，最低 API 21，target API 35，原生架构 arm64-v8a；声明联网和请求安装包权限。后者用于更新时的安装请求，不代表可以悄悄安装其他应用。

APK SHA-256：`9f48eb5a0a529971d70b5f403cf20c868d5ecac3fba71eecf38a238e08d52e42`，与 GitHub 该附件摘要一致；APK v1/v2/v3 签名验证通过，证书 SHA-256：`c996ebad47f3f9881abbba3947a5a3374800cfe20b500c3a5f554569dc71a5ac`。这证明文件完整性和签名可验证，不证明应用安全或节目授权。不将维护者的平板测试当作电视验收。

私人设备本轮实测支持 armeabi-v7a / armeabi，因此安装的是 ARM32 原包，而不是 ARM64 包。ARM32 摘要 `7d01aaf4181cfa7373156d2fa7eccb5e8162fcd01c6fdc63e610b505a0630995` 与发行附件一致，签名验证通过、证书与 ARM64 相同；安装成功，首页及方向键选剧可用，看到实际视频画面。测试中有一次取源或准备失败、重试未恢复，换另一部可出画面。不能据此保证所有片源、续集、实体遥控和主观音画效果；不将其列为已全面适配的默认组件。下载附件、节目画面和个人设备日志均只保存在仓库外。

## 在线电视与有线电视

软件许可与节目传播权是两层权利。开源播放器的许可不授予频道、影视剧、海报或节目单的传播权；网络地址可访问也不是授权证明。电视运营商服务还可能要求指定地区、设备、账号或订阅，不承诺桌面能替代其条件。

本项目不附带来路不明的 IPTV 源、共享账号、收费解锁或绕过访问控制的配置。尚未指定并核对具体直播应用，因此没有一个已获批准的“直播应用整合名单”。是否可提供特定应用或播放源，需取得其准确来源及适用授权后逐项判断。

依据：[著作权法（2020 年修正）](https://www.nsfc.gov.cn/p1/2871/2872/69475.html)第十条的复制、发行、广播与信息网络传播等权利。标注来源、免费分享或称作非官方整合包，均不能自行替代所需许可。

## 后续实现的产品选择（未实现安装中心）

先做“可选应用指引”，让用户自己选择需要的应用，跳转其经过核对的原发行入口；已安装的应用可以直接加入常用 Dock。基础桌面仍可独立、离线使用，不把几个外部服务的可用性绑定为桌面的启动条件。

以后只有明确允许再分发的应用，才考虑加入独立可选安装清单：准确版本、原下载地址、包名、ABI、最低系统、文件摘要、签名证书、许可链接、审核日期与更新责任。安装由系统流程取得必要的用户确认；普通桌面 App 不能把其他 App 变成系统预装，也不保证跨电视静默一键安装。参考 [Android PackageInstaller](https://developer.android.com/reference/android/content/pm/PackageInstaller)。更新失败保留已有版本，不通过卸载处理签名冲突，不默认启用未知来源或辅助功能。

本次只完成边界评估和横向 Dock 改进。未制作第三方整合包、未替作者授权、未联系维护者，也未发布新的桌面版本。
