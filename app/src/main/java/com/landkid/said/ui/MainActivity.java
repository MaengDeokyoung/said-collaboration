package com.landkid.said.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.landkid.said.R;
import com.landkid.said.data.api.Router;
import com.landkid.said.data.api.dribbble.DribbblePreferences;
import com.landkid.said.data.api.model.SaidItem;
import com.landkid.said.ui.widget.CollapsingBarLayout;
import com.landkid.said.ui.widget.GooeyFloatingActionButton;
import com.landkid.said.util.ViewUtils;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnSearchCompleteListener, NavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.list) RecyclerView mRvFeeds;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.pb_loading) ProgressBar mPbLoading;
    @BindView(R.id.bt_login) Button mBtLogin;
    @BindView(R.id.fab_search) GooeyFloatingActionButton mFabSearch;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsing_bar_layout) CollapsingBarLayout mCollapsingBarLayout;
    @BindView(R.id.nav_view) NavigationView navView;

    private Router router;

    static Handler transitionHandler;

    @Override
    public void onFragmentInteraction() {
        mFragments.remove(0);
    }

    void setActionBarAndDrawer() {
        setSupportActionBar(toolbar);
        //toolbar.setPadding((int) (10 * getResources().getDisplayMetrics().density), getStatusBarHeight(), 0, 0);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_logo);
//            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

    }

    public List<Fragment> mFragments;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            if(mFragments.size() > 0){
                ((SearchFragment) mFragments.get(0)).completeSearch();
                mFragments.remove(0);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the ic_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                if(!router.mode.equals(Router.MODE_POPULAR)) {
                    router.loadPopular();
                }
                mRvFeeds.smoothScrollToPosition(0);
                break;
            case R.id.action_filter:
                //drawer.openDrawer(GravityCompat.END);
                break;

            case R.id.behance_projects:
                router.loadProjects();
                break;
            case R.id.dribbble_popular:
                router.loadPopular();
                break;
            case R.id.action_login:
                Intent login = new Intent(MainActivity.this, DribbbleLogin.class);
                startActivity(login);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    void showProgress(int gravity){
        ((FrameLayout.LayoutParams) mPbLoading.getLayoutParams()).gravity = gravity;
        mPbLoading.setVisibility(View.VISIBLE);
        mPbLoading.requestLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-R.ttf"))
                .addBold(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-B.ttf"))
                .addBoldItalic(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-BI.ttf"))
                .addItalic(Typekit.createFromAsset(this, "fonts/ubuntu/Ubuntu-RI.ttf"));

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Fresco.initialize(this);
        setActionBarAndDrawer();


//        final View homeBtn = toolbar.getChildAt(0);
//
//        homeBtn.setTranslationX(- ResourceUtils.dpToPx(100f, getApplicationContext()));
//        homeBtn.setRotation(-360);
//
//        //TODO make home button animation
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//
//                homeBtn.animate()
//                        .translationX(0)
//                        .rotation(0)
//                        .setInterpolator(new DecelerateInterpolator(2.0f))
//                        .setDuration(300)
//                        .start();
//            }
//        }, 1000);

        drawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        drawer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // inset the toolbar down by the status bar height
                ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) toolbar
                        .getLayoutParams();
                lpToolbar.topMargin += insets.getSystemWindowInsetTop();
                lpToolbar.leftMargin += insets.getSystemWindowInsetLeft();
                lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
                toolbar.setLayoutParams(lpToolbar);
//                toolbar.setPadding(
//                        0,
//                        (int) ViewUtils.dp(10, getApplicationContext()),
//                        (int) ViewUtils.dp(15, getApplicationContext()),
//                        (int) ViewUtils.dp(10, getApplicationContext()));

//                ViewGroup.MarginLayoutParams homeBtn = (ViewGroup.MarginLayoutParams) toolbar.getChildAt(0).getLayoutParams();
//                homeBtn.leftMargin += ViewUtils.dp(15, getApplicationContext());


                // inset the fab for the navbar
                ViewGroup.MarginLayoutParams lpFab = (ViewGroup.MarginLayoutParams) mFabSearch
                        .getLayoutParams();
                lpFab.bottomMargin += insets.getSystemWindowInsetBottom(); // portrait
                lpFab.rightMargin += insets.getSystemWindowInsetRight(); // landscape
                mFabSearch.setLayoutParams(lpFab);

                navView.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);

                // set for being not applied again.
                drawer.setOnApplyWindowInsetsListener(null);

                return insets.consumeSystemWindowInsets();
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        FeedAdapter mFeedAdapter = new FeedAdapter<>(this);
        mRvFeeds.setLayoutManager(llm);
        mRvFeeds.setAdapter(mFeedAdapter);

       //setPopularDataManager();

        mRvFeeds.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                FeedAdapter.FeedViewHolder holder = (FeedAdapter.FeedViewHolder) parent.getChildViewHolder(view);
                int position = holder.getAdapterPosition();

                outRect.top = (int) (20 * getResources().getDisplayMetrics().density);

                if(position == parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = (int) (150 * getResources().getDisplayMetrics().density);

                }
            }
        });

        mRvFeeds.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int scrollY = recyclerView.computeVerticalScrollOffset();
                if(scrollY > 0){
                    mCollapsingBarLayout.setCardElevation(ViewUtils.dp(4, getApplicationContext()));
                } else {
                    mCollapsingBarLayout.setCardElevation(ViewUtils.dp(0, getApplicationContext()));
                }
            }
        });

        router = new Router(getApplicationContext(), mRvFeeds) {

            @Override
            public void onStart(@Mode String mode) {
                showProgress(Gravity.CENTER);
            }

            @Override
            public void onDataLoaded(@Router.Mode String mode, List<? extends SaidItem> data) {
                mPbLoading.setVisibility(View.GONE);
            }
        };

        dribbblePreferences = DribbblePreferences.get(getApplicationContext());

        mBtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(MainActivity.this, DribbbleLogin.class);
                startActivity(login);
            }
        });

        mFabSearch.setOnOptionItemClickListener(new GooeyFloatingActionButton.OnOptionItemClickListener() {
            @Override
            public boolean onItemClick(View view, int itemId) {

                switch (itemId){
                    case R.id.text_search:

                        int cx = (int) (view.getX() + view.getWidth() / 2 + mFabSearch.getX());
                        int cy = (int) (view.getY() + view.getHeight() / 2 + mFabSearch.getY());

                        SearchFragment searchFragment = SearchFragment.newInstance("TEXT", cx, cy);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.search_fragment, searchFragment)
                                .commit();

                        mFragments.add(searchFragment);

                        return true;

                    case R.id.palette_search:

                        return true;
                }
                return false;
            }
        });

        mFragments = new ArrayList<>();

        router.loadPopular();

        navView.setNavigationItemSelectedListener(this);

        transitionHandler = new TransitionHandler(this, mFabSearch, router);
        //router.loadProjects();

    }
    DribbblePreferences dribbblePreferences;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static final int TO_DRIBBLE_SHOT_ACTIVITY = 1 << 2;
    public static final int TO_BEHANCE_PROJECT_ACTIVITY = 1 << 4;
    public static final int TO_SEARCH_RESULT = 1 << 3;

    private static class TransitionHandler extends Handler {

        Context mContext;
        GooeyFloatingActionButton mFabSearch;
        Router mRouter;

        TransitionHandler(Context context, GooeyFloatingActionButton fabSearch, Router router){
            this.mContext = context;
            this.mFabSearch = fabSearch;
            this.mRouter = router;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data;
            Intent intent;
            ActivityOptions options;
            switch (msg.what){
                case TO_DRIBBLE_SHOT_ACTIVITY:
                    data = msg.getData();

                    intent = new Intent(mContext, DribbbleShotActivity.class);
                    intent.putExtra(FeedAdapter.KEY_SHOT, data.getParcelable(FeedAdapter.KEY_SHOT));
                    intent.putExtra(mContext.getString(R.string.swatch_colors_key), data.getIntArray(mContext.getString(R.string.swatch_colors_key)));

                    mFabSearch.getParentButton().setTransitionName(mContext.getString(R.string.feed_detail));

                    options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, mFabSearch.getParentButton(),
                            mContext.getString(R.string.feed_detail));
                    ActivityCompat.startActivity(mContext, intent, options.toBundle());
                    break;
                case TO_BEHANCE_PROJECT_ACTIVITY:
                    data = msg.getData();

                    intent = new Intent(mContext, BehanceProjectActivity.class);
                    intent.putExtra(BehanceProjectActivity.KEY_PROJECT_ID, data.getLong(BehanceProjectActivity.KEY_PROJECT_ID));

                    mFabSearch.getParentButton().setTransitionName(mContext.getString(R.string.feed_detail));

                    options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, mFabSearch.getParentButton(),
                            mContext.getString(R.string.feed_detail));
                    ActivityCompat.startActivity(mContext, intent, options.toBundle());
                    break;
                case TO_SEARCH_RESULT:
                    data = msg.getData();
                    mRouter.search(data.getString(SearchFragment.SEARCH_KEYWORD));

                    break;
            }
        }
    }


    /*@SuppressLint("handlerLeak")
    final static Handler transitionHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data;
            Intent intent;
            ActivityOptions options;
            switch (msg.what){
                case TO_DRIBBLE_SHOT_ACTIVITY:
                    data = msg.getData();

                    intent = new Intent(getApplicationContext(), DribbbleShotActivity.class);
                    intent.putExtra(FeedAdapter.KEY_SHOT, data.getParcelable(FeedAdapter.KEY_SHOT));
                    intent.putExtra(getString(R.string.swatch_colors_key), data.getIntArray(getString(R.string.swatch_colors_key)));

                    mFabSearch.getParentButton().setTransitionName(getString(R.string.feed_detail));

                    options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, mFabSearch.getParentButton(),
                            getString(R.string.feed_detail));
                    ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
                    break;
                case TO_BEHANCE_PROJECT_ACTIVITY:
                    data = msg.getData();

                    intent = new Intent(getApplicationContext(), BehanceProjectActivity.class);
                    intent.putExtra(BehanceProjectActivity.KEY_PROJECT_ID, data.getLong(BehanceProjectActivity.KEY_PROJECT_ID));

                    mFabSearch.getParentButton().setTransitionName(getString(R.string.feed_detail));

                    options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, mFabSearch.getParentButton(),
                            getString(R.string.feed_detail));
                    ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
                    break;
                case TO_SEARCH_RESULT:
                    data = msg.getData();
                    router.search(data.getString(SearchFragment.SEARCH_KEYWORD));

                    break;
            }
        }
    };*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.dribbble_popular:
                router.loadPopular();
                break;
            case R.id.behance_projects:
                router.loadProjects();
                break;
        }

        drawer.closeDrawer(Gravity.END);
        return true;
    }
}
