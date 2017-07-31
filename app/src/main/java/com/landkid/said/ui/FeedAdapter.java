package com.landkid.said.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.landkid.said.R;
import com.landkid.said.data.api.model.SaidItem;
import com.landkid.said.data.api.model.behance.Project;
import com.landkid.said.data.api.model.dribbble.Shot;
import com.landkid.said.util.HtmlUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sds on 2017. 6. 5..
 */

public class FeedAdapter<SI extends SaidItem> extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    protected static final String KEY_SHOT = "KEY_SHOT";

    private List<SI> items;
    private Context mContext;

    FeedAdapter(Context context) {
        this.items = new ArrayList<>();
        this.mContext = context;
    }

    public void setItems(List<SI> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public void addShots(List<SI> shots) {

//        int previousShotLength = items.size();
//        for (SaidItem shot : items) {
//            this.items.add((Shot) shot);
//        }
//        notifyItemRangeInserted(previousShotLength, previousShotLength + items.size() - 1);
        for (SI shot : shots) {
            this.items.add(shot);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(FeedViewHolder feedViewHolder) {
        super.onViewRecycled(feedViewHolder);
        if(feedViewHolder instanceof ItemViewHolder) {
            ((ItemViewHolder) feedViewHolder).isReady = false;
            if(((ItemViewHolder) feedViewHolder).image != null) {

            }
        }
        //holder.imageLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 0;

    @Override
    public int getItemViewType(int position) {

        if(items.get(position).isHeaderItem){
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
        if(items.get(position) instanceof Shot) {
            final Shot shot = (Shot) items.get(position);

            if (getItemViewType(position) == TYPE_HEADER) {
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) feedViewHolder;
                headerViewHolder.header.setText(shot.headerTitle);
                return;
            }

            final ItemViewHolder holder = (ItemViewHolder) feedViewHolder;

            holder.username.setText(HtmlUtils.fromHtml(shot.user.name));

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.isReady) {
                        new Thread(new StatusBarColorChangeRunnable((BitmapDrawable) holder.image.getDrawable(), shot)).start();

//                        Bundle bundle = new Bundle();
//                        bundle.putParcelable(KEY_SHOT, shot);
//
//
//                        Message message = new Message();
//                        message.what = MainActivity.TO_DRIBBLE_SHOT_ACTIVITY;
//                        message.setData(bundle);
//                        ((MainActivity) mContext).transitionHandler.sendMessage(message);


                        //mContext.startActivity(intent);
                    }
                }
            });

//            ImageRequest imageRequest = ImageRequestBuilder
//                    .newBuilderWithSource(Uri.parse(shot.images.best()))
//                    .setRequestPriority(Priority.HIGH)
//                    .setProgressiveRenderingEnabled(true)
//                    .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
//                    .build();
//
//
//            DraweeController controller = Fresco.newDraweeControllerBuilder()
//                    .setImageRequest(imageRequest)
//                    .setOldController(holder.image.getController())
//                    .setAutoPlayAnimations(true)
//                    .setControllerListener(new ControllerListener<ImageInfo>() {
//                        @Override
//                        public void onSubmit(String id, Object callerContext) {
//
//                        }
//
//                        @Override
//                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
//                            holder.isReady = true;
//                        }
//
//                        @Override
//                        public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
//
//                        }
//
//                        @Override
//                        public void onIntermediateImageFailed(String id, Throwable throwable) {
//
//                        }
//
//                        @Override
//                        public void onFailure(String id, Throwable throwable) {
//
//                        }
//
//                        @Override
//                        public void onRelease(String id) {
//
//                        }
//                    })
//                    .build();
//            holder.image.setController(controller);

            Glide.with(holder.itemView.getContext())
                    .load(shot.images.best())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.isReady = true;
                            return false;
                        }
                    })
                    .override(shot.images.bestSize()[0], shot.images.bestSize()[1])
                    .into(holder.image);

