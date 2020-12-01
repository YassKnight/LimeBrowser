package com.snxun.limebrowser.controller;


import com.snxun.limebrowser.module.webview.tab.Tab;

public interface UiController extends WebViewController {
    void onWebsiteIconClicked(String url);

    void selectTab(Tab tab);

    void closeTab(Tab tab);

    void onTabCountChanged();

    void onTabDataChanged(Tab tab);
}
