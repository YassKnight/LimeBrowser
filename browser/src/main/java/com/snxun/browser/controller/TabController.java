package com.snxun.browser.controller;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.snxun.browser.R;
import com.snxun.browser.module.webview.tab.Tab;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TabController {
    private final String TAG = "TabController";
    private static long sNextId = 1;
    private static final String POSITIONS = "positions";
    private static final String CURRENT = "current";

    public static interface OnThumbnailUpdatedListener {
        void onThumbnailUpdated(Tab t);
    }

    // Maximum number of tabs.
    private int mMaxTabs;
    // Private array of WebViews that are used as tabs.
    private ArrayList<Tab> mTabs;
    // Queue of most recently viewed tabs.
    private ArrayList<Tab> mTabQueue;
    // Current position in mTabs.
    private int mCurrentTab = -1;
    private OnThumbnailUpdatedListener mOnThumbnailUpdatedListener;
    /// M: add for browser memory optimization, maintain the free tabs index @ {
    private CopyOnWriteArrayList<Integer> mFreeTabIndex = new CopyOnWriteArrayList<Integer>();
    /// @ }
    // the main browser controller
    private UiController mController;

    /**
     * Construct a new TabControl object
     */
    public TabController(Context context, UiController controller) {
        mController = controller;
        mMaxTabs = context.getResources().getInteger(R.integer.max_tab_count);
        mTabs = new ArrayList<Tab>(mMaxTabs);
        mTabQueue = new ArrayList<Tab>(mMaxTabs);
    }

    public synchronized static long getNextId() {
        return sNextId++;
    }

    /**
     * Return the current tab's main WebView. This will always return the main
     * WebView for a given tab and not a subwindow.
     *
     * @return The current tab's WebView.
     */
    public WebView getCurrentWebView() {
        Tab t = getTab(mCurrentTab);
        if (t == null) {
            return null;
        }
        return t.getWebView();
    }

    /**
     * return the list of tabs
     */
    public List<Tab> getTabs() {
        return mTabs;
    }

    /**
     * Return the tab at the specified position.
     *
     * @return The Tab for the specified position or null if the tab does not
     * exist.
     */
    public Tab getTab(int position) {
        if (position >= 0 && position < mTabs.size()) {
            return mTabs.get(position);
        }
        return null;
    }

    /**
     * Return the current tab.
     *
     * @return The current tab.
     */
    public Tab getCurrentTab() {
        return getTab(mCurrentTab);
    }

    /**
     * Return the current tab position.
     *
     * @return The current tab position
     */
    public int getCurrentPosition() {
        return mCurrentTab;
    }

    /**
     * Given a Tab, find it's position
     *
     * @return position of Tab or -1 if not found
     */
    public int getTabPosition(Tab tab) {
        if (tab == null) {
            return -1;
        }
        return mTabs.indexOf(tab);
    }

    public boolean canCreateNewTab() {
        return mMaxTabs > mTabs.size();
    }

    public Tab createNewTab() {
        return createNewTab(null);
    }

    public Tab createNewTab(Bundle state) {
        // Create a new tab and add it to the tab list
        Tab t = new Tab(mController, state);
        mTabs.add(t);
        if (mController != null) {
            mController.onTabCountChanged();
        }
        // Initially put the tab in the background.
        t.putInBackground();
        return t;
    }

    public boolean removeTab(int index) {
        Log.e(TAG, "removeTab :: index =:" + index + ",getTabCount() =:" + getTabCount());
        if (index < 0 || index >= getTabCount()) {
            return false;
        }
        return removeTab(mTabs.get(index));
    }

    /**
     * Remove the tab from the list. If the tab is the current tab shown, the
     * last created tab will be shown.
     *
     * @param t The tab to be removed.
     */
    public boolean removeTab(Tab t) {
        if (t == null) {
            return false;
        }

        // Grab the current tab before modifying the list.
        Tab current = getCurrentTab();

        // Remove t from our list of tabs.
        mTabs.remove(t);

        // Put the tab in the background only if it is the current one.
        if (current == t) {
            t.putInBackground();
            mCurrentTab = -1;
        } else {
            // If a tab that is earlier in the list gets removed, the current
            // index no longer points to the correct tab.
            mCurrentTab = getTabPosition(current);
            Log.e(TAG, "removeTab mCurrentTab =:" + mCurrentTab + ",getTabCount() =:" + getTabCount());
            if (mCurrentTab >= getTabCount()) {
                mCurrentTab--;
            }
        }

        // destroy the tab
        t.destroy();

        // Remove it from the queue of viewed tabs.
        mTabQueue.remove(t);
        if (mController != null) {
            mController.onTabCountChanged();
        }
        return true;
    }

    /**
     * Destroy all the tabs and subwindows
     */
    public void destroy() {
        for (Tab t : mTabs) {
            t.destroy();
        }
        mTabs.clear();
        mTabQueue.clear();
        if (mController != null) {
            mController.onTabCountChanged();
        }
    }

    /**
     * Returns the number of tabs created.
     *
     * @return The number of tabs created.
     */
    public int getTabCount() {
        return mTabs.size();
    }


    // Used by Tab.onJsAlert() and friends
    public void setActiveTab(Tab tab) {
        mCurrentTab = mTabs.indexOf(tab);
        WebView webView = getCurrentWebView();
        if (webView != null && webView.getParent() != null) {
            ((ViewGroup) webView.getParent()).removeView(webView);
        }
    }

    public void setOnThumbnailUpdatedListener(OnThumbnailUpdatedListener listener) {
        mOnThumbnailUpdatedListener = listener;
        for (Tab t : mTabs) {
            WebView web = t.getWebView();
            if (web != null) {
                // web.setPictureListener(listener != null ? t : null);
            }
        }
    }

    public OnThumbnailUpdatedListener getOnThumbnailUpdatedListener() {
        return mOnThumbnailUpdatedListener;
    }
}