//            new GlideDrawableImageViewTarget(holder.image) {
//                @Override
//                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                    super.onResourceReady(resource, animation);
//                            if (resource instanceof GifDrawable) {
//                                GifDrawable gif = (GifDrawable) resource;
//                                gif.start();
//                            }
//                    holder.isReady = true;
//                }
//            }

            holder.likeCount.setText(String.valueOf(shot.likes_count));
            holder.viewCount.setText(String.valueOf(shot.views_count));

            if (shot.created_at != null) {
                Date today = new Date();
                SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", Locale.ENGLISH);

                if (format.format(today).equals(format.format(shot.created_at))) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH", Locale.ENGLISH);
                    int differ = Integer.parseInt(timeFormat.format(today)) - Integer.parseInt(timeFormat.format(shot.created_at));
                    holder.createAt.setText(String.format(mContext.getString(R.string.hours_ago_postfix), differ));
                } else {
                    holder.createAt.setText(format.format(shot.created_at));
                }
            } else {
                holder.createAt.setVisibility(View.GONE);
            }

            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.isReady) {
                        new ShareDribbbleImageTask((Activity) holder.itemView.getContext(), shot).execute();
                    }
                }
            });
        } else if (items.get(position) instanceof Project){
            final Project project = (Project) items.get(position);

            if (getItemViewType(position) == TYPE_HEADER) {
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) feedViewHolder;
                headerViewHolder.header.setText(project.headerTitle);
                return;
            }

            final ItemViewHolder holder = (ItemViewHolder) feedViewHolder;

            holder.username.setText(HtmlUtils.fromHtml(project.name));

            Glide.with(holder.itemView.getContext())
                    .load(project.covers.get("404"))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new GlideDrawableImageViewTarget(holder.image) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            if (resource instanceof GifDrawable) {
                                GifDrawable gif = (GifDrawable) resource;
                                gif.start();
                            }
                            holder.isReady = true;
                        }
                    });

            Date createdOn = new Date(TimeUnit.SECONDS.toMillis(project.created_on));

            if (createdOn != null) {
                Date today = new Date();
                SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", Locale.ENGLISH);

                if (format.format(today).equals(format.format(createdOn))) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH", Locale.ENGLISH);
                    int differ = Integer.parseInt(timeFormat.format(today)) - Integer.parseInt(timeFormat.format(createdOn));
                    holder.createAt.setText(String.format(mContext.getString(R.string.hours_ago_postfix), differ));
                    //holder.createAt.setText((Integer.parseInt(timeFormat.format(today)) - Integer.parseInt(timeFormat.format(createdOn))) + " hours ago");
                } else {
                    holder.createAt.setText(format.format(createdOn));
                }
            } else {
                holder.createAt.setVisibility(View.GONE);
            }

            final String url = project.url;
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    mContext.startActivity(i);

                    if (holder.isReady) {

                        Bundle bundle = new Bundle();
                        bundle.putLong(BehanceProjectActivity.KEY_PROJECT_ID, project.id);


                        Message message = new Message();
                        message.what = MainActivity.TO_BEHANCE_PROJECT_ACTIVITY;
                        message.setData(bundle);
                        ((MainActivity) mContext).transitionHandler.sendMessage(message);

                        //mContext.startActivity(intent);
                    }

                }
            });
            holder.viewCount.setText(String.valueOf(project.stats.views));

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
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

    private class ShareDribbbleImageTask extends AsyncTask<Void, Void, File> {

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

    private class StatusBarColorChangeRunnable implements Runnable {

        private BitmapDrawable resource;
        private Bitmap bitmap;
        private Shot shot;


        private StatusBarColorChangeRunnable(Bitmap bitmap, Shot shot){
            this.bitmap = bitmap;
        }

        private StatusBarColorChangeRunnable(BitmapDrawable drawable, Shot shot){
            this.resource = drawable;
            this.shot = shot;
        }

        @Override
        public void run() {

            if(bitmap == null){
                bitmap = resource.getBitmap();
            }

            Palette palette = Palette.from(bitmap).generate();

            int vibrantSwatch = palette.getVibrantColor(-1);
            int vibrantLightSwatch = palette.getLightVibrantColor(-1);
            int vibrantDarkSwatch = palette.getDarkVibrantColor(-1);
            int mutedSwatch = palette.getMutedColor(-1);
            int mutedLightSwatch = palette.getLightMutedColor(-1);
            int mutedDarkSwatch = palette.getDarkMutedColor(-1);

            int [] colors = {vibrantSwatch, vibrantLightSwatch, vibrantDarkSwatch, mutedSwatch, mutedLightSwatch, mutedDarkSwatch};

            Bundle bundle = new Bundle();
            bundle.putParcelable(FeedAdapter.KEY_SHOT, shot);
            bundle.putIntArray(mContext.getString(R.string.swatch_colors_key), colors);

            Message message = new Message();
            message.what = MainActivity.TO_DRIBBLE_SHOT_ACTIVITY;
            message.setData(bundle);
            ((MainActivity) mContext).transitionHandler.sendMessage(message);

//            Message msg = new Message();
//            Bundle bundle = new Bundle();
//            bundle.putIntArray(getString(R.string.swatch_colors_key), colors);
//            msg.setData(bundle);
//            colorHandler.sendMessage(msg);
        }
    }
}

