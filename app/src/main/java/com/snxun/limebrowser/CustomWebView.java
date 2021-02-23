package com.snxun.limebrowser;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @ProjectName : LimeBrowser
 * @Author : Yangjw
 * @Time : 2021/2/22
 * @Description :
 */
public class CustomWebView extends WebView {
    public CustomWebView(@NonNull Context context) {
        super(context);
    }

    public CustomWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        return super.onTouchEvent(event);
    }
}
