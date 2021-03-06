package com.snxun.limebrowser;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lodz.android.corekt.utils.ToastUtils;
import com.lodz.android.pandora.base.activity.AbsActivity;
import com.snxun.browser.widget.browser.LimeBrowser;

import org.jetbrains.annotations.Nullable;

/**
 * 测试类
 * Created by Yangjw on 2020/12/8.
 */
public class TestLimeBrowserActivity extends AbsActivity {
    private LimeBrowser limeBrowser;
    private Button mBtn;
    private Button mtenxunBtn;
    private Button mBiliBtn;

    @Override
    protected int getAbsLayoutId() {
        return R.layout.activity_test_limebrowser;
    }

    @Override
    protected void findViews(@Nullable Bundle savedInstanceState) {
        super.findViews(savedInstanceState);

        limeBrowser = findViewById(R.id.browser);
        //设置底部显隐，默认显示
//        limeBrowser.setBottomLayoutVisibility(View.GONE);

        limeBrowser.setTitleBackgroud(R.color.themePink);

        //设置标题栏显隐 ,默认显示
        limeBrowser.setTitleLayoutVisibility(View.GONE);

        //设置搜索栏显隐(如果搜索栏和标题栏同时设置显示，只显示搜索栏)
        limeBrowser.setSearchBarLayoutVisibility(View.GONE);

        limeBrowser.setContentLayout(R.layout.layout_custom_content);
        mBtn = (Button) limeBrowser.findContentLayoutChildViewById(R.id.mybtn);
        mtenxunBtn = (Button) limeBrowser.findContentLayoutChildViewById(R.id.mybtn_tenxun);
        mBiliBtn = (Button) limeBrowser.findContentLayoutChildViewById(R.id.mybtn_bili);

        //webview工厂建议最后一步设置
        limeBrowser.setWebViewFactory(new TestWebViewFactory());


    }


    @Override
    protected void setListeners() {
        super.setListeners();
        limeBrowser.setOnHomeBtnClickListener(() -> ToastUtils.showShort(getContext(), "点击了home按钮"));
        limeBrowser.setonGoBackBtnClickListener(() -> ToastUtils.showShort(getContext(), "点击了后退按钮"));

        limeBrowser.setonGoForwardBtnClickListener(() -> ToastUtils.showShort(getContext(), "点击了前进按钮"));

        limeBrowser.setExitBtnClickListener(() -> ToastUtils.showShort(getContext(), "点击了离开按钮"));

        limeBrowser.setMultiWindowsBtnClickListener(() -> ToastUtils.showShort(getContext(), "点击了多窗口按钮"));


        mBtn.setOnClickListener(v -> limeBrowser.load("https://www.baidu.com"));
        mtenxunBtn.setOnClickListener(v -> limeBrowser.load("https://www.tencent.com/zh-cn"));
        mBiliBtn.setOnClickListener(v -> limeBrowser.load("https://www.bilibili.com/"));


    }

}
