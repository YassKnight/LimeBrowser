package com.snxun.browser.module.fragment.browsinghistory;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.snxun.browser.R;
import com.snxun.browser.module.fragment.LazyFragment;
import com.snxun.browser.util.AnkoKeyBoardKt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName : LimeBrowser
 * @Author : Yangjw
 * @Time : 2021/1/5
 * @Description :
 */
public class BrowsingHistroyFragment extends LazyFragment {
    private static final String TAG = "BrowsingHistroyFragment";
    private LinearLayout mMainLayout;
    private LinearLayout mClearlayout;
    private RecyclerView mRecyclerView;
    private BrowsingHistoryRecyclerAdapter mAdapter;
    HistoryItemClickCallBack mHistoryItemClickCallBack;

    public static final Fragment newInstance() {
        BrowsingHistroyFragment fragment = new BrowsingHistroyFragment();
        return fragment;
    }

    @Override
    protected int getAbsLayoutId() {
        return R.layout.layout_browsing_histroy_fragment;
    }

    @Override
    protected void findViews(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.findViews(view, savedInstanceState);
        mMainLayout = view.findViewById(R.id.histrory_mainlayout);
        mClearlayout = view.findViewById(R.id.clear_browsing_history_layout);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(lm);
        mAdapter = new BrowsingHistoryRecyclerAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void setListeners(@NotNull View view) {
        super.setListeners(view);
        //隐藏键盘
        mMainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AnkoKeyBoardKt.hideInputMethod(v);
                return false;
            }
        });

        mClearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空历史记录

            }
        });

        mAdapter.setDeleteClickCallBack(new BrowsingHistoryRecyclerAdapter.DeleteClickCallBack() {
            @Override
            public void onDeleteClickCallBack(int position) {
                //删除一条浏览的历史记录

            }
        });

        mAdapter.setItemClickCallBack(new BrowsingHistoryRecyclerAdapter.ItemClickCallBack() {
            @Override
            public void onItemClickCallBack(String history_url) {
                //点击一条浏览的记录
                mHistoryItemClickCallBack.onHistoryItemClickCallBack(history_url);
            }
        });

    }

    @Override
    protected void initData(@NotNull View view) {
        super.initData(view);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("https://www.baidu.com");
            list.add("https://www.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.com");
            list.add("https://www.bilibili.com");
        }

        mAdapter.setData(list);
    }


    public interface HistoryItemClickCallBack {
        void onHistoryItemClickCallBack(String history_url);
    }

    public void setHistoryItemClickCallBack(HistoryItemClickCallBack callBack) {
        this.mHistoryItemClickCallBack = callBack;
    }
}
