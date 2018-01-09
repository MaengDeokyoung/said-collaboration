package com.landkid.said.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.landkid.said.R;
import com.landkid.said.data.api.dribbble.DribbblePreferences;
import com.landkid.said.data.api.model.dribbble.Comment;
import com.landkid.said.data.api.model.dribbble.Like;
import com.landkid.said.data.api.model.dribbble.Shot;
import com.landkid.said.ui.listener.SubScrollListener;
import com.landkid.said.ui.widget.DragDismissLayout;
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

public class DribbbleShotActivity extends AppCompatActivity implements View.OnClickListener {

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
    @BindView(R.id.sub_image) SimpleDraweeView image;
    @BindView(R.id.fab_back) FloatingActionButton fabBack;
    @BindView(R.id.rv_comments) RecyclerView rvComments;
    @BindView(R.id.response_count) TextView responseCount;
    @BindView(R.id.scroll_area) LinearLayout scrollArea;

    @BindView(R.id.sub_scroll_view) NestedScrollView subScrollView;
    @BindView(R.id.iv_like) ImageView like;
    @BindView(R.id.iv_liked) ImageView liked;
    @BindView(R.id.fl_like_icon) FrameLayout likeIcon;
    @BindView(R.id.tags) TextView tags;
    @BindView(R.id.tags_title) TextView tagsTitle;

    @BindView(R.id.drag_dismiss_layout) DragDismissLayout dismissLayout;

    private DribbblePreferences dribbblePreferences;
    private boolean performingLike = false;

