package io.github.op7418.guizangtv;

import android.app.role.RoleManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

/** Read actual system state; request owner consent instead of silently changing defaults. */
final class StartupSetup {
    static boolean configured(Context c){return c.getPackageName().equals(Settings.System.getString(c.getContentResolver(),"start_3rd_app"));}
    static boolean defaultHome(Context c){
        android.content.pm.ResolveInfo home=c.getPackageManager().resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),PackageManager.MATCH_DEFAULT_ONLY);
        return home!=null&&home.activityInfo!=null&&c.getPackageName().equals(home.activityInfo.packageName);
    }
    static String bootSummary(Context c){
        if(!XiaomiControls.enabled())return "按系统默认桌面设置；本机开机适配未验证";
        boolean signal="1".equals(Settings.System.getString(c.getContentResolver(),"boot_auto_play"));
        if(!configured(c))return "还需电脑完成一次配置；仅开开关不会生效";
        if(signal)return "启动目标已配置，但开机信号源优先仍开启";
        return BootStart.enabled(c)?"配置与开关已开启；仍需重启验收":"系统目标已配置；应用开关未开启";
    }
    static void requestDefault(FrameHomeActivity a){
        try{
            if(Build.VERSION.SDK_INT>=29){
                RoleManager roles=a.getSystemService(RoleManager.class);
                if(roles!=null&&roles.isRoleAvailable(RoleManager.ROLE_HOME)&&!roles.isRoleHeld(RoleManager.ROLE_HOME)){
                    a.startActivityForResult(roles.createRequestRoleIntent(RoleManager.ROLE_HOME),711);return;
                }
            }
            Intent settings=new Intent(Settings.ACTION_HOME_SETTINGS);
            if(settings.resolveActivity(a.getPackageManager())!=null){a.startActivityForResult(settings,711);return;}
        }catch(RuntimeException unavailable){android.util.Log.i("JianbingshiTV.Startup","System default-home chooser unavailable");}
        a.message("系统未开放默认桌面选择。请查看启动指南，按机型完成一次配置。");
    }
    static boolean anotherEdition(Context c){
        String other=c.getPackageName().equals("io.github.jhcina.yitv")?"io.github.op7418.guizangtv.yi":"io.github.jhcina.yitv";
        try{c.getPackageManager().getApplicationInfo(other,0);return true;}catch(PackageManager.NameNotFoundException absent){return false;}
    }
}
