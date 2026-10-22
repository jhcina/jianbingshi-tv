package io.github.op7418.guizangtv;

final class HomeReturnPolicy {
    static boolean matches(String pkg, String activity, boolean paused) {
        return !paused && "com.mitv.tvhome".equals(pkg)
                && ("com.mitv.tvhome.MainActivityUserMode".equals(activity)
                || "com.mitv.tvhome.MainActivity".equals(activity));
    }
}
