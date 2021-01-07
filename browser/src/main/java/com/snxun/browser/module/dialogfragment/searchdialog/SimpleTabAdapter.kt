package com.snxun.browser.module.dialogfragment.searchdialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @ProjectName : LimeBrowser
 * @Author : Yangjw
 * @Time : 2021/1/5
 * @Description :
 */
class SimpleTabAdapter : FragmentStateAdapter {
    private val mFragments: List<Fragment>

    constructor(fragmentActivity: FragmentActivity, fragments: List<Fragment>) : super(fragmentActivity) {
        mFragments = fragments
    }

    constructor(fragment: Fragment, fragments: List<Fragment>) : super(fragment) {
        mFragments = fragments
    }

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle, fragments: List<Fragment>) : super(fragmentManager, lifecycle) {
        mFragments = fragments
    }


    override fun getItemCount(): Int = mFragments.size

    override fun createFragment(position: Int): Fragment = mFragments[position]
}
