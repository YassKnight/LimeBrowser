package com.snxun.limebrowser.module.bottomview;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.snxun.limebrowser.module.rootview.RootView;

import static com.snxun.limebrowser.module.rootview.RootView.SCROLL_VERTICALLY;

/**
 * 底部工具栏 （优化滚动自动隐藏、点击效果）
 * Created by Yangjw on 2020/11/27.
 */
public class LimeBottomView extends LinearLayout implements RootView.ScrollStateListener {
    private int mStartX;
    private int mStartY;
    private int mEndY;
    private float mStartScale;
    private float mEndScale;
    private int mDistanceY;
    private float mScale;
    protected boolean mTransYEnable = false;
    protected boolean mTransXEnable = false;
    protected boolean mScaleEnable = false;
    protected Context mContext;
    protected Resources mRes;
    protected boolean mStartScroll = false;
    protected int mDirection = RootView.SCROLL_NONE;

    public LimeBottomView(Context context) {
        super(context);
    }

    public LimeBottomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LimeBottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();

    }

    protected void init() {
        mRes = mContext.getResources();
    }

    public void setTransYEnable(boolean transYEnable) {
        mTransYEnable = transYEnable;
    }


    /**
     * @param from 起始位置
     * @param to   最终位置
     */
    public void initTranslationY(int from, int to) {
        mStartY = from;
        mEndY = to;
        setTranslationY(from);
        mDistanceY = from - to;
    }


    private void setScaleXY(float rate) {
        setScaleY(calculateScale(rate));
    }

    /**
     * @param rate 滑动的相对比率
     * @return
     */
    public float calculateTransY(float rate) {
        if (Math.abs(rate) < 1) {
            return mStartY + mDistanceY * rate;
        } else {
            return mEndY;
        }
    }

    public float calculateScale(float rate) {
        return mStartScale + mScale * rate;
    }

    @Override
    public void onStartScroll(int direction) {
        mDirection = direction;
        mStartScroll = true;
        setVisibility(VISIBLE);
    }

    @Override
    public void onScroll(float rate) {
        if (!mStartScroll || rate > 0 || rate < -1) {
            return;
        }

        switch (mDirection) {
            case SCROLL_VERTICALLY:
                if (mTransYEnable) setTranslationY(calculateTransY(rate));
                break;
        }
        if (mScaleEnable) {
            setScaleXY(-rate);
        }
    }

    @Override
    public void onEndScroll() {
        mStartScroll = false;
    }

    @Override
    public void move(float x, float y) {

    }
}
