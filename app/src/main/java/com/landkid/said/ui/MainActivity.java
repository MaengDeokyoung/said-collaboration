package com.landkid.said.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringDef;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.landkid.said.R;
import com.landkid.said.data.api.BaseDataManager;
import com.landkid.said.data.api.ShotDataManager;
import com.landkid.said.data.api.SearchDataManager;
import com.landkid.said.data.api.dribbble.DribbblePreferences;
import com.landkid.said.data.api.model.Shot;
import com.landkid.said.ui.listener.InfiniteScrollListener;
import com.landkid.said.ui.listener.ParallaxScrollListener;
import com.landkid.said.ui.widget.CollapsingBarLayout;
import com.landkid.said.ui.widget.GooeyFloatingActionButton;
import com.landkid.said.util.ResourceUtils;
import com.landkid.said.util.ViewUtils;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnSearchCompleteListener{


    private static final String MODE_POPULAR = "MODE_POPULAR";
    private static final String MODE_SEARCH = "MODE_SEARCH";

    public static final String POPULAR_SHOTS_HEADER = "Popular Shots";
    public static final String SEARCH_HEADER_PREFIX = "Searched By: ";

    @Mode String mode = MODE_POPULAR;

    @BindView(R.id.list) RecyclerView mRvFeeds;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.et_search) EditText mEtSearch;
    @BindView(R.id.iv_search) ImageView mIvSearch;
    @BindView(R.id.pb_loading) ProgressBar mPbLoading;
    @BindView(R.id.bt_login) Button mBtLogin;
    @BindView(R.id.fab_search) GooeyFloatingActionButton mFabSearch;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsing_bar_layout) CollapsingBarLayout mCollapsingBarLayout;

    @Override
    public void onFragmentInteraction() {
        mFragments.remove(0);
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            MODE_POPULAR,
            MODE_SEARCH
    })
    @interface Mode{}

    boolean isModeChanging = false;

    private FeedAdapter mFeedAdapter;

    private ShotDataManager shotDataManager;
    private SearchDataManager searchDataManager;

    InfiniteScrollListener infiniteScrollListener;

    void resetDataManager(String headerTitle){
        //mFeedAdapter.setShots(new ArrayList<Shot>());
        isModeChanging = true;
        loadStarted(headerTitle);
        if(headerTitle.equals(POPULAR_SHOTS_HEADER)){
            shotDataManager.resetNextPageIndexes();
            infiniteScrollListener.setDataManager(shotDataManager);
        } else if(headerTitle.startsWith(SEARCH_HEADER_PREFIX)){
            shotDataManager.resetNextPageIndexes();
            infiniteScrollListener.setDataManager(searchDataManager);
        }

        //if(searchDataManager != null)
    }

    private void loadStarted(String headerTitle) {
        BaseDataManager.loadCancel();
        Shot shot = new Shot(headerTitle, true);
        List<Shot> initShots = new ArrayList<>();
        initShots.add(0, shot);
        mFeedAdapter.setShots(initShots);
        showProgress(Gravity.CENTER);

    }

    String mQuerySearched;
    String mPreviousQuerySearched;

    public void onSearchButtonClick(String keyword){
        //mTvSearchKeyword.setText(Html.fromHtml("Search by <a href=\"\"'>" + mQuerySearched + "</href>"));
        mQuerySearched = keyword;
        searchDataManager = new SearchDataManager(getApplicationContext()) {
            @Override
            public void onDataLoaded(List<Shot> items) {

                List<Shot> shots = new ArrayList<>();
                for(Shot shot : items){
                    shots.add(shot);
                }
                if(mode != MODE_SEARCH ||
                        (mode == MODE_SEARCH && mPreviousQuerySearched != mQuerySearched)) {
                    mFeedAdapter.addShots(shots);
                    mPreviousQuerySearched = mQuerySearched;
                    mode = MODE_SEARCH;
                    mRvFeeds.scrollBy(0, - mRvFeeds.computeVerticalScrollOffset());
                    isModeChanging = false;
                }
                else {
                    mFeedAdapter.addShots(shots);
                }
                mPbLoading.setVisibility(View.GONE);
            }
        };
        resetDataManager(SEARCH_HEADER_PREFIX + keyword);
        searchDataManager.createDribbbleSearchApi();
        searchDataManager.search(mQuerySearched);
        drawer.closeDrawer(GravityCompat.END);

        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(mEtSearch.getWindowToken(), 0);
    }

    void setSearchDataManager(){
        mEtSearch.setLines(1);
        mEtSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //onSearchButtonClick();
                return false;
            }
        });
        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onSearchButtonClick();
            }

        });
    }



    void setActionBarAndDrawer() {
        setSupportActionBar(toolbar);
        //toolbar.setPadding((int) (10 * getResources().getDisplayMetrics().density), getStatusBarHeight(), 0, 0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_logo);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("");

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
        getMenuInflater().inflate(R.menu.main, menu);

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
                if(mode != MODE_POPULAR) {
                    resetDataManager(POPULAR_SHOTS_HEADER);
                    shotDataManager.loadPopular();
                    //mTvSearchKeyword.setText(ResourceUtils.getString(R.string.popular, getApplicationContext()));
                }
                mRvFeeds.smoothScrollToPosition(0);
                break;
            case R.id.action_filter:
                drawer.openDrawer(GravityCompat.END);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    void setPopularDataManager(){
        shotDataManager = new ShotDataManager(getApplicationContext()) {

            @Override
            public void onDataLoaded(List<Shot> items) {

                if(mode != MODE_POPULAR) {
                    mode = MODE_POPULAR;
                    mFeedAdapter.addShots(items);
                    isModeChanging = false;

                } else {
                    mFeedAdapter.addShots(items);
                }
                mPbLoading.setVisibility(View.GONE);

            }
        };
        loadStarted(POPULAR_SHOTS_HEADER);
        shotDataManager.createApi();
        shotDataManager.loadPopular();

        infiniteScrollListener = new InfiniteScrollListener() {
            @Override
            public void onLoadMore() {
                if(!isModeChanging) {
                    showProgress(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                    switch (mode) {
                        case MODE_POPULAR:
                            shotDataManager.loadPopular();
                            break;
                        case MODE_SEARCH:
                            searchDataManager.search(mQuerySearched);
                            break;
                    }
                }
            }
        };
        infiniteScrollListener.setDataManager(shotDataManager);
        mRvFeeds.addOnScrollListener(infiniteScrollListener);
        mRvFeeds.addOnScrollListener(new ParallaxScrollListener());
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
        setActionBarAndDrawer();


        final View homeBtn = toolbar.getChildAt(0);

        homeBtn.setTranslationX(- ResourceUtils.dpToPx(100f, getApplicationContext()));
        homeBtn.setRotation(-360);

        //TODO make home button animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                homeBtn.animate()
                        .translationX(0)
                        .rotation(0)
                        .setInterpolator(new DecelerateInterpolator(2.0f))
                        .setDuration(300)
                        .start();
            }
        }, 1000);

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
                toolbar.setPadding(
                        0,
                        (int) ViewUtils.dp(10, getApplicationContext()),
                        (int) ViewUtils.dp(15, getApplicationContext()),
                        (int) ViewUtils.dp(10, getApplicationContext()));

                ViewGroup.MarginLayoutParams homeBtn = (ViewGroup.MarginLayoutParams) toolbar.getChildAt(0).getLayoutParams();
                homeBtn.leftMargin += ViewUtils.dp(15, getApplicationContext());


                // inset the fab for the navbar
                ViewGroup.MarginLayoutParams lpFab = (ViewGroup.MarginLayoutParams) mFabSearch
                        .getLayoutParams();
                lpFab.bottomMargin += insets.getSystemWindowInsetBottom(); // portrait
                lpFab.rightMargin += insets.getSystemWindowInsetRight(); // landscape
                mFabSearch.setLayoutParams(lpFab);

                // set for being not applied again.
                drawer.setOnApplyWindowInsetsListener(null);

                return insets.consumeSystemWindowInsets();
            }
        });




        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mFeedAdapter = new FeedAdapter(this);
        mRvFeeds.setLayoutManager(llm);
        mRvFeeds.setAdapter(mFeedAdapter);

        setPopularDataManager();
        setSearchDataManager();

        mRvFeeds.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                FeedAdapter.FeedViewHolder holder = (FeedAdapter.FeedViewHolder) parent.getChildViewHolder(view);
                int position = holder.getAdapterPosition();

                outRect.top = (int) (20 * getResources().getDisplayMetrics().density);

                if(position != parent.getAdapter().getItemCount() - 1) {

                } else {
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

    }
    DribbblePreferences dribbblePreferences;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static final int TO_SUB_ACTIVITY = 1 << 2;
    public static final int TO_SEARCH_RESULT = 1 << 3;

    final Handler transitionHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data;
            switch (msg.what){
                case TO_SUB_ACTIVITY:
                    data = msg.getData();

                    Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                    intent.putExtra(FeedAdapter.KEY_SHOT, data.getParcelable(FeedAdapter.KEY_SHOT));

                    mFabSearch.getParentButton().setTransitionName(getString(R.string.feed_detail));

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, mFabSearch.getParentButton(),
                            getString(R.string.feed_detail));
                    ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
                    break;
                case TO_SEARCH_RESULT:

                    data = msg.getData();

                    onSearchButtonClick(data.getString(SearchFragment.SEARCH_KEYWORD));
                    break;
            }
        }
    };

    void startSharedElementTransition(){
    }

}
