package com.snxun.browser.module.webview.factory;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

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

    /**
     * 构建自定义view
     * @return
     */
    public View creareCustomWebView(WebSettings settings, WebViewClient webViewClient, WebChromeClient webChromeClient);
}
