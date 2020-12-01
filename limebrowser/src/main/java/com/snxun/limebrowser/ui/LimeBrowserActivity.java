package com.snxun.limebrowser.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lodz.android.pandora.base.activity.AbsActivity;
import com.snxun.limebrowser.R;
import com.snxun.limebrowser.bean.WebApplicationBean;
import com.snxun.limebrowser.controller.TabController;
import com.snxun.limebrowser.controller.UiController;
import com.snxun.limebrowser.module.bottomview.LimeBottomView;
import com.snxun.limebrowser.module.recyclerview.AppRecyclerViewAdapter;
import com.snxun.limebrowser.module.rootview.RootView;
import com.snxun.limebrowser.module.stackview.widget.LimeStackView;
import com.snxun.limebrowser.module.stackview.widget.LimeTabCard;
import com.snxun.limebrowser.module.webview.factory.WebViewFactory;
import com.snxun.limebrowser.module.webview.tab.Tab;
import com.snxun.limebrowser.module.webview.tab.TabAdapter;
import com.snxun.limebrowser.util.ViewUtils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;


/**
 * 浏览器主页
 * Created by Yangjw on 2020/11/27.
 */
public class LimeBrowserActivity extends AbsActivity implements LimeStackView.OnChildDismissedListener, UiController {

    private static final String TAG = "LimeBrowserActivity";
    /**
     * 浏览器首页应用数据的key
     */
    private static final String WEB_APP_DATA = "WEB_APP_DATA";
    /**
     * 浏览器首页应用数据集
     */
    private ArrayList<WebApplicationBean> mList;
    /**
     * 当前显示的是否是主界面（包含应用列表的界面）
     */
    private boolean mIsInMain = true;
    /**
     * 当前是否显示多窗口管理界面
     */
    private boolean mTabsManagerUIShown = false;
    private boolean mIsAnimating = false;

    /**
     * tab页面相关
     */
    private TabController mTabController;
    private Tab mActiveTab;
    private TabAdapter mTabAdapter;
    /**
     * webview工厂
     */
    private WebViewFactory mFactory;
    /**
     * 控件：rootview
     */
    private RootView mRootView;
    /**
     * 控件:stackview
     */
    private LimeStackView mStackView;
    /**
     * 布局：存放rootview的帧布局
     */
    private FrameLayout mContentWrapper;
    /**
     * 布局：多窗口管理界面布局
     */
    private FrameLayout mTabsManagerLayout;
    /**
     * 控件：多窗口管理界面的添加按钮
     */
    private ImageView mAddTabBtn;
    /**
     * 控件：关闭多窗口管理页面，返回主界面
     */
    private TextView mBackToMainBtn;
    /**
     * 控件：打开多窗口管理界面的按钮
     */
    private FrameLayout mOpenMultiBtn;
    /**
     * 控件：打开多窗口管理界面按钮里面的显示的窗口数量textview
     */
    private TextView mMultiNumTv;
    /**
     * 布局：底部菜单栏
     */
    private LimeBottomView mBottomLayout;

    public static void start(Context context, ArrayList<WebApplicationBean> list) {
        Intent intent = new Intent();
        intent.setClass(context, LimeBrowserActivity.class);
        intent.putParcelableArrayListExtra(WEB_APP_DATA, list);
        context.startActivity(intent);
    }

    /**
     * 展示应用列表的rv相关
     */
    private RecyclerView mRecyclerView;
    private AppRecyclerViewAdapter mRecyclerViewAdapter;

    @Override
    protected int getAbsLayoutId() {
        return R.layout.activity_browser_home;
    }


