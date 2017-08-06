package com.landkid.said.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
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
import com.landkid.said.data.api.model.SaidItem;
import com.landkid.said.data.api.model.behance.Project;
import com.landkid.said.data.api.model.dribbble.Shot;
import com.landkid.said.util.HtmlUtils;
import com.landkid.said.util.glide.FeedImageTarget;

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
 * Created by landkid on 2017. 6. 5..
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

    public void addItems(List<SI> items) {

//        int previousShotLength = items.size();
//        for (SaidItem shot : items) {
//            this.items.add((Shot) shot);
//        }
//        notifyItemRangeInserted(previousShotLength, previousShotLength + items.size() - 1);
        for (SI shot : items) {
            this.items.add(shot);
        }
        notifyDataSetChanged();
    }

    public void removeItems(List<SI> items){
        this.items.removeAll(items);
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

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_SKELETON = 2;

    @Override
    public int getItemViewType(int position) {

        if(items.get(position).isHeaderItem){
            return TYPE_HEADER;
        } else if(items.get(position).isSkeletonItem){
            return TYPE_SKELETON;
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_header, parent, false);
                return new HeaderViewHolder(view);

            case TYPE_SKELETON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_skeleton, parent, false);
                return new SkeletonViewHolder(view);

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
                return new ItemViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(FeedViewHolder feedViewHolder, int position) {

        if (getItemViewType(position) == TYPE_HEADER) {

            SaidItem saidItem = items.get(position);

            HeaderViewHolder headerViewHolder = (HeaderViewHolder) feedViewHolder;
            headerViewHolder.header.setText(saidItem.headerTitle);
            return;
        }

        if(getItemViewType(position) == TYPE_SKELETON){
            return;
        }

        if(items.get(position) instanceof Shot) {
            final Shot shot = (Shot) items.get(position);

            final ItemViewHolder holder = (ItemViewHolder) feedViewHolder;

            holder.username.setText(HtmlUtils.fromHtml(shot.user.name));

            holder.image.setOnClickListener(new View.OnClickListener() {

                private static final long MIN_CLICK_INTERVAL = 600;

                private long mLastClickTime;

                @Override
                public void onClick(View v) {

                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;

                    if(elapsedTime <= MIN_CLICK_INTERVAL){
                        return;
                    }

                    if (holder.isReady) {
                        Bitmap bitmap;
                        if(holder.image.getDrawable() instanceof GlideBitmapDrawable) {
                            bitmap = ((GlideBitmapDrawable) holder.image.getDrawable()).getBitmap();
                        } else {
                            bitmap = ((GifDrawable) holder.image.getDrawable()).getFirstFrame();

                        }


                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette p) {
                                int[] colors = {
                                        p.getVibrantColor(-1),
                                        p.getLightVibrantColor(-1),
                                        p.getDarkVibrantColor(-1),
                                        p.getMutedColor(-1),
                                        p.getLightMutedColor(-1),
                                        p.getDarkMutedColor(-1)};

                                Bundle bundle = new Bundle();
                                bundle.putParcelable(FeedAdapter.KEY_SHOT, shot);
                                bundle.putIntArray(mContext.getString(R.string.swatch_colors_key), colors);

                                Message message = new Message();
                                message.what = MainActivity.TO_DRIBBLE_SHOT_ACTIVITY;
                                message.setData(bundle);
                                ((MainActivity) mContext).transitionHandler.sendMessage(message);
                            }
                        });

                    }
                }
            });

            Glide.with(holder.itemView.getContext())
                    .load(shot.images.best())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.isReady = true;
                            resource.stop();
                            return false;
                        }
                    })
                    .fitCenter()
                    .override(shot.images.bestSize()[0], shot.images.bestSize()[1])
                    .into(new FeedImageTarget(holder.image, false));

            if(shot.images.best().endsWith(".gif")){
                holder.gifIndicator.setVisibility(View.VISIBLE);
            } else {
                holder.gifIndicator.setVisibility(View.GONE);
            }

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

            final ItemViewHolder holder = (ItemViewHolder) feedViewHolder;

            holder.username.setText(HtmlUtils.fromHtml(project.owners.get(0).getFullName()));

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

                private static final long MIN_CLICK_INTERVAL = 600;

                private long mLastClickTime;

                @Override
                public void onClick(View v) {

                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;

                    if(elapsedTime <= MIN_CLICK_INTERVAL){
                        return;
                    }

                    if (holder.isReady) {

                        Bundle bundle = new Bundle();
                        bundle.putLong(BehanceProjectActivity.KEY_PROJECT_ID, project.id);

                        Message message = new Message();
                        message.what = MainActivity.TO_BEHANCE_PROJECT_ACTIVITY;
                        message.setData(bundle);
                        ((MainActivity) mContext).transitionHandler.sendMessage(message);
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
        @BindView(R.id.gif_indicator) LinearLayout gifIndicator;

        boolean isReady = false;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public CardView getImageCard(){
            return imageCard;
        }

    }

    static class SkeletonViewHolder extends FeedViewHolder {

        SkeletonViewHolder(View itemView){
            super(itemView);
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

        private GlideDrawable resource;
        private Bitmap bitmap;
        private Shot shot;


        private StatusBarColorChangeRunnable(Bitmap bitmap, Shot shot){
            this.bitmap = bitmap;
        }

        private StatusBarColorChangeRunnable(GlideDrawable drawable, Shot shot){
            this.resource = drawable;
            this.shot = shot;
        }

        @Override
        public void run() {

            if(bitmap == null){
                if(resource instanceof GlideBitmapDrawable) {
                    bitmap = ((GlideBitmapDrawable) resource).getBitmap();
                } else {
                    bitmap = ((GifDrawable) resource).getFirstFrame();

                }
            }


            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                public void onGenerated(Palette p) {
                    int [] colors = {
                            p.getVibrantColor(-1),
                            p.getLightVibrantColor(-1),
                            p.getDarkVibrantColor(-1),
                            p.getMutedColor(-1),
                            p.getLightMutedColor(-1),
                            p.getDarkMutedColor(-1)};

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(FeedAdapter.KEY_SHOT, shot);
                    bundle.putIntArray(mContext.getString(R.string.swatch_colors_key), colors);

                    Message message = new Message();
                    message.what = MainActivity.TO_DRIBBLE_SHOT_ACTIVITY;
                    message.setData(bundle);
                    ((MainActivity) mContext).transitionHandler.sendMessage(message);
                }
            });
        }
    }
}

