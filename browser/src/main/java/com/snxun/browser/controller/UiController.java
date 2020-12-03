package com.snxun.browser.controller;


import com.snxun.browser.module.webview.tab.Tab;

public interface UiController extends WebViewController {
    void onWebsiteIconClicked(String url);

    void selectTab(Tab tab);

    void closeTab(Tab tab);

    void onTabCountChanged();

    void onTabDataChanged(Tab tab);
}
