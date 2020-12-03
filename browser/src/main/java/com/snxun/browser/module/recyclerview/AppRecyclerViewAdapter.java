package com.snxun.browser.module.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lodz.android.imageloaderkt.ImageLoader;
import com.snxun.browser.R;
import com.snxun.browser.bean.WebApplicationBean;

import java.util.ArrayList;

/**
 * Created by Yangjw on 2020/11/30.
 */
public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.AppViewHolder> {
    private Context mContext;
    private ArrayList<WebApplicationBean> mList;
    private ItemClickListener mListener;

    public AppRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
        mList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_app_list, null);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        WebApplicationBean data = mList.get(position);
        holder.appName.setText(data.appName);
        setAppImage(holder.appIcon, data.appIconUrl);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setOnItemClickListener(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;


        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.item_app_icon);
            appName = itemView.findViewById(R.id.item_app_name);
        }
    }

    public interface ItemClickListener {
        void setOnItemClickListener(WebApplicationBean bean);
    }

    public void setItemListener(ItemClickListener listener) {
        this.mListener = listener;
    }

    public void setData(ArrayList<WebApplicationBean> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 设置app图标
     *
     * @param imageView 需要设置图标的控件
     * @param url       图标的路径
     */
    private void setAppImage(ImageView imageView, String url) {
        ImageLoader.create(mContext)
                .loadUrl(url)
                .setFitCenter()
                .into(imageView);
    }
}
