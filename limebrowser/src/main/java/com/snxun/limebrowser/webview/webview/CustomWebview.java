package com.snxun.limebrowser.webview.webview;

import android.app.Application;
import android.graphics.Bitmap;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.snxun.limebrowser.application.BaseApplication;

import java.util.ArrayList;

/**
 * Created by Yangjw on 2020/11/26.
 */
public abstract class
CustomWebview {
    /**
     * 原始URl
     */
    String originalUrl;

    /**
     * 当前Url
     */
    String presentUrl;

    /**
     * 网站图标
     */
    Bitmap webIcon;

    /**
     * webview的身份ID
     */
    String identityId;

    /**
     * 网站标题
     */
    String webTitle;


    /**
     * 重新加载
     */
    public void reLoad() {

    }

    /**
     * 释放webview
     */
    public void release() {

    }

    /**
     * 设置setting
     *
     * @param setting webviewSetting
     */
    public void setWebViewSetting(WebSettings setting) {

    }

    /**
     * 设置客户端
     *
     * @param client webviewClient
     */
    public void setWebViewClient(WebViewClient client) {

    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getPresentUrl() {
        return presentUrl;
    }

    public void setPresentUrl(String presentUrl) {
        this.presentUrl = presentUrl;
    }

    public Bitmap getWebIcon() {
        return webIcon;
    }

    public void setWebIcon(Bitmap webIcon) {
        this.webIcon = webIcon;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public void setWebTitle(String webTitle) {
        this.webTitle = webTitle;
    }
}
