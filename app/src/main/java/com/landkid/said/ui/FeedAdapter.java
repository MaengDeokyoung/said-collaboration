package com.landkid.said.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.landkid.said.R;
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

class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    List<Shot> shots;

    FeedAdapter() {
        this.shots = new ArrayList<>();
    }

    public void setShots(List<Shot> shots){
        this.shots = shots;
        notifyDataSetChanged();
    }

    public void addShots(List<Shot> shots) {

        for (SaidItem shot : shots) {
            this.shots.add((Shot) shot);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(FeedViewHolder holder) {
        super.onViewRecycled(holder);
        holder.info.setVisibility(View.GONE);
        //holder.imageLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder holder, int position) {
        final Shot shot = shots.get(position);
        holder.username.setText(Html.fromHtml(shot.user.name));
        Glide.with(holder.itemView.getContext())
                .load(shot.images.best())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(shot.images.bestSize()[0], shot.images.bestSize()[1])
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        //holder.imageLoadingIndicator.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(new GlideDrawableImageViewTarget(holder.image));

        /*Glide.with(holder.itemView.getContext())
                .load(shot.user.avatar_url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(100, 100)
                .into(new GlideDrawableImageViewTarget(holder.profilePhoto));*/

        holder.likeCount.setText(shot.likes_count + "");
        //holder.replyCount.setText(shot.comments_count + "");
        holder.viewCount.setText(shot.views_count + "");
        holder.info.setVisibility(View.GONE);
        holder.title.setText(shot.title);

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



        if(shot.description != null) {
            holder.description.setText(Html.fromHtml(shot.description));
            //LinkifyCompat.addLinks(holder.description, Linkify.ALL);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.info.setVisibility(View.VISIBLE);
            }
        });

        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.info.setVisibility(View.GONE);
            }
        });

        if(shot.animated){
            holder.gifIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.gifIndicator.setVisibility(View.GONE);
        }

//            ViewCompat.setScaleX(holder.itemView, 0.5f);
//            ViewCompat.setScaleY(holder.itemView, 0.5f);
//            holder.itemView.animate()
//                    .scaleX(1)
//                    .scaleY(1)
//                    .setInterpolator(new AccelerateDecelerateInterpolator())
//                    .setDuration(300)
//                    .start();

//            ViewCompat.setTranslationX(holder.itemView, ResourceUtils.dpToPx(-50, getApplicationContext()));
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    holder.itemView.animate()
//                            .translationX(0)
//                            .setInterpolator(new AccelerateDecelerateInterpolator())
//                            .setDuration(300)
//                            .start();
//                }
//            }, 100);

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareDribbbleImageTask((Activity) holder.itemView.getContext(), shot).execute();
            }
        });

        holder.liked.setScaleX(0);
        holder.liked.setScaleY(0);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.liked.getScaleY() == 0) {
                    holder.liked.setPivotX(holder.liked.getWidth() / 2f);
                    holder.liked.setPivotY(holder.liked.getHeight() / 1.2f);
                    holder.liked.animate()
                            .scaleX(1)
                            .scaleY(1)
                            .setDuration(300)
                            .start();
                } else {
                    holder.liked.setPivotX(holder.liked.getWidth() / 2f);
                    holder.liked.setPivotY(holder.liked.getHeight() / 1.2f);
                    holder.liked.animate()
                            .scaleX(0)
                            .scaleY(0)
                            .setDuration(300)
                            .start();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shots.size();
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_username) TextView username;
        @BindView(R.id.iv_image) ImageView image;
        //@BindView(R.id.iv_profile_photo) ImageView profilePhoto;
        @BindView(R.id.tv_like_count) TextView likeCount;
        //@BindView(R.id.tv_reply_count) TextView replyCount;
        @BindView(R.id.iv_like) ImageView like;
        @BindView(R.id.iv_liked) ImageView liked;
        @BindView(R.id.tv_view_count) TextView viewCount;
        @BindView(R.id.ll_user_info_area) View userInfoArea;
        @BindView(R.id.ll_info) LinearLayout info;
        @BindView(R.id.tv_title) TextView title;
        @BindView(R.id.tv_description) TextView description;
        @BindView(R.id.tv_date) TextView createAt;
        @BindView(R.id.cv_gif_indicator) FrameLayout gifIndicator;
        @BindView(R.id.pb_loading_image) View imageLoadingIndicator;
        @BindView(R.id.iv_share) ImageView share;

        FeedViewHolder(View itemView) {
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
}

