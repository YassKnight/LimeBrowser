package com.snxun.browser.module.webview.client;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by Yangjw on 2020/11/27.
 */
public class LimeWebChromeClient extends WebChromeClient {
    public onPageChangeListener mListener;

    public interface onPageChangeListener {
        void onProgressChanged(WebView view, int newProgress);

        void onReceivedTitle(WebView view, String title);

        void onReceivedIcon(WebView view, Bitmap icon);

        void onShowCustomView(View view, CustomViewCallback callback);

        void onHideCustomView();
    }

    public void setonPageChangeListener(onPageChangeListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        mListener.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        mListener.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
        mListener.onReceivedIcon(view, icon);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
        mListener.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
        mListener.onHideCustomView();
    }
}
