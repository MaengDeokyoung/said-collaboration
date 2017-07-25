package com.landkid.said.ui.listener;

import android.support.v7.widget.RecyclerView;

import com.landkid.said.ui.FeedAdapter;

/**
 * Created by landkid on 2017. 7. 24..
 */

public class ParallaxScrollListener extends RecyclerView.OnScrollListener {

    int newState;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        this.newState = newState;
        if (newState == RecyclerView.SCROLL_AXIS_NONE) {
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                if (recyclerView.getChildViewHolder(recyclerView.getChildAt(i)) instanceof FeedAdapter.ItemViewHolder) {
                    FeedAdapter.ItemViewHolder holder = (FeedAdapter.ItemViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                    if (holder != null) {
                        holder.imageCard.animate()
                                .translationY(0)
                                .setDuration(700)
                                .start();
                    }
                }
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (this.newState != RecyclerView.SCROLL_STATE_IDLE) {
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                if (recyclerView.getChildViewHolder(recyclerView.getChildAt(i)) instanceof FeedAdapter.ItemViewHolder) {
                    FeedAdapter.ItemViewHolder holder = (FeedAdapter.ItemViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                    if (holder != null) {
                        if (dy > 0 && holder.imageCard.getTranslationY() <= 20)
                            holder.imageCard.setTranslationY(holder.imageCard.getTranslationY() + dy / 5.0f);

                        if (dy < 0 && holder.imageCard.getTranslationY() >= -20)
                            holder.imageCard.setTranslationY(holder.imageCard.getTranslationY() + dy / 5.0f);

                    }
                }
            }
        }
    }
}
