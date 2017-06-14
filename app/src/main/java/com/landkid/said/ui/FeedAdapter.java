package com.landkid.said.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.landkid.said.R;
import com.landkid.said.data.api.dribbble.DribbblePrefs;
import com.landkid.said.data.api.model.Like;
import com.landkid.said.data.api.model.SaidItem;
import com.landkid.said.data.api.model.Shot;
import com.landkid.said.util.ResourceUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sds on 2017. 6. 5..
 */

class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    List<Shot> shots;
    DribbblePrefs dribbblePrefs;

    FeedAdapter(Context context) {
        this.shots = new ArrayList<>();
        this.dribbblePrefs = DribbblePrefs.get(context);
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
            ItemViewHolder holder = (ItemViewHolder) feedViewHolder;
            holder.info.setVisibility(View.GONE);
            holder.colorPalette.setVisibility(View.GONE);

            holder.mutedDarkSwatch.setBackgroundColor(0x00000000);
            holder.mutedLightSwatch.setBackgroundColor(0x00000000);
            holder.mutedSwatch.setBackgroundColor(0x00000000);
            holder.vibrantDarkSwatch.setBackgroundColor(0x00000000);
            holder.vibrantLightSwatch.setBackgroundColor(0x00000000);
            holder.vibrantSwatch.setBackgroundColor(0x00000000);
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


    void setSwatchColor(View view, int swatch, int order){
        if(swatch != -1) {
            view.setBackgroundColor(swatch);
            view.setVisibility(View.VISIBLE);
//            view.setScaleX(0);
//            view.setPivotX(0);
//            view.animate()
//                    .scaleX(1)
//                    .setInterpolator(new AccelerateInterpolator())
//                    .setStartDelay(order * 100)
//                    .setDuration(100)
//                    .start();
        } else {
            view.setVisibility(View.GONE);
        }
    }

    void setSwatchColor(View view, int swatch){
        if(swatch != -1) {
            view.setBackgroundColor(swatch);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }



    void setSwatchColor(View view, Palette.Swatch swatch){
        if(swatch != null) {
            view.setBackgroundColor(swatch.getRgb());
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
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

        final Handler colorHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.getData().containsKey("colors")) {
                    int [] colors = msg.getData().getIntArray("colors");

                    setSwatchColor(holder.vibrantSwatch, colors[0], 0);
                    setSwatchColor(holder.vibrantLightSwatch, colors[1], 1);
                    setSwatchColor(holder.vibrantDarkSwatch, colors[2], 2);
                    setSwatchColor(holder.mutedSwatch, colors[3], 3);
                    setSwatchColor(holder.mutedLightSwatch, colors[4], 4);
                    setSwatchColor(holder.mutedDarkSwatch, colors[5], 5);
                    holder.colorPalette.setVisibility(View.VISIBLE);
                    holder.colorPalette.setAlpha(0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.colorPalette.animate()
                                    .alpha(1)
                                    .setInterpolator(new AccelerateInterpolator())
                                    .setDuration(200)
                                    .start();
                        }
                    }, 100);


//                    setSwatchColor(holder.vibrantSwatch, vibrantSwatch);
//                    setSwatchColor(holder.vibrantLightSwatch, vibrantLightSwatch);
//                    setSwatchColor(holder.vibrantDarkSwatch, vibrantDarkSwatch);
//                    setSwatchColor(holder.mutedSwatch, mutedSwatch);
//                    setSwatchColor(holder.mutedLightSwatch, mutedLightSwatch);
//                    setSwatchColor(holder.mutedDarkSwatch, mutedDarkSwatch);
                }
            }
        };

        Glide.with(holder.itemView.getContext())
                .load(shot.images.best())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(shot.images.bestSize()[0], shot.images.bestSize()[1])
                .into(new GlideDrawableImageViewTarget(holder.image){

                    @Override
                    public void onResourceReady(final GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap;
                                if(!(resource instanceof GifDrawable)) {
                                    bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                                }else {
                                    GifDrawable gifDrawable = (GifDrawable) resource;
//                                    gifDrawable.stop();
                                    bitmap = ((GifDrawable) resource).getFirstFrame();
                                }

                                Palette palette = Palette.from(bitmap).generate();
//                                    int vibrantSwatch = palette.getVibrantSwatch().getRgb();
//                                    int vibrantLightSwatch = palette.getLightVibrantSwatch().getRgb();
//                                    int vibrantDarkSwatch = palette.getDarkVibrantSwatch().getRgb();
//                                    int mutedSwatch = palette.getMutedSwatch().getRgb();
//                                    int mutedLightSwatch = palette.getLightMutedSwatch().getRgb();
//                                    int mutedDarkSwatch = palette.getDarkMutedSwatch().getRgb();

                                int vibrantSwatch = palette.getVibrantColor(-1);
                                int vibrantLightSwatch = palette.getLightVibrantColor(-1);
                                int vibrantDarkSwatch = palette.getDarkVibrantColor(-1);
                                int mutedSwatch = palette.getMutedColor(-1);
                                int mutedLightSwatch = palette.getLightMutedColor(-1);
                                int mutedDarkSwatch = palette.getDarkMutedColor(-1);

                                int [] colors = {vibrantSwatch, vibrantLightSwatch, vibrantDarkSwatch, mutedSwatch, mutedLightSwatch, mutedDarkSwatch};

                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putIntArray("colors", colors);
                                msg.setData(bundle);
                                colorHandler.sendMessage(msg);

                            }
                        }).start();


                    }
                });

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

        final Call<Like> likeCall = dribbblePrefs.getApi().checkLiked(shot.id);
        likeCall.enqueue(new Callback<Like>() {
            @Override
            public void onResponse(Call<Like> call, Response<Like> response) {
                if(response.body() != null){
                    holder.liked.setScaleX(1);
                    holder.liked.setScaleY(1);
                } else {
                    holder.liked.setScaleX(0);
                    holder.liked.setScaleY(0);
                }
            }

            @Override
            public void onFailure(Call<Like> call, Throwable t) {

            }
        });


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.liked.getScaleY() == 0) {
                    doLike(shot, true);
                    holder.liked.setPivotX(holder.liked.getWidth() / 2f);
                    holder.liked.setPivotY(holder.liked.getHeight() / 1.2f);
                    holder.liked.animate()
                            .scaleX(1)
                            .scaleY(1)
                            .setDuration(500)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();

                    holder.likeIcon.animate()
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    holder.likeIcon.animate()
                                            .translationY(0)
                                            .setInterpolator(new EaseOutElasticInterpolator())
                                            .setDuration(1000)
                                            .start();
                                }
                            })
                            .translationY(ResourceUtils.dpToPx(10, holder.itemView.getContext()))
                            .setInterpolator(new DecelerateInterpolator())
                            .setDuration(500)
                            .start();

                } else {
                    doLike(shot, false);
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
    boolean performingLike = false;

    void checkLike(Shot shot){
        final Call<Like> likeCall = dribbblePrefs.getApi().checkLiked(shot.id);
        likeCall.enqueue(new Callback<Like>() {
            @Override
            public void onResponse(Call<Like> call, Response<Like> response) {
                performingLike = false;
            }

            @Override
            public void onFailure(Call<Like> call, Throwable t) {
                performingLike = false;
            }
        });
    }

    void doLike(Shot shot, boolean liked) {
        performingLike = true;
        if (liked) {
            final Call<Like> likeCall = dribbblePrefs.getApi().like(shot.id);
            likeCall.enqueue(new Callback<Like>() {
                @Override
                public void onResponse(Call<Like> call, Response<Like> response) {
                    performingLike = false;
                }

                @Override
                public void onFailure(Call<Like> call, Throwable t) {
                    performingLike = false;
                }
            });
        } else {
            final Call<Void> unlikeCall = dribbblePrefs.getApi().unlike(shot.id);
            unlikeCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    performingLike = false;
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    performingLike = false;
                }
            });
        }
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

    static class ItemViewHolder extends FeedViewHolder {

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
        @BindView(R.id.fl_like_icon) FrameLayout likeIcon;

        @BindView(R.id.v_muted_dark_swatch) View mutedDarkSwatch;
        @BindView(R.id.v_muted_light_swatch) View mutedLightSwatch;
        @BindView(R.id.v_muted_swatch) View mutedSwatch;
        @BindView(R.id.v_vibrant_dark_swatch) View vibrantDarkSwatch;
        @BindView(R.id.v_vibrant_light_swatch) View vibrantLightSwatch;
        @BindView(R.id.v_vibrant_swatch) View vibrantSwatch;
        @BindView(R.id.cv_color_palette) CardView colorPalette;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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

    class EaseOutElasticInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            float p = 0.3f;
            float PI = (float) Math.PI;
            float a = (float) Math.pow(2, -10 * input);
            float b = (float) Math.sin((input - p / 4) * ( 2 * PI ) / p);

            return a * b + 1;
        }
    }
}

