package io.github.op7418.guizangtv;

import android.app.*;
import android.view.*;
import android.widget.*;
import android.text.TextUtils;
import java.util.*;

/** Shared remote-first sheet for application menus and experimental confirmations. */
final class TvPanel {
    static Dialog show(Activity a,String title,String message,String[] labels,java.util.function.IntConsumer action,int columns){
        float density=a.getResources().getDisplayMetrics().density;
        Dialog dialog=new Dialog(a);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel=new LinearLayout(a);panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(density,30),dp(density,24),dp(density,30),dp(density,24));
        android.graphics.drawable.GradientDrawable bg=WallpaperMaterial.rounded(0xFA20252A,dp(density,22));
        bg.setStroke(dp(density,1),0x526F7880);panel.setBackground(bg);
        TextView heading=text(a,title,28);panel.addView(heading);
        TextView help=text(a,message==null?"确定打开 · 返回关闭":message,16);
        help.setTextColor(0xFFC4C9CC);help.setPadding(0,dp(density,12),0,dp(density,18));panel.addView(help);
        ScrollView scroll=new ScrollView(a);scroll.setFillViewport(false);scroll.setClipToPadding(false);
        LinearLayout rows=new LinearLayout(a);rows.setOrientation(LinearLayout.VERTICAL);rows.setPadding(dp(density,3),dp(density,3),dp(density,3),dp(density,3));scroll.addView(rows);
        List<TextView> buttons=new ArrayList<>();
        int count=labels.length;
        for(int i=0;i<count;i+=columns){
            LinearLayout row=new LinearLayout(a);row.setClipChildren(false);rows.addView(row,new LinearLayout.LayoutParams(-1,-2));
            for(int c=0;c<columns;c++){
                final int index=i+c;
                if(index>=count){View spacer=new View(a);row.addView(spacer,new LinearLayout.LayoutParams(0,1,1));continue;}
                TextView button=text(a,labels[index],19);button.setMaxLines(2);button.setEllipsize(TextUtils.TruncateAt.END);
                button.setGravity(Gravity.CENTER_VERTICAL);button.setPadding(dp(density,18),0,dp(density,18),0);
                button.setId(View.generateViewId());button.setFocusable(true);button.setClickable(true);TvMotion.bind(button);
                style(button,false,density);
                button.setOnFocusChangeListener((v,f)->style(button,f,density));
                button.setOnClickListener(v->{dialog.dismiss();action.accept(index);});
                button.setOnKeyListener((v,key,event)->(key==KeyEvent.KEYCODE_DPAD_CENTER||key==KeyEvent.KEYCODE_ENTER)&&event.getAction()==KeyEvent.ACTION_DOWN&&event.getRepeatCount()>0);
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(density,62),1);lp.setMargins(dp(density,4),dp(density,4),dp(density,4),dp(density,4));row.addView(button,lp);buttons.add(button);
            }
        }
        // Explicit navigation keeps focus in the sheet, including an incomplete last row.
        for(int i=0;i<buttons.size();i++){
            TextView b=buttons.get(i);int col=i%columns;
            b.setNextFocusLeftId(buttons.get(col==0?i:i-1).getId());
            b.setNextFocusRightId(buttons.get(col==columns-1||i+1>=count?i:i+1).getId());
            b.setNextFocusUpId(buttons.get(i<columns?i:i-columns).getId());
            b.setNextFocusDownId(buttons.get(i+columns<count?i+columns:i).getId());
        }
        int maxHeight=Math.round(a.getResources().getDisplayMetrics().heightPixels*.55f);
        panel.addView(scroll,new LinearLayout.LayoutParams(-1,Math.min(maxHeight,dp(density,70)*((count+columns-1)/columns))));
        TextView close=text(a,"返回",18);close.setGravity(Gravity.CENTER);close.setPadding(dp(density,24),0,dp(density,24),0);close.setId(View.generateViewId());close.setFocusable(true);close.setClickable(true);TvMotion.bind(close);style(close,false,density);close.setOnFocusChangeListener((v,f)->style(close,f,density));close.setOnClickListener(v->dialog.dismiss());
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-2,dp(density,44));cp.topMargin=dp(density,16);cp.gravity=Gravity.END;panel.addView(close,cp);
        for(int i=Math.max(0,count-columns);i<count;i++)if(i+columns>=count)buttons.get(i).setNextFocusDownId(close.getId());
        if(count>0){close.setNextFocusUpId(buttons.get(count-1).getId());buttons.get(0).requestFocus();}else close.requestFocus();
        dialog.setContentView(panel);dialog.show();Window w=dialog.getWindow();w.setBackgroundDrawableResource(android.R.color.transparent);w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);w.setDimAmount(.48f);
        w.setLayout(Math.min(Math.round(a.getResources().getDisplayMetrics().widthPixels*.88f),dp(density,columns>1?880:680)),-2);
        w.getDecorView().setSystemUiVisibility(5894);TvMotion.enter(panel);return dialog;
    }
    private static void style(TextView v,boolean f,float d){v.setTextColor(f?0xFF262A24:0xFFF4F2EB);v.setBackground(f?new FocusPaper(d):WallpaperMaterial.rounded(0xFF30373C,dp(d,12)));}
    private static TextView text(Activity a,String s,int size){TextView v=new TextView(a);v.setText(s);v.setTextSize(size);v.setTextColor(0xFFF4F2EB);return v;}
    private static int dp(float d,float n){return Math.round(d*n);}
}