    @Override
    protected void findViews(@Nullable Bundle savedInstanceState) {
        super.findViews(savedInstanceState);
        initView();
        initRecyclerView();
        mTabController = new TabController(getContext(), this);
        mTabAdapter = new TabAdapter(this, this);
        mStackView.setAdapter(mTabAdapter);
        mFactory = new WebViewFactory(this);
        // 先建立一个tab标记主页
        if (mTabController.getTabCount() <= 0) {
            addTab(false);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mStackView.setOnChildDismissedListener(this);
        //app点击事件，切换至webview
        mRecyclerViewAdapter.setItemListener(new AppRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void setOnItemClickListener(WebApplicationBean bean) {
                load(bean.appLoadUrl);
            }
        });
        //添加一个窗口
        mAddTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTab(true);
            }
        });
        //关闭多窗口
        mBackToMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTabs(true);
            }
        });
        //打开多窗口
        mOpenMultiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTabs();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mList = getIntent().getParcelableArrayListExtra(WEB_APP_DATA);
        if (mList != null) {
            mRecyclerViewAdapter.setData(mList);
        }

    }

    /**
     * findViewById
     */
    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerview);
        mRootView = findViewById(R.id.root_view);
        mContentWrapper = findViewById(R.id.home_content_layout);
        mTabsManagerLayout = findViewById(R.id.flPagersManager);
        mStackView = findViewById(R.id.stack_view);
        mAddTabBtn = findViewById(R.id.ivAddPager);
        mBackToMainBtn = findViewById(R.id.tvBack);
        mOpenMultiBtn = findViewById(R.id.bottom_multi_layout);
        mMultiNumTv = findViewById(R.id.bottom_multi_tv);
        mBottomLayout=findViewById(R.id.home_bottom_layout);

    }

    /**
     * 初始化Rv
     */
    private void initRecyclerView() {
        //布局
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        //适配器
        mRecyclerViewAdapter = new AppRecyclerViewAdapter(getContext());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

    }

    private void load(String url) {
        if (mActiveTab != null) {
            mActiveTab.clearWebHistory();
            mActiveTab.loadUrl(url, null, true);
            switchToTab();
        }
    }

    /**
     * @param animate 是否有动画，有动画时即UCRootView从下往上移
     */
    private void addTab(boolean animate) {
        if (animate) {
            switchToMain();
            mContentWrapper.bringToFront();
            animateShowFromBottomToTop(mContentWrapper, new Runnable() {
                @Override
                public void run() {
                    hideTabs(false); // 把页面管理页隐藏
                    mBottomLayout.bringToFront();
                    //            initWindow();
                }
            });
        }
        Tab tab = mTabController.createNewTab();
        mActiveTab = tab;
        mTabController.setActiveTab(mActiveTab);
    }

    /**
     * 切换至主界面
     */
    private void switchToMain() {
        if (mRootView.getParent() == null) {
            mContentWrapper.addView(mRootView);
        }
        mRootView.bringToFront();
        WebView view = mActiveTab.getWebView();
        if (view != null) {
            mContentWrapper.removeView(view);
        }
        mActiveTab.stopLoading();
        mIsInMain = true;
    }

    /**
     * 切换至webview
     */
    private void switchToTab() {
        if (mRootView.getParent() != null) {
            mContentWrapper.removeView(mRootView);
        }
        WebView view = mActiveTab.getWebView();
        if (view != null && view.getParent() == null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
            if (lp == null) {
                lp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            }
//            lp.topMargin = getResources().getDimensionPixelSize(R.dimen.dimen_48dp);
            mContentWrapper.addView(view, lp);
        }
        mIsInMain = false;
    }

    /**
     * 隐藏tab
     *
     * @param animated
     */
    public void hideTabs(boolean animated) {
        if (isAnimating()) {
            return;
        }
        if (animated) {
            mStackView.animateShow(mTabController.getCurrentPosition(), mContentWrapper, mTabsManagerLayout, false, new Runnable() {
                @Override
                public void run() {
                    mTabsManagerLayout.setVisibility(View.GONE);
//                    initWindow();
                }
            });
            View selectedChild = mStackView.getSelectedChild();
            if (selectedChild != null) {
                LimeTabCard card = selectedChild.findViewById(R.id.ucTabCard);
                card.active(false, 350, 40, null);
            }
            animateShowFromAlpha(mTabsManagerLayout.findViewById(R.id.bottomBar),
                    false, animated, 350, 40, null);
        } else {
//            initWindow();
        }
        mContentWrapper.setVisibility(View.VISIBLE);
        mTabsManagerUIShown = false;
    }

    /**
     * 进入多窗口管理界面，用动画改变选择页（可以理解为一张截图）的Y和scale
     */
    public void showTabs() {
        if (isAnimating()) {
            return;
        }
        mActiveTab.capture();
        mTabAdapter.updateData(mTabController.getTabs());
        mTabsManagerLayout.bringToFront();
        mTabsManagerLayout.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.windowGrayL, null));
        }
        mStackView.animateShow(mTabController.getCurrentPosition(), mContentWrapper, mTabsManagerLayout, true, new Runnable() {
            @Override
            public void run() {
                mContentWrapper.setVisibility(View.GONE);
                mTabsManagerUIShown = true;
            }
        });
        View selectedChild = mStackView.getSelectedChild();
        if (selectedChild != null) {
            LimeTabCard card = selectedChild.findViewById(R.id.ucTabCard);
            card.active(true, 350, 40, null);
        }
        animateShowFromAlpha(mTabsManagerLayout.findViewById(R.id.bottomBar),
                true, true, 300, 40, null);
    }

    /**
     * 关闭已打开的tab
     *
     * @param index 需要关闭的tab位置
     */
    private void onTabClosed(int index) {
        removeTab(index);
        if (mStackView.getChildCount() <= 0) {
            addTab(true);
        }
    }

    /**
     * 移除tab集合中记录的tab
     *
     * @param index 移除的tab位置
     */
    private void removeTab(int index) {
        mTabController.removeTab(index);
    }

    public boolean isAnimating() {
        return mRootView.isAnimating() || mStackView.isAnimating() || mIsAnimating;
    }

    /**
     * 从底部至上显示动画
     *
     * @param view               应用动画的view
     * @param onCompleteRunnable 运行的子线程
     */
    public void animateShowFromBottomToTop(View view, final Runnable onCompleteRunnable) {
        mContentWrapper.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                view,
                "translationY",
                ViewUtils.getScreenSize(this).y,
                0);
        animator.setDuration(500);
        animator.start();
        mIsAnimating = true;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
                if (onCompleteRunnable != null) {
                    onCompleteRunnable.run();
                }
            }
        });
    }

    /**
     * 透明度动画
     *
     * @param view
     * @param show
     * @param animate
     * @param duration
     * @param startDelay
     * @param onCompleteRunnable
     */
    public void animateShowFromAlpha(final View view, final boolean show,
                                     boolean animate, int duration, int startDelay, final Runnable onCompleteRunnable) {
        if (animate) {
            float startAlpha = show ? 0.0f : 1.0f;
            float finalAlpha = show ? 1.0f : 0.0f;
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", startAlpha, finalAlpha);
            animator.setDuration(duration);
            animator.setStartDelay(startDelay);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    if (onCompleteRunnable != null) {
                        onCompleteRunnable.run();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (onCompleteRunnable != null) {
                        onCompleteRunnable.run();
                    }
                }
            });
            animator.start();
        } else {
            view.setVisibility(show ? View.VISIBLE : View.GONE);
            view.setAlpha(show ? 1.0f : 0.0f);
        }
    }

    @Override
    public void onChildDismissed(int index) {
        Log.e(TAG, "onChildDismissed :: index =: " + index);
        onTabClosed(index);
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public TabController getTabController() {
        return mTabController;
    }

    @Override
    public WebViewFactory getWebViewFactory() {
        return mFactory;
    }

    @Override
    public void onSetWebView(Tab tab, WebView view) {

    }

    @Override
    public void onPageStarted(Tab tab, WebView webView, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(Tab tab) {
        tab.shouldUpdateThumbnail(true);
        mTabAdapter.notifyDataSetChanged();
    }

    @Override
    public void onProgressChanged(Tab tab) {

    }

    @Override
    public void onReceivedTitle(Tab tab, String title) {

    }

    @Override
    public void onWebsiteIconClicked(String url) {
    }

    @Override
    public void selectTab(Tab tab) {
        if (isAnimating()) {
            return;
        }
        int index = mTabController.getTabPosition(tab);
        mActiveTab = tab;
        mTabController.setActiveTab(mActiveTab);
        if (!mActiveTab.isBlank()) {
            switchToTab();
        } else {
            switchToMain();
        }
        mStackView.selectTab(index, new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "onSelect ----- mActiveTab.checkUrlNotNull() =:" + mActiveTab.checkUrlNotNull() + "mActiveTab " + mActiveTab.getTitle() + "," + mActiveTab.getUrl());
                mContentWrapper.setVisibility(View.VISIBLE);
                mTabsManagerLayout.setVisibility(View.GONE);
                mTabsManagerUIShown = false;
//                initWindow();
            }
        });
        View selectedChild = mStackView.getSelectedChild();
        if (selectedChild != null) {
            LimeTabCard card = selectedChild.findViewById(R.id.ucTabCard);
            card.active(false, 350, 40, null);
        }
        animateShowFromAlpha(mTabsManagerLayout.findViewById(R.id.bottomBar),
                false, true, 300, 40, null);
        Log.e(TAG, "onSelect :: key =:" + tab.getId());
    }

    @Override
    public void closeTab(Tab tab) {
        mStackView.closeTab(mTabController.getTabPosition(tab));
    }

    @Override
    public void onTabCountChanged() {
        //更新页面数量
        mMultiNumTv.setText("" + mTabController.getTabCount());
    }

    @Override
    public void onTabDataChanged(Tab tab) {
        mTabAdapter.notifyDataSetChanged();
    }
}
