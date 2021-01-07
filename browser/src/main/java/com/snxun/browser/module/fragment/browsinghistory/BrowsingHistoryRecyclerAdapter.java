package com.snxun.browser.module.fragment.browsinghistory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.snxun.browser.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName : LimeBrowser
 * @Author : Yangjw
 * @Time : 2021/1/6
 * @Description :浏览历史记录
 */
public class BrowsingHistoryRecyclerAdapter extends RecyclerView.Adapter<BrowsingHistoryRecyclerAdapter.BhViewHolder> {
    private Context mContext;
    private List<String> mList = new ArrayList<>();

    ItemClickCallBack mItemClickCallBack;
    DeleteClickCallBack mDeleteClickCallBack;

    public BrowsingHistoryRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<String> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    @NonNull
    @Override
    public BhViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_browsing_history, null);
        return new BhViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BhViewHolder holder, int position) {
        holder.historyTv.setText(mList.get(position));
        holder.historyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickCallBack.onItemClickCallBack(mList.get(position));
            }
        });
        holder.historyDeleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteClickCallBack.onDeleteClickCallBack(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class BhViewHolder extends RecyclerView.ViewHolder {
        TextView historyTv;
        ImageView historyDeleteImg;

        public BhViewHolder(@NonNull View itemView) {
            super(itemView);
            historyTv = itemView.findViewById(R.id.history_tv);
            historyDeleteImg = itemView.findViewById(R.id.history_delete_img);
        }
    }


    public interface ItemClickCallBack {
        void onItemClickCallBack(String history_url);
    }

    public interface DeleteClickCallBack {
        void onDeleteClickCallBack(int position);
    }

    public void setItemClickCallBack(ItemClickCallBack callBack) {
        this.mItemClickCallBack = callBack;
    }

    public void setDeleteClickCallBack(DeleteClickCallBack callBack) {
        this.mDeleteClickCallBack = callBack;
    }
}
