package com.snxun.browser.module.browser;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * 对外提供的浏览器控件
 * Created by Yangjw on 2020/12/7.
 */
public class LimeBrowser extends ViewGroup {
    private static final String TAG = "LimeBrowser";

    public LimeBrowser(Context context) {
        super(context);
    }

    public LimeBrowser(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LimeBrowser(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

}
