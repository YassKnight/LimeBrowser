package com.snxun.browser.module.dialog;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lodz.android.pandora.widget.dialog.BaseCenterDialog;
import com.snxun.browser.R;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Yangjw on 2020/12/2.
 */
public class ExitDialog extends BaseCenterDialog {
    /**
     * 确定按钮
     */
    private TextView mSureBtn;
    /**
     * 取消按钮
     */
    private TextView mCancelBtn;

    public ExitDialog(@NotNull Context context) {
        super(context);
    }

    public ExitDialog(@NotNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public boolean hasAnimations() {
        return true;
    }

    @Override
    protected int configAnimations() {
        if (hasAnimations()) {
            return R.style.animation_center_in_center_out;
        }
        return super.configAnimations();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_dialog_exit;
    }

    @Override
    protected void findViews() {
        super.findViews();
        mSureBtn = findViewById(R.id.dialog_sure_tv);
        mCancelBtn = findViewById(R.id.dialgo_cancel_tv);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOwnerActivity().finish();
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        Window wd = getWindow();
        if (wd != null) {
            WindowManager.LayoutParams layoutParams = wd.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            wd.setAttributes(layoutParams);
        }
        super.show();
    }
}
