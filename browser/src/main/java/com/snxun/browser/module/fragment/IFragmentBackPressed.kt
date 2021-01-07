package com.snxun.browser.module.fragment

/**
 * 让Fragment实现返回按钮监听接口
 * Created by zhouL on 2018/6/20.
 */
interface IFragmentBackPressed {
    fun onPressBack(): Boolean
}