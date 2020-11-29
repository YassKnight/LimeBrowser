package com.snxun.limebrowser.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lodz.android.corekt.utils.ToastUtils;
import com.lodz.android.pandora.base.activity.AbsActivity;
import com.lodz.android.pandora.widget.rv.drag.RecyclerViewDragHelper;
import com.snxun.limebrowser.R;
import com.snxun.limebrowser.application.WebViewApplication;
import com.snxun.limebrowser.module.recyclerview.AppRecyclerViewAdapter;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;


/**
 * 浏览器主页
 * Created by Yangjw on 2020/11/27.
 */
public class LimeBrowserActivity extends AbsActivity {

    /**
     * 浏览器首页应用数据的key
     */
    private static final String WEB_APP_DATA = "WEB_APP_DATA";
    /**
     * 浏览器首页应用数据集
     */
    private ArrayList<WebViewApplication> mList;


    public static void start(Context context, ArrayList<WebViewApplication> list) {
        Intent intent = new Intent();
        intent.setClass(context, LimeBrowserActivity.class);
        intent.putParcelableArrayListExtra(WEB_APP_DATA, list);
        context.startActivity(intent);
    }

    /**
     * 展示应用列表的rv相关
     */
    private RecyclerView mRecyclerView;
    private AppRecyclerViewAdapter mRecyclerViewAdapter;
    private RecyclerViewDragHelper<WebViewApplication> mRecyclerViewHelper;

    @Override
    protected int getAbsLayoutId() {
        return R.layout.activity_browser_home;
    }

    @Override
    protected void findViews(@Nullable Bundle savedInstanceState) {
        super.findViews(savedInstanceState);
        initView();
        initRecyclerView();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mRecyclerViewAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.showShort(getContext(), "打开" + position);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mList = getIntent().getParcelableArrayListExtra(WEB_APP_DATA);
        if (mList != null) {
            mRecyclerViewAdapter.setData(mList);
            mRecyclerViewHelper.setList(mList);
        }

    }

    /**
     * findViewById
     */
    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerview);
    }

    /**
     * 初始化Rv
     */
    private void initRecyclerView() {
        //布局
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        //适配器
        mRecyclerViewAdapter = new AppRecyclerViewAdapter(getContext());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        //拖拽
        mRecyclerViewHelper = new RecyclerViewDragHelper<WebViewApplication>(getContext());
        mRecyclerViewHelper.setUseDrag(true).setSwipeEnabled(false);
        mRecyclerViewHelper.build(mRecyclerView, mRecyclerViewAdapter);

    }
}
