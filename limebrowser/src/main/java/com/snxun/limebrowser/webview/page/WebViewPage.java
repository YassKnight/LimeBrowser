package com.snxun.limebrowser.webview.page;

import com.snxun.limebrowser.webview.webview.CustomWebview;

/**
 * webview具体的页面信息
 * Created by Yangjw on 2020/11/26.
 */
public class WebViewPage extends Page {
    private CustomWebview mWebView;

    public WebViewPage(CustomWebview webView) {
        this.mWebView = webView;
        id = mWebView.getIdentityId();
    }

    public void onPageStarted() {
        url = mWebView.getOriginalUrl();
    }

    public void onPageFinished() {
        url = mWebView.getPresentUrl();
        title = mWebView.getWebTitle();
    }
}
