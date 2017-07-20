package com.landkid.said.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.text.util.LinkifyCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.landkid.said.R;
import com.landkid.said.data.api.dribbble.DribbblePrefs;
import com.landkid.said.data.api.model.Comment;
import com.landkid.said.data.api.model.Like;
import com.landkid.said.data.api.model.SaidItem;
import com.landkid.said.data.api.model.Shot;
import com.landkid.said.util.ResourceUtils;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by landkid on 2017. 6. 18..
 */

public class SubActivity extends AppCompatActivity {

    @BindView(R.id.v_muted_dark_swatch) CardView mutedDarkSwatch;
    @BindView(R.id.v_muted_light_swatch) CardView mutedLightSwatch;
    @BindView(R.id.v_muted_swatch) CardView mutedSwatch;
    @BindView(R.id.v_vibrant_dark_swatch) CardView vibrantDarkSwatch;
    @BindView(R.id.v_vibrant_light_swatch) CardView vibrantLightSwatch;
    @BindView(R.id.v_vibrant_swatch) CardView vibrantSwatch;
    @BindView(R.id.cv_color_palette) LinearLayout colorPalette;

    @BindView(R.id.tv_like_count) TextView likeCount;
    @BindView(R.id.tv_comment_count) TextView commentCount;
    @BindView(R.id.tv_view_count) TextView viewCount;

    @BindView(R.id.title) TextView title;
    @BindView(R.id.description) TextView description;

    @BindView(R.id.profile_photo) ImageView profilePhoto;
    @BindView(R.id.designer_name) TextView designerName;
    @BindView(R.id.location) TextView location;

    @BindView(R.id.sub_image) ImageView image;
    @BindView(R.id.fab_back) FloatingActionButton fabBack;
    @BindView(R.id.rv_comments) RecyclerView rvComments;


    final Handler colorHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.getData().containsKey("colors")) {
                int [] colors = msg.getData().getIntArray("colors");

                setSwatchColor(vibrantSwatch, colors[0], 0);
                setSwatchColor(vibrantLightSwatch, colors[1], 1);
                setSwatchColor(vibrantDarkSwatch, colors[2], 2);
                setSwatchColor(mutedSwatch, colors[3], 3);
                setSwatchColor(mutedLightSwatch, colors[4], 4);
                setSwatchColor(mutedDarkSwatch, colors[5], 5);
                colorPalette.setVisibility(View.VISIBLE);
                colorPalette.setAlpha(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        colorPalette.animate()
                                .alpha(1)
                                .setInterpolator(new AccelerateInterpolator())
                                .setDuration(200)
                                .start();
                    }
                }, 100);
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-R.ttf"))
                .addBold(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-B.ttf"))
                .addBoldItalic(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-BI.ttf"))
                .addItalic(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-RI.ttf"));

        setContentView(R.layout.activity_sub);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Shot shot = intent.getParcelableExtra(FeedAdapter.KEY_SHOT);

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        title.setText(shot.title);

        if(shot.description != null) {

            int[][] states = new int[][] {
                    new int[] { android.R.attr.state_pressed}, // enabled
                    new int[] {}, // disabled
            };

            int[] colors = new int[] {
                    ResourcesCompat.getColor(getResources(), R.color.colorHeartFilled, getTheme()),
                    ResourcesCompat.getColor(getResources(), R.color.colorHeartFilled, getTheme())
            };

            final ColorStateList linkTextColor = new ColorStateList(states, colors);


            Spannable span = new SpannableString(Html.fromHtml(shot.description));
            description.setText(span);

            //description.setText(Html.fromHtml(shot.description));



            LinkifyCompat.addLinks(description, Linkify.ALL);


            description.setLinkTextColor(linkTextColor);
        } else {
            description.setVisibility(View.GONE);
        }

        likeCount.setText(shot.likes_count + "");
        commentCount.setText(shot.comments_count + "");
        viewCount.setText(shot.views_count + "");

        designerName.setText(Html.fromHtml(shot.user.name));
        location.setText(shot.user.location);

        setCommentsView();

        final Call<List<Comment>> commentsCall = DribbblePrefs.get(getApplicationContext()).getApi().getComments(shot.id);
        commentsCall.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                ((CommentsAdapter) rvComments.getAdapter()).setComments(response.body());
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

            }
        });

        Glide.with(getApplicationContext())
                .load(shot.user.avatar_url)
                .into(profilePhoto);

        Glide.with(getApplicationContext())
                .load(shot.images.best())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(shot.images.bestSize()[0], shot.images.bestSize()[1])
                .into(new GlideDrawableImageViewTarget(image){

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
                                    bitmap = gifDrawable.getFirstFrame();
                                }

                                Palette palette = Palette.from(bitmap).generate();

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

                                if(vibrantSwatch != -1) {
                                    if (Build.VERSION.SDK_INT >= 21) {
                                        Window window = getWindow();
                                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                        window.setStatusBarColor(vibrantSwatch);
                                    }
                                }

                            }
                        }).start();


                    }
                });
    }

    void setCommentsView(){
        rvComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        CommentsAdapter adapter = new CommentsAdapter(getApplicationContext());
        rvComments.setAdapter(adapter);
        rvComments.setVerticalScrollBarEnabled(false);
    }

    void setSwatchColor(CardView view, int swatch, int order){
        if(swatch != -1) {
            view.setCardBackgroundColor(swatch);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private class CommentsAdapter extends RecyclerView.Adapter<CommentsViewHolder>{

        List<Comment> comments;
        DribbblePrefs dribbblePrefs;
        Context mContext;

        CommentsAdapter(Context context) {
            this.comments = new ArrayList<>();
            this.dribbblePrefs = DribbblePrefs.get(context);
            this.mContext = context;
        }

        public void setComments(List<Comment> comments){
            this.comments = comments;
            notifyDataSetChanged();
        }

        public void addShots(List<Comment> comments) {

//        int previousShotLength = shots.size();
//        for (SaidItem shot : shots) {
//            this.shots.add((Shot) shot);
//        }
//        notifyItemRangeInserted(previousShotLength, previousShotLength + shots.size() - 1);
            for (Comment comment : comments) {
                this.comments.add(comment);
            }
            notifyDataSetChanged();
        }

        @Override
        public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CommentsViewHolder(view);        }

        @Override
        public void onBindViewHolder(CommentsViewHolder holder, int position) {
            Comment comment = comments.get(position);
            holder.userName.setText(comment.user.name);
            holder.comment.setText(Html.fromHtml(comment.body.toString()));

            Glide.with(getApplicationContext())
                    .load(comment.user.avatar_url)
                    .into(holder.profilePhoto);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.profile_photo) ImageView profilePhoto;
        @BindView(R.id.user_name) TextView userName;
        @BindView(R.id.comment) TextView comment;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
