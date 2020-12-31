package com.snxun.browser.module.webview.factory;

import android.content.Context;
import android.webkit.WebView;

import com.snxun.browser.module.webview.client.LimeWebChromeClient;
import com.snxun.browser.module.webview.client.LimeWebViewClient;

/**
 * Created by Yangjw on 2020/11/27.
 */
public interface WebViewFactory {

    /**
     * 创建新的webiew，需保证在该方法中创建对象，否则每次创建的webiew将是同一个对象
     * 如果设置websetting也请在该方法进行设置
     *
     * @param context 创建webview需要的上下文
     * @return WebView
     */
    WebView createWebView(Context context);

    /**
     * 创建webview需要的webviewClient，只需要返回LimeWebViewClient类型的Client，不需要进行设置
     *
     * @return LimeWebViewClient
     */
    LimeWebViewClient createWebViewClient();

    /**
     * 创建webview需要的webChromeClient，只需要返回LimeWebChromeClient类型的Client，不需要进行设置
     *
     * @return LimeWebChromeClient
     */
    LimeWebChromeClient createWebChromeClient();

}
