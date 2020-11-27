package com.snxun.limebrowser.ui;

import android.content.Context;
import android.content.Intent;

import com.lodz.android.pandora.base.activity.AbsActivity;
import com.snxun.limebrowser.R;


/**
 * 浏览器主页
 * Created by Yangjw on 2020/11/27.
 */
public class LimeBrowserActivity extends AbsActivity {

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, LimeBrowserActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected int getAbsLayoutId() {
        return R.layout.activity_browser_home;
    }
}
