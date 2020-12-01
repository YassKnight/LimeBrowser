package com.snxun.limebrowser.module.webview.factory;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by Yangjw on 2020/11/27.
 */
public interface ViewFactory {
    /**
     * 构建View
     *
     * @return
     */
    public View createWebView();;
}
