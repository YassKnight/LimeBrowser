package com.snxun.browser.module.dialog

import android.content.Context
import android.view.Gravity
import androidx.annotation.StyleRes
import com.snxun.browser.R

/**
 *
 * Created by Yangjw on 2020/12/8.
 */
abstract class BaseCenterDialog: BaseDialog {

    constructor(context: Context) : super(context)

    constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId)

    @StyleRes
    override fun configAnimations(): Int {
        if (hasAnimations()) {
            return R.style.animation_center_in_center_out
        }
        return super.configAnimations()
    }

    /** 是否有动画 */
    open fun hasAnimations(): Boolean = true

    override fun show() {
        window?.setGravity(Gravity.CENTER)
        super.show()
    }
}