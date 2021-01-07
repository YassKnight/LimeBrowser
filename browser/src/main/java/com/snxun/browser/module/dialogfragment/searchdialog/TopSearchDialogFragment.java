package com.snxun.browser.module.dialogfragment.searchdialog;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.snxun.browser.R;
import com.snxun.browser.module.dialogfragment.BaseTopDialogFragment;
import com.snxun.browser.module.fragment.bookmark.BookMarkFragment;
import com.snxun.browser.module.fragment.browsinghistory.BrowsingHistroyFragment;
import com.snxun.browser.util.AnkoKeyBoardKt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ProjectName : LimeBrowser
 * @Author : Yangjw
 * @Time : 2021/1/5
 * @Description :
 */
public class TopSearchDialogFragment extends BaseTopDialogFragment {
    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;
    private ImageView mBackImg;
    private EditText mSearchEd;
    private LinearLayout mMainLayout;
    private ImageView mSearchImg;

    /**
     * 搜索回调
     */
    SearchCallBack mSearchCallBack;
    /**
     * 进入的url
     */
    private String mUrl;

    private BrowsingHistroyFragment mHistroyFragment;
    private BookMarkFragment mBookMarkFragment;

    public TopSearchDialogFragment(String mUrl) {
        this.mUrl = mUrl;
    }

    private final List<Integer> mTabs = Arrays.asList(R.string.browsing_history, R.string.bookmark);


    @Override
    protected int getLayoutId() {
        return R.layout.layout_dialogfragment_search;
    }

    @Override
    protected void findViews(View view, Bundle savedInstanceState) {
        super.findViews(view, savedInstanceState);
        bindView(view);
        initSearchEd();
        initViewPager();
    }

    /**
     * 初始化搜索框配置
     */
    private void initSearchEd() {
        //默认搜索框光标在文本末尾
        mSearchEd.setSelection(mSearchEd.getText().length());
        //搜索框设置焦点
        mSearchEd.setFocusable(true);
        mSearchEd.setFocusableInTouchMode(true);
        mSearchEd.requestFocus();

        //默认弹出键盘
        AnkoKeyBoardKt.showInputMethod(mSearchEd);


    }

    /**
     * 绑定view
     *
     * @param view
     */
    private void bindView(View view) {
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.view_pager);
        mBackImg = view.findViewById(R.id.ivSearchBack);
        mSearchEd = view.findViewById(R.id.edSearchUrl);
        mMainLayout = view.findViewById(R.id.search_main_layout);
        mSearchImg = view.findViewById(R.id.ivSearch);
    }

    /**
     * 初始化viewpager配置
     */
    private void initViewPager() {
        ArrayList<Fragment> list = new ArrayList<Fragment>();
        mBookMarkFragment = (BookMarkFragment) BookMarkFragment.newInstance();
        mHistroyFragment = (BrowsingHistroyFragment) BrowsingHistroyFragment.newInstance();
        list.add(mHistroyFragment);
        list.add(mBookMarkFragment);
        mViewPager.setAdapter(new SimpleTabAdapter(this, list));
        mViewPager.setOffscreenPageLimit(mTabs.size());
        mViewPager.setCurrentItem(0, true);
        new TabLayoutMediator(mTabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(getContext().getText(mTabs.get(position)));
            }
        }).attach();
    }


    @Override
    protected void setListeners(View view) {
        super.setListeners(view);
        mMainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AnkoKeyBoardKt.hideInputMethod(v);
                return false;
            }
        });
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnkoKeyBoardKt.hideInputMethod(v);
                getFragment().dismiss();
            }
        });
        mSearchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchCallBack.onSearchCallBack(mSearchEd.getText().toString().trim());
                getFragment().dismiss();
            }
        });
        //点击浏览记录item
        mHistroyFragment.setHistoryItemClickCallBack(new BrowsingHistroyFragment.HistoryItemClickCallBack() {
            @Override
            public void onHistoryItemClickCallBack(String history_url) {
                mSearchCallBack.onSearchCallBack(history_url);
            }
        });

    }

    @Override
    protected void initData(View view) {
        super.initData(view);
        mSearchEd.setText(mUrl);
    }

    /**
     * 获取当前fragment上下文
     *
     * @return
     */
    public TopSearchDialogFragment getFragment() {
        return this;
    }

    public interface SearchCallBack {
        void onSearchCallBack(String searchUrl);
    }

    public void setSearchCallBack(SearchCallBack callBack) {
        this.mSearchCallBack = callBack;
    }

}
