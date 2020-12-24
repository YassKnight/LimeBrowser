package com.snxun.browser.module.webview.client;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Yangjw on 2020/11/27.
 */
public class LimeWebViewClient extends WebViewClient {
    public onPageChangeListener mListener;

    public interface onPageChangeListener {
        void onPageStarted(WebView view, String url, Bitmap favicon);

        void onPageFinished(WebView view, String url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mListener.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mListener.onPageFinished(view, url);
    }

    public void setOnPageChangeListener(onPageChangeListener listener) {
        this.mListener = listener;
    }
}
