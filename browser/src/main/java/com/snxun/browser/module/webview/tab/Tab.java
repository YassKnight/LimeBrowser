package com.snxun.browser.module.webview.tab;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.snxun.browser.R;
import com.snxun.browser.controller.TabController;
import com.snxun.browser.controller.WebViewController;
import com.snxun.browser.module.webview.client.LimeWebChromeClient;
import com.snxun.browser.module.webview.client.LimeWebViewClient;
import com.snxun.browser.util.ViewUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class Tab {
    public static final String DEFAULT_BLANK_URL = "about:blank";
    private final static String TAG = "TAB";
    private static final int INITIAL_PROGRESS = 5;
    private static final int MSG_CAPTURE = 42;
    private boolean mWillBeClosed = false;
    private static Bitmap sDefaultFavicon; //默认网站图标

    private long mId = -1;

    // WebView controller
    WebViewController mWebViewController;

    //
    Context mContext;

    // Main WebView wrapper
    private View mContainer;
    // Main WebView
    private WebView mMainView;
    // Subwindow container
    private View mSubViewContainer;
    // Subwindow WebView
    private WebView mSubView;
    // Saved bundle for when we are running low on memory. It contains the
    // information needed to restore the WebView if the user goes back to the
    // tab.
    private Bundle mSavedState;

    // If true, the tab is in page loading state (after onPageStarted,
    // before onPageFinsihed)
    private boolean mInPageLoad;

    // The last reported progress of the current page
    private int mPageLoadProgress;

    // The time the load started, used to find load page time
    private long mLoadStartTime;

    private int mCaptureWidth;
    private int mCaptureHeight;

    private Bitmap mCapture;
    private Handler mHandler;
    private boolean mUpdateThumbnail;

    public String mSavePageTitle;
    public String mSavePageUrl;
    // save page
    HashMap<Integer, Long> mSavePageJob;
    private PageState mCurrentState;

    // 用来存储页面信息
    static final String ID = "_id";
    static final String CURRENT_URL = "currentUrl";
    static final String CURRENT_TITLE = "currentTitle";
    private boolean mInForeground;
    private static Paint sAlphaPaint = new Paint();
    private Stack<String> mBrowsedHistory = new Stack<>();
    private int mNetType = 1;

    static {
        sAlphaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        sAlphaPaint.setColor(Color.TRANSPARENT);
    }

    // 获取默认网页图标
    private static synchronized Bitmap getDefaultFavicon(Context context) {
        if (sDefaultFavicon == null) {
            sDefaultFavicon = BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.ic_home);
        }
        return sDefaultFavicon;
    }


    public void loadBlank() {
        loadUrl(DEFAULT_BLANK_URL, null, false);
    }

    public Tab(WebViewController webViewController) {
        this(webViewController, null);
    }

    public Tab(WebViewController webViewController, Bundle state) {
        mSavePageJob = new HashMap<Integer, Long>();
        mWebViewController = webViewController;
        mContext = mWebViewController.getContext();
        mCurrentState = new PageState(mContext);
        mInPageLoad = false;
        mCaptureWidth = ViewUtils.getScreenSize(mContext).x;
        mCaptureHeight = ViewUtils.getScreenSize(mContext).y;
        updateShouldCaptureThumbnails();
        restoreState(state);
        if (getId() == -1) {
            mId = TabController.getNextId();
        }
        setWebView(createNewWebView());
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_CAPTURE:
                        capture();
                        break;
                }
            }
        };
        mBrowsedHistory.push(DEFAULT_BLANK_URL);
    }

    private void restoreState(Bundle state) {
        mSavedState = state;
        if (mSavedState == null) {
            return;
        }
        mId = state.getLong(ID);
        String url = state.getString(CURRENT_URL);
        String title = state.getString(CURRENT_TITLE);
        mCurrentState = new PageState(mContext, url, title);
    }

    public void shouldUpdateThumbnail(boolean should) {
        mUpdateThumbnail = should;
        if (should) capture();
    }

    /**
     * This is used to get a new ID when the tab has been preloaded, before it is displayed and
     * added to TabControl. Preloaded tabs can be created before restoreInstanceState, leading
     * to overlapping IDs between the preloaded and restored tabs.
     */
    public void refreshIdAfterPreload() {
        mId = TabController.getNextId();
    }

    public void updateShouldCaptureThumbnails() {
        synchronized (Tab.this) {
            if (mCapture == null) {
                mCapture = Bitmap.createBitmap(mCaptureWidth, mCaptureHeight,
                        Bitmap.Config.RGB_565);
                mCapture.eraseColor(Color.WHITE);
            }
        }
    }

    public void setController(WebViewController ctl) {
        mWebViewController = ctl;
        updateShouldCaptureThumbnails();
    }

    public long getId() {
        return mId;
    }

    public void setWebView(WebView w) {
        setWebView(w, true);
    }

    /**
     * Sets the WebView for this tab, correctly removing the old WebView from
     * the container view.
     */
    private void setWebView(WebView w, boolean restore) {
        if (mMainView == w) {
            return;
        }
        mWebViewController.onSetWebView(this, w);

        if (mMainView != null) {
            mMainView.setPictureListener(null);
            if (w != null) {
                syncCurrentState(w, null);
            } else {
                mCurrentState = new PageState(mContext);
            }
        }
        // set the new one
        mMainView = w;
        // attach the WebViewClient, WebChromeClient and DownloadListener
        TabController tc = mWebViewController.getTabController();
        if (mMainView != null) {
            if (restore && (mSavedState != null)) {
                WebBackForwardList restoredState
                        = mMainView.restoreState(mSavedState);
                if (restoredState == null || restoredState.getSize() == 0) {
                    loadUrl(mCurrentState.mOriginalUrl, null, true);
                }
                mSavedState = null;
            }
        }
    }

    /**
     * Recreate the main WebView of the given tab.
     */
    public void recreateWebView() {

        this.destroy();
        setWebView(createNewWebView(), false);
    }

    /**
     * Destroy the tab's main WebView and subWindow if any
     */
    public void destroy() {
        if (mMainView != null) {
            dismissSubWindow();
            // save the WebView to call destroy() after detach it from the tab
            WebView webView = mMainView;
            setWebView(null);
            webView.destroy();
        }
    }

    /**
     * Dismiss the subWindow for the tab.
     */
    void dismissSubWindow() {
        if (mSubView != null) {
            mSubView.destroy();
            mSubView = null;
            mSubViewContainer = null;
        }
    }

    void resume() {
        if (mMainView != null) {
            setupHwAcceleration(mMainView);
            mMainView.onResume();
            if (mSubView != null) {
                mSubView.onResume();
            }
        }
    }

    void pause() {
        if (mMainView != null) {
            mMainView.onPause();
            if (mSubView != null) {
                mSubView.onPause();
            }
        }
    }

    public void putInForeground() {
        if (mInForeground) {
            return;
        }
        mInForeground = true;
        resume();
    }

    public void putInBackground() {
        Log.e(TAG, "putInBackground ------- mInForeground =:" + mInForeground);
        if (!mInForeground) {
            return;
        }
        capture();
        mInForeground = false;
        pause();
        mMainView.setOnCreateContextMenuListener(null);
        if (mSubView != null) {
            mSubView.setOnCreateContextMenuListener(null);
        }
    }

    boolean inForeground() {
        return mInForeground;
    }

    /**
     * Return the main window of this tab. Note: if a tab is freed in the
     * background, this can return null. It is only guaranteed to be
     * non-null for the current tab.
     *
     * @return The main WebView of this tab.
     */
    public WebView getWebView() {
        return mMainView;
    }

    void setViewContainer(View container) {
        mContainer = container;
    }

    public String getUrl() {
        return mCurrentState.mUrl;
    }

    public boolean checkUrlNotNull() {
        return mCurrentState.checkUrlNotNull();
    }

    public String getCurrentUrl() {
        return mBrowsedHistory.peek();
    }

    public String getPreUrl() {
        int size = mBrowsedHistory.size();
        int pre = size - 2;
        if (pre >= 0) {
            return mBrowsedHistory.elementAt(pre);
        }
        return DEFAULT_BLANK_URL;
    }

    public String getOriginalUrl() {
        if (mCurrentState.mOriginalUrl == null) {
            return getUrl();
        }
        return mCurrentState.mOriginalUrl;
    }

    /**
     * Get the title of this tab.
     */
    public String getTitle() {
        if (mCurrentState.mTitle == null && mInPageLoad) {
            return mContext.getString(R.string.title_bar_loading);
        }
        return mCurrentState.mTitle;
    }

    /**
     * Get the favicon of this tab.
     */
    public Bitmap getFavicon() {
        if (mCurrentState.mFavicon != null) {
            return mCurrentState.mFavicon;
        }
        return getDefaultFavicon(mContext);
    }

    public int getPageLoadProgress() {
        return mPageLoadProgress;
    }

    public boolean isBlank() {
        return mBrowsedHistory.peek().equals(DEFAULT_BLANK_URL);
    }

    public void insertBlank() {
        mBrowsedHistory.push(DEFAULT_BLANK_URL);
        for (int i = 0; i < mBrowsedHistory.size(); i++) {
            Log.e(TAG, "insertBlank :: 第 " + i + "项  :" + mBrowsedHistory.elementAt(i));
        }
    }

    public void clearWebHistory() {
        mMainView.clearHistory();
        mMainView.clearCache(true);
        popBrowsedHistory();
//        mMainView.loadUrl(DEFAULT_BLANK_URL);
    }

    public void popBrowsedHistory() {
        mBrowsedHistory.pop();
    }


    public void clearTabData() {
        mUpdateThumbnail = false;
        mCurrentState.mUrl = DEFAULT_BLANK_URL;
        mCurrentState.mOriginalUrl = DEFAULT_BLANK_URL;
        mCurrentState.mTitle = mContext.getString(R.string.defaultWebTitle);
        mCurrentState.mFavicon = getDefaultFavicon(mContext);
        mBrowsedHistory.clear();
        insertBlank();
    }

    /**
     * @return TRUE if onPageStarted is called while onPageFinished is not
     * called yet.
     */
    boolean inPageLoad() {
        return mInPageLoad;
    }

    /**
     * @return The Bundle with the tab's state if it can be saved, otherwise null
     */
    public Bundle saveState() {
        // If the WebView is null it means we ran low on memory and we already
        // stored the saved state in mSavedState.
        if (mMainView == null) {
            return mSavedState;
        }

        if (TextUtils.isEmpty(mCurrentState.mUrl)) {
            return null;
        }

        mSavedState = new Bundle();
        WebBackForwardList savedList = mMainView.saveState(mSavedState);
        if (savedList == null || savedList.getSize() == 0) {
            Log.w(TAG, "Failed to save back/forward list for "
                    + mCurrentState.mUrl);
        }

        mSavedState.putLong(ID, mId);
        mSavedState.putString(CURRENT_URL, mCurrentState.mUrl);
        mSavedState.putString(CURRENT_TITLE, mCurrentState.mTitle);
        return mSavedState;
    }

    public Bitmap getScreenshot() {
        synchronized (Tab.this) {
            return mCapture;
        }
    }

    private void setupHwAcceleration(View web) {
        if (web == null) return;
        // 这里需要用户自己设置
        if (true) {
            web.setLayerType(View.LAYER_TYPE_NONE, null);
        } else {
            web.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void stopLoading() {
        if (mMainView != null && inPageLoad()) {
            mMainView.stopLoading();
        }
    }

    public void reloadPage() {
        mMainView.reload();
    }

    private void syncCurrentState(WebView view, String url) {
        if (mWillBeClosed) {
            return;
        }
        mCurrentState.mUrl = view.getUrl();
        if (mCurrentState.mUrl == null) {
            mCurrentState.mUrl = mContext.getString(R.string.defaultWebTitle);
        }
        mCurrentState.mOriginalUrl = view.getOriginalUrl();
        mCurrentState.mTitle = view.getTitle();
        mCurrentState.mFavicon = view.getFavicon();
    }

    public void loadUrl(String url, Map<String, String> headers, boolean record) {
        if (mMainView != null) {
            mPageLoadProgress = INITIAL_PROGRESS;
            mInPageLoad = true;
            mWebViewController.onPageStarted(this, mMainView, null);
            try {
                mMainView.loadUrl(url, headers);
                if (record) mBrowsedHistory.push(url);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public void setNetType(int netType) {
        mNetType = netType;
    }

    public int getNetType() {
        return mNetType;
    }

    public void capture() {
        if (mMainView == null || mCapture == null) return;
        View view = !isBlank() ? mMainView : mWebViewController.getActivity().getWindow().getDecorView();
        mCapture = Bitmap.createScaledBitmap(mCapture, mCaptureWidth, mCaptureHeight, true);
        Canvas c = new Canvas(mCapture);
        int state = c.save();
        if (!isBlank()) {
            c.translate(0, mContext.getResources().getDimensionPixelSize(R.dimen.dimen_48dp));
        }
        view.draw(c);
        c.restoreToCount(state);
        c.setBitmap(null);
        mHandler.removeMessages(MSG_CAPTURE);
        TabController tc = mWebViewController.getTabController();
        if (tc != null) {
            TabController.OnThumbnailUpdatedListener updateListener
                    = tc.getOnThumbnailUpdatedListener();
            if (updateListener != null) {
                updateListener.onThumbnailUpdated(this);
            }
        }
    }

    public boolean canGoBack() {
        boolean isBlank = DEFAULT_BLANK_URL.equals(mBrowsedHistory.peek());
        boolean isSingle = mBrowsedHistory.size() == 1;
        return mMainView != null ? !(isSingle && isBlank) : false;
    }

    public boolean canGoForward() {
        return mMainView != null ? mMainView.canGoForward() : false;
    }

    public void goBack() {
        if (mMainView != null) {
            mMainView.loadUrl(mBrowsedHistory.pop());
        }
    }

    public boolean webCanGoBack() {
        if (mMainView != null) {

            return mMainView.canGoBack();
        }
        return false;
    }

    public void webGoBack() {
        if (mMainView != null) {
            mMainView.goBack();
        }
    }

    public boolean webCanGoForward() {
        if (mMainView != null) {
            return mMainView.canGoForward();
        }
        return false;
    }

    public void webGoForward() {
        if (mMainView != null) {
            mMainView.goForward();
        }
    }

    public static class PageState {
        String mUrl;
        String mOriginalUrl;
        String mTitle;
        Bitmap mFavicon;


        PageState(Context context) {
            this(context, "", getDefaultFavicon(context));
        }

        PageState(Context context, String url, Bitmap favicon) {
            this(url, context.getString(R.string.defaultWebTitle), favicon);
        }

        PageState(Context context, String url, String title) {
            this(url, title, getDefaultFavicon(context));
        }

        PageState(String url, String title, Bitmap favicon) {
            mUrl = mOriginalUrl = url;
            mTitle = title;
            mFavicon = favicon;
        }

        boolean checkUrlNotNull() {
            return !TextUtils.isEmpty(mUrl);
        }
    }


    /**
     * Creates a new WebView and registers it with the global settings.
     */
    private WebView createNewWebView() {
        WebView w = mWebViewController.getWebViewFactory().createWebView(mContext);
        LimeWebViewClient webViewClient = mWebViewController.getWebViewFactory().createWebViewClient();
        if (webViewClient != null) {
            webViewClient.setOnPageChangeListener(new LimeWebViewClient.onPageChangeListener() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    mInPageLoad = true;
                    mUpdateThumbnail = true;
                    mPageLoadProgress = INITIAL_PROGRESS;
                    mCurrentState = new PageState(mContext, url, favicon);
                    mLoadStartTime = SystemClock.uptimeMillis();
                    mWebViewController.onPageStarted(Tab.this, view, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    syncCurrentState(view, url);
                    if (url != null && url.equals(mSavePageUrl)) {
                        mCurrentState.mTitle = mSavePageTitle;
                        mCurrentState.mUrl = mSavePageUrl;
                    }
                    mWebViewController.onPageFinished(Tab.this);
                }
            });
            w.setWebViewClient(webViewClient);
        }
        LimeWebChromeClient chromeClient = mWebViewController.getWebViewFactory().createWebChromeClient();
        if (chromeClient != null) {
            chromeClient.setonPageChangeListener(new LimeWebChromeClient.onPageChangeListener() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    mPageLoadProgress = newProgress;
                    if (newProgress == 100) {
                        mInPageLoad = false;
                        syncCurrentState(view, view.getUrl());
                    }
                    mWebViewController.onProgressChanged(Tab.this);
                    if (mUpdateThumbnail && newProgress == 100) {
                        mUpdateThumbnail = false;
                    }
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    mCurrentState.mTitle = title;
                    mWebViewController.onReceivedTitle(Tab.this, title);
                }

                @Override
                public void onReceivedIcon(WebView view, Bitmap icon) {
                    mCurrentState.mFavicon = icon;
//            mWebViewController.onFavicon(Tab.this, view, icon);
                }

                @Override
                public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {

                }

                @Override
                public void onHideCustomView() {

                }
            });
            w.setWebChromeClient(chromeClient);
        }
        return w;
    }

}
