package com.snxun.limebrowser;

import android.os.Bundle;

import com.lodz.android.corekt.utils.ToastUtils;
import com.lodz.android.pandora.base.activity.AbsActivity;
import com.snxun.browser.widget.browser.LimeBrowser;
import com.snxun.browser.widget.browser.listener.ExitBtnClickListener;
import com.snxun.browser.widget.browser.listener.GoBackBtnClickListener;
import com.snxun.browser.widget.browser.listener.GoForwardBtnClickListener;
import com.snxun.browser.widget.browser.listener.HomeBtnClickListener;
import com.snxun.browser.widget.browser.listener.MultiWindowsBtnClickListener;

import org.jetbrains.annotations.Nullable;

/**
 * 测试类
 * Created by Yangjw on 2020/12/8.
 */
public class TestLimeBrowserActivity extends AbsActivity {
    private LimeBrowser limeBrowser;

    @Override
    protected int getAbsLayoutId() {
        return R.layout.activity_test_limebrowser;
    }

    @Override
    protected void findViews(@Nullable Bundle savedInstanceState) {
        super.findViews(savedInstanceState);

        limeBrowser = findViewById(R.id.browser);
//        limeBrowser.setBottomLayoutVisibility(View.GONE);
        limeBrowser.setContentLayoutId(R.layout.layout_custom_content);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        limeBrowser.setOnHomeBtnClickListener(new HomeBtnClickListener() {
            @Override
            public void onHomeBtnClick() {
                ToastUtils.showShort(getContext(), "点击了home按钮");
            }
        });
        limeBrowser.setonGoBackBtnClickListener(new GoBackBtnClickListener() {
            @Override
            public void onGoBackBtnClick() {
                ToastUtils.showShort(getContext(), "点击了后腿按钮");
            }
        });

        limeBrowser.setonGoForwardBtnClickListener(new GoForwardBtnClickListener() {
            @Override
            public void onGoForwardBtnClickListener() {
                ToastUtils.showShort(getContext(), "点击了前进按钮");
            }
        });

        limeBrowser.setExitBtnClickListener(new ExitBtnClickListener() {
            @Override
            public void onExitBtnClickListener() {
                ToastUtils.showShort(getContext(), "点击了离开按钮");
            }
        });

        limeBrowser.setMultiWindowsBtnClickListener(new MultiWindowsBtnClickListener() {
            @Override
            public void onMultiWindowsBtnClick() {
                ToastUtils.showShort(getContext(), "点击了多窗口按钮");
            }
        });
    }

}
