package io.github.op7418.guizangtv;

/** Resource names only; third-party artwork is an optional local build input. */
final class IconArtwork {
    static String name(String id){
        switch(id){
            case "com.zentraedi.zspace.tv":return "yi_icon_zspace";
            case "com.xiaodianshi.tv.yst":return "yi_icon_bilibili";
            case "cn.hongguo.tv":return "yi_icon_hongguo";
            case "control:settings":case "com.android.settings":case "com.android.tv.settings":case "com.xiaomi.mitv.settings":return "yi_icon_settings";
            case "com.mitv.gallery":case "com.xiaomi.tv.gallery":return "yi_icon_photos";
            case "com.xiaomi.mitv.mediaexplorer":return "yi_icon_files";
            case "control:apps":return "yi_icon_apps";
            default:return null;
        }
    }
    static int sample(int width,int height,int target){
        int step=1;
        if(target<=0)return step;
        while(width/(step*2)>=target&&height/(step*2)>=target)step*=2;
        return step;
    }
}
