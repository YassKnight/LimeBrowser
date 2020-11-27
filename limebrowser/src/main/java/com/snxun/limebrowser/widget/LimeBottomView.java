package com.snxun.limebrowser.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * 底部工具栏 （优化滚动自动隐藏、点击效果）
 * Created by Yangjw on 2020/11/27.
 */
public class LimeBottomView extends LinearLayout {
    public LimeBottomView(Context context) {
        super(context);
    }

    public LimeBottomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LimeBottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LimeBottomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
