package io.github.op7418.guizangtv;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.UserManager;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;

/** Local opt-in HOME handoff. Never reads view nodes or filters any remote key. */
public final class HomeReturnService extends AccessibilityService {
    private static HomeReturnService connected;
    @Override protected void onServiceConnected() { connected=this; }
    static void pause(Context c,boolean paused){c.getSharedPreferences("home-return",0).edit().putBoolean("paused",paused).apply();}
    static boolean enabled(Context c){String value=Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);return value!=null&&value.contains(c.getPackageName()+"/");}
    static void disable(Context c){if(connected!=null)connected.disableSelf();else openSettings(c);}
    static void openSettings(Context c){
        try{c.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));}
        catch(RuntimeException unavailable){android.widget.Toast.makeText(c,"请从电视系统设置打开辅助功能",android.widget.Toast.LENGTH_LONG).show();}
    }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!getSystemService(UserManager.class).isUserUnlocked()
                ||!XiaomiProfile.mftr0(Build.MODEL,Build.VERSION.INCREMENTAL))return;
        String pkg=event.getPackageName()==null?"":event.getPackageName().toString();
        String activity=event.getClassName()==null?"":event.getClassName().toString();
        if(event.getEventType()!=AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                ||!HomeReturnPolicy.matches(pkg,activity,getSharedPreferences("home-return",0).getBoolean("paused",false)))return;
        try{startActivity(new Intent(this,FrameHomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));}
        catch(RuntimeException unavailable){android.util.Log.w("YiTV.Home","Home handoff unavailable",unavailable);}
    }
    @Override public void onDestroy(){if(connected==this)connected=null;super.onDestroy();}
    @Override public void onInterrupt(){}
}
