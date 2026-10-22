package io.github.op7418.guizangtv;

import android.app.*;
import android.content.*;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.text.*;
import java.util.*;

/** Native TV home and one grouped Settings entry. */
public final class FrameHomeActivity extends Activity implements HomeSurface.Actions {
    final Handler handler = new Handler(Looper.getMainLooper());
    HomeAppearance appearance;
    HomeItems items;
    FrameWallpapers wallpapers;
    HomeSettings settings;
    HomeWeather weather;
    HomeAssistant assistant;
    HomeSurface surface;
    private FrameLayout root;
    private ImageView photo, transition;
    private AlertDialog library;
    private boolean resumed;
    private int selected;
    private boolean photoMode, importing;
    private TextView photoHint;
    private static final int IMPORT_PHOTO = 710;
    private final Runnable hidePhotoHint = () -> {if(photoHint!=null)photoHint.setVisibility(View.GONE);};
    private final Runnable tick = new Runnable(){public void run(){
        String time=new SimpleDateFormat("HH:mm",Locale.CHINA).format(new Date());
        if(surface!=null)surface.clock.setText(time);
        if(settings!=null)settings.time(time);
        handler.postDelayed(this,60000-System.currentTimeMillis()%60000);
    }};
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        appearance=new HomeAppearance(this);items=new HomeItems(this);
        selected=getSharedPreferences("frame-home",0).getInt("selected",0);
        root=new FrameLayout(this);photo=new ImageView(this);transition=new ImageView(this);
        photo.setScaleType(ImageView.ScaleType.CENTER_CROP);transition.setScaleType(ImageView.ScaleType.CENTER_CROP);transition.setAlpha(0);
        root.addView(photo,new FrameLayout.LayoutParams(-1,-1));root.addView(transition,new FrameLayout.LayoutParams(-1,-1));
        wallpapers=new FrameWallpapers(this,photo,transition);
        weather=new HomeWeather(this,this::weatherChanged);
        settings=new HomeSettings(this);
        assistant=new HomeAssistant(this);
        wallpapers.changed=()->{if(surface!=null)surface.wallpaper(wallpaperDrawable());};
        setContentView(root);refreshHome();surface.focusApp();
    }
    void refreshHome(){
        int restore=Math.max(0,Math.min(items.order.size()-1,selected));
        selected=restore;
        if(surface!=null)root.removeView(surface);
        root.setBackgroundColor(appearance.light()?0xFFF0EFE9:0xFF11151B);
        photo.setVisibility(appearance.get("background")==0?View.VISIBLE:View.INVISIBLE);
        transition.setVisibility(photo.getVisibility());
        surface=new HomeSurface(this,appearance,items,wallpaperDrawable(),restore,false,this);
        root.addView(surface,new FrameLayout.LayoutParams(-1,-1));
        surface.setVisibility(photoMode?View.INVISIBLE:View.VISIBLE);
        surface.apps.get(restore).requestFocus();
        selected=restore;
        handler.removeCallbacks(tick);tick.run();weatherChanged();updateRotation();
    }
    HomeSurface preview(){HomeSurface view=new HomeSurface(this,appearance,items,wallpaperDrawable(),selected,true,this);view.weather(weather.current());return view;}
    void weatherChanged(){if(surface!=null)surface.weather(weather.current());if(settings!=null)settings.weatherChanged();}
    Drawable wallpaperDrawable(){return photo.getDrawable();}
    void updateRotation(){if(weather!=null)weather.active(resumed&&!settings.showing()&&(assistant==null||!assistant.showing())&&(library==null||!library.isShowing()));if(resumed&&appearance.get("background")==0&&!settings.showing()&&(assistant==null||!assistant.showing())&&(library==null||!library.isShowing()))wallpapers.resume();else wallpapers.pause();}
    @Override public void selected(int index){selected=index;getSharedPreferences("frame-home",0).edit().putInt("selected",index).apply();}
    @Override public void open(String id){
        if("com.android.settings".equals(id)){systemSettings();return;}
        if(id.startsWith("hdmi:")){if(!XiaomiControls.openHdmi(this,Integer.parseInt(id.substring(5))))message("暂时无法切换信号源");return;}
        if("com.mitv.tvhome".equals(id)){stockHome();return;}
        if("com.xiaomi.mitv.mediaexplorer".equals(id)) {
            launch(new Intent("duokan.mediaexplorer.intent.action.BROWSE_DEVICE_LIST")
                    .setPackage(id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),"暂时无法打开文件管理");return;
        }
        Intent intent=getPackageManager().getLeanbackLaunchIntentForPackage(id);
        if(intent==null)intent=getPackageManager().getLaunchIntentForPackage(id);
        launch(intent,"应用暂时不可用");
    }
    @Override public void control(int index){
        if(index==0)showLibrary();
        else if(index==1){if(!XiaomiControls.showInputs(this))message("暂时无法打开信号源");}
        else if(index==2)settings.show();
        else if(index==3&&HomeAssistant.enabled(this))assistant.show();
    }
    void stockHome(){HomeReturnService.pause(this,true);if(!XiaomiControls.startStockHome(this,new ComponentName("com.mitv.tvhome","com.mitv.tvhome.MainActivity"))){HomeReturnService.pause(this,false);message("暂时无法打开小米原桌面");}}
    void systemSettings(){Intent i=XiaomiControls.settings(this,Settings.ACTION_SETTINGS);if(i==null)i=new Intent(Settings.ACTION_SETTINGS);launch(i,"暂时无法打开设置");}
    private void launch(Intent i,String error){if(i==null){message(error);return;}try{startActivity(i);}catch(RuntimeException unavailable){message(error);}}
    private void showLibrary(){
        items.discover();List<String> ids=new ArrayList<>(items.names.keySet());ids.removeIf(id->id.startsWith("hdmi:") || (!id.equals("com.xiaomi.mitv.mediaexplorer") && getPackageManager().getLeanbackLaunchIntentForPackage(id)==null && getPackageManager().getLaunchIntentForPackage(id)==null));
        Collator collator=Collator.getInstance(Locale.CHINA);ids.sort((a,b)->collator.compare(items.name(a),items.name(b)));
        String[] labels=new String[ids.size()];for(int i=0;i<labels.length;i++)labels[i]=items.name(ids.get(i));
        library=new AlertDialog.Builder(this).setTitle("全部应用").setItems(labels,(d,i)->open(ids.get(i))).setNegativeButton("返回",null).create();
        library.setOnDismissListener(d->{surface.controls.get(0).requestFocus();updateRotation();});library.show();updateRotation();
    }
    @Override protected void onResume(){super.onResume();HomeReturnService.pause(this,false);resumed=true;assistant.active(true);BootStart.confirm(this);handler.removeCallbacks(tick);tick.run();updateRotation();}
    @Override protected void onNewIntent(Intent i){super.onNewIntent(i);setIntent(i);if(photoMode)leavePhotoMode();}
    @Override protected void onPause(){resumed=false;assistant.active(false);weather.active(false);handler.removeCallbacks(tick);wallpapers.pause();if(library!=null)library.dismiss();super.onPause();}
    @Override protected void onDestroy(){assistant.destroy();settings.close();weather.destroy();wallpapers.destroy();handler.removeCallbacksAndMessages(null);super.onDestroy();}
    @Override public void onBackPressed(){if(photoMode){leavePhotoMode();return;}if(surface.controls.contains(getCurrentFocus()))surface.focusApp();else if(XiaomiControls.enabled())stockHome();else super.onBackPressed();}
    @Override public void dump(String prefix,java.io.FileDescriptor fd,java.io.PrintWriter out,String[] args){
        super.dump(prefix,fd,out,args);wallpapers.dump(out);BootStart.dump(this,out);weather.dump(out);
        out.println("HomeAppearance: "+appearance.prefs.getAll()+" items="+items.order+" settings="+settings.page());
    }
    void message(String value){Toast.makeText(this,value,Toast.LENGTH_LONG).show();}
    void importPhoto(){
        if(importing){message("照片正在导入，请稍候");return;}
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        android.content.pm.ResolveInfo picker=getPackageManager().resolveActivity(intent,0);
        if(picker!=null&&"com.android.tv.frameworkpackagestubs.Stubs$DocumentsStub".equals(picker.activityInfo.name)){
            message("这台电视只提供占位文件选择器，请用电脑导入。方法见项目的照片指南。");return;
        }
        try{startActivityForResult(intent,IMPORT_PHOTO);}
        catch(ActivityNotFoundException unavailable){message("这台电视缺少系统文件选择器。可用电脑导入，方法见项目的照片指南。");}
    }
    @Override protected void onActivityResult(int request,int result,Intent data){
        super.onActivityResult(request,result,data);
        if(request!=IMPORT_PHOTO||result!=RESULT_OK||data==null||data.getData()==null)return;
        if(importing)return;
        importing=true;message("正在导入到我的壁纸…");
        android.net.Uri uri=data.getData();
        new Thread(()->{
            String error=null;
            try{PhotoImporter.add(getApplicationContext(),uri);}
            catch(Exception failure){error="导入失败：请选择有效图片，并确认图库未满 30 张。";}
            final String outcome=error;
            handler.post(()->{
                importing=false;if(isDestroyed()||isFinishing())return;
                if(outcome!=null){message(outcome);return;}
                wallpapers.reloadPrivate();settings.imported();message("已加入我的壁纸，尚未切换当前壁纸");
            });
        },"LocalPhotoImport").start();
    }
    void showPhotoMode(){
        if(appearance.get("background")!=0){message("先在背景中选择照片，再进入看照片");return;}
        settings.close();photoMode=true;surface.setVisibility(View.INVISIBLE);
        if(photoHint==null){
            photoHint=new TextView(this);photoHint.setText("左右  换照片    确定 / 返回  回桌面");
            photoHint.setTextSize(15);photoHint.setTextColor(0xFFFFFFFF);photoHint.setGravity(Gravity.CENTER);
            photoHint.setBackground(WallpaperMaterial.rounded(0xC0151719,dp(18)));
            FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(dp(390),dp(42),Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
            p.bottomMargin=dp(30);root.addView(photoHint,p);
        }
        photoHint.setVisibility(View.VISIBLE);photoHint.bringToFront();
        handler.removeCallbacks(hidePhotoHint);handler.postDelayed(hidePhotoHint,4000);
        updateRotation();
    }
    private void leavePhotoMode(){
        photoMode=false;handler.removeCallbacks(hidePhotoHint);
        if(photoHint!=null)photoHint.setVisibility(View.GONE);
        surface.setVisibility(View.VISIBLE);surface.focusApp();updateRotation();
    }
    @Override public boolean dispatchKeyEvent(KeyEvent event){
        int key=event.getKeyCode();
        if(photoMode&&(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN))return true;
        if(photoMode&&(key==KeyEvent.KEYCODE_BACK||key==KeyEvent.KEYCODE_DPAD_CENTER
                ||key==KeyEvent.KEYCODE_ENTER||key==KeyEvent.KEYCODE_MENU
                ||key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT)){
            if(event.getAction()==KeyEvent.ACTION_UP){
                if(key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT)wallpapers.step(key==KeyEvent.KEYCODE_DPAD_LEFT?-1:1);
                else leavePhotoMode();
            }
            return true;
        }
        if(key==KeyEvent.KEYCODE_MENU&&!settings.showing()&&!assistant.showing()&&(library==null||!library.isShowing())){
            if(event.getAction()==KeyEvent.ACTION_UP)showPhotoMode();return true;
        }
        return super.dispatchKeyEvent(event);
    }
    int dp(float v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
