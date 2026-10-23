package io.github.op7418.guizangtv;

import android.animation.*;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/** Property-only motion; no per-frame blur, layout or idle animation. */
final class TvMotion {
    static void bind(View view){
        StateListAnimator states=new StateListAnimator();
        states.addState(new int[]{android.R.attr.state_pressed},pose(view,.985f,0,85));
        states.addState(new int[]{android.R.attr.state_focused},pose(view,1.012f,-1,150));
        states.addState(new int[]{},pose(view,1,0,130));
        view.setStateListAnimator(states);
    }
    private static Animator pose(View v,float scale,float lift,long duration){
        AnimatorSet set=new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(v,View.SCALE_X,scale),ObjectAnimator.ofFloat(v,View.SCALE_Y,scale),
                ObjectAnimator.ofFloat(v,View.TRANSLATION_Y,lift*v.getResources().getDisplayMetrics().density));
        set.setDuration(ValueAnimator.areAnimatorsEnabled()?duration:0);set.setInterpolator(new DecelerateInterpolator());return set;
    }
    static void enter(View v){
        if(!ValueAnimator.areAnimatorsEnabled())return;
        v.setAlpha(0);v.setTranslationY(8*v.getResources().getDisplayMetrics().density);
        v.animate().alpha(1).translationY(0).setDuration(180).setInterpolator(new DecelerateInterpolator()).start();
    }
}
