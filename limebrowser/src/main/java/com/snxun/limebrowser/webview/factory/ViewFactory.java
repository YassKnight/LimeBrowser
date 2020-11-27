package com.snxun.limebrowser.webview.factory;

import android.content.Context;
import android.view.View;

/**
 * Created by Yangjw on 2020/11/27.
 */
public interface ViewFactory {
    /**
     * 构建View
     *
     * @param context 上下文环境
     * @param type    类型
     * @return
     */
    public View create(Context context, int type);
}
