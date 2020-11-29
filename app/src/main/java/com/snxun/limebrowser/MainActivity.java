package com.snxun.limebrowser;

import android.view.View;

import com.lodz.android.pandora.base.activity.AbsActivity;
import com.snxun.limebrowser.application.WebViewApplication;
import com.snxun.limebrowser.ui.LimeBrowserActivity;

import java.util.ArrayList;

public class MainActivity extends AbsActivity {
    private ArrayList<WebViewApplication> mList;

    @Override
    protected int getAbsLayoutId() {
        return R.layout.activity_main;
    }

    public void startBrowser(View view) {
        LimeBrowserActivity.start(this, mList);
    }

    @Override
    protected void initData() {
        super.initData();
        mList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mList.add(new WebViewApplication("https://bkimg.cdn.bcebos" +
                    ".com/pic/b8014a90f603738da97755563251a751f81986184626?x-bce-process=image" +
                    "/resize,m_lfit,w_268,limit_1/format,f_jpg", "百度", "https://www.baidu.com"));
            mList.add(new WebViewApplication("https://bkimg.cdn.bcebos" +
                    ".com/pic/c2fdfc039245d688d43f28dea2886a1ed21b0ef48e3d?x-bce-process=image" +
                    "/resize,m_lfit,w_268,limit_1/format,f_jpg", "网易", "https://www.163.com"));
        }
    }
}