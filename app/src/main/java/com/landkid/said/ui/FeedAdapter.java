package com.landkid.said.ui;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.landkid.said.R;
import com.landkid.said.data.api.dribbble.DribbblePreferences;
import com.landkid.said.data.api.model.SaidItem;
import com.landkid.said.data.api.model.Shot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sds on 2017. 6. 5..
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    public static final String KEY_SHOT = "KEY_SHOT";

    List<Shot> shots;
    DribbblePreferences dribbblePreferences;
    Context mContext;

    FeedAdapter(Context context) {
        this.shots = new ArrayList<>();
        this.dribbblePreferences = DribbblePreferences.get(context);
        this.mContext = context;
    }

    public void setShots(List<Shot> shots){
        this.shots = shots;
        notifyDataSetChanged();
    }

    public void addShots(List<Shot> shots) {

//        int previousShotLength = shots.size();
//        for (SaidItem shot : shots) {
//            this.shots.add((Shot) shot);
//        }
//        notifyItemRangeInserted(previousShotLength, previousShotLength + shots.size() - 1);
        for (SaidItem shot : shots) {
            this.shots.add((Shot) shot);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(FeedViewHolder feedViewHolder) {
        super.onViewRecycled(feedViewHolder);
        if(feedViewHolder instanceof ItemViewHolder) {
            ((ItemViewHolder) feedViewHolder).isReady = false;
        }
        //holder.imageLoadingIndicator.setVisibility(View.VISIBLE);
    }

    static final int TYPE_HEADER = 1;
    static final int TYPE_ITEM = 0;

    @Override
    public int getItemViewType(int position) {

        if(shots.get(position).isHeaderItem){
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }

        //return super.getItemViewType(position);
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_header, parent, false);
                return new HeaderViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
                return new ItemViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(FeedViewHolder feedViewHolder, int position) {
        final Shot shot = shots.get(position);

        if(getItemViewType(position) == TYPE_HEADER){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) feedViewHolder;
            headerViewHolder.header.setText(shot.headerTitle);
            return;
        }

        final ItemViewHolder holder = (ItemViewHolder) feedViewHolder;

        holder.username.setText(Html.fromHtml(shot.user.name));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.isReady) {

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(KEY_SHOT, shot);


                    Message message = new Message();
                    message.what = MainActivity.TO_SUB_ACTIVITY;
                    message.setData(bundle);
                    ((MainActivity) mContext).transitionHandler.sendMessage(message);



                    //mContext.startActivity(intent);
                }
            }
        });

        Glide.with(holder.itemView.getContext())
                .load(shot.images.best())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(shot.images.bestSize()[0], shot.images.bestSize()[1])
                .into(new GlideDrawableImageViewTarget(holder.image){
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        holder.isReady = true;
                    }
                });

        holder.likeCount.setText(shot.likes_count + "");
        holder.viewCount.setText(shot.views_count + "");

        if(shot.created_at != null) {
            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", Locale.ENGLISH);

            if (format.format(today).equals(format.format(shot.created_at))) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH", Locale.ENGLISH);
                holder.createAt.setText((Integer.parseInt(timeFormat.format(today)) - Integer.parseInt(timeFormat.format(shot.created_at))) + " hours ago");
            } else {
                holder.createAt.setText(format.format(shot.created_at));
            }
        } else {
            holder.createAt.setVisibility(View.GONE);
        }

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.isReady) {
                    new ShareDribbbleImageTask((Activity) holder.itemView.getContext(), shot).execute();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shots.size();
    }

    static abstract class FeedViewHolder extends RecyclerView.ViewHolder{
        FeedViewHolder(View itemView){
            super(itemView);
        }
    }

    public static class ItemViewHolder extends FeedViewHolder {

        @BindView(R.id.tv_username) TextView username;
        @BindView(R.id.iv_image) ImageView image;
        @BindView(R.id.tv_like_count) TextView likeCount;
        @BindView(R.id.tv_view_count) TextView viewCount;
        @BindView(R.id.ll_user_info_area) View userInfoArea;
        @BindView(R.id.tv_date) TextView createAt;
        @BindView(R.id.pb_loading_image) View imageLoadingIndicator;
        @BindView(R.id.iv_share) ImageView share;

        @BindView(R.id.image_card) CardView imageCard;

        boolean isReady = false;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public CardView getImageCard(){
            return imageCard;
        }

    }

    static class HeaderViewHolder extends FeedViewHolder {

        @BindView(R.id.tv_header) TextView header;

        HeaderViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ShareDribbbleImageTask extends AsyncTask<Void, Void, File> {

        private final Activity activity;
        private final Shot shot;

        ShareDribbbleImageTask(Activity activity, Shot shot) {
            this.activity = activity;
            this.shot = shot;
        }

        @Override
        protected File doInBackground(Void... params) {
            final String url = shot.images.best();
            try {
                return Glide
                        .with(activity)
                        .load(url)
                        .downloadOnly((int) shot.width, (int) shot.height)
                        .get();
            } catch (Exception ex) {
                Log.w("SHARE", "Sharing " + url + " failed", ex);
                return null;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) { return; }
            // glide cache uses an unfriendly & extension-less name,
            // massage it based on the original
            String fileName = shot.images.best();
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
            File renamed = new File(result.getParent(), fileName);
            result.renameTo(renamed);
            Uri uri = FileProvider.getUriForFile(activity, "com.landkid.fileprovider", renamed);
            ShareCompat.IntentBuilder.from(activity)
                    .setText(getShareText())
                    .setType(getImageMimeType(fileName))
                    .setSubject(shot.title)
                    .setStream(uri)
                    .startChooser();
        }

        private String getShareText() {
            return new StringBuilder()
                    .append("“")
                    .append(shot.title)
                    .append("” by ")
                    .append(shot.user.name)
                    .append("\n")
                    .append(shot.url)
                    .toString();
        }

        private String getImageMimeType(@NonNull String fileName) {
            if (fileName.endsWith(".png")) {
                return "image/png";
            } else if (fileName.endsWith(".gif")) {
                return "image/gif";
            }
            return "image/jpeg";
        }
    }

    class EaseInOutElasticInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            float t = input * 2;
            float p = 0.45f;
            float PI = (float) Math.PI;

            if(t < 1) {
                float a = (float) Math.pow(2, 10 * (t -= 1));
                float b = (float) Math.sin((t - p / 4) * ( 2 * PI ) / p);
                return - a * b / 2;
            }

            else {
                float c = (float) Math.pow(2, -10 * (t -= 1));
                float d = (float) Math.sin((t - p / 4) * ( 2 * PI ) / p);
                return c * d / 2 + 1 ;
            }
        }
    }


}

