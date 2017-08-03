package com.landkid.said.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.landkid.said.R;
import com.landkid.said.data.api.behance.BehancePreferences;
import com.landkid.said.data.api.dribbble.DribbblePreferences;
import com.landkid.said.data.api.model.behance.Comment;
import com.landkid.said.data.api.model.behance.Module;
import com.landkid.said.data.api.model.behance.Project;
import com.landkid.said.ui.widget.DragDismissLayout;
import com.landkid.said.util.HtmlUtils;
import com.landkid.said.util.ResourceUtils;
import com.landkid.said.util.ViewUtils;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

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

public class BehanceProjectActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_PROJECT_ID = "KEY_PROJECT_ID";

//    @BindView(R.id.tv_like_count) TextView likeCount;
//    @BindView(R.id.tv_comment_count) TextView commentCount;
//    @BindView(R.id.tv_view_count) TextView viewCount;

    @BindView(R.id.title) TextView title;
    @BindView(R.id.description) TextView description;

    @BindView(R.id.profile_photo) ImageView profilePhoto;
    @BindView(R.id.designer_name) TextView designerName;
    @BindView(R.id.location) TextView location;

    @BindView(R.id.fab_back) FloatingActionButton fabBack;
    @BindView(R.id.rv_comments) RecyclerView rvComments;
    @BindView(R.id.response_count) TextView responseCount;
    @BindView(R.id.scroll_area) LinearLayout scrollArea;

    @BindView(R.id.sub_scroll_view) NestedScrollView subScrollView;
    @BindView(R.id.modules) RecyclerView mRvModules;
    @BindView(R.id.color_palette_text) View mColorPaletteText;
    @BindView(R.id.cv_color_palette) View mCvColorPalette;
