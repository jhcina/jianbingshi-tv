package io.github.op7418.guizangtv;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Warm enamel: a bright lip on dark photos, a fine dark keyline on light photos. */
final class FocusPaper extends Drawable {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF face=new RectF();
    private final float density;
    private Shader light;
    private int opacity=255;
    FocusPaper(float density){this.density=density;}
    @Override protected void onBoundsChange(Rect b){
        face.set(b.left+density,b.top+density,b.right-density,b.bottom-2*density);
        light=new LinearGradient(0,face.top,0,face.bottom,0xFFFFE383,0xFFFFCB22,Shader.TileMode.CLAMP);
    }
    private void color(int c){paint.setColor(c);paint.setAlpha(Math.round(Color.alpha(c)*opacity/255f));}
    @Override public void draw(Canvas canvas){
        float r=12*density;
        paint.setStyle(Paint.Style.FILL);paint.setShader(light);paint.setAlpha(opacity);
        canvas.drawRoundRect(face,r,r,paint);
        paint.setShader(null);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(density);
        color(0xAA6B5011);canvas.drawRoundRect(face,r,r,paint);
        canvas.save();canvas.clipRect(face.left,face.top,face.right,face.centerY());
        RectF lip=new RectF(face);lip.inset(density,density);
        color(0xCFFFF4C4);canvas.drawRoundRect(lip,r-density,r-density,paint);canvas.restore();
        paint.setStyle(Paint.Style.FILL);
    }
    @Override public void setAlpha(int alpha){opacity=Math.max(0,Math.min(255,alpha));invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
