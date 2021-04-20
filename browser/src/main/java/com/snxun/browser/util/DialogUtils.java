package com.snxun.browser.util;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.snxun.browser.R;

public class DialogUtils {

    private static AlertDialog.Builder mAlertDialogbuilder = null;
    private static AlertDialog mLoadingDialog;


    public static void showLoadingDialog(Context context) {
        try {
            if (mAlertDialogbuilder == null) {
                mAlertDialogbuilder = new AlertDialog.Builder(context);
            }
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
                mLoadingDialog = null;
            }
            mLoadingDialog = mAlertDialogbuilder.create();
            View dialogView = View.inflate(context, R.layout.dialog_loading_layout, null);
            mLoadingDialog.setView(dialogView);
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "打开加载弹窗失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void dismissLoadingDialog(Context context) {
        try {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "关闭加载弹窗失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
