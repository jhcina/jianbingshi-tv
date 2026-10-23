# 首页返回与品牌统一

人体验收反馈：按 Home 返回小米桌面，未回到定制桌面。读取到私人包 home-return 的 paused=true。旧版首页 Back 会调用 stockHome 并写入暂停标记，是已确认的失败路径；不声称它解释待机授权清空。

改为：首页 Back 留在首页；只有明确打开原桌面才暂停；重开意图清除暂停。保留原桌面入口与无障碍权限边界。对外品牌统一为煎饼使TV，仓库/新分发文件名使用 jianbingshi-tv，内部应用身份不变。新版本 0.5.0-preview.2 / versionCode 9。

验证：37 项 Java 单元测试与 lint 通过；三轮真实设备 Back→文件管理→Home 通过，用户真实遥控器确认正常返回。新增 check_home.py 锁定此路径。待机持久性、连续长按音量仍单独跟踪。

Agent: Codex
Change-Id: jianbingshi-tv-20261023-home-back-brand
Agent-Session: 01a16249-d276-76e2-90a8-9b0d69dd9381
