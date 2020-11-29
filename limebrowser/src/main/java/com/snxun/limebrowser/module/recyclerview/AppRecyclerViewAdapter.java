package com.snxun.limebrowser.module.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lodz.android.imageloaderkt.ImageLoader;
import com.lodz.android.pandora.widget.rv.recycler.BaseRecyclerViewAdapter;
import com.snxun.limebrowser.R;
import com.snxun.limebrowser.application.WebViewApplication;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by Yangjw on 2020/11/29
 */
public class AppRecyclerViewAdapter extends BaseRecyclerViewAdapter<WebViewApplication> {

    private ArrayList<WebViewApplication> mList;

    public AppRecyclerViewAdapter(@NotNull Context context) {
        super(context);
        mList = new ArrayList<>();
    }

    @Override
    public void onBind(@NotNull RecyclerView.ViewHolder viewHolder, int i) {
        WebViewApplication bean = getItem(i);
        if (bean == null) return;
        AppViewHolder holder = (AppViewHolder) viewHolder;
        holder.appName.setText(bean.appName);
        setAppImage(holder.appIcon, bean.appIconUrl);

    }

    public void setData(ArrayList<WebViewApplication> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppViewHolder(getLayoutView(parent, R.layout.item_app_list, false));
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {

    }


    static class AppViewHolder extends RecyclerView.ViewHolder {
        TextView appName;
        ImageView appIcon;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.item_app_icon);
            appName = itemView.findViewById(R.id.item_app_name);
        }
    }

    /**
     * 设置app图标
     *
     * @param imageView 需要设置图标的控件
     * @param url       图标的路径
     */
    private void setAppImage(ImageView imageView, String url) {
        ImageLoader.create(getContext())
                .loadUrl(url)
                .setFitCenter()
                .into(imageView);
    }
}
