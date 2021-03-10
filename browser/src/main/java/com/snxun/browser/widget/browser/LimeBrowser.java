package com.snxun.browser.widget.browser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.FragmentActivity;

import com.lwj.widget.loadingdialog.SimpleLoadingDialog;
import com.snxun.browser.R;
import com.snxun.browser.controller.TabController;
import com.snxun.browser.controller.UiController;
import com.snxun.browser.module.bottomview.LimeBottomView;
import com.snxun.browser.module.dialog.ExitDialog;
import com.snxun.browser.module.rootview.RootView;
import com.snxun.browser.module.stackview.widget.LimeStackView;
import com.snxun.browser.module.stackview.widget.LimeTabCard;
import com.snxun.browser.module.webview.factory.LimeWebWebViewFactory;
import com.snxun.browser.module.webview.factory.WebViewFactory;
import com.snxun.browser.module.webview.tab.Tab;
import com.snxun.browser.module.webview.tab.TabAdapter;
import com.snxun.browser.util.RxUtils;
import com.snxun.browser.util.UiHandler;
import com.snxun.browser.util.ViewUtils;
import com.snxun.browser.widget.browser.listener.AddMultiWindowsBtnClickListener;
import com.snxun.browser.widget.browser.listener.ClearMultiWindoswListener;
import com.snxun.browser.widget.browser.listener.CloseMultiPageListener;
import com.snxun.browser.widget.browser.listener.ExitBtnClickListener;
import com.snxun.browser.widget.browser.listener.GoBackBtnClickListener;
import com.snxun.browser.widget.browser.listener.GoForwardBtnClickListener;
import com.snxun.browser.widget.browser.listener.HomeBtnClickListener;
import com.snxun.browser.widget.browser.listener.MultiWindowsBtnClickListener;
import com.snxun.browser.widget.browser.listener.NetworkReconnectBtnClickListener;
import com.snxun.browser.widget.browser.listener.RefreshBtnClickListener;
import com.snxun.browser.widget.browser.listener.TabSelectListener;
import com.snxun.browser.widget.browser.listener.VpnReconnectBtnClickListener;

import java.io.Serializable;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 对外提供的浏览器控件
 * Created by Yangjw on 2020/12/7.
 */
public class LimeBrowser extends FrameLayout implements UiController, LimeStackView.OnChildDismissedListener, Serializable {

    private TypedArray typedArray;
    private Context mContext;
    /**
     * tag
     */
    private static final String TAG = "LimeBrowser";
    /**
     * 是否显示头部布局，默认为true（显示）
     */
    private boolean mIsShowTopBar;
    /**
     * 是否显示底部按钮 全部默认显示
     */
    private boolean mIsShowBackBtn;
    private boolean mIsShowForwardBtn;
    private boolean mIsShowHomeBtn;
    private boolean mIsShowMultiWindowBtn;
    private boolean mIsShowExitBtn;
    private boolean mIsShowRefreshBtn;
    /**
     * 顶部标题
     */
    private TextView mTitleTv;
    /**
     * 顶部标题栏图标
     */
    private ImageView mTitleIcon;
    /**
     * 底部标题栏布局（不包含分隔线）
     */
    private LinearLayout mTitleContentLayout;
    /**
     * 顶部标题栏布局（包含分隔线）
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
     * xml中设置标题栏背景资源
     */
    private int mTitleContentLayoutBgRes;
    /**
     * 主页内容布局资源
     */
    private int mContentLayoutRes = 0;
    /**
     * 多窗口管理界面布局
     */
    private FrameLayout mPagersManagelayout;
    /**
     * 底部功能栏布局
     */
    private LimeBottomView mBottomLayout;
    /**
     * 底部返回按钮
     */
    private ImageView mBottomBackImg;
    private LinearLayout mBottomBackLy;
    /**
     * 底部前进按钮
     */
    private ImageView mBottomGoForwardImg;
    private LinearLayout mBottomGoForwardLy;
    /**
     * 底部主页按钮
     */
    private LinearLayout mBottomHomeImg;
    /**
     * 底部离开按钮
     */
    private LinearLayout mBottomExitImg;
    /**
     * 底部刷新按钮
     */
    private LinearLayout mBottomRefreshImg;
    /**
     * 底部多窗口按钮
     */
    private LinearLayout mBottomMultily;
    /**
     * 底部多窗口按钮显示数量的tv
     */
    private TextView mBottomMultiNumTv;
    /**
     * 多窗口管理界面添加窗口按钮
     */
    private ImageView mAddMultiWindows;
    /**
     * 清空所有多窗口按钮
     */
    private TextView mClearMultiWindosw;
    /**
     * 关闭多窗口管理界面按钮
     */
    private TextView mCloseMultiPage;
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
     * 添加窗口监听器
     */
    private AddMultiWindowsBtnClickListener mAddMultiWindowsBtnClickListener;
    /**
     * 清除多窗口监听
     */
    private ClearMultiWindoswListener mClearMultiWindoswListener;
    /**
     * 关闭多窗口监听
     */
    private CloseMultiPageListener mCloseMultiPageListener;
    /**
     * 底部退出按钮监听器
     */
    private ExitBtnClickListener mExitBtnClickListener;
    /**
     * 底部刷新按钮监听器
     */
    private RefreshBtnClickListener mRefreshBtnClickListener;
    /**
     * 窗口选择监听器
     */
    private TabSelectListener mTabSelectListener;
    /**
     * 网络重连按钮监听器
     */
    private NetworkReconnectBtnClickListener mNetworkReconnectBtnClickListener;
    /**
     * vpn重连按钮监听器
     */
    private VpnReconnectBtnClickListener mVpnReconnectBtnClickListener;
    /**
     * 浏览器主页的内容布局
     */
    private LinearLayout mLimeBrowserHomeContentLayout;
    /**
     * 默认主页布局按钮
     */
    private Button mDefaultBtn;
    /**
     * 自定义内容布局
     */
    private ViewGroup mCustomContentLayout = null;
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
     * 加载进度条
     */
    private ProgressBar mProgressBar;
    /**
     * 当前显示的是否是主界面
     */
    private boolean mIsInMain = true;

