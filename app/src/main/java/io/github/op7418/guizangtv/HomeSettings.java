package io.github.op7418.guizangtv;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.util.*;

/** Remote-first grouped settings. Focus browses; OK commits; Back returns one level. */
final class HomeSettings {
    private final FrameHomeActivity a;
    private Dialog dialog;
    private String page="overview";
    private final Map<String,Integer> remembered=new HashMap<>();
    private final List<View> choices=new ArrayList<>();
    private final Deque<List<String>> undo=new ArrayDeque<>();
    private LinearLayout options;
    private HomeSurface preview;
    private FrameLayout previewHost;
    private TextView status;
    private boolean rendering;
    private FrameLayout stage;
    private ImageView backdrop;
    private View content;
    private int backdropRequest;
    private int cityRequest;
    private boolean searching;
    private EditText cityInput;
    private String cityQuery="",cityError="";
    private List<HomeWeather.City> cityResults=new ArrayList<>();
    HomeSettings(FrameHomeActivity activity){a=activity;}
    boolean showing(){return dialog!=null&&dialog.isShowing();}
    String page(){return showing()?page:"closed";}
    void imported(){if(showing()){page="private-wallpaper";render();}}
    void time(String time){if(preview!=null)preview.clock.setText(time);}
    void weatherChanged(){if(preview!=null)preview.weather(a.weather.current());if(showing()&&page.equals("weather")&&status!=null)status.setText(a.weather.city()+" · "+a.weather.status());}
    void show(){
        if(showing())return;
        page="overview";undo.clear();
        dialog=new Dialog(a){@Override public void onBackPressed(){back();}};
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnDismissListener(d->{a.wallpapers.cancelPreview();a.wallpapers.cancelSelection();undo.clear();a.updateRotation();a.surface.controls.get(2).requestFocus();});
        dialog.show();dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(-1,-1);
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        stage=new FrameLayout(a);stage.setBackgroundColor(0xFF121619);
        backdrop=new ImageView(a);backdrop.setScaleType(ImageView.ScaleType.CENTER_CROP);stage.addView(backdrop,new FrameLayout.LayoutParams(-1,-1));
        View shade=new View(a);shade.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xC2080C0F,0x7D080C0F}));stage.addView(shade,new FrameLayout.LayoutParams(-1,-1));
        dialog.setContentView(stage);render();TvMotion.enter(stage);a.updateRotation();
    }
    void showStartup(){if(!showing())show();go("startup");}
    void startupChanged(){if(showing()&&page.equals("startup"))render();}
    void close(){cityRequest++;searching=false;if(dialog!=null)dialog.dismiss();}
    private void back(){
        a.wallpapers.cancelPreview();a.wallpapers.cancelSelection();
        if(page.equals("overview")){close();return;}
        go(SettingsNavigation.parent(page));
    }
    private void go(String next){cityRequest++;searching=false;a.wallpapers.cancelPreview();a.wallpapers.cancelSelection();page=next;render();}
    private String title(){
        switch(page){case "overview":return "设置";case "background":return "照片与背景";case "appearance":return "桌面外观";case "more":return "更多";case "wallpaper":return "默认壁纸";case "private-wallpaper":return "我的壁纸 · 仅本机";case "rotation":return "轮播";
            case "weather":return "时间与天气";case "city":return "选择城市";case "layout":return "布局";case "type":return "字体";case "focus":return "选中样式";case "home":return "常用应用";case "add":return "首页内容 / 添加";case "tv":return "电视与启动";case "startup":return "首次启动设置";case "experiments":return "实验性功能";
            default:return "首页内容 / "+a.items.name(page.substring(5));}
    }
    private void render(){
        rendering=true;choices.clear();
        boolean overview=page.equals("overview");
        LinearLayout panel=new LinearLayout(a);panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(a.dp(70),a.dp(overview?42:24),a.dp(70),a.dp(30));
        if(!overview){
            String parent=SettingsNavigation.parent(page);
            String parentLabel=parent.equals("background")?"照片与背景":parent.equals("appearance")?"桌面外观":parent.equals("more")?"更多":parent.equals("weather")?"时间与天气":parent.equals("home")?"常用应用":"设置";
            TextView back=label("‹  "+parentLabel,14);
            back.setTextColor(0xBBFFFFFF);back.setFocusable(true);back.setOnClickListener(v->back());
            back.setPadding(a.dp(8),0,a.dp(10),0);
            back.setOnFocusChangeListener((v,f)->decorate(v,f));
            LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-2,a.dp(24));bp.leftMargin=-a.dp(8);panel.addView(back,bp);
        }
        TextView title=label(title(),28);title.setPadding(0,a.dp(6),0,0);
        panel.addView(title,new LinearLayout.LayoutParams(-1,a.dp(overview?64:68)));
        LinearLayout body=new LinearLayout(a);body.setOrientation(LinearLayout.HORIZONTAL);
        panel.addView(body,new LinearLayout.LayoutParams(-1,0,1));
        ScrollView scroll=new ScrollView(a);scroll.setVerticalScrollBarEnabled(false);scroll.setClipToPadding(false);scroll.setClipChildren(false);scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        options=new LinearLayout(a);options.setOrientation(LinearLayout.VERTICAL);options.setClipChildren(false);options.setClipToPadding(false);options.setPadding(0,0,0,a.dp(12));
        scroll.addView(options,new ScrollView.LayoutParams(-1,-2));
        body.addView(scroll,new LinearLayout.LayoutParams(a.dp(overview?528:320),-1));
        LinearLayout right=new LinearLayout(a);right.setOrientation(LinearLayout.VERTICAL);right.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(0,-1,1);rp.leftMargin=a.dp(38);body.addView(right,rp);
        previewHost=new FrameLayout(a){@Override protected void onSizeChanged(int w,int h,int oldw,int oldh){super.onSizeChanged(w,h,oldw,oldh);scalePreview(w,h);}};
        previewHost.setBackground(WallpaperMaterial.rounded(0x40000000,a.dp(10)));previewHost.setClipToOutline(true);
        right.addView(previewHost,new LinearLayout.LayoutParams(-1,a.dp(260)));
        status=label("首页预览",14);status.setTextColor(0xBFFFFFFF);status.setPadding(0,a.dp(16),0,0);
        right.addView(status,new LinearLayout.LayoutParams(-1,a.dp(65)));
        if(overview)right.setVisibility(View.GONE);
        TextView footer=label(overview?"":"确定  应用设置        返回  上一级",13);footer.setTextColor(0xA6FFFFFF);
        panel.addView(footer,new LinearLayout.LayoutParams(-1,a.dp(26)));
        switch(page){
            case "overview":
                entry("看照片", "收起首页文字 · 左右换图 · 返回退出",a::showPhotoMode);
                TextView photoAction=(TextView)choices.get(choices.size()-1);photoAction.setCompoundDrawables(null,null,null,null);
                ((LinearLayout.LayoutParams)photoAction.getLayoutParams()).bottomMargin=a.dp(12);
                entry("照片与背景","壁纸、我的照片与轮播",()->go("background"));
                entry("桌面外观","布局、文字与焦点质感",()->go("appearance"));
                entry("常用应用","添加、排序与隐藏",()->go("home"));
                entry("电视与启动","系统设置、主页键与开机",()->go("tv"));
                entry("更多","天气与实验功能",()->go("more"));break;
            case "appearance":
                entry("布局与图标","列表或 Dock · 应用与操作显示",()->go("layout"));
                entry("文字与字号","字体风格与首页字号",()->go("type"));
                entry("焦点与质感","高亮、磨砂与未选中项淡化",()->go("focus"));break;
            case "more":
                entry("时间与天气","城市、温度与天气显示",()->go("weather"));
                entry("实验性功能","Codex 对话 · 默认关闭",()->go("experiments"));break;
            case "background":
                row("背景","background",new String[]{"照片","浅色","深色"});
                if(a.appearance.get("background")==0){entry("默认壁纸","风景、空景与内置照片",()->go("wallpaper"));entry("我的壁纸","仅保存在这台电视",()->go("private-wallpaper"));entry("轮播",a.wallpapers.rotating()?"每 "+a.wallpapers.minutes()+" 分钟":"固定壁纸",()->go("rotation"));}
                else note("切换回照片背景后，可选择壁纸和轮播。原来的照片与轮播偏好会保留。");break;
            case "layout":
                row("首页布局","layout",new String[]{"纵向列表","横向 Dock"});row("应用显示","apps",new String[]{"文字","图标","图标＋文字"});row("右侧操作","controls",new String[]{"文字","图标","图标＋文字"});
                note("仅显示图标时，聚焦后会显示名称。");break;
            case "type":
                row("字体风格","font",new String[]{"轻盈","标准","衬线"});row("首页字号","size",new String[]{"小","标准","大"});note("使用电视本机字体，中文由系统回退显示。字体应用于首页名称和时钟；设置页保持清晰易读。Dock 使用适合卡片的字号。");break;
            case "focus":
                row("焦点表现","focus",new String[]{"文字高亮","毛玻璃"});
                if(a.appearance.get("focus")==1)row("底板质感","material",new String[]{"柔和","浓雾","清透"});else note("启用毛玻璃后可调整质感。");
                row("未选中项淡化","dim",new String[]{"轻","标准","明显"});break;
            case "rotation":
                option("固定壁纸",!a.wallpapers.rotating(),()->{if(a.wallpapers.rotation(false,a.wallpapers.minutes()))render();else failed();});
                for(int m:WallpaperCycle.MINUTES){final int value=m;option("每 "+m+" 分钟",a.wallpapers.rotating()&&a.wallpapers.minutes()==m,()->{
                    if(a.wallpapers.rotation(true,value))render();else failed();});}
                note("只在照片首页显示时计时；设置页、其他应用和纯色背景会暂停轮播。选择壁纸不会改变轮播模式。");break;
            case "wallpaper":gallery(false);break;
            case "private-wallpaper":gallery(true);break;
            case "home":
                note("保留 1–9 个入口。选择一项可调整位置或从首页隐藏。");
                for(int i=0;i<a.items.order.size();i++){String id=a.items.order.get(i);entry((i+1)+"  "+a.items.name(id),id.startsWith("hdmi:")?"HDMI "+id.substring(5):"应用",()->go("item:"+id));}
                entry("添加入口","应用或 HDMI 信号源",()->go("add"));if(!undo.isEmpty())entry("撤销上一步","恢复本次编辑前的顺序",this::undo);break;
            case "add":
                a.items.discover();for(String id:a.items.names.keySet())if(!a.items.order.contains(id))entry(a.items.name(id),"添加到首页末尾",()->{
                    if(a.items.order.size()>=9){a.message("首页最多保留 9 个入口");return;}List<String> next=new ArrayList<>(a.items.order);next.add(id);if(save(next))go("home");});break;
            case "weather":
                option("关闭天气",!a.weather.enabled(),()->{if(a.weather.enabled(false)){a.refreshHome();render();}else failed();});
                option("显示天气",a.weather.enabled(),()->{if(a.weather.enabled(true)){a.refreshHome();render();}else failed();});
                option("自动识别城市",a.weather.automatic(),()->{if(a.weather.autoCity()){a.refreshHome();render();}else failed();});
                entry("手动选择城市",a.weather.city(),()->go("city"));
                note("天气显示在时间右侧，每 30 分钟更新。自动城市根据网络出口识别；使用代理时可能不准确，可手动纠正。");
                note("数据：Open-Meteo · CC BY 4.0\n城市：IPWhois · 不申请精确定位");break;
            case "city":
                cityInput=new EditText(a);cityInput.setText(cityQuery);cityInput.setTextColor(0xF2FFFFFF);cityInput.setHintTextColor(0x99FFFFFF);cityInput.setTextSize(18);
                cityInput.setHint("城市名称或拼音");cityInput.setSingleLine(true);cityInput.setPadding(a.dp(14),0,a.dp(14),0);cityInput.setBackground(WallpaperMaterial.rounded(0x26FFFFFF,a.dp(10)));
                cityInput.setOnFocusChangeListener((v,focus)->v.setBackground(WallpaperMaterial.rounded(focus?0x45FFFFFF:0x26FFFFFF,a.dp(10))));
                options.addView(cityInput,new LinearLayout.LayoutParams(-1,a.dp(44)));
                entry("搜索城市",searching?"正在搜索…":"确认后查看匹配城市",this::searchCities);
                for(HomeWeather.City city:cityResults)entry(city.name,city.region,()->{if(a.weather.select(city)){a.refreshHome();go("weather");}else failed();});
                if(!cityError.isEmpty())note(cityError);else note("确认省份和国家后选择。返回不会更改当前城市。");break;
            case "experiments":
                heading("Codex 对话");
                option("关闭",!HomeAssistant.enabled(a),()->experiment(false));
                option("开启",HomeAssistant.enabled(a),()->experiment(true));
                note("开启后，首页显示 Codex 入口。需要同一网络内的电脑运行连接服务，并登录 Codex；使用相应账号额度。");
                note("当前支持文字对话；语音输入取决于系统输入法。尚未接入日程、米家或电脑控制。");
                note("关闭后隐藏入口并停止电视端连接，保留配对。已发送的回答可能继续在电脑完成；完全停用请在电脑停止连接服务。");break;
            case "startup":
                note("一次设置后不需要电脑保持在线。能否成为默认桌面，由电视系统决定。");
                entry("设置默认桌面",StartupSetup.defaultHome(a)?"系统已将当前应用设为默认桌面":"打开系统选择，需本人确认",()->StartupSetup.requestDefault(a));
                entry("开机自动进入",StartupSetup.bootSummary(a),()->BootStart.show(a));
                if(XiaomiProfile.mftr0(android.os.Build.MODEL,android.os.Build.VERSION.INCREMENTAL))
                    entry("主页键返回",HomeReturnService.enabled(a)?"当前已启用可选返回服务":"指定小米固件的可选辅助功能",()->HomeReturnService.openSettings(a));
                else note("本机优先使用系统默认桌面；当前没有额外主页键返回适配。");
                entry("重新检查状态","读取实际配置，不自动修改系统权限",this::render);
                note(StartupSetup.anotherEdition(a)?"检测到另一份煎饼使TV：公开版与私人试用版各存各的数据。先保留原片，再决定保留哪份。":"当前未检测到另一个版本。重复快捷图标不等于安装了两份；可在电视系统应用管理中核对。");
                note("小米若显示需要电脑配置：请查看仓库「第一次使用 → 启动指南」。开机目标与应用内开关都完成后，再重启验证。");break;
            case "tv":
                entry("启动设置向导","默认桌面、开机配置与版本检查",()->go("startup"));
                if(XiaomiProfile.mftr0(android.os.Build.MODEL,android.os.Build.VERSION.INCREMENTAL)){
                    entry("主页键返回新桌面",HomeReturnService.enabled(a)?"已开启；点击查看或关闭":"未开启；点击打开辅助功能",()->HomeReturnService.openSettings(a));
                    if(HomeReturnService.enabled(a))entry("关闭主页自动返回","按主页键恢复进入小米原桌面",()->{HomeReturnService.disable(a);a.message("已关闭主页自动返回");render();});
                }
                if(XiaomiControls.enabled())entry("开机自动进入",BootStart.enabled(a)?"已开启":"已关闭",()->BootStart.show(a));
                entry("电视系统设置","网络、声音、画面等",a::systemSettings);
                if(XiaomiControls.enabled())entry("小米原桌面","打开并暂停主页跳转；重开本桌面恢复",a::stockHome);break;
            default:
                String id=page.substring(5);int pos=a.items.order.indexOf(id);
                if(pos>0)entry("上移","与上一项交换位置",()->move(id,-1));if(pos>=0&&pos<a.items.order.size()-1)entry("下移","与下一项交换位置",()->move(id,1));
                if(a.items.order.size()>1)entry("从首页隐藏","应用仍保留在电视上",()->{List<String> next=new ArrayList<>(a.items.order);next.remove(id);if(save(next))go("home");});
                else note("至少保留一个首页入口。");break;
        }
        if(content!=null)stage.removeView(content);content=panel;stage.addView(panel,new FrameLayout.LayoutParams(-1,-1));
        refreshPreview();background(a.appearance.get("background")==0?a.wallpaperDrawable():null);rendering=false;weatherChanged();
        if(!choices.isEmpty())choices.get(Math.min(remembered.getOrDefault(page,0),choices.size()-1)).requestFocus();
    }
    private void experiment(boolean enabled){
        if(!a.assistant.enabled(enabled)){failed();return;}
        a.refreshHome();render();
    }
    private void searchCities(){
        if(searching)return;cityQuery=cityInput.getText().toString().trim();
        ((android.view.inputmethod.InputMethodManager)a.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(cityInput.getWindowToken(),0);
        searching=true;cityError="";cityResults.clear();int request=++cityRequest;render();
        a.weather.search(cityQuery,(cities,error)->{if(request!=cityRequest||!showing()||!page.equals("city"))return;searching=false;cityResults=cities;cityError=error;render();});
    }
    private void gallery(boolean personal){
        int imageCount=0;
        if(personal)entry("导入照片","从 U 盘或本机选择 · 仅存本机",a::importPhoto);
        for(int i=0;i<a.wallpapers.count();i++){
            if(a.wallpapers.privateImage(i)!=personal)continue;
            imageCount++;
            final int index=i;
            TextView v=button(a.wallpapers.name(i),()->{
                if(index==a.wallpapers.selected()){status.setText("已应用这张壁纸");return;}
                status.setText("正在应用壁纸…");String origin=page;
                a.wallpapers.select(index,()->{if(showing()&&page.equals(origin)){render();status.setText("已应用 · "+a.wallpapers.name(index));}});
            });
            android.graphics.drawable.Drawable thumb=a.wallpapers.thumbnail(i);
            if(thumb!=null){thumb=roundedThumb(thumb);thumb.setBounds(0,0,a.dp(48),a.dp(27));v.setCompoundDrawables(thumb,null,checkmark(i==a.wallpapers.selected()),null);v.setCompoundDrawablePadding(a.dp(14));}
            v.setOnFocusChangeListener((view,focus)->{
                decorate(view,focus);if(!focus)return;remembered.put(page,choices.indexOf(view));
                a.wallpapers.cancelSelection();
                status.setText(index==a.wallpapers.selected()?"已应用 · "+a.wallpapers.name(index):"预览 · 按确定应用 "+a.wallpapers.name(index));
                if(preview!=null){HomeSurface target=preview;a.wallpapers.preview(index,target.previewPhoto,()->{
                    if(preview!=target||!showing())return;
                    target.wallpaper(target.previewPhoto.getDrawable());background(target.previewPhoto.getDrawable());
                });}
            });
            options.addView(v,new LinearLayout.LayoutParams(-1,a.dp(46)));
        }
        note(imageCount==0?"这台电视还没有添加私有壁纸。最多 30 张；导入不会自动设为壁纸。":"上下浏览预览，按确定应用。默认图库与我的壁纸各自轮播。");
    }
    private void row(String title,String key,String[] labels){
        heading(title);
        for(int i=0;i<labels.length;i++){
            final int value=i;
            TextView v=button(labels[i],()->{
                if(a.appearance.get(key)==value)return;
                if(a.appearance.set(key,value)){a.refreshHome();render();}else failed();
            });
            v.setCompoundDrawables(null,null,checkmark(a.appearance.get(key)==i),null);
            options.addView(v,new LinearLayout.LayoutParams(-1,a.dp(42)));
        }
    }
    private void option(String name,boolean selected,Runnable run){
        TextView v=button(name,run);v.setCompoundDrawables(null,null,checkmark(selected),null);
        options.addView(v,new LinearLayout.LayoutParams(-1,a.dp(44)));
    }
    private Drawable checkmark(boolean selected){
        if(!selected)return null;
        Drawable mark=a.getDrawable(R.drawable.mark_check).mutate();mark.setTint(0xDFFFFFFF);mark.setBounds(0,0,a.dp(16),a.dp(16));return mark;
    }
    private void heading(String s){TextView v=label(s,14);v.setTextColor(0xADFFFFFF);v.setPadding(a.dp(12),a.dp(12),0,a.dp(7));options.addView(v);}
    private void note(String s){TextView v=label(s,13);v.setTextColor(0xA6FFFFFF);v.setLineSpacing(a.dp(3),1);v.setPadding(a.dp(12),a.dp(14),a.dp(8),a.dp(12));options.addView(v);}
    private void entry(String name,String subtitle,Runnable run){
        TextView v=button(name+(subtitle.isEmpty()?"":"\n"+subtitle),run);
        if(!subtitle.isEmpty()){
            SpannableString text=new SpannableString(v.getText());
            text.setSpan(new AbsoluteSizeSpan(13,true),name.length()+1,text.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            v.setText(text);v.setLineSpacing(a.dp(3),1);
        }
        v.setTag(Boolean.TRUE);
        Drawable arrow=a.getDrawable(R.drawable.mark_caret_right).mutate();arrow.setTint(0xAAFFFFFF);arrow.setBounds(0,0,a.dp(14),a.dp(14));v.setCompoundDrawables(null,null,arrow,null);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,a.dp(subtitle.isEmpty()?44:56));lp.bottomMargin=a.dp(4);options.addView(v,lp);decorate(v,false);
    }
    private TextView button(String text,Runnable run){
        TextView v=label(text,19);v.setPadding(a.dp(14),a.dp(5),a.dp(16),a.dp(5));v.setGravity(Gravity.CENTER_VERTICAL);v.setId(View.generateViewId());v.setFocusable(true);v.setFocusableInTouchMode(true);v.setClickable(true);TvMotion.bind(v);choices.add(v);
        v.setOnFocusChangeListener((view,focus)->{decorate(view,focus);if(focus&&!rendering)remembered.put(page,choices.indexOf(view));});
        v.setOnClickListener(view->run.run());v.setOnKeyListener((view,key,e)->(key==KeyEvent.KEYCODE_DPAD_CENTER||key==KeyEvent.KEYCODE_ENTER)&&e.getAction()==KeyEvent.ACTION_DOWN&&e.getRepeatCount()>0);return v;
    }
    private void decorate(View view,boolean focus){
        TextView v=(TextView)view;
        v.setBackground(focus?new FocusPaper(a.getResources().getDisplayMetrics().density):WallpaperMaterial.rounded(0x00000000,a.dp(10)));
        v.setTextColor(focus?0xFF202321:0xEBFFFFFF);
        String value=v.getText().toString();int line=value.indexOf('\n');
        if(line>=0){SpannableString text=new SpannableString(value);
            text.setSpan(new AbsoluteSizeSpan(13,true),line+1,value.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(new ForegroundColorSpan(focus?0xA6202321:0xA6FFFFFF),line+1,value.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);v.setText(text);}

        Drawable arrow=v.getCompoundDrawables()[2];if(arrow!=null)arrow.setTint(focus?0xFF262A24:0xCCFFFFFF);
    }
    private TextView label(String s,int size){TextView v=new TextView(a);v.setText(s);v.setTextSize(size);v.setTextColor(0xF2FFFFFF);v.setTypeface(android.os.Build.VERSION.SDK_INT>=28?Typeface.create(Typeface.create("sans-serif",Typeface.NORMAL),300,false):Typeface.create("sans-serif-light",Typeface.NORMAL));v.setIncludeFontPadding(false);return v;}
    private void background(Drawable source){
        int request=++backdropRequest;
        if(source==null){backdrop.setImageDrawable(null);return;}
        WallpaperMaterial.request(source,42,image->{if(request==backdropRequest&&showing()&&image!=null)backdrop.setImageBitmap(image);});
    }
    private Drawable roundedThumb(Drawable source){
        android.graphics.Bitmap image=android.graphics.Bitmap.createBitmap(a.dp(48),a.dp(27),android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas c=new android.graphics.Canvas(image);android.graphics.Path path=new android.graphics.Path();
        path.addRoundRect(0,0,image.getWidth(),image.getHeight(),a.dp(4),a.dp(4),android.graphics.Path.Direction.CW);c.clipPath(path);
        source.setBounds(0,0,image.getWidth(),image.getHeight());source.draw(c);return new android.graphics.drawable.BitmapDrawable(a.getResources(),image);
    }
    private void refreshPreview(){
        preview=a.preview();previewHost.removeAllViews();previewHost.addView(preview,new FrameLayout.LayoutParams(a.dp(960),a.dp(540)));
        preview.setPivotX(0);preview.setPivotY(0);scalePreview(previewHost.getWidth(),previewHost.getHeight());
        preview.clock.setText(new java.text.SimpleDateFormat("HH:mm",Locale.CHINA).format(new Date()));
    }
    private void scalePreview(int w,int h){if(preview!=null){float scale=Math.min(w/(float)a.dp(960),h/(float)a.dp(540));preview.setScaleX(scale);preview.setScaleY(scale);}}
    private boolean save(List<String> next){List<String> old=new ArrayList<>(a.items.order);if(!a.items.save(next)){failed();return false;}undo.push(old);a.refreshHome();return true;}
    private void move(String id,int direction){List<String> next=new ArrayList<>(a.items.order);int index=next.indexOf(id);Collections.swap(next,index,index+direction);if(save(next))render();}
    private void undo(){if(undo.isEmpty())return;if(a.items.save(undo.peek())){undo.pop();a.refreshHome();render();}else failed();}
    private void failed(){a.message("保存失败，保留原设置，请重试");}
}
