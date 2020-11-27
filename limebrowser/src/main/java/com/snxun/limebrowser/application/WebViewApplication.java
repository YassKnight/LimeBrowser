package com.snxun.limebrowser.application;

/**
 * webview应用
 * Created by Yangjw on 2020/11/26.
 */
public class WebViewApplication extends BaseApplication {
    /**
     * webview应用需要加载的url
     */
    public String appLoadUrl;

    public WebViewApplication(String appIconUrl, String appName, String appLoadUrl) {
        this.appIconUrl = appIconUrl;
        this.appName = appName;
        this.appLoadUrl = appLoadUrl;
    }
}
