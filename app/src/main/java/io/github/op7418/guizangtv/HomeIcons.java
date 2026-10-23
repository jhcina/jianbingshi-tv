package io.github.op7418.guizangtv;

import android.content.Context;
import android.graphics.drawable.Drawable;

/** Optional local artwork with monochrome vector fallbacks; see assets/icons/SOURCES.json. */
final class HomeIcons {
    private static final android.util.LruCache<Integer,android.graphics.Bitmap> ART=new android.util.LruCache<>(8);
    static Drawable themed(Context context,String id){
        String name=IconArtwork.name(id);if(name==null)return null;
        int resource=context.getResources().getIdentifier(name,"drawable",context.getPackageName());
        if(resource==0)return null;
        android.graphics.Bitmap bitmap=ART.get(resource);
        if(bitmap==null){
            android.graphics.BitmapFactory.Options options=new android.graphics.BitmapFactory.Options();
            options.inJustDecodeBounds=true;android.graphics.BitmapFactory.decodeResource(context.getResources(),resource,options);
            int target=Math.max(64,Math.round(64*context.getResources().getDisplayMetrics().density));
            options.inSampleSize=IconArtwork.sample(options.outWidth,options.outHeight,target);
            options.inJustDecodeBounds=false;options.inScaled=false;
            bitmap=android.graphics.BitmapFactory.decodeResource(context.getResources(),resource,options);
            if(bitmap==null)return null;ART.put(resource,bitmap);
        }
        return new android.graphics.drawable.BitmapDrawable(context.getResources(),bitmap);
    }
    static Drawable get(Context context,String id,int color){
        int resource;
        switch(id){
            case "hdmi:2":resource=R.drawable.mark_playstation;break;
            case "hdmi:1":resource=R.drawable.mark_appletv;break;
            case "com.xiaodianshi.tv.yst":resource=R.drawable.mark_bilibili;break;
            case "com.xiaomi.mitv.smartshare":resource=R.drawable.mark_broadcast;break;
            case "com.mitv.gallery":resource=R.drawable.mark_image;break;
            case "com.mitv.tvhome":resource=R.drawable.mark_house;break;
            case "control:assistant":resource=R.drawable.mark_chat_circle_dots;break;
            case "control:settings":resource=R.drawable.mark_gear_six;break;
            case "control:inputs":resource=R.drawable.mark_monitor_play;break;
            case "control:apps":resource=R.drawable.mark_squares_four;break;
            default:resource=id.startsWith("hdmi:")?R.drawable.mark_monitor_play:R.drawable.mark_app_window;
        }
        Drawable mark=context.getDrawable(resource).mutate();mark.setTint(color);return mark;
    }
}
