package com.snxun.browser.module.webview.factory;

import android.content.Context;
import android.webkit.WebView;

import com.snxun.browser.module.webview.client.LimeWebChromeClient;
import com.snxun.browser.module.webview.client.LimeWebViewClient;

/**
 * Created by Yangjw on 2020/11/27.
 */
public interface WebViewFactory {

    WebView createWebView(Context context);

    LimeWebViewClient createWebViewClient();

    LimeWebChromeClient createWebChromeClient();

}
