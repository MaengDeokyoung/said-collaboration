package com.landkid.said.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.text.util.LinkifyCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
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
import com.landkid.said.data.api.dribbble.DribbblePreferences;
import com.landkid.said.data.api.model.Comment;
import com.landkid.said.data.api.model.Like;
import com.landkid.said.data.api.model.Shot;
import com.landkid.said.util.HtmlUtils;
import com.landkid.said.util.interpolator.EaseOutElasticInterpolator;
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

    @BindView(R.id.sub_image_card) CardView imageCard;
    @BindView(R.id.sub_image) ImageView image;
    @BindView(R.id.fab_back) FloatingActionButton fabBack;
    @BindView(R.id.rv_comments) RecyclerView rvComments;
    @BindView(R.id.response_count) TextView responseCount;
    @BindView(R.id.scroll_area) LinearLayout scrollArea;

    @BindView(R.id.sub_scroll_view) NestedScrollView subScrollView;
    @BindView(R.id.iv_like) ImageView like;
    @BindView(R.id.iv_liked) ImageView liked;
    @BindView(R.id.fl_like_icon) FrameLayout likeIcon;

    private DribbblePreferences dribbblePreferences;
    private boolean performingLike = false;


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

                for(int color : colors){
                    if(color != -1){
                        int presentColor = color;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(presentColor + 0xcc000000);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            }

                        }
                        break;
                    }
                }
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

        dribbblePreferences = DribbblePreferences.get(getApplicationContext());

        Intent intent = getIntent();
        final Shot shot = intent.getParcelableExtra(FeedAdapter.KEY_SHOT);

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        scrollArea.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        scrollArea.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // inset the toolbar down by the status bar height
                ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) imageCard
                        .getLayoutParams();
                topMargin = insets.getSystemWindowInsetTop();
                lpToolbar.topMargin += insets.getSystemWindowInsetTop();
                lpToolbar.leftMargin += insets.getSystemWindowInsetLeft();
                lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
                imageCard.setLayoutParams(lpToolbar);

                // inset the fab for the navbar
                ViewGroup.MarginLayoutParams lpFab = (ViewGroup.MarginLayoutParams) fabBack
                        .getLayoutParams();
                lpFab.bottomMargin += insets.getSystemWindowInsetBottom(); // portrait
                lpFab.rightMargin += insets.getSystemWindowInsetRight(); // landscape
                fabBack.setLayoutParams(lpFab);

                scrollArea.setOnApplyWindowInsetsListener(null);

                return insets.consumeSystemWindowInsets();
            }
        });


        subScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

            boolean isAnimated;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                imageCard.setPivotX(imageCard.getMeasuredWidth() - 20 * getResources().getDisplayMetrics().density);
                imageCard.setPivotY(20 * getResources().getDisplayMetrics().density);
                if(!isAnimated) {
                    if (scrollY > image.getMeasuredHeight()) {

                        if(scrollY > oldScrollY) {

                            imageCard.animate()
                                    .scaleX(0.5f)
                                    .scaleY(0.5f)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            super.onAnimationStart(animation);
                                            isAnimated = true;

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            isAnimated = false;

                                        }
                                    })
                                    .setInterpolator(new DecelerateInterpolator())
                                    .setDuration(300)
                                    .start();
                        }

                    } else {

                        if(scrollY < oldScrollY) {

                            imageCard.animate()
                                    .scaleX(1)
                                    .scaleY(1)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            super.onAnimationStart(animation);
                                            isAnimated = true;

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            isAnimated = false;

                                        }
                                    })
                                    .setInterpolator(new DecelerateInterpolator())
                                    .setDuration(300)
                                    .start();
                        }
                    }
                }
                if(scrollY > 0){
                    imageCard.setCardElevation(8 * getResources().getDisplayMetrics().density);

                } else {
                    ObjectAnimator elevationAnimator = ObjectAnimator.ofFloat(imageCard, "cardElevation", 8 * getResources().getDisplayMetrics().density, 0);
                    elevationAnimator.setDuration(300).start();
                    //imageCard.setCardElevation(0);

                }
                imageCard.setTranslationY(scrollY);
            }
        });

        Call<Shot> shotCall = dribbblePreferences.getApiWithCache(getApplicationContext()).getShot(shot.id);
        shotCall.enqueue(new Callback<Shot>() {
            @Override
            public void onResponse(Call<Shot> call, Response<Shot> response) {
                Shot shot = response.body();
                bindShot(shot);
            }

            @Override
            public void onFailure(Call<Shot> call, Throwable t) {
                bindShot(shot);
            }
        });


    }

    private void bindShot(final Shot shot){
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

            HtmlUtils.setTextWithNiceLinks(description, HtmlUtils.parseHtml(shot.description,
                    ContextCompat.getColorStateList(getApplicationContext(), R.color.link_text_color),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorHeartFilled)));
