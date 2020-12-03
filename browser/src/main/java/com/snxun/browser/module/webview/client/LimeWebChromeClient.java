package com.snxun.browser.module.webview.client;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by Yangjw on 2020/11/27.
 */
public class LimeWebChromeClient extends WebChromeClient {
    public static LimeWebChromeClient newInstance() {
        return new LimeWebChromeClient();
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
    }
}
