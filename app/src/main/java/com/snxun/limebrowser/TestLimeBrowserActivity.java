package com.snxun.limebrowser;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.snxun.browser.widget.browser.LimeBrowser;
import com.snxun.browser.widget.browser.listener.AddMultiWindowsBtnClickListener;
import com.snxun.browser.widget.browser.listener.NetworkReconnectBtnClickListener;
import com.snxun.browser.widget.browser.listener.RefreshBtnClickListener;

/**
 * 测试类
 * Created by Yangjw on 2020/12/8.
 */
public class TestLimeBrowserActivity extends AppCompatActivity {
    private LimeBrowser limeBrowser;
    private Button mBtn;
    private Button mtenxunBtn;
    private Button mBiliBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_limebrowser);

        limeBrowser = findViewById(R.id.browser);
        //设置底部显隐，默认显示
//        limeBrowser.setBottomLayoutVisibility(View.GONE);

        limeBrowser.setTitleBackgroud(R.color.themePink);

        //设置标题栏显隐 ,默认显示
        limeBrowser.setTitleLayoutVisibility(View.VISIBLE);

        limeBrowser.setContentLayout(R.layout.layout_custom_content);
        mBtn = (Button) limeBrowser.findContentLayoutChildViewById(R.id.mybtn);
        mtenxunBtn = (Button) limeBrowser.findContentLayoutChildViewById(R.id.mybtn_tenxun);
        mBiliBtn = (Button) limeBrowser.findContentLayoutChildViewById(R.id.mybtn_bili);

        //webview工厂
        limeBrowser.setWebViewFactory(new TestWebViewFactory());

        limeBrowser.setLoadView(getResources().getDrawable(R.drawable.ic_background));

        limeBrowser.setRefreshBtnVisibility(View.VISIBLE);


        setListeners();
    }


    protected void setListeners() {
        limeBrowser.setOnHomeBtnClickListener(() -> ToastUtils.showShort(getContext(), "点击了home按钮"));
        limeBrowser.setonGoBackBtnClickListener(() -> ToastUtils.showShort(getContext(), "点击了后退按钮"));

        limeBrowser.setonGoForwardBtnClickListener(() -> ToastUtils.showShort(getContext(), "点击了前进按钮"));

        limeBrowser.setExitBtnClickListener(() -> limeBrowser.setNetworkErrorLayoutVisibility(View.VISIBLE));

        limeBrowser.setMultiWindowsBtnClickListener(() -> ToastUtils.showShort(getContext(), "点击了多窗口按钮"));
        limeBrowser.setAddMultiWindowsBtnClickListener(new AddMultiWindowsBtnClickListener() {
            @Override
            public void onAddMultiWindowsBtnClick() {
                limeBrowser.addTab(true);
            }
        });

        mBtn.setOnClickListener(v -> limeBrowser.load("https://www.baidu.com"));
        mtenxunBtn.setOnClickListener(v -> limeBrowser.load("https://www.tencent.com/zh-cn"));
        mBiliBtn.setOnClickListener(v -> limeBrowser.load("https://www.bilibili.com/"));

        limeBrowser.setNetworkReconnectBtnClickListener(new NetworkReconnectBtnClickListener() {
            @Override
            public void onNetworkReconnectBtnClick() {
                limeBrowser.setNetworkErrorLayoutVisibility(View.GONE);
                limeBrowser.getTabController().getCurrentTab().reloadPage();
            }
        });

        limeBrowser.setRefresgBtnClickListener(new RefreshBtnClickListener() {
            @Override
            public void onRefreshBtnClick() {
                ToastUtils.showShort(getContext(), "点击了刷新按钮");
                Log.e("onRefreshBtnClick", "onRefreshBtnClick: 点击了刷新按钮");
            }
        });

    }


    protected Context getContext() {
        return TestLimeBrowserActivity.this;
    }

}
