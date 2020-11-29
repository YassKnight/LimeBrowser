package com.snxun.limebrowser.module.webview.client;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Yangjw on 2020/11/27.
 */
public class LimeWebViewClient extends WebViewClient {

    public static LimeWebViewClient newInstance() {
        return new LimeWebViewClient();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }
}
