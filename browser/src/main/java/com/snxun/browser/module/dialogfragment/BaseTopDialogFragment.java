package com.snxun.browser.module.dialogfragment;

import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.snxun.browser.R;

/**
 * @ProjectName : LimeBrowser
 * @Author : Yangjw
 * @Time : 2021/1/5
 * @Description :
 */
public abstract class BaseTopDialogFragment extends BaseDialogFragment {
    @Override
    protected int configAnimations() {
        return R.style.animation_top_in_top_out;
    }

    @Override
    protected void configDialogWindow(Window window) {
        super.configDialogWindow(window);
        window.setGravity(Gravity.TOP);
        if (isMatchWidth()) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
    }

    /**
     * 是否需要填满宽度
     *
     * @return
     */
    protected boolean isMatchWidth() {
        return true;
    }
}
