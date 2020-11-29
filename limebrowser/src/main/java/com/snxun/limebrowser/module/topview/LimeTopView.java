package com.snxun.limebrowser.module.topview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * 顶部标题栏（优化滚动自动隐藏）
 * Created by Yangjw on 2020/11/27.
 */
public class LimeTopView extends LinearLayout {
    public LimeTopView(Context context) {
        super(context);
    }

    public LimeTopView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LimeTopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
