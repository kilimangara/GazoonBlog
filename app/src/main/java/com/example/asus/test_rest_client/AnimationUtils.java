package com.example.asus.test_rest_client;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;



public class AnimationUtils {

    public static void animateCardView(RecyclerView.ViewHolder holder, boolean goesDown){
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator translateY = ObjectAnimator.ofFloat(holder.itemView,"translationY", goesDown ? 200: -200, 0);
        translateY.setDuration(1000);
        animatorSet.playTogether(translateY);
        animatorSet.start();
    }
}