//            description.setText(HtmlUtils.parseHtml(shot.description,
//                    ContextCompat.getColorStateList(getApplicationContext(), R.color.link_text_color),
//                    ContextCompat.getColor(getApplicationContext(), R.color.colorHeartFilled)));
//            LinkifyCompat.addLinks(description, Linkify.ALL);
            description.setLinkTextColor(linkTextColor);
        } else {
            description.setVisibility(View.GONE);
        }

        likeCount.setText(shot.likes_count + "");
        commentCount.setText(shot.comments_count + "");
        viewCount.setText(shot.views_count + "");

        designerName.setText(HtmlUtils.fromHtml(shot.user.name));
        location.setText(shot.user.location);

        if(shot.comments_count > 1) {
            responseCount.setText(shot.comments_count + " Responses");
        } else if(shot.comments_count == 1){
            responseCount.setText(shot.comments_count + " Response");
        } else if(shot.comments_count == 0){
            responseCount.setText("No Response");

        }

        setCommentsView();

        if(shot.comments_count > 0) {

            final Call<List<Comment>> commentsCall = dribbblePreferences.getApi().getComments(shot.id);
            commentsCall.enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                    if(response.body() != null) {
                        if (response.body().size() > 0) {
                            ((CommentsAdapter) rvComments.getAdapter()).setComments(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Comment>> call, Throwable t) {

                }
            });
        }

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

                            }
                        }).start();


                    }
                });

        liked.setScaleX(0);
        liked.setScaleY(0);

        final Call<Like> checkLikedCall = dribbblePreferences.getApi().checkLiked(shot.id);
        checkLikedCall.enqueue(new Callback<Like>() {
            @Override
            public void onResponse(Call<Like> call, Response<Like> response) {
                if(response.body() != null){
                    liked.setScaleX(1);
                    liked.setScaleY(1);
                } else {
                    liked.setScaleX(0);
                    liked.setScaleY(0);
                }
            }

            @Override
            public void onFailure(Call<Like> call, Throwable t) {

            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liked.getScaleY() == 0) {
                    doLike(shot, true);
                    liked.setPivotX(liked.getWidth() / 2f);
                    liked.setPivotY(liked.getHeight() / 1.2f);
                    liked.animate()
                            .scaleX(1)
                            .scaleY(1)
                            .setDuration(500)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();

                    likeIcon.animate()
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    likeIcon.animate()
                                            .translationY(0)
                                            .setInterpolator(new EaseOutElasticInterpolator())
                                            .setDuration(1000)
                                            .start();
                                }
                            })
                            .translationY(ResourceUtils.dpToPx(10, getApplicationContext()))
                            .setInterpolator(new DecelerateInterpolator())
                            .setDuration(500)
                            .start();

                } else {
                    doLike(shot, false);
                    liked.setPivotX(liked.getWidth() / 2f);
                    liked.setPivotY(liked.getHeight() / 1.2f);
                    liked.animate()
                            .scaleX(0)
                            .scaleY(0)
                            .setDuration(300)
                            .start();
                }
            }
        });
    }

    int topMargin;

    void setCommentsView(){
        rvComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        CommentsAdapter adapter = new CommentsAdapter(getApplicationContext());
        rvComments.setAdapter(adapter);
        rvComments.setNestedScrollingEnabled(false);
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
        DribbblePreferences dribbblePreferences;
        Context mContext;

        CommentsAdapter(Context context) {
            this.comments = new ArrayList<>();
            this.dribbblePreferences = DribbblePreferences.get(context);
            this.mContext = context;
        }

        public void setComments(List<Comment> comments){
            this.comments = comments;
            notifyDataSetChanged();
        }

        public void addShots(List<Comment> comments) {

            this.comments.addAll(comments);
            notifyDataSetChanged();
        }

        @Override
        public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CommentsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentsViewHolder holder, int position) {
            Comment comment = comments.get(position);
            holder.userName.setText(comment.user.name);

//            HtmlUtils.setTextWithNiceLinks(description, HtmlUtils.parseHtml(comment.body,
//                    ContextCompat.getColorStateList(getApplicationContext(), R.color.link_text_color),
//                    ContextCompat.getColor(getApplicationContext(), R.color.colorHeartFilled)));

            holder.comment.setText(Html.fromHtml(comment.body));

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

    void doLike(Shot shot, boolean liked) {
        performingLike = true;
        if (liked) {
            final Call<Like> likeCall = dribbblePreferences.getApi().like(shot.id);
            likeCall.enqueue(new Callback<Like>() {
                @Override
                public void onResponse(Call<Like> call, Response<Like> response) {
                    performingLike = false;
                    likeCount.setText((Integer.parseInt(likeCount.getText().toString()) + 1) + "");
                }

                @Override
                public void onFailure(Call<Like> call, Throwable t) {
                    performingLike = false;
                }
            });
        } else {
            final Call<Void> unlikeCall = dribbblePreferences.getApi().unlike(shot.id);
            unlikeCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    performingLike = false;
                    likeCount.setText((Integer.parseInt(likeCount.getText().toString()) - 1) + "");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    performingLike = false;
                }
            });
        }
    }
}
