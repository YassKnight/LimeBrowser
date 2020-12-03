package com.snxun.browser.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yangjw on 2020/12/1.
 */
public class WebApplicationBean implements Parcelable {
    /**
     * webview应用需要加载的url
     */
    public String appLoadUrl;
    /**
     * app图标的路径
     */
    public String appIconUrl;
    /**
     * app的名称
     */
    public String appName;

    public WebApplicationBean(String appLoadUrl, String appIconUrl, String appName) {
        this.appLoadUrl = appLoadUrl;
        this.appIconUrl = appIconUrl;
        this.appName = appName;
    }

    protected WebApplicationBean(Parcel in) {
        appLoadUrl = in.readString();
        appIconUrl = in.readString();
        appName = in.readString();
    }

    public static final Creator<WebApplicationBean> CREATOR = new Creator<WebApplicationBean>() {
        @Override
        public WebApplicationBean createFromParcel(Parcel in) {
            return new WebApplicationBean(in);
        }

        @Override
        public WebApplicationBean[] newArray(int size) {
            return new WebApplicationBean[size];
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
