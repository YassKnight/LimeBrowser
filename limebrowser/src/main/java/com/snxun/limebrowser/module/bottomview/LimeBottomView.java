package com.snxun.limebrowser.module.bottomview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.snxun.limebrowser.module.rootview.RootView;

/**
 * 底部工具栏 （优化滚动自动隐藏、点击效果）
 * Created by Yangjw on 2020/11/27.
 */
public class LimeBottomView extends LinearLayout implements RootView.ScrollStateListener {
    public LimeBottomView(Context context) {
        super(context);
    }

    public LimeBottomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LimeBottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onStartScroll(int direction) {

    }

    @Override
    public void onScroll(float rate) {

    }

    @Override
    public void onEndScroll() {

    }

    @Override
    public void move(float x, float y) {

    }
}