    @SuppressLint("HandlerLeak")
    final Handler colorHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.getData().containsKey(getString(R.string.swatch_colors_key))) {
                int [] colors = msg.getData().getIntArray(getString(R.string.swatch_colors_key));
                if(colors != null) {
                    setSwatchColor(vibrantSwatch, colors[0]);
                    setSwatchColor(vibrantLightSwatch, colors[1]);
                    setSwatchColor(vibrantDarkSwatch, colors[2]);
                    setSwatchColor(mutedSwatch, colors[3]);
                    setSwatchColor(mutedLightSwatch, colors[4]);
                    setSwatchColor(mutedDarkSwatch, colors[5]);

                    for (int color : colors) {
                        if (color != -1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Window window = getWindow();
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.setStatusBarColor(color + 0xcc000000);
                                image.setBackgroundColor(color);
                            }
                            break;
                        }
                    }

                    colorPalette.setVisibility(View.VISIBLE);
                    colorPalette.setAlpha(0);
                    colorPalette.animate()
                            .alpha(1)
                            .setInterpolator(new AccelerateInterpolator())
                            .setDuration(200)
                            .setStartDelay(100)
                            .start();
                }
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

        setContentView(R.layout.activity_dribbble_shot);
        ButterKnife.bind(this);
        Fresco.initialize(this);

        dribbblePreferences = DribbblePreferences.get(getApplicationContext());

        Intent intent = getIntent();
        final Shot shot = intent.getParcelableExtra(FeedAdapter.KEY_SHOT);

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putIntArray(getString(R.string.swatch_colors_key), intent.getIntArrayExtra(getString(R.string.swatch_colors_key)));
        msg.setData(bundle);
        colorHandler.sendMessage(msg);

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
                ViewGroup.MarginLayoutParams lpImageCard = (ViewGroup.MarginLayoutParams) imageCard
                        .getLayoutParams();
                topMargin = insets.getSystemWindowInsetTop();
                lpImageCard.topMargin += insets.getSystemWindowInsetTop();
                lpImageCard.leftMargin += insets.getSystemWindowInsetLeft();
                lpImageCard.rightMargin += insets.getSystemWindowInsetRight();
                imageCard.setLayoutParams(lpImageCard);

                scrollArea.setPadding(0, 0, 0, scrollArea.getPaddingBottom() + insets.getSystemWindowInsetBottom());
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


        subScrollView.setOnScrollChangeListener(new SubScrollListener(getApplicationContext(), imageCard));

        Call<Shot> shotCall = dribbblePreferences.getApi().getShot(shot.id);
        shotCall.enqueue(new Callback<Shot>() {
            @Override
            public void onResponse(Call<Shot> call, Response<Shot> response) {
                Shot shot = response.body();
                if(shot != null) {
                    ValueAnimator likeCountAnimator = ValueAnimator.ofInt(0, (int) shot.likes_count);
                    likeCountAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @SuppressLint("StringFormatMatches")
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            likeCount.setText(String.format(getString(R.string.number), valueAnimator.getAnimatedValue()));
                        }
                    });
                    likeCountAnimator.setDuration(300);
                    likeCountAnimator.start();

                    ValueAnimator commentCountAnimator = ValueAnimator.ofInt(0, (int) shot.comments_count);
                    commentCountAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @SuppressLint("StringFormatMatches")
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            commentCount.setText(String.format(getString(R.string.number), valueAnimator.getAnimatedValue()));
                        }
                    });
                    commentCountAnimator.setStartDelay(300);
                    commentCountAnimator.setDuration(300);
                    commentCountAnimator.start();

                    ValueAnimator viewCountAnimator = ValueAnimator.ofInt(0, (int) shot.views_count);
                    viewCountAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @SuppressLint("StringFormatMatches")
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            viewCount.setText(String.format(getString(R.string.number), valueAnimator.getAnimatedValue()));
                        }
                    });
                    viewCountAnimator.setStartDelay(600);
                    viewCountAnimator.setDuration(300);
                    viewCountAnimator.start();

                    //likeCount.setText(shot.likes_count + "");
                    //commentCount.setText(shot.comments_count + "");
                    //viewCount.setText(shot.views_count + "");
                }

            }

            @Override
            public void onFailure(Call<Shot> call, Throwable t) {
                //bindShot(shot);
            }
        });

        bindShot(shot);

        dismissLayout.setOnDragDismissListener(new DragDismissLayout.OnDragDismissListener() {
            @Override
            public void onDismiss() {
                onBackPressed();
            }
        });

    }

    private void bindShot(final Shot shot){
        title.setText(shot.title);

        if(shot.description != null) {
            description.setVisibility(View.VISIBLE);
            HtmlUtils.setTextWithLinks(description, HtmlUtils.parseHtml(shot.description,
                    ContextCompat.getColorStateList(getApplicationContext(), R.color.link_text_color),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorHeartFilled)));

        } else {
            description.setVisibility(View.GONE);
        }

        if(shot.tags != null && shot.tags.size() > 0) {
            tags.setVisibility(View.VISIBLE);
            tagsTitle.setVisibility(View.VISIBLE);

            tags.setText(shot.getParsedTags());

            HtmlUtils.setTextWithLinks(tags,
                    HtmlUtils.parseHtml(shot.getParsedTags(),
                    ContextCompat.getColorStateList(getApplicationContext(), R.color.tag_text_color),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
        } else {
            tags.setVisibility(View.GONE);
            tagsTitle.setVisibility(View.GONE);
        }

        designerName.setText(HtmlUtils.fromHtml(shot.user.name));
        location.setText(shot.user.location);

        setCommentsView();

        if(shot.comments_count > 0) {

            final Call<List<Comment>> commentsCall = dribbblePreferences.getApi().getComments(shot.id);
            commentsCall.enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                    int commentCount = response.body() != null ? response.body().size() : 0;
                    if (commentCount > 0)
                        ((CommentsAdapter) rvComments.getAdapter()).setComments(response.body());

                    if(commentCount > 1)
                        responseCount.setText(String.format(getString(R.string.response_count_postfix), commentCount));
                     else if(shot.comments_count == 1)
                        responseCount.setText(String.format(getString(R.string.response_count_postfix), commentCount));
                     else if(commentCount == 0)
                        responseCount.setText(getString(R.string.no_response));
                }

                @Override
                public void onFailure(Call<List<Comment>> call, Throwable t) {

                }
            });
        }

        Glide.with(getApplicationContext())
                .load(shot.user.avatar_url)
                .into(profilePhoto);

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(shot.images.best()))
                .setRequestPriority(Priority.HIGH)
                .setProgressiveRenderingEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setAutoPlayAnimations(true)
                .build();

        image.setController(controller);

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
                animateHeartFill(shot);
            }
        });
    }

    int topMargin;

    void animateHeartFill(Shot shot){
        if(!performingLike) {
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
    }

    void setCommentsView(){
        rvComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        CommentsAdapter adapter = new CommentsAdapter(getApplicationContext());
        rvComments.setAdapter(adapter);
        rvComments.setNestedScrollingEnabled(false);
    }

    void setSwatchColor(CardView view, final int swatch){
        if(swatch != -1) {
            view.setCardBackgroundColor(swatch);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String strColor = String.format("%06X", 0xFFFFFF & swatch);
                    String url = "https://dribbble.com/colors/" + strColor;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

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

        private void setComments(List<Comment> comments){
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

            HtmlUtils.setTextWithLinks(holder.comment, HtmlUtils.parseHtml(comment.body,
                    ContextCompat.getColorStateList(getApplicationContext(), R.color.link_text_color),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorHeartFilled)));

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
                    int currentCount = Integer.parseInt(likeCount.getText().toString());
                    likeCount.setText(String.format(getString(R.string.number), currentCount + 1));
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
                    int currentCount = Integer.parseInt(likeCount.getText().toString());
                    likeCount.setText(String.format(getString(R.string.number), currentCount - 1));
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    performingLike = false;
                }
            });
        }
    }
}
