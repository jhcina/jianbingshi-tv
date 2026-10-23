package io.github.op7418.guizangtv;

final class SettingsNavigation {
    static String parent(String page){
        if(page.equals("startup"))return "tv";
        if(page.equals("city"))return "weather";
        if(page.equals("wallpaper")||page.equals("private-wallpaper")||page.equals("rotation"))return "background";
        if(page.startsWith("item:")||page.equals("add"))return "home";
        if(page.equals("layout")||page.equals("type")||page.equals("focus"))return "appearance";
        if(page.equals("weather")||page.equals("experiments"))return "more";
        return "overview";
    }
    static boolean systemSettings(String id){
        return id.equals("com.android.settings")||id.equals("com.android.tv.settings")||id.equals("com.xiaomi.mitv.settings");
    }
}
