package com.snxun.browser.controller;


import com.snxun.browser.module.webview.tab.Tab;

public interface UiController extends WebViewController {

    void selectTab(Tab tab, boolean isShowAnimating);

    void closeTab(Tab tab);

    void onTabCountChanged();

    void onTabDataChanged(Tab tab);
}
