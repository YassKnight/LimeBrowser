package com.snxun.browser.util

import android.os.Looper
import java.util.*

/**
 * app帮助类
 * Created by Yangjw on 2020/12/8.
 */
object AppUtils {

    /** 当前是否在主线程（UI线程） */
    @JvmStatic
    fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

    /** 获取36位随机UUID */
    @JvmStatic
    fun getUUID36(): String = UUID.randomUUID().toString()

    /** 获取32位随机UUID */
    @JvmStatic
    fun getUUID32(): String = UUID.randomUUID().toString().replace("-", "")

}