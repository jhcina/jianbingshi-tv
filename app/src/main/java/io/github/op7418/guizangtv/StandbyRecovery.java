package io.github.op7418.guizangtv;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.UserManager;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;

/** Optional MFTR0-only recovery. No polling, ADB changes or activity launches. */
final class StandbyRecovery {
    private static final String PREFS = "standby-recovery";
    static boolean supported() { return XiaomiProfile.mftr0(Build.MODEL, Build.VERSION.INCREMENTAL); }
    static boolean enabled(Context c) {
        UserManager users = c.getSystemService(UserManager.class);
        return users != null && users.isUserUnlocked()
                && c.getSharedPreferences(PREFS, 0).getBoolean("enabled", false);
    }
    static boolean permitted(Context c) {
        return c.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
    }
    static ComponentName listener(Context c) { return new ComponentName(c, StandbyRecoveryListener.class); }
    static boolean listenerAllowed(Context c) {
        String entries = Settings.Secure.getString(c.getContentResolver(), "enabled_notification_listeners");
        if (entries == null) return false;
        for (String entry : entries.split(":")) {
            if (listener(c).equals(ComponentName.unflattenFromString(entry))) return true;
        }
        return false;
    }
    static void disable(Context c) {
        c.getSharedPreferences(PREFS, 0).edit().putBoolean("enabled", false).commit();
        StandbyRecoveryListener.unbind();
    }
    static void recover(Context c) {
        if (!supported() || !enabled(c) || !permitted(c) || !listenerAllowed(c)) return;
        String own = new ComponentName(c, HomeReturnService.class).flattenToString();
        String current = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        String next = StandbyRecoveryPolicy.appendService(current, own);
        try {
            if (!next.equals(current) && !Settings.Secure.putString(c.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, next)) return;
            if (Settings.Secure.getInt(c.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0) != 1)
                Settings.Secure.putInt(c.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 1);
            c.getSharedPreferences(PREFS, 0).edit().putInt("recoveredBoot",
                    Settings.Global.getInt(c.getContentResolver(), Settings.Global.BOOT_COUNT, -1)).apply();
            android.util.Log.i("JianbingshiTV.Recovery", "Listener rebound; own Home service checked");
        } catch (SecurityException unavailable) {
            android.util.Log.w("JianbingshiTV.Recovery", "Owner permission unavailable", unavailable);
        }
    }
    static void show(Activity a) {
        String explanation = "仅用于已适配小米固件的待机恢复，默认关闭。\n"
                + "需要一次电脑授予系统设置写入权限，并由本人允许通知监听。代码不读取通知，不改 ADB，不主动切换 HDMI 或其他应用。\n"
                + "系统设置权限：" + (permitted(a) ? "已授予" : "未授予")
                + "\n通知监听：" + (listenerAllowed(a) ? "已允许" : "未允许")
                + "\n关闭请用本页或“关闭主页自动返回”；只在系统中关闭辅助功能，可能被已开启的恢复功能重新启用。";
        TvPanel.show(a, "待机后恢复（实验）", explanation,
                new String[]{enabled(a) ? "关闭待机恢复" : "开启待机恢复", "通知监听系统设置"}, i -> {
                    if (i == 1) {
                        try { a.startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)); }
                        catch (RuntimeException absent) { android.widget.Toast.makeText(a,"系统未提供此入口，请按启动指南配置",android.widget.Toast.LENGTH_LONG).show(); }
                        return;
                    }
                    if (enabled(a)) { disable(a); return; }
                    if (!supported() || !permitted(a) || !listenerAllowed(a)) {
                        android.widget.Toast.makeText(a,"尚未完成本人授权，请先按启动指南配置；未修改权限",android.widget.Toast.LENGTH_LONG).show(); return;
                    }
                    a.getSharedPreferences(PREFS,0).edit().putBoolean("enabled",true).commit();
                    recover(a);
                    NotificationListenerService.requestRebind(listener(a));
                }, 1);
    }
}
