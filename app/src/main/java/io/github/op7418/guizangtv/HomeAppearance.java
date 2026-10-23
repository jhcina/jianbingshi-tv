package io.github.op7418.guizangtv;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

/** Local appearance choices; defaults preserve the original photo/list home. */
final class HomeAppearance {
    final SharedPreferences prefs;
    HomeAppearance(Context c) {
        prefs = c.getSharedPreferences("home-appearance", Context.MODE_PRIVATE);
        if(!prefs.contains("yi_defaults_applied"))prefs.edit().putBoolean("yi_defaults_applied",true)
                .putInt("font",1).putInt("dim",0).putInt("focus",1).apply();
    }
    int get(String key) { return AppearanceValues.normalize(key, prefs.getInt(key, AppearanceValues.defaultValue(key))); }
    boolean set(String key, int value) { return prefs.edit().putInt(key, AppearanceValues.normalize(key, value)).commit(); }
    boolean light() { return get("background") == 1; }
    int ink() { return light() ? 0xFF262A24 : 0xFFFFFDF7; }
    float faded() { return new float[]{.90f, .78f, .66f}[get("dim")]; }
    Typeface typeface() {
        return Typeface.create(new String[]{"sans-serif-light", "sans-serif", "serif"}[get("font")], Typeface.NORMAL);
    }
    float size() { return new float[]{32, 38, 42}[get("size")]; }
}
