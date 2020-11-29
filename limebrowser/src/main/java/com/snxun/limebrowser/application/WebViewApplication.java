package com.snxun.limebrowser.application;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * webview应用
 * Created by Yangjw on 2020/11/26.
 */
public class WebViewApplication extends BaseApplication implements Parcelable {
    /**
     * webview应用需要加载的url
     */
    public String appLoadUrl;

    public WebViewApplication(String appIconUrl, String appName, String appLoadUrl) {
        this.appIconUrl = appIconUrl;
        this.appName = appName;
        this.appLoadUrl = appLoadUrl;
    }

    protected WebViewApplication(Parcel in) {
        appLoadUrl = in.readString();
        appIconUrl = in.readString();
        appName = in.readString();
    }

    public static final Creator<WebViewApplication> CREATOR = new Creator<WebViewApplication>() {
        @Override
        public WebViewApplication createFromParcel(Parcel in) {
            return new WebViewApplication(in);
        }

        @Override
        public WebViewApplication[] newArray(int size) {
            return new WebViewApplication[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appLoadUrl);
        dest.writeString(appIconUrl);
        dest.writeString(appName);
    }
}
