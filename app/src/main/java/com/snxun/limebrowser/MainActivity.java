package com.snxun.limebrowser;

import android.view.View;

import com.lodz.android.pandora.base.activity.AbsActivity;
import com.snxun.limebrowser.ui.LimeBrowserActivity;

public class MainActivity extends AbsActivity {
    @Override
    protected int getAbsLayoutId() {
        return R.layout.activity_main;
    }

    public void startBrowser(View view) {
        LimeBrowserActivity.start(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }
}