package com.snxun.limebrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.snxun.browser.module.webview.client.LimeWebChromeClient;
import com.snxun.browser.module.webview.client.LimeWebViewClient;
import com.snxun.browser.module.webview.factory.WebViewFactory;

/**
 * @ProjectName : LimeBrowser
 * @Author : Yangjw
 * @Time : 2020/12/23
 * @Description :
 */
public class TestWebViewFactory implements WebViewFactory {

    @Override
    public WebView createWebView(Context context) {
        WebView w = new WebView(context, null, android.R.attr.webViewStyle);
        initWebSetting(w, context);
        return w;
    }

    @Override
    public LimeWebViewClient createWebViewClient() {
        return new MyWebviewClient();
    }

    @Override
    public LimeWebChromeClient createWebChromeClient() {
        return new MyChromeClient();
    }


    class MyWebviewClient extends LimeWebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    class MyChromeClient extends LimeWebChromeClient {

    }


    /**
     * 初始化webview配置
     *
     * @param webView
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initWebSetting(WebView webView, Context mContext) {
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

        String cacheDirPath = mContext.getFilesDir().getAbsolutePath() + "cache";
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
