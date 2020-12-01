package com.snxun.limebrowser.module.stackview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.snxun.limebrowser.R;
import com.snxun.limebrowser.util.ViewUtils;




public class LimeTabCard extends RelativeLayout {
    private ImageView mPrevew;
    private View mTitleBar;
    private int mScreenHeight;
    private Context mContext;
    private int mDesiredHeight;
    private final static String TAG = LimeTabCard.class.getSimpleName();
    private boolean isActive = false;
    int mCurrentHeight;
    public LimeTabCard(@NonNull Context context) {
        this(context, null);
    }

    public LimeTabCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LimeTabCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mScreenHeight = ViewUtils.getScreenSize(mContext).y - mContext.getResources().getDimensionPixelSize(R.dimen.dimen_48dp);
        mDesiredHeight = mContext.getResources().getDimensionPixelSize(R.dimen.uc_tab_card_height);
        mCurrentHeight = mDesiredHeight;
    }

    public void active(boolean active, int duration,int delay, final Runnable onCompleteRunnable){
        final float startAlpha = active ? 0.3f : 1.0f;
        final float finalAlpha = active ? 1.0f : 0.0f;
        final int dis = mScreenHeight - mDesiredHeight;
        PropertyValuesHolder holder = PropertyValuesHolder.ofFloat("alpha",startAlpha,finalAlpha);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mTitleBar,holder);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float rate = ((Float) animation.getAnimatedValue()).floatValue();
                mCurrentHeight = (int) (mScreenHeight - dis * rate);
                requestLayout();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if(onCompleteRunnable != null){
                    onCompleteRunnable.run();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(onCompleteRunnable != null){
                    onCompleteRunnable.run();
                }
            }
        });
        animator.start();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPrevew = findViewById(R.id.ivPagePreview);
        mTitleBar = findViewById(R.id.rlPageHead);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mPrevew.getMeasuredWidth(),mScreenHeight);
        setMeasuredDimension(mPrevew.getMeasuredWidth(),mCurrentHeight);
    }
}
