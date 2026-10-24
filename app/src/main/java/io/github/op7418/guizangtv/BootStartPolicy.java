package io.github.op7418.guizangtv;

final class BootStartPolicy {
    private BootStartPolicy() { }
    /** A previous boot's success must never appear as evidence for this boot. */
    static String evidenceSummary(int boot, int attempted, int confirmed) {
        if (boot < 0) return "无法读取本次开机编号，结果待确认";
        if (confirmed == boot) return "本次开机已确认进入桌面；待机保持仍需另测";
        if (attempted == boot) return "本次开机已收到启动调用，尚未确认桌面显示";
        if (attempted >= 0 || confirmed >= 0) return "仅有此前开机记录，本次尚未记录启动调用";
        return "尚未记录启动调用，不能确认自动进入";
    }
    static String skipReason(boolean supported, boolean enabled, int boot, int attempted, long elapsed) {
        if (!supported) return "当前机型未适配，已跳过";
        if (!enabled) return "开机自动进入已关闭";
        if (boot < 0) return "无法确认本次启动，已跳过";
        if (boot == attempted) return "本次开机已尝试，未重复打开";
        if (elapsed < 0 || elapsed > 10 * 60_000L) return "启动通知过晚，已跳过以免打断使用";
        return null;
    }
}
