package io.github.op7418.guizangtv;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/** A restrained yellow action surface, lit from the upper left. No blur or layout padding. */
final class FocusPaper extends Drawable {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF face=new RectF(),shadow=new RectF();
    private final float density;
    private Shader light;
    FocusPaper(float density){this.density=density;}
    @Override protected void onBoundsChange(Rect b){
        float offset=3*density,inset=density;
        face.set(b.left+inset,b.top+inset,b.right-offset-inset,b.bottom-offset-inset);
        shadow.set(face);shadow.offset(offset,offset);
        light=new LinearGradient(face.left,face.top,face.right,face.bottom,0xFFFFDE67,0xFFFFCB22,Shader.TileMode.CLAMP);
    }
    @Override public void draw(Canvas canvas){
        paint.setShader(null);paint.setStyle(Paint.Style.FILL);paint.setColor(0xD9020302);
        canvas.drawRoundRect(shadow,14*density,14*density,paint);
        paint.setShader(light);canvas.drawRoundRect(face,14*density,14*density,paint);
        paint.setShader(null);paint.setColor(0xFF262A24);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.5f*density);
        canvas.drawRoundRect(face,14*density,14*density,paint);paint.setStyle(Paint.Style.FILL);
    }
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
