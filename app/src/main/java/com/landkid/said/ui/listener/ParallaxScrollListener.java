package com.landkid.said.ui.listener;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import com.landkid.said.ui.FeedAdapter;
import com.landkid.said.util.ResourceUtils;

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
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                if (viewHolder != null && viewHolder instanceof FeedAdapter.ItemViewHolder) {
                    FeedAdapter.ItemViewHolder holder = (FeedAdapter.ItemViewHolder) viewHolder;
                    CardView imageCard = holder.getImageCard();

                    imageCard.animate()
                            .translationY(0)
                            .setDuration(700)
                            .start();
                }
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (this.newState != RecyclerView.SCROLL_STATE_IDLE) {
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));

                if (viewHolder != null &&
                        viewHolder instanceof FeedAdapter.ItemViewHolder) {
                    FeedAdapter.ItemViewHolder holder = (FeedAdapter.ItemViewHolder) viewHolder;
                    CardView imageCard = holder.getImageCard();
                    float currentTranslationY = imageCard.getTranslationY();
                    float offset = ResourceUtils.dpToPx(5, recyclerView.getContext());

                    if ((dy > 0 && currentTranslationY <= offset) ||
                            (dy < 0 && currentTranslationY >= - offset))
                        imageCard.setTranslationY(imageCard.getTranslationY() + dy / 5.0f);
                }
            }
        }
    }
}