    private SimpleLoadingDialog mSimpleLoadingDialog;
    private LinearLayout mLoadingLayout;
    private LinearLayout mNetworkErrorLayout;
    private LinearLayout mVpnErrorLayout;
    private Button mNetworkReconnectbtn;
    private Button mVPNReconnectbtn;

    private FrameLayout mMainLayout;
    private Drawable mLoadViewRes;

    public LimeBrowser(Context context) {
        super(context);
    }

    public LimeBrowser(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_limebrowser, this, true);
        initAttrs(context, attrs);
    }

    public LimeBrowser(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化Attr资源
     *
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.LimeBrowser);
        mTitleIconRes = typedArray.getResourceId(R.styleable.LimeBrowser_titleIcon, R.drawable.browser_icon);
        mTitleContentLayoutBgRes = typedArray.getResourceId(R.styleable.LimeBrowser_titleBackgroud, R.color.white);
        mTitleText = typedArray.getString(R.styleable.LimeBrowser_titleText);
        mIsShowTopBar = typedArray.getBoolean(R.styleable.LimeBrowser_showTopbar, true);
        mIsShowBackBtn = typedArray.getBoolean(R.styleable.LimeBrowser_showGobackBtn, true);
        mIsShowForwardBtn = typedArray.getBoolean(R.styleable.LimeBrowser_showGoForwardBtn, true);
        mIsShowHomeBtn = typedArray.getBoolean(R.styleable.LimeBrowser_showHomeBtn, true);
        mIsShowMultiWindowBtn = typedArray.getBoolean(R.styleable.LimeBrowser_showMultiWindowBtn, true);
        mIsShowExitBtn = typedArray.getBoolean(R.styleable.LimeBrowser_showExitBtn, true);
        mIsShowExitBtn = typedArray.getBoolean(R.styleable.LimeBrowser_showRefreshBtn, true);
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
     * 初始化tab相关配置
     */
    private void initTabConfig() {
        mTabController = new TabController(getContext(), this);
        mTabAdapter = new TabAdapter(getContext(), this);
        mStackView.setAdapter(mTabAdapter);
//        mFactory = new WebViewFactory(getContext());
        // 建立一个tab主页
        if (mTabController.getTabCount() <= 0) {
            addTab(false);
        }
    }


    /**
     * 设置WebViewFactory，由用户提供
     *
     * @param factory WebViewFactory
     */
    public void setWebViewFactory(WebViewFactory factory) {
        mFactory = factory;
        mTabController.destroy();
        addTab(true);

    }


    /**
     * @param animate 是否有动画，有动画时即UCRootView从下往上移
     */
    public void addTab(boolean animate) {
        if (animate) {
            switchToMain();
            mContentWrapper.bringToFront();
            animateShowFromBottomToTop(mContentWrapper, new Runnable() {
                @Override
                public void run() {
                    hideTabs(false); // 把页面管理页隐藏
                    mBottomLayout.bringToFront();
                    if (isTitleLayoutShow())
                        mTitleContentLayout.bringToFront();

                }
            });
        }
        Tab tab = mTabController.createNewTab();
        mActiveTab = tab;
        mTabController.setActiveTab(mActiveTab);
    }

    /**
     * 隐藏tab(关闭多窗口管理界面)
     *
     * @param animated
     */
    public void hideTabs(boolean animated) {
        if (isAnimating()) {
            return;
        }
        if (animated) {
            if (mTabController.getTabCount() > 0 && mTabController.getCurrentPosition() == -1) {
                selectTab(mTabController.getTab(mTabController.getTabCount() - 1), animated);
            } else {
                mStackView.animateShow(mTabController.getCurrentPosition(), mContentWrapper, mPagersManagelayout, false, new Runnable() {
                    @Override
                    public void run() {
                        mPagersManagelayout.setVisibility(View.GONE);
                    }
                });
            }
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
    public void switchToMain() {
        if (mRootView.getParent() == null) {
            mContentWrapper.addView(mRootView);
        }

        if (!isTitleLayoutShow()) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mRootView.getLayoutParams();
            lp.topMargin = getResources().getDimensionPixelSize(R.dimen.dimen_48dp);
            mRootView.setLayoutParams(lp);
        }


        mRootView.bringToFront();
        WebView view = mActiveTab.getWebView();
        if (view != null) {
            mContentWrapper.removeView(view);
        }
        mActiveTab.stopLoading();
        mIsInMain = true;
        mBottomHomeImg.setClickable(false);
        setBackBtnClickable(false);
        setForwardBtnClickable(false);
    }

    /**
     * 设置后退按钮是否可以点击
     *
     * @param clickable true：可以点击，false：不可点击
     */
    private void setBackBtnClickable(boolean clickable) {
        mBottomBackLy.setClickable(clickable);
        if (clickable)
            mBottomBackImg.setImageResource(R.drawable.ic_back);
        else
            mBottomBackImg.setImageResource(R.drawable.ic_back_dis);
    }

    /**
     * 设置前进按钮是否可以点击
     *
     * @param clickable true：可以点击，false：不可点击
     */
    private void setForwardBtnClickable(boolean clickable) {
        mBottomGoForwardLy.setClickable(clickable);
        if (clickable)
            mBottomGoForwardImg.setImageResource(R.drawable.ic_forward);
        else
            mBottomGoForwardImg.setImageResource(R.drawable.ic_forward_dis);
    }

    /**
     * 从底部弹出动画
     *
     * @param view               应用动画的view
     * @param onCompleteRunnable 运行的子线程
     */
    private void animateShowFromBottomToTop(View view, final Runnable onCompleteRunnable) {
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
        //移除窗口监听
        mStackView.setOnChildDismissedListener(this);
        //主页按钮设置监听
        RxUtils.viewClick(mBottomHomeImg).subscribe(new Observer<View>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull View view) {
//                switchToMain();
//                mActiveTab.clearTabData();
//                mActiveTab.recreateWebView();
                mProgressBar.setVisibility(GONE);
                if (mHomeBtnClickListener != null)
                    mHomeBtnClickListener.onHomeBtnClick();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        //多窗口按钮设置监听
        RxUtils.viewClick(mBottomMultily).subscribe(new Observer<View>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull View view) {
                showTabs(false, false, 0);
                if (mMultiWindowsBtnClickListener != null)
                    mMultiWindowsBtnClickListener.onMultiWindowsBtnClick();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        //返回按钮设置监听
        mBottomBackLy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActiveTab != null) {
                    if (mActiveTab.webCanGoBack()) {
                        mActiveTab.webGoBack();
                    } else {
                        switchToMain();
                        mActiveTab.clearTabData();
                        mActiveTab.recreateWebView();
                    }
                }
                if (mGoBackBtnClickListener != null)
                    mGoBackBtnClickListener.onGoBackBtnClick();
            }
        });
        //前进按钮设置监听
        mBottomGoForwardLy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActiveTab != null) {
                    if (mActiveTab.webCanGoForward()) {
                        mActiveTab.webGoForward();
                    }
                }
                if (mGoForwardBtnClickListener != null) {
                    mGoForwardBtnClickListener.onGoForwardBtnClickListener();
                }
            }
        });
        //退出按钮设置监听
        RxUtils.viewClick(mBottomExitImg).subscribe(new Observer<View>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull View view) {
                new ExitDialog(getContext()).show();

                if (mExitBtnClickListener != null)
                    mExitBtnClickListener.onExitBtnClickListener();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);
        anim.setFillAfter(true);//设置旋转后停止
        //刷新按钮设置监听
        RxUtils.viewClick(mBottomRefreshImg).subscribe(new Observer<View>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull View view) {
                mBottomRefreshImg.startAnimation(anim);
                if (mRefreshBtnClickListener != null)
                    mRefreshBtnClickListener.onRefreshBtnClick();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        //添加窗口按钮设置监听
        RxUtils.viewClick(mAddMultiWindows).subscribe(new Observer<View>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull View view) {
//                addTab(true);
                if (mAddMultiWindowsBtnClickListener != null)
                    mAddMultiWindowsBtnClickListener.onAddMultiWindowsBtnClick();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        //关闭多窗口管理界面按钮设置监听
        mCloseMultiPage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTabs(true);
                if (mCloseMultiPageListener != null)
                    mCloseMultiPageListener.onCloseMultiPageClick();
            }
        });
        //清空所有多窗口按钮设置监听
        RxUtils.viewClick(mClearMultiWindosw).subscribe(new Observer<View>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull View view) {
                mTabController.destroy();
                if (mClearMultiWindoswListener != null)
                    mClearMultiWindoswListener.onClearMultiWindoswBtnClick();
//                addTab(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        //默认内容布局的按钮设置监听
        mDefaultBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                load("https://github.com/YassKnight/LimeBrowser");
            }
        });
        RxUtils.viewClick(mNetworkReconnectbtn).subscribe(new Observer<View>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull View view) {
                if (mNetworkReconnectBtnClickListener != null)
                    mNetworkReconnectBtnClickListener.onNetworkReconnectBtnClick();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        RxUtils.viewClick(mVPNReconnectbtn).subscribe(new Observer<View>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull View view) {
                if (mVpnReconnectBtnClickListener != null)
                    mVpnReconnectBtnClickListener.onVpnReconnectBtnClick();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

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
        //设置标题背景
        setTitleBackgroud(mTitleContentLayoutBgRes);

        //设置标题栏显隐
        if (mIsShowTopBar) setTitleLayoutVisibility(VISIBLE);
        else setTitleLayoutVisibility(GONE);

        //设置返回按钮显隐
        if (mIsShowBackBtn) setBackBtnVisibility(VISIBLE);
        else setBackBtnVisibility(GONE);

        //设置前进按钮显隐
        if (mIsShowForwardBtn) setGoforwardBtnVisibility(VISIBLE);
        else setGoforwardBtnVisibility(GONE);

        //设置主页按钮显隐
        if (mIsShowHomeBtn) setHomeBtnVisibility(VISIBLE);
        else setHomeBtnVisibility(GONE);
        //设置多窗口按钮显隐
        if (mIsShowMultiWindowBtn) setMultiWindowBtnVisibility(VISIBLE);
        else setMultiWindowBtnVisibility(GONE);

        //设置退出按钮显隐
        if (mIsShowExitBtn) setExitBtnVisibility(VISIBLE);
        else setExitBtnVisibility(GONE);

        //设置刷新按钮显隐
        if (mIsShowRefreshBtn) setRefreshBtnVisibility(VISIBLE);
        else setRefreshBtnVisibility(GONE);

        //多窗口文本赋值,默认设置"1"
        setmBottomMultiText("1");

        //设置浏览器默认首页布局
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_default_content, null);
        mLimeBrowserHomeContentLayout.addView(view, params);

        mDefaultBtn = mLimeBrowserHomeContentLayout.findViewById(R.id.default_btn);

    }

    /**
     * findview 绑定控件
     */
    private void findView() {
        mTitleIcon = findViewById(R.id.limeBrowser_title_icon);
        mTitleTv = findViewById(R.id.limeBrowser_title_tv);
        mTitleLayout = findViewById(R.id.root_title_layout);
        mTitleContentLayout = findViewById(R.id.title_content_layout);
        mPagersManagelayout = findViewById(R.id.flPagersManager);
        mProgressBar = findViewById(R.id.pbFloatSearchProgress);
        mBottomLayout = findViewById(R.id.home_bottom_layout);
        mBottomBackImg = findViewById(R.id.bottom_back_img);
        mBottomBackLy = findViewById(R.id.bottom_back_layout);
        mBottomGoForwardImg = findViewById(R.id.bottom_forward_img);
        mBottomGoForwardLy = findViewById(R.id.bottom_forward_layout);
        mBottomHomeImg = findViewById(R.id.bottom_home_img);
        mBottomExitImg = findViewById(R.id.bottom_exit_img);
        mBottomRefreshImg = findViewById(R.id.bottom_refresh_img);
        mBottomMultily = findViewById(R.id.bottom_multi_layout);
        mBottomMultiNumTv = findViewById(R.id.bottom_multi_tv);
        mLimeBrowserHomeContentLayout = findViewById(R.id.limeBrowser_content_layout);
        mLoadingLayout = findViewById(R.id.loading_layout);
        mNetworkErrorLayout = findViewById(R.id.network_error_layout);
        mVpnErrorLayout = findViewById(R.id.vpn_error_layout);
        mNetworkReconnectbtn = findViewById(R.id.network_reconnect_btn);
        mVPNReconnectbtn = findViewById(R.id.vpn_reconnect_btn);
        mStackView = findViewById(R.id.stack_view);
        mRootView = findViewById(R.id.root_view);
        mContentWrapper = findViewById(R.id.home_content_layout);
        mAddMultiWindows = findViewById(R.id.ivAddPager);
        mClearMultiWindosw = findViewById(R.id.tvClear);
        mCloseMultiPage = findViewById(R.id.tvBack);
        mMainLayout = findViewById(R.id.lime_main_layout);
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
     * 设置标题背景
     *
     * @param titleContentLayoutBgRes 背景资源
     */
    public void setTitleBackgroud(int titleContentLayoutBgRes) {
        mTitleContentLayout.setBackgroundResource(titleContentLayoutBgRes);
    }

    /**
     * 设置底部多窗口按钮显示的数量
     *
     * @param multiText 多窗口数量
     */
    private void setmBottomMultiText(String multiText) {
        mBottomMultiNumTv.setText(multiText);
    }

    /**
     * 设置标题栏布局显/隐
     *
     * @param titleLayoutVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setTitleLayoutVisibility(int titleLayoutVisibility) {
        mTitleContentLayout.setVisibility(titleLayoutVisibility);
    }

    /**
     * 判断标题栏是否显示
     *
     * @return true:显示 / false：隐藏
     */
    public boolean isTitleLayoutShow() {
        return mTitleContentLayout.getVisibility() == VISIBLE;
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
     * 获取窗口管理界面显隐
     *
     * @return
     */
    public int getPagersManagelayoutVisibility() {
        return mPagersManagelayout.getVisibility();
    }

    /**
     * 获取内容页面显隐
     *
     * @return
     */
    public int getContentWrapperLayoutVisibility() {
        return mContentWrapper.getVisibility();
    }

    /**
     * 获取是否在主页
     *
     * @return
     */
    public boolean isMain() {
        return mIsInMain;
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
     * 设置底部返回按显隐
     *
     * @param backBtnVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setBackBtnVisibility(int backBtnVisibility) {
        mBottomBackLy.setVisibility(backBtnVisibility);
    }

    /**
     * 设置底部前进按钮显隐
     *
     * @param goforwardBtnVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setGoforwardBtnVisibility(int goforwardBtnVisibility) {
        mBottomGoForwardLy.setVisibility(goforwardBtnVisibility);
    }

    /**
     * 设置底部主页按钮显隐
     *
     * @param homeBtnVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setHomeBtnVisibility(int homeBtnVisibility) {
        mBottomHomeImg.setVisibility(homeBtnVisibility);
    }

    /**
     * 设置底部多窗口按钮显隐
     *
     * @param multiWindowBtnVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setMultiWindowBtnVisibility(int multiWindowBtnVisibility) {
        mBottomMultily.setVisibility(multiWindowBtnVisibility);
    }


    /**
     * 设置底部退出按钮显隐
     *
     * @param exitBtnVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setExitBtnVisibility(int exitBtnVisibility) {
        mBottomExitImg.setVisibility(exitBtnVisibility);
    }

    /**
     * 设置底部刷新按钮显隐
     *
     * @param refreshBtnVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setRefreshBtnVisibility(int refreshBtnVisibility) {
        mBottomRefreshImg.setVisibility(refreshBtnVisibility);
    }

    /**
     * 设置网络连接错误页面显隐
     *
     * @param networkErrorLayoutVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setNetworkErrorLayoutVisibility(int networkErrorLayoutVisibility) {
        //如果页面管理界面显示，就不在显示错误界面
//        if (mPagersManagelayout.getVisibility() == VISIBLE) {
//            mNetworkErrorLayout.setVisibility(GONE);
//            return;
//        }
        if (networkErrorLayoutVisibility == View.VISIBLE) {
            mNetworkErrorLayout.bringToFront();
            if (mNetworkErrorLayout.getVisibility() == GONE)
                setShowAnimation(mNetworkErrorLayout);
        } else {
            if (mNetworkErrorLayout.getVisibility() == VISIBLE)
                setHideAnimation(mNetworkErrorLayout);
        }
        mNetworkErrorLayout.setVisibility(networkErrorLayoutVisibility);
        for (Tab tab : mTabController.getTabs()) {
            tab.setNetworkConnectErrorVisibility(networkErrorLayoutVisibility);
        }
    }

    /**
     * 设置vpn连接错误页面显隐
     *
     * @param vpnErrorLayoutVisibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setVpnErrorLayoutVisibility(int vpnErrorLayoutVisibility) {

        //如果页面管理界面显示，就不在显示错误界面
//        if (mPagersManagelayout.getVisibility() == VISIBLE) {
//            mVpnErrorLayout.setVisibility(GONE);
//            return;
//        }
        if (vpnErrorLayoutVisibility == VISIBLE) {
            mVpnErrorLayout.bringToFront();
            if (mVpnErrorLayout.getVisibility() == GONE)
                setShowAnimation(mVpnErrorLayout);
        } else {
            //如果当前vpn连接错误界面显示，再添加动画，避免无效动画
            if (mVpnErrorLayout.getVisibility() == VISIBLE)
                setHideAnimation(mVpnErrorLayout);
        }
        mVpnErrorLayout.setVisibility(vpnErrorLayoutVisibility);

        for (Tab tab : mTabController.getTabs()) {
            //三类区应用
            if (tab.getNetType() == 3) {
                tab.setVpnConnectErrorVisibility(vpnErrorLayoutVisibility);
            }
        }
    }


    /**
     * 设置显示动画
     *
     * @param view
     */
    public void setShowAnimation(View view) {
        TranslateAnimation showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        showAnim.setDuration(500);
        view.startAnimation(showAnim);
    }

    /**
     * 设置隐藏动画
     *
     * @param view
     */
    public void setHideAnimation(View view) {
        TranslateAnimation hideAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        hideAnim.setDuration(500);
        view.startAnimation(hideAnim);
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
     * 设置底部刷新按钮监听器
     *
     * @param listener 离开按钮的监听器
     */
    public void setRefresgBtnClickListener(RefreshBtnClickListener listener) {
        this.mRefreshBtnClickListener = listener;
    }

    /**
     * 设置网络重连按钮监听器
     *
     * @param listener
     */
    public void setNetworkReconnectBtnClickListener(NetworkReconnectBtnClickListener listener) {
        this.mNetworkReconnectBtnClickListener = listener;
    }

    /**
     * 设置vpn重连按钮监听器
     *
     * @param listener
     */
    public void setVpnReconnectBtnClickListener(VpnReconnectBtnClickListener listener) {
        this.mVpnReconnectBtnClickListener = listener;
    }

    /**
     * 设置底部多窗口按钮监听器
     *
     * @param listener 多窗口按钮的监听器
     */
    public void setMultiWindowsBtnClickListener(MultiWindowsBtnClickListener listener) {
        this.mMultiWindowsBtnClickListener = listener;
    }

    public void setAddMultiWindowsBtnClickListener(AddMultiWindowsBtnClickListener listener) {
        this.mAddMultiWindowsBtnClickListener = listener;
    }

    public void setClearMultiWindoswListener(ClearMultiWindoswListener listener) {
        this.mClearMultiWindoswListener = listener;
    }

    public void setCloseMultiPageListener(CloseMultiPageListener listener) {
        this.mCloseMultiPageListener = listener;
    }

    /**
     * 设置主页内容布局
     *
     * @param contentLayoutRes 内容布局id
     */
    public void setContentLayout(@LayoutRes int contentLayoutRes) {
        mContentLayoutRes = contentLayoutRes;
        try {
            //添加浏览器首页布局
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mLimeBrowserHomeContentLayout.removeAllViews();
            View view = LayoutInflater.from(getContext()).inflate(mContentLayoutRes, null);
            mLimeBrowserHomeContentLayout.addView(view, params);
            mCustomContentLayout = (ViewGroup) view;
        } catch (Exception e) {
            Log.e(TAG, "setContentLayoutId: " + e);
        }
    }


    /**
     * 通过id获取主页内容布局view
     *
     * @return 自定义内容布局
     */
    public ViewGroup getContentLayoutById() {
        return mCustomContentLayout;
    }

    /**
     * 通过id获取内容主页布局view的子控件
     *
     * @param contentLayoutChildViewId 自定义内容布局的子控件ID
     * @return
     */
    public View findContentLayoutChildViewById(@IdRes int contentLayoutChildViewId) {
        if (mCustomContentLayout != null)
            return mCustomContentLayout.findViewById(contentLayoutChildViewId);
        else {
            Log.e(TAG, "can't find contentlayout!");
            return null;
        }
    }

    /**
     * 如果要加载H5网页，请调用这个方法开启webview，加载网址
     *
     * @param url 需要加载的网页url
     */
    public void load(String url) {
        if (mActiveTab != null) {
            mActiveTab.clearWebHistory();
            mActiveTab.loadUrl(url, null, true);
            switchToTab();
        }
    }

    /**
     * 如果要加载H5网页，请调用这个方法开启webview，加载网址
     *
     * @param url     需要加载的网页url
     * @param netType 加载的网页的区类
     */
    public void load(String url, int netType) {
        if (mActiveTab != null) {
            mActiveTab.clearWebHistory();
            mActiveTab.loadUrl(url, null, true);
            mActiveTab.setNetType(netType);
            switchToTab();
        }
    }

    /**
     * 进入多窗口管理界面，用动画改变选择页（可以理解为一张截图）的Y和scale
     */
    public void showTabs(boolean isShowLoading, boolean isNeedDelay, long delay) {
        if (isAnimating()) {
            return;
        }
        for (int i = 0; i < mTabController.getTabCount(); i++) {
            mTabController.getTab(i).getWebView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mActiveTab.capture();
        mTabAdapter.updateData(mTabController.getTabs());
        UiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isShowLoading) {
                    //SimpleLoadingDialog
                    mSimpleLoadingDialog = new SimpleLoadingDialog(getContext());
                    //显示加载框
                    mSimpleLoadingDialog.showFirst("加载中.....");
                    UiHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSimpleLoadingDialog.dismiss();
                        }
                    }, 1000);
                }
                mPagersManagelayout.bringToFront();
                mPagersManagelayout.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((Activity) mContext).getWindow().setStatusBarColor(getResources().getColor(R.color.windowGrayL, null));
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
        }, isNeedDelay ? delay : 0);


    }

    private boolean isAnimating() {
        return mRootView.isAnimating() || mStackView.isAnimating() || mIsAnimating;
    }


    private void animateShowFromAlpha(final View view, final boolean show,
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
//            addTab(true);
            getActivity().finish();
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
    public void switchToTab() {
        if (mRootView.getParent() != null) {
            mContentWrapper.removeView(mRootView);
        }
        WebView view = mActiveTab.getWebView();

        FrameLayout.LayoutParams lp;
        if (view != null && view.getParent() == null) {
            lp = (FrameLayout.LayoutParams) view.getLayoutParams();
            if (lp == null) {
                lp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            }
            lp.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dimen_48dp);

            mContentWrapper.addView(view, lp);
            mContentWrapper.bringToFront();
            mBottomLayout.bringToFront();

            if (mActiveTab.getVpnConnectErrorVisibility() == VISIBLE)
                mVpnErrorLayout.bringToFront();
            if (mActiveTab.getNetworkConnectErrorVisibility() == VISIBLE)
                mNetworkErrorLayout.bringToFront();

        }

        mIsInMain = false;
        mBottomHomeImg.setClickable(true);
        setBackBtnClickable(mActiveTab.getWebView().canGoBack());
        setForwardBtnClickable(mActiveTab.getWebView().canGoForward());

    }


    @Override
    public void selectTab(Tab tab, boolean isShowAnimating) {
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
        }, isShowAnimating);
        View selectedChild = mStackView.getSelectedChild();
        if (selectedChild != null) {
            LimeTabCard card = selectedChild.findViewById(R.id.ucTabCard);
            card.active(false, 350, 30, null);
        }
        animateShowFromAlpha(mPagersManagelayout.findViewById(R.id.bottomBar),
                false, true, 300, 30, null);
        if (mTabSelectListener != null)
            mTabSelectListener.onTabSelect(tab.getNetType());

