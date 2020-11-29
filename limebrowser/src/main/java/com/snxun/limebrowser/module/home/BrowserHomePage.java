package com.snxun.limebrowser.module.home;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.snxun.limebrowser.application.BaseApplication;

import java.util.ArrayList;

/**
 * 浏览器展示应用的首页
 * Created by Yangjw on 2020/11/26.
 */
public class BrowserHomePage extends FrameLayout {
    ArrayList<BaseApplication> appList;

    public BrowserHomePage(@NonNull Context context) {
        super(context);
    }

    public BrowserHomePage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BrowserHomePage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
