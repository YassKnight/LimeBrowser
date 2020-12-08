package com.snxun.browser.widget.browser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snxun.browser.R;
import com.snxun.browser.controller.TabController;
import com.snxun.browser.controller.UiController;
import com.snxun.browser.module.bottomview.LimeBottomView;
import com.snxun.browser.module.rootview.RootView;
import com.snxun.browser.module.stackview.widget.LimeStackView;
import com.snxun.browser.module.stackview.widget.LimeTabCard;
import com.snxun.browser.module.webview.factory.WebViewFactory;
import com.snxun.browser.module.webview.tab.Tab;
import com.snxun.browser.module.webview.tab.TabAdapter;
import com.snxun.browser.util.ViewUtils;
import com.snxun.browser.widget.browser.listener.ExitBtnClickListener;
import com.snxun.browser.widget.browser.listener.GoBackBtnClickListener;
import com.snxun.browser.widget.browser.listener.GoForwardBtnClickListener;
import com.snxun.browser.widget.browser.listener.HomeBtnClickListener;
import com.snxun.browser.widget.browser.listener.MultiWindowsBtnClickListener;

/**
 * 对外提供的浏览器控件
 * Created by Yangjw on 2020/12/7.
 */
public class LimeBrowser extends FrameLayout implements UiController ,LimeStackView.OnChildDismissedListener{

    private TypedArray typedArray;
    /**
     * tag
     */
    private static final String TAG = "LimeBrowser";
    /**
     * 是否显示头部布局，默认为true（显示）
     */
    private boolean mIsShowTopBar;
    /**
     * 是否显示底部返回按钮，默认为false（不显示）
     */
    private boolean mIsShowBackBtn;
    /**
     * 是否显示底部前进按钮，默认为false（不显示）
     */
    private boolean mIsshowGoforawrdBtn;
    /**
     * 顶部标题
     */
    private TextView mTitleTv;
    /**
     * 顶部标题栏图标
     */
    private ImageView mTitleIcon;
    /**
     * 底部标题栏布局
     */
    private LinearLayout mTitleLayout;
    /**
     * xml中设置的标题内容
     */
    private String mTitleText;
    /**
     * xml中设置标题图标资源
     */
    private int mTitleIconRes;
    /**
     * 主页内容布局资源
     */
    private int mContentLayoutRes = 0;
    /**
     * 多窗口管理界面布局
     */
    private FrameLayout mPagersManagelayout;
    /**
     * 加载界面布局
     */
    private RelativeLayout mLoadingLayout;
    /**
     * 底部功能栏布局
     */
    private LimeBottomView mBottomLayout;
    /**
     * 底部返回按钮
     */
    private ImageView mBottomBackImg;
    /**
     * 底部前进按钮
     */
    private ImageView mBottomGoForwardImg;
    /**
     * 底部主页按钮
     */
    private ImageView mBottomHomeImg;
    /**
     * 底部离开按钮
     */
    private ImageView mBottomExitImg;
    /**
     * 底部多窗口按钮
     */
    private FrameLayout mBottomMultily;
    /**
     * 底部多窗口按钮显示数量的tv
     */
    private TextView mBottomMultiNumTv;
    /**
     * 底部主页按钮监听器
     */
    private HomeBtnClickListener mHomeBtnClickListener;
    /**
     * 底部返回按钮监听器
     */
    private GoBackBtnClickListener mGoBackBtnClickListener;
    /**
     * 底部前进按钮监听器
     */
    private GoForwardBtnClickListener mGoForwardBtnClickListener;
    /**
     * 底部多窗口监听器
     */
    private MultiWindowsBtnClickListener mMultiWindowsBtnClickListener;
    /**
     * 底部退出按钮监听器
     */
    private ExitBtnClickListener mExitBtnClickListener;
    /**
     * 浏览器主页的内容布局
     */
    private LinearLayout mLimeBrowserHomeContentLayout;
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
     * 当前显示的是否是主界面
     */
    private boolean mIsInMain = true;

    public LimeBrowser(Context context) {
        super(context);
    }

