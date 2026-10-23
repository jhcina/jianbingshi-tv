package io.github.op7418.guizangtv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import org.json.JSONArray;
import java.util.*;

/** Stable app/source identities; removing an entry never uninstalls an app. */
final class HomeItems {
    static final String[] DEFAULTS = {"com.xiaodianshi.tv.yst", "com.ktcp.video",
            "com.zentraedi.zspace.tv", "com.xiaomi.mitv.mediaexplorer"};
    final Context context;
    final LinkedHashMap<String,String> names = new LinkedHashMap<>();
    final List<String> order = new ArrayList<>();
    HomeItems(Context c) {
        context = c;
        String[] labels = {"哔哩哔哩", "腾讯视频", "极空间", "文件管理"};
        for (int i=0;i<DEFAULTS.length;i++) names.put(DEFAULTS[i], labels[i]);
        discover();
        try {
            JSONArray data = new JSONArray(c.getSharedPreferences("home-items",0).getString("order","[]"));
            for (int i=0;i<data.length() && order.size()<9;i++) {
                String id = data.getString(i);
                if (validId(id) && !order.contains(id)) order.add(id);
            }
        } catch (Exception ignored) { order.clear(); }
        // One-time consolidation: system settings remain under the single Settings entry.
        android.content.SharedPreferences prefs=c.getSharedPreferences("home-items",0);
        if(!prefs.getBoolean("settings_consolidated_v1",false)){
            List<String> compact=new ArrayList<>(order);compact.removeIf(SettingsNavigation::systemSettings);
            android.content.SharedPreferences.Editor edit=prefs.edit().putBoolean("settings_consolidated_v1",true);
            if(!compact.isEmpty()&&!compact.equals(order)){
                edit.putString("before_settings_consolidation",new JSONArray(order).toString());
                edit.putString("order",new JSONArray(compact).toString());
            }
            if(edit.commit()&&!compact.isEmpty()){order.clear();order.addAll(compact);}
        }
        if (order.isEmpty()) {
            for(String id:DEFAULTS)if(available(id))order.add(id);
            List<String> installed=new ArrayList<>(names.keySet());
            installed.sort(java.text.Collator.getInstance(Locale.CHINA));
            for(String id:installed)if(order.size()<4&&!order.contains(id)&&available(id))order.add(id);
            if(order.isEmpty()){names.put("com.android.settings","电视设置");order.add("com.android.settings");}
        }
    }
    private boolean available(String id) {
        if(id.startsWith("hdmi:"))return false;
        if(id.equals("com.android.settings")){
            Intent settings=XiaomiControls.settings(context,android.provider.Settings.ACTION_SETTINGS);
            return settings!=null&&settings.resolveActivity(context.getPackageManager())!=null;
        }
        if(id.equals("com.xiaomi.mitv.mediaexplorer"))return context.getPackageManager().resolveActivity(
                new Intent("duokan.mediaexplorer.intent.action.BROWSE_DEVICE_LIST").setPackage(id),0)!=null;
        return context.getPackageManager().getLeanbackLaunchIntentForPackage(id)!=null
                ||context.getPackageManager().getLaunchIntentForPackage(id)!=null;
    }
    void discover() {
        Intent system=XiaomiControls.settings(context,android.provider.Settings.ACTION_SETTINGS);
        if(system!=null&&system.resolveActivity(context.getPackageManager())!=null)names.put("com.android.settings","电视系统设置");
        if (XiaomiControls.enabled()) {
            names.put("hdmi:1", "HDMI 1"); names.put("hdmi:2", "HDMI 2");
            if (XiaomiProfile.mftr0(android.os.Build.MODEL, android.os.Build.VERSION.INCREMENTAL)) names.put("hdmi:3", "HDMI 3");
            names.put("com.xiaomi.mitv.mediaexplorer", "文件管理");
        }
        for (String category : new String[]{Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER}) {
            for (ResolveInfo r : context.getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(category), 0)) {
                String id = r.activityInfo.packageName;
                if (!id.equals(context.getPackageName()) && !SettingsNavigation.systemSettings(id) && r.activityInfo.enabled && r.activityInfo.exported)
                    names.putIfAbsent(id, SettingsNavigation.systemSettings(id)?"电视系统设置":XiaomiControls.label(id, r.loadLabel(context.getPackageManager()).toString()));
            }
        }
    }
    String name(String id) { return names.getOrDefault(id, context.getSharedPreferences("home-items",0).getString("name:"+id,id)); }
    static boolean validId(String id) { return id.equals("hdmi:1") || id.equals("hdmi:2") || id.equals("hdmi:3") || id.matches("[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z0-9_]+)+"); }
    boolean save(List<String> next) {
        if (next.isEmpty() || next.size()>9 || new HashSet<>(next).size()!=next.size()) return false;
        for(String id : next) if(!validId(id)) return false;
        android.content.SharedPreferences.Editor e = context.getSharedPreferences("home-items",0).edit()
                .putString("order",new JSONArray(next).toString());
        for(String id:next) e.putString("name:"+id,name(id));
        if(!e.commit()) return false;
        order.clear(); order.addAll(next); return true;
    }
}