//    @BindView(R.id.iv_like) ImageView like;
//    @BindView(R.id.iv_liked) ImageView liked;
//    @BindView(R.id.fl_like_icon) FrameLayout likeIcon;
    @BindView(R.id.tags) TextView tags;
    @BindView(R.id.tags_title) TextView tagsTitle;
    @BindView(R.id.sub_area) LinearLayout mLlSubArea;

    @BindView(R.id.drag_dismiss_layout) DragDismissLayout dismissLayout;


    private BehancePreferences behancePreferences;
    private boolean performingLike = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    String linkColor;
    String backgroundColor;
    String stylesheet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-R.ttf"))
                .addBold(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-B.ttf"))
                .addBoldItalic(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-BI.ttf"))
                .addItalic(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-RI.ttf"));

        setContentView(R.layout.activity_behance_project);
        ButterKnife.bind(this);
        Fresco.initialize(this);
        mLlSubArea.setAlpha(0);
        behancePreferences = BehancePreferences.get(getApplicationContext());

        mColorPaletteText.setVisibility(View.GONE);
        mCvColorPalette.setVisibility(View.GONE);

        Intent intent = getIntent();
        final long projectId = intent.getLongExtra(KEY_PROJECT_ID, 0);

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
                // inset the toolbar down by the status bar heigh

                scrollArea.setPadding(0, scrollArea.getPaddingTop() + insets.getSystemWindowInsetTop(), 0, scrollArea.getPaddingBottom() + insets.getSystemWindowInsetBottom());
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

        setCommentsView();
        setModulesView();

        Call<Project> projectCall = behancePreferences.getApi().getProject(projectId);
        projectCall.enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {

                Project project = response.body().project;
                if(project != null) {

                    if(project.styles != null && project.styles.background != null){
                        if(project.styles.background.get("color") != null){
                            backgroundColor = project.styles.background.get("color");
                            mRvModules.setBackgroundColor(Color.parseColor("#" + project.styles.background.get("color")));
                        }
                        stylesheet = project.getStylesheetForHtml(getApplicationContext());
                    }

                    bindProject(project);
                    subScrollView.scrollTo(0, 0);
                    mLlSubArea.animate()
                            .alpha(1)
                            .setDuration(300)
                            .start();

                }

            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                //bindProject(shot);
            }
        });

        dismissLayout.setOnDragDismissListener(new DragDismissLayout.OnDragDismissListener() {
            @Override
            public void onDismiss() {
                onBackPressed();
            }
        });


    }

    private void bindProject(final Project project) {

        if(project.styles != null && project.styles.text != null && project.styles.text.get("link") != null)
        linkColor = project.styles.text.get("link").get("color");
        title.setText(project.name);

        if (project.description != null && !project.description.equals("")) {
            description.setVisibility(View.VISIBLE);
            HtmlUtils.setTextWithLinks(description, HtmlUtils.parseHtml(project.description,
                    ContextCompat.getColorStateList(getApplicationContext(), R.color.link_text_color),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorHeartFilled)));

        } else {
            description.setVisibility(View.GONE);
        }

        ((ModulesAdapter) mRvModules.getAdapter()).setModules(project.modules);

        //TODO
        designerName.setText(HtmlUtils.fromHtml(project.owners.get(0).first_name + " " + project.owners.get(0).last_name));
        location.setText(project.owners.get(0).location);

        if(project.tags != null && project.tags.size() > 0) {
            tags.setVisibility(View.VISIBLE);
            tagsTitle.setVisibility(View.VISIBLE);

            tags.setText(project.getTags());

        } else {
            tags.setVisibility(View.GONE);
            tagsTitle.setVisibility(View.GONE);
        }


        //setCommentsView();

        //TODO
        if (project.stats.comments > 0) {

            final Call<Comment> commentsCall = behancePreferences.getApi().getComments(project.id);
            commentsCall.enqueue(new Callback<Comment>() {
                @Override
                public void onResponse(Call<Comment> call, Response<Comment> response) {

                    List<Comment> comments = response.body().comments;

                    int commentCount = comments != null ? comments.size() : 0;
                    if (commentCount > 0)
                        ((CommentsAdapter) rvComments.getAdapter()).setComments(comments);

                    if (commentCount > 1)
                        responseCount.setText(String.format(getString(R.string.response_count_postfix), commentCount));
                    else if (commentCount == 1)
                        responseCount.setText(String.format(getString(R.string.response_count_postfix), commentCount));
                    else if (commentCount == 0)
                        responseCount.setText(getString(R.string.no_response));
                }

                @Override
                public void onFailure(Call<Comment> call, Throwable t) {

                }
            });
        }

        Glide.with(getApplicationContext())
                .load(project.owners.get(0).images.get("115"))
                .into(profilePhoto);
    }

    void setCommentsView(){
        rvComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        CommentsAdapter adapter = new CommentsAdapter(getApplicationContext());
        rvComments.setAdapter(adapter);
        rvComments.setNestedScrollingEnabled(false);
    }

    void setModulesView(){
        mRvModules.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ModulesAdapter adapter = new ModulesAdapter(getApplicationContext());
        mRvModules.setAdapter(adapter);
        mRvModules.setNestedScrollingEnabled(false);
    }


    @Override
    public void onClick(View view) {

    }

    private class CommentsAdapter extends RecyclerView.Adapter<CommentsViewHolder>{

        List<Comment> comments;
        Context mContext;

        CommentsAdapter(Context context) {
            this.comments = new ArrayList<>();
            this.mContext = context;
        }

        private void setComments(List<Comment> comments){
            this.comments = comments;
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
            holder.userName.setText(comment.user.first_name + " " + comment.user.last_name);

            HtmlUtils.setTextWithLinks(holder.comment, HtmlUtils.parseHtml(comment.comment,
                    ContextCompat.getColorStateList(getApplicationContext(), R.color.link_text_color),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorHeartFilled)));

            Glide.with(getApplicationContext())
                    .load(comment.user.images.get("115"))
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

    private class ModulesAdapter extends RecyclerView.Adapter<ModuleViewHolder>{

        List<Module> modules;
        DribbblePreferences dribbblePreferences;
        Context mContext;

        ModulesAdapter(Context context) {
            this.modules = new ArrayList<>();
            this.dribbblePreferences = DribbblePreferences.get(context);
            this.mContext = context;
        }

        private void setModules(List<Module> modules){
            this.modules = modules;
            notifyDataSetChanged();
        }

        public void addModules(List<Module> comments) {
            this.modules.addAll(comments);
            notifyDataSetChanged();
        }

        @Override
        public ModuleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_behance_project_module, parent, false);
            return new ModuleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ModuleViewHolder holder, int position) {
            Module module = modules.get(position);

            if(module.type.equals("image")){
//                holder.image.setVisibility(View.VISIBLE);
//                holder.text.setVisibility(View.GONE);

                int width = ViewUtils.getScreenWidth(getApplicationContext());
                int height = (int) (width * module.height / (module.width * 1.0f));

                holder.image.getLayoutParams().width = width;
                holder.image.getLayoutParams().height = height;
                holder.image.requestLayout();

//                Picasso.with(getApplicationContext())
//                        .load(module.src)
//                        .resize(width, height)
//                        .into(holder.image);

                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(module.src))
                        .setRequestPriority(Priority.HIGH)
                        .setProgressiveRenderingEnabled(true)
                        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                        .build();

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setAutoPlayAnimations(true)
                        .build();
                holder.image.setController(controller);

//                Glide.with(getApplicationContext())
//                        .load(module.sizes.get("max_1200"))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .override(width, height)
//                        .into(new GlideDrawableImageViewTarget(holder.image));

//                WebView webView = new WebView(getApplicationContext());
//                ((ViewGroup) holder.itemView).addView(webView);
//                webView.setBackgroundColor(Color.parseColor("#" + backgroundColor));
//                webView.setWebChromeClient(new WebChromeClient());
//                webView.getSettings().setAllowFileAccess(true);
//                webView.setWebViewClient(new WebViewClient());
//                webView.getSettings().setLoadWithOverviewMode(true);
//                webView.getSettings().setUseWideViewPort(true);
//                webView.getSettings().setTextZoom(200);
//
//                webView.loadUrl(module.src);
//
//                webView.setWebViewClient(new WebViewClient() {
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
//                        browserIntent.setData(request.getUrl());
//                        startActivity(browserIntent);
//                        return true;
//                    }
//                });
//
//                holder.image.setVisibility(View.GONE);
//                holder.text.setVisibility(View.GONE);
            } else if(module.type.equals("text")){
//                holder.image.setVisibility(View.GONE);
//                holder.text.setVisibility(View.VISIBLE);

//                Elements shotElements =
//                        Jsoup.parse(holder.text)

//                ColorStateList linkColorStateList = makeLinkColorStateList(ResourcesCompat.getColor(getResources(), android.R.color.white, getTheme()), Color.parseColor(linkColor));
//
//                HtmlUtils.setTextWithLinks(holder.text, HtmlUtils.parseHtml(module.text,
//                        linkColorStateList,
//                        Color.parseColor(linkColor)));

//                HtmlUtils.setTextWithLinks(holder.text, HtmlUtils.parseHtml(module.text,
//                        ContextCompat.getColorStateList(getApplicationContext(), R.color.link_text_color),
//                        ContextCompat.getColor(getApplicationContext(), R.color.colorHeartFilled)));

                WebView webView = new WebView(getApplicationContext());
                holder.itemView.setPadding((int) ResourceUtils.dpToPx(15, getApplicationContext()),
                        (int) ResourceUtils.dpToPx(10, getApplicationContext()),
                        (int) ResourceUtils.dpToPx(15, getApplicationContext()),
                        (int) ResourceUtils.dpToPx(10, getApplicationContext()));

                ((ViewGroup) holder.itemView).addView(webView);
                webView.setBackgroundColor(Color.parseColor("#" + backgroundColor));
                webView.setWebChromeClient(new WebChromeClient());
                webView.getSettings().setAllowFileAccess(true);
                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.getSettings().setTextZoom(200);

                webView.loadData(stylesheet + module.getParsedText(backgroundColor), "text/html", "UTF-8");

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(request.getUrl());
                        startActivity(browserIntent);
                        return true;
                    }
                });

                holder.image.setVisibility(View.GONE);
                holder.text.setVisibility(View.GONE);

            } else if(module.type.equals("embed")){

                WebView webView = new WebView(getApplicationContext());
                ((ViewGroup) holder.itemView).addView(webView);

                webView.setInitialScale(1);
                webView.setWebChromeClient(new WebChromeClient());
                webView.getSettings().setAllowFileAccess(true);
                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);

                webView.loadData(module.embed, "text/html", "UTF-8");

                holder.image.setVisibility(View.GONE);
                holder.text.setVisibility(View.GONE);
            } else {
                holder.image.setVisibility(View.GONE);
                holder.text.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return modules.size();
        }
    }

    class ModuleViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.image) SimpleDraweeView image;
        @BindView(R.id.text) TextView text;

        public ModuleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    ColorStateList makeLinkColorStateList(int pressedColor, int defaultColor){
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{}
                },
                new int[]{
                        pressedColor,
                        defaultColor
                }
        );
    }
}
