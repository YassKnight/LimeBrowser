package com.snxun.browser.module.fragment.bookmark;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.snxun.browser.R;
import com.snxun.browser.module.fragment.LazyFragment;
import com.snxun.browser.util.AnkoKeyBoardKt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @ProjectName : LimeBrowser
 * @Author : Yangjw
 * @Time : 2021/1/5
 * @Description :
 */
public class BookMarkFragment extends LazyFragment {
    private LinearLayout mMainLayout;

    public static final Fragment newInstance() {
        BookMarkFragment fragment = new BookMarkFragment();
        return fragment;
    }

    @Override
    protected int getAbsLayoutId() {
        return R.layout.layout_bookmark_fragment;
    }

    @Override
    protected void findViews(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.findViews(view, savedInstanceState);
        mMainLayout = view.findViewById(R.id.bookmark_mainlayout);
    }

    @Override
    protected void setListeners(@NotNull View view) {
        super.setListeners(view);
        mMainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AnkoKeyBoardKt.hideInputMethod(v);
                return false;
            }
        });
    }
}
