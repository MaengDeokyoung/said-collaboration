package com.landkid.said.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.StringDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.landkid.said.R;
import com.landkid.said.data.api.BaseDataManager;
import com.landkid.said.data.api.ShotDataManager;
import com.landkid.said.data.api.SearchDataManager;
import com.landkid.said.data.api.dribbble.DribbblePrefs;
import com.landkid.said.data.api.model.Shot;
import com.landkid.said.ui.widget.SearchActivity;
import com.landkid.said.util.ResourceUtils;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    static final String MODE_POPULAR = "MODE_POPULAR";
    static final String MODE_SEARCH = "MODE_SEARCH";

    @Mode String mode = MODE_POPULAR;

    @BindView(R.id.list) RecyclerView mRvFeeds;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.et_search) EditText mEtSearch;
    @BindView(R.id.iv_search) ImageView mIvSearch;
    @BindView(R.id.pb_loading) ProgressBar mPbLoading;
    @BindView(R.id.bt_login) Button mBtLogin;
    //@BindView(R.id.pb_loading) LottieAnimationView mPbLoading;
    //@BindView(R.id.tv_search_keyword) TextView mTvSearchKeyword;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            MODE_POPULAR,
            MODE_SEARCH
    })
    @interface Mode{}

    boolean isModeChanging = false;

    private FeedAdapter mFeedAdapter;

    ShotDataManager shotDataManager;
    SearchDataManager searchDataManager;

    InfiniteScrollListener infiniteScrollListener;

    void resetDataManager(){
        //mFeedAdapter.setShots(new ArrayList<Shot>());
        isModeChanging = true;
        loadStarted();
        if(shotDataManager != null)
            shotDataManager.resetNextPageIndexes();
        if(searchDataManager != null)
            infiniteScrollListener.removeDataManager(searchDataManager);
    }

    private void loadStarted() {
        BaseDataManager.loadCancel();
        Shot shot = new Shot(0,null,null,0,0,null,0,0,0,0,0,0,null,null,null,null,null,null,null,null,null,false,null,null,null);
        shot.isHeaderItem = true;
        List<Shot> initShots = new ArrayList<>();
        initShots.add(0, shot);
        mFeedAdapter.setShots(initShots);
        showProgress(Gravity.CENTER);
    }

    String mQuerySearched;
    String mPreviousQuerySearched;

    void onSearchButtonClick(){
        resetDataManager();
        mQuerySearched = mEtSearch.getText().toString();
        //mTvSearchKeyword.setText(Html.fromHtml("Search by <a href=\"\"'>" + mQuerySearched + "</href>"));
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

        infiniteScrollListener.addDataManager(searchDataManager);
        searchDataManager.createDribbbleSearchApi();
        searchDataManager.search(mQuerySearched);
        drawer.closeDrawer(GravityCompat.END);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEtSearch.getWindowToken(), 0);
    }

    void setSearchDataManager(){
        mEtSearch.setLines(1);
        mEtSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onSearchButtonClick();
                return false;
            }
        });
        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchButtonClick();
            }

        });
    }

    void setActionBarAndDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setPadding((int) (10 * getResources().getDisplayMetrics().density), 0, 0, 0);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_logo);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
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
                    resetDataManager();
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
        loadStarted();
        shotDataManager.createApi();
        shotDataManager.loadPopular();

        infiniteScrollListener = new InfiniteScrollListener((LinearLayoutManager) mRvFeeds.getLayoutManager()) {
            @Override
            public void onLoadMore() {
                if(!isModeChanging) {
                    showProgress(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                    switch (mode) {
                        case MODE_POPULAR:
                            shotDataManager.loadPopular();
                            break;
                        case MODE_SEARCH:
                            searchDataManager.search(mEtSearch.getText().toString());
                            break;
                    }
                }
            }
        };
        infiniteScrollListener.addDataManager(shotDataManager);
        mRvFeeds.addOnScrollListener(infiniteScrollListener);


    }

    void showProgress(int gravity){
        ((CoordinatorLayout.LayoutParams) mPbLoading.getLayoutParams()).gravity = gravity;
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
//                .addNormal(Typekit.createFromAsset(this, "fonts/RobotoMono-Regular.ttf"))
//                .addBold(Typekit.createFromAsset(this, "fonts/RobotoMono-Bold.ttf"))
//                .addBoldItalic(Typekit.createFromAsset(this, "fonts/RobotoMono-BoldItalic.ttf"))
//                .addItalic(Typekit.createFromAsset(this, "fonts/RobotoMono-Italic.ttf"));

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setActionBarAndDrawer();

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

//                if (position == 0) {
//                    TypedValue typedValue = new TypedValue();
//                    getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true);
//                    outRect.top = (int) typedValue.getDimension(getResources().getDisplayMetrics());
//                }

                outRect.top = (int) (20 * getResources().getDisplayMetrics().density);

                if(position != parent.getAdapter().getItemCount() - 1) {
                    //outRect.bottom = (int) (20 * getResources().getDisplayMetrics().density);
                } else {
                    outRect.bottom = (int) (150 * getResources().getDisplayMetrics().density);
                }
                //super.getItemOffsets(outRect, view, parent, state);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("cx", view.getX() + view.getWidth() / 2);
                intent.putExtra("cy", view.getY() + view.getHeight() / 2);
                startActivity(intent);
            }
        });

        dribbblePrefs = DribbblePrefs.get(getApplicationContext());


        mBtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(MainActivity.this, DribbbleLogin.class);
                startActivity(login);
            }
        });

    }
    DribbblePrefs dribbblePrefs;
}