    public LimeBrowser(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_limebrowser, this, true);
        initAttrs(context, attrs);

    }


    /**
     * 初始化tab相关配置
     */
    private void initTabConfig() {
        mTabController = new TabController(getContext(), this);
        mTabAdapter = new TabAdapter(getContext(), this);
        mStackView.setAdapter(mTabAdapter);
        mFactory = new WebViewFactory(getContext());
        Tab tab = mTabController.createNewTab();
        mActiveTab = tab;
        mTabController.setActiveTab(mActiveTab);
        // 先建立一个tab标记主页
        if (mTabController.getTabCount() <= 0) {
            addTab(false);
        }
    }

    public LimeBrowser(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.LimeBrowser);
        mTitleIconRes = typedArray.getResourceId(R.styleable.LimeBrowser_titleIcon, R.drawable.browser_icon);
        mTitleText = typedArray.getString(R.styleable.LimeBrowser_titleText);
        mIsShowTopBar = typedArray.getBoolean(R.styleable.LimeBrowser_showTopbar, true);
        mIsShowBackBtn = typedArray.getBoolean(R.styleable.LimeBrowser_showGobackBtn, false);
        mIsshowGoforawrdBtn = typedArray.getBoolean(R.styleable.LimeBrowser_showGoForwardBtn, false);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findView();
        initTabConfig();
        bindViewData();
        bindViewClickListener();

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
                }
            });
        }
        Tab tab = mTabController.createNewTab();
        mActiveTab = tab;
        mTabController.setActiveTab(mActiveTab);
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
            mStackView.animateShow(mTabController.getCurrentPosition(), mContentWrapper, mPagersManagelayout, false, new Runnable() {
                @Override
                public void run() {
                    mPagersManagelayout.setVisibility(View.GONE);
                }
            });
            View selectedChild = mStackView.getSelectedChild();
            if (selectedChild != null) {
                LimeTabCard card = selectedChild.findViewById(R.id.ucTabCard);
                card.active(false, 350, 40, null);
            }
            animateShowFromAlpha(mPagersManagelayout.findViewById(R.id.bottomBar),
                    false, animated, 350, 40, null);
        }
        mContentWrapper.setVisibility(View.VISIBLE);
        mTabsManagerUIShown = false;
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
                ViewUtils.getScreenSize(getContext()).y,
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
     * 绑定控件监听器
     */
    private void bindViewClickListener() {
        //主页按钮设置监听
        mBottomHomeImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mHomeBtnClickListener.onHomeBtnClick();
            }
        });
        //多窗口按钮设置监听
        mBottomMultily.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMultiWindowsBtnClickListener.onMultiWindowsBtnClick();
            }
        });
        //返回按钮设置监听
        mBottomBackImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoBackBtnClickListener.onGoBackBtnClick();
            }
        });
        //前进按钮设置监听
        mBottomGoForwardImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoForwardBtnClickListener.onGoForwardBtnClickListener();
            }
        });
        //退出按钮设置监听
        mBottomExitImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mExitBtnClickListener.onExitBtnClickListener();

            }
        });
    }

    /**
     * 控件赋值
     */
    private void bindViewData() {
        //设置标题内容
        if (!TextUtils.isEmpty(mTitleText)) {
            setTitleText(mTitleText);
        }
        //设置标题图标
        setTitleIcon(mTitleIconRes);

        //设置标题栏显隐
        if (mIsShowTopBar) {
            setTitleLayoutVisibility(VISIBLE);
        } else {
            setTitleLayoutVisibility(GONE);
        }
        //设置返回按钮显隐
        if (mIsShowBackBtn) {
            setBackBtnVisibility(VISIBLE);
        } else {
            setBackBtnVisibility(GONE);
        }
        //设置前进按钮显隐
        if (mIsshowGoforawrdBtn) {
            setGoforwardBtnVisibility(VISIBLE);
        } else {
            setGoforwardBtnVisibility(GONE);
        }
        //多窗口文本赋值,默认设置"1"
        setmBottomMultiText("1");

        //设置浏览器默认首页布局
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layotu_default_content, null);
        mLimeBrowserHomeContentLayout.addView(view, -1, params);


    }

    /**
     * findview 绑定控件
     */
    private void findView() {
        mTitleIcon = findViewById(R.id.limeBrowser_title_icon);
        mTitleTv = findViewById(R.id.limeBrowser_title_tv);
        mTitleLayout = findViewById(R.id.root_title_layout);
        mPagersManagelayout = findViewById(R.id.flPagersManager);
        mLoadingLayout = findViewById(R.id.loading_layout);
        mBottomLayout = findViewById(R.id.home_bottom_layout);
        mBottomBackImg = findViewById(R.id.bottom_back_img);
        mBottomGoForwardImg = findViewById(R.id.bottom_forward_img);
        mBottomHomeImg = findViewById(R.id.bottom_home_img);
        mBottomExitImg = findViewById(R.id.bottom_exit_img);
        mBottomMultily = findViewById(R.id.bottom_multi_layout);
        mBottomMultiNumTv = findViewById(R.id.bottom_multi_tv);
        mLimeBrowserHomeContentLayout = findViewById(R.id.limeBrowser_content_layout);
        mStackView = findViewById(R.id.stack_view);
        mRootView = findViewById(R.id.root_view);
        mContentWrapper = findViewById(R.id.home_content_layout);
    }

    /**
     * 设置标题文字
     *
     * @param titleText 标题文本内容
     */
    public void setTitleText(String titleText) {
        mTitleTv.setText(titleText);
    }

    /**
     * 设置标题图标
     *
     * @param titleIconRes 图标资源
     */
    public void setTitleIcon(int titleIconRes) {
        mTitleIcon.setImageResource(titleIconRes);
    }

    /**
     * 设置底部多窗口按钮显示的数量
     *
     * @param multiText 多窗口数量
     */
    public void setmBottomMultiText(String multiText) {
        mBottomMultiNumTv.setText(multiText);
    }

    /**
     * 设置标题栏布局显/隐
     *
     * @param titleLayoutVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setTitleLayoutVisibility(int titleLayoutVisibility) {
        mTitleLayout.setVisibility(titleLayoutVisibility);
    }

    /**
     * 设置多窗口管理界面显隐
     *
     * @param pagersManagelayoutVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setPagersManagelayoutVisibility(int pagersManagelayoutVisibility) {
        mPagersManagelayout.setVisibility(pagersManagelayoutVisibility);
    }

    /**
     * 设置底部栏显隐
     *
     * @param bottomLayoutVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setBottomLayoutVisibility(int bottomLayoutVisibility) {
        mBottomLayout.setVisibility(bottomLayoutVisibility);
    }

    /**
     * 设置加载界面显隐
     *
     * @param loadingLayoutVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setLoadingLayoutVisibility(int loadingLayoutVisibility) {
        mLoadingLayout.setVisibility(loadingLayoutVisibility);
    }

    /**
     * 设置底部返回按显隐
     *
     * @param backBtnVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setBackBtnVisibility(int backBtnVisibility) {
        mBottomBackImg.setVisibility(backBtnVisibility);
    }

    /**
     * 设置底部前进按钮显隐
     *
     * @param goforwardBtnVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setGoforwardBtnVisibility(int goforwardBtnVisibility) {
        mBottomGoForwardImg.setVisibility(goforwardBtnVisibility);
    }


    /**
     * 设置底部主页按钮监听器
     *
     * @param listener 主页按钮的监听器
     */
    public void setOnHomeBtnClickListener(HomeBtnClickListener listener) {
        this.mHomeBtnClickListener = listener;
    }

    /**
     * 设置底部返回按钮监听器
     *
     * @param listener 返回按钮的监听器
     */
    public void setonGoBackBtnClickListener(GoBackBtnClickListener listener) {
        this.mGoBackBtnClickListener = listener;
    }

    /**
     * 设置底部前进按钮监听器
     *
     * @param listener 前进按钮的监听器
     */
    public void setonGoForwardBtnClickListener(GoForwardBtnClickListener listener) {
        this.mGoForwardBtnClickListener = listener;
    }

    /**
     * 设置底部退出按钮监听器
     *
     * @param listener 离开按钮的监听器
     */
    public void setExitBtnClickListener(ExitBtnClickListener listener) {
        this.mExitBtnClickListener = listener;
    }

    /**
     * 设置底部多窗口按钮监听器
     *
     * @param listener 多窗口按钮的监听器
     */
    public void setMultiWindowsBtnClickListener(MultiWindowsBtnClickListener listener) {
        showTabs();
        this.mMultiWindowsBtnClickListener = listener;
    }

    /**
     * 设置主页内容布局
     *
     * @param contentLayoutRes 内容布局id
     */
    public void setContentLayoutId(int contentLayoutRes) {
        mContentLayoutRes = contentLayoutRes;
        //添加浏览器首页布局
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mContentLayoutRes == 0) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layotu_default_content, null);
            mLimeBrowserHomeContentLayout.addView(view, -1, params);
        } else {
            mLimeBrowserHomeContentLayout.removeAllViews();
            View view = LayoutInflater.from(getContext()).inflate(mContentLayoutRes, null);
            mLimeBrowserHomeContentLayout.addView(view, -1, params);
        }
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
        mPagersManagelayout.bringToFront();
        mPagersManagelayout.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.windowGrayL, null));
        }
        mStackView.animateShow(mTabController.getCurrentPosition(), mContentWrapper, mPagersManagelayout, true, new Runnable() {
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
        animateShowFromAlpha(mPagersManagelayout.findViewById(R.id.bottomBar),
                true, true, 300, 40, null);
    }

    public boolean isAnimating() {
        return mRootView.isAnimating() || mStackView.isAnimating() || mIsAnimating;
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
            lp.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dimen_48dp);
            mContentWrapper.addView(view, lp);
        }
        mIsInMain = false;
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
                mContentWrapper.setVisibility(View.VISIBLE);
                mPagersManagelayout.setVisibility(View.GONE);
                mTabsManagerUIShown = false;
            }
        });
        View selectedChild = mStackView.getSelectedChild();
        if (selectedChild != null) {
            LimeTabCard card = selectedChild.findViewById(R.id.ucTabCard);
            card.active(false, 350, 40, null);
        }
        animateShowFromAlpha(mPagersManagelayout.findViewById(R.id.bottomBar),
                false, true, 300, 40, null);
    }

    @Override
    public void closeTab(Tab tab) {
        mStackView.closeTab(mTabController.getTabPosition(tab));
    }

    @Override
    public void onTabCountChanged() {
        //更新页面数量
        mBottomMultiNumTv.setText("" + mTabController.getTabCount());
    }

    @Override
    public void onTabDataChanged(Tab tab) {
        mTabAdapter.notifyDataSetChanged();
    }

    @Override
    public Activity getActivity() {
        return null;
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
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.bringToFront();
    }

    @Override
    public void onPageFinished(Tab tab) {
        tab.shouldUpdateThumbnail(true);
        mTabAdapter.notifyDataSetChanged();
        mLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onProgressChanged(Tab tab) {

    }

    @Override
    public void onReceivedTitle(Tab tab, String title) {

    }

    @Override
    public void onChildDismissed(int index) {
        onTabClosed(index);
    }
}