//        UiHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < mTabController.getTabCount(); i++) {
//                    mTabController.getTab(i).getWebView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
//                }
//            }
//        }, 2000);

//        UiHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mLoadingLayout.setBackground(mLoadViewRes != null ? mLoadViewRes : new BitmapDrawable(getScreenBitmapFromSystem()));
//                //SimpleLoadingDialog
//                mSimpleLoadingDialog = new SimpleLoadingDialog(getContext());
//                //显示加载框
//                mSimpleLoadingDialog.showFirst("加载中.....");
//                mLoadingLayout.setVisibility(VISIBLE);
//                mLoadingLayout.bringToFront();
//            }
//        }, mLoadViewRes != null ? 0 : 360);
//        UiHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mLoadingLayout.setVisibility(GONE);
//                mSimpleLoadingDialog.dismiss();
//            }
//        }, 2000);

    }

    public void setTabSelectListener(TabSelectListener listener) {
        this.mTabSelectListener = listener;
    }

    public void setLoadView(Drawable loadView) {
        this.mLoadViewRes = loadView;
    }


    public Bitmap getScreenBitmapFromSystem() {
        View view = getActivity().getWindow().getDecorView();
        view.buildDrawingCache();

        // get status bar
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        // get screen width
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // save cache info
        view.setDrawingCacheEnabled(true);

        // remove status bar
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeight, width, height - statusBarHeight);

        // destory cache
        view.destroyDrawingCache();
        return bitmap;
    }

    @Override
    public void closeTab(Tab tab) {
        if (mTabController.getTabCount() == 1) {
            mStackView.closeTab(mTabController.getTabPosition(tab));
            getActivity().finish();
        }
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
    public FragmentActivity getActivity() {
        return (FragmentActivity) mContext;
    }

    @Override
    public TabController getTabController() {
        return mTabController;
    }

    @Override
    public WebViewFactory getWebViewFactory() {
        if (mFactory != null)
            return mFactory;
        else
            return mFactory = new LimeWebWebViewFactory();
    }

    @Override
    public void onSetWebView(Tab tab, WebView view) {

    }

    @Override
    public void onPageStarted(Tab tab, WebView webView, Bitmap favicon) {
        mProgressBar.bringToFront();
        mProgressBar.setVisibility(VISIBLE);

        //SimpleLoadingDialog
//        if (mSimpleLoadingDialog == null)
//            mSimpleLoadingDialog = new SimpleLoadingDialog(getContext());
//        //显示加载框
//        mSimpleLoadingDialog.showFirst("加载中.....");
    }

    @Override
    public void onPageFinished(Tab tab) {
        mProgressBar.setVisibility(INVISIBLE);
        tab.shouldUpdateThumbnail(true);
        mTabAdapter.notifyDataSetChanged();
        setBackBtnClickable(mActiveTab.getWebView().canGoBack());
        setForwardBtnClickable(mActiveTab.getWebView().canGoForward());
        if (mSimpleLoadingDialog != null)
            mSimpleLoadingDialog.dismiss();
    }

    @Override
    public void onProgressChanged(Tab tab) {
        mProgressBar.setProgress(tab.getPageLoadProgress());
    }

    @Override
    public void onReceivedTitle(Tab tab, String title) {

    }

    @Override
    public void onChildDismissed(int index) {
        onTabClosed(index);
    }

}
