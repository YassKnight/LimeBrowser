package com.snxun.browser.module.webview.tab;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.snxun.browser.R;
import com.snxun.browser.controller.UiController;
import com.snxun.browser.module.stackview.adapter.StackAdapter;
import com.snxun.browser.module.stackview.widget.LimeStackView;

import java.util.ArrayList;
import java.util.List;


public class TabAdapter extends StackAdapter<Tab> {
    private UiController mController;
    private int mCurrent;
    private List<Tab> mTabs;

    public TabAdapter(Context context, UiController controller) {
        super(context);
        mController = controller;
        mTabs = new ArrayList<Tab>();
        mCurrent = -1;
    }

    @Override
    public Tab getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public long getItemId(int position) {
        return position;
    }

    public void setCurrent(int index) {
        mCurrent = index;
    }

    @Override
    public void bindView(Tab tab, int position, LimeStackView.ViewHolder holder) {
        TabViewHolder pagerViewHolder = (TabViewHolder) holder;
        pagerViewHolder.bind(tab, position);
    }

    @Override
    protected LimeStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        CardView card = (CardView) mInflater.inflate(R.layout.layout_recycler_card, parent, false);
        card.setCardElevation(4);
        card.setRadius(mContext.getResources().getDimension(R.dimen.dimen_8dp));
        mInflater.inflate(R.layout.layout_tab_card, card, true);
        return new TabAdapter.TabViewHolder(card);
    }

    class TabViewHolder extends LimeStackView.ViewHolder implements View.OnClickListener {

        View content;
        ImageView ivPagePreview, ivWebsiteIcon, ivClose;
        TextView tvPosition;
        Tab tab;
        int position;

        public TabViewHolder(View view) {
            super(view);
            content = view;
            ivPagePreview = (ImageView) view.findViewById(R.id.ivPagePreview);
            ivWebsiteIcon = (ImageView) view.findViewById(R.id.ivWebsiteIcon);
            ivClose = (ImageView) view.findViewById(R.id.ivPageClose);
            tvPosition = (TextView) view.findViewById(R.id.tvPagerUC);
        }

        public void bind(Tab tab, int position) {
            String title = tab.getTitle();
            tvPosition.setText(title);
            Bitmap favicon = tab.getFavicon();
            if (favicon != null) {
                ivWebsiteIcon.setImageBitmap(favicon);
            }
            Bitmap preview = tab.getScreenshot();
            if (preview != null) {
                ivPagePreview.setImageBitmap(preview);
            }
            ivClose.setOnClickListener(this);
            content.setOnClickListener(this);
            this.tab = tab;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (view == content) {
                if (mController != null) {
                    mController.selectTab(tab);
                }
            } else if (view == ivClose) {
                if (mController != null) {
                    mController.closeTab(tab);
                }
            }
        }
    }
}
