package com.snxun.browser.module.webview.factory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.snxun.browser.constant.Constants;

/**
 * webview工厂
 * Created by Yangjw on 2020/11/26.
 */
public class WebViewFactory implements ViewFactory {
    private Context mContext;

    public WebViewFactory(Context mContext) {
        this.mContext = mContext;
    }

    private WebView instantiateWebView(AttributeSet attrs, int defStyle) {
        return new WebView(mContext, attrs, defStyle);
    }

    @Override
    public WebView createWebView() {
        WebView w = instantiateWebView(null, android.R.attr.webViewStyle);
        initWebSetting(w);
        return w;
    }

    /**
     * 初始化webview配置
     *
     * @param webView
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initWebSetting(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        //设置渲染的优先级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        String cacheDirPath = mContext.getFilesDir().getAbsolutePath() + Constants.APP_CACHE_DIRNAME;
        //设置  Application Caches 缓存目录
        webSettings.setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        webSettings.setAppCacheEnabled(true);

        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        /// M: Add to disable overscroll mode
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        final PackageManager pm = mContext.getPackageManager();
        boolean supportsMultiTouch =
                pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)
                        || pm.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT);
        webSettings.setDisplayZoomControls(!supportsMultiTouch);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(webView, cookieManager.acceptCookie());
        webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        // Remote Web Debugging is always enabled, where available.
        WebView.setWebContentsDebuggingEnabled(true);

    }
}
