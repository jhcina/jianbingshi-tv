# 待机恢复实验

仅支持 MiTV-MFTR0 / OS3.0.101.0.UFTMATV，默认关闭。不要照搬到其他品牌或固件。

## 为什么需要它

2026-10-24 控制试验：关屏前 Home 服务有效；关屏进入 Asleep 后应用被标为 stopped=true，服务列表变空；唤醒仍是小米桌面，开机编号没变。单独设置 HOME 角色仍复现。与 Aerial 的同机型笔记相符，但本分支仍按自身测试结果验收。

## 做了什么

系统重新绑定指定的通知监听服务后，候选在本人已开启恢复开关、用户已解锁、精确机型匹配且权限仍有效时，检查并恢复自己的 Home 服务。保留其他辅助功能条目，不轮询，不读取屏幕节点或通知，不改 ADB，不主动启动 HDMI 或覆盖其他应用。通知监听元数据禁止所有通知类别；两项通知回调为空。

当前仅恢复 Home 服务；完整重启自动进入需另验。不能凭监听服务已绑定或设置写入成功宣称桌面已显示。

## 两项额外授权

安装 APK 本身不会获得这些授权。本人需要分别同意系统设置写入权限 WRITE_SECURE_SETTINGS 与指定通知监听授权；系统设置权限本身范围较广，本分支当前写入限定为自身辅助功能启用项及辅助功能总开关。再在“设置 → 电视与启动 → 启动设置向导 → 待机后恢复（实验）”开启开关。

下列为已审阅候选的配置模板，仅在本人明确同意后执行，把 APP_ID 换成实际安装包名；私人包 io.github.op7418.guizangtv.yi，公开包 io.github.jhcina.yitv。Java 组件命名空间始终是 io.github.op7418.guizangtv，不能把它一起替换为应用 ID。

```text
adb shell pm grant APP_ID android.permission.WRITE_SECURE_SETTINGS
adb shell cmd notification allow_listener APP_ID/io.github.op7418.guizangtv.StandbyRecoveryListener
```

## 关闭与撤销

先在同一页面关闭恢复，或点击“关闭主页自动返回”（会先停用恢复）；此后系统重新绑定也不会恢复 Home 服务。只在系统辅助功能页关闭 Home 服务，可能被仍开启的恢复功能重新启用。

需要彻底撤销时，再执行本人的实际应用 ID：

```text
adb shell cmd notification disallow_listener APP_ID/io.github.op7418.guizangtv.StandbyRecoveryListener
adb shell pm revoke APP_ID android.permission.WRITE_SECURE_SETTINGS
```

这些操作不删照片，也不删除其他辅助功能。若还要恢复原始开机目标，按 STARTUP.md 使用本机备份单独恢复。

## 验收状态

设备本人已明确授权在自己的电视启用实验。连续三次短时关屏／唤醒均保留或恢复自身 Home 服务，唤醒前台为本桌面；其中一次关屏前为原版红果客户端。播放器与 HDMI 在前台时单独停止桌面，监听重新绑定后前台没有被覆盖。这些是短时样本，不代表长期待机、断电、实体遥控或完整重启全部通过。后续结果按兼容记录补充。

来源与许可：[Aerial 同机型笔记](https://github.com/pengchujin/aerial-tv/blob/70c18ff12ae04513f4ce2884c8554971ca8316d7/docs/devices/xiaomi-mitv-mftr0.md)、[Android NotificationListenerService](https://developer.android.com/reference/android/service/notification/NotificationListenerService)。MIT 通知随 APK 保留于 assets/licenses/AERIAL-MIT.txt。
