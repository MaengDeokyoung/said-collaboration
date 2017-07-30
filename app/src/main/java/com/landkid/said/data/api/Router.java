package com.landkid.said.data.api;

import android.content.Context;
import android.support.annotation.StringDef;
import android.support.v7.widget.RecyclerView;

import com.landkid.said.data.api.behance.ProjectDataManager;
import com.landkid.said.data.api.dribbble.SearchDataManager;
import com.landkid.said.data.api.dribbble.ShotDataManager;
import com.landkid.said.data.api.model.SaidItem;
import com.landkid.said.data.api.model.behance.Project;
import com.landkid.said.data.api.model.behance.Projects;
import com.landkid.said.data.api.model.dribbble.Shot;
import com.landkid.said.ui.FeedAdapter;
import com.landkid.said.ui.listener.InfiniteScrollListener;
import com.landkid.said.ui.listener.ParallaxScrollListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public abstract class Router {

    @Mode
    public String mode = MODE_POPULAR;

    public static final String MODE_POPULAR = "MODE_POPULAR";
    private static final String MODE_SEARCH = "MODE_SEARCH";
    private static final String MODE_PROJECTS = "MODE_PROJECTS";

    private static final String POPULAR_SHOTS_HEADER = "Popular Shots";
    private static final String BEHANCE_PROJECTS_HEADER = "Behance Projects";
    private static final String SEARCH_HEADER_PREFIX = "Searched By: ";
    private final Context mContext;
    private final InfiniteScrollListener infiniteScrollListener;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            MODE_POPULAR,
            MODE_SEARCH,
            MODE_PROJECTS
    })
    protected  @interface Mode{}

    private RecyclerView mRvFeeds;
    private FeedAdapter mFeedAdapter;

    private String mQuerySearched;
    private String mPreviousQuerySearched = "";


    private ShotDataManager shotDataManager;
    private SearchDataManager searchDataManager;
    private ProjectDataManager projectDataManager;

    private boolean isModeChanging = false;

    protected Router(Context context, RecyclerView recyclerView){
        mContext = context;
        mRvFeeds = recyclerView;
        mFeedAdapter = (FeedAdapter) recyclerView.getAdapter();
        infiniteScrollListener = new InfiniteScrollListener() {
            @Override
            public void onLoadMore() {
                if (!isModeChanging) {
                    switch (mode) {
                        case MODE_POPULAR:
                            shotDataManager.loadPopular();
                            break;
                        case MODE_SEARCH:
                            searchDataManager.search(mQuerySearched);
                            break;
                        case MODE_PROJECTS:
                            projectDataManager.loadProject();
                            break;
                    }
                }
            }
        };
        mRvFeeds.addOnScrollListener(infiniteScrollListener);
        mRvFeeds.addOnScrollListener(new ParallaxScrollListener());
    }

    private void resetDataManager(String mode){
        this.mode = mode;
        isModeChanging = true;
        loadStarted(mode);

        switch (mode) {
            case MODE_POPULAR:
                shotDataManager.resetNextPageIndexes();
                infiniteScrollListener.setDataManager(shotDataManager);
                break;
            case MODE_SEARCH:
                searchDataManager.resetNextPageIndexes();
                infiniteScrollListener.setDataManager(searchDataManager);
                break;
            case MODE_PROJECTS:
                projectDataManager.resetNextPageIndexes();
                infiniteScrollListener.setDataManager(projectDataManager);
                break;
        }
    }

    private String getHeaderTitleByMode(String mode){
        switch (mode){
            case MODE_POPULAR:
                return POPULAR_SHOTS_HEADER;
            case MODE_SEARCH:
                return SEARCH_HEADER_PREFIX + mQuerySearched;
            case MODE_PROJECTS:
                return BEHANCE_PROJECTS_HEADER;
        }
        return "";
    };

    private void loadStarted(String mode) {
        BaseDataManager.loadCancel();
        String headerTitle = getHeaderTitleByMode(mode);
        Shot shot = new Shot();
        shot.headerTitle = headerTitle;
        shot.isHeaderItem = true;
        List<Shot> initShots = new ArrayList<>();
        initShots.add(0, shot);
        mFeedAdapter.setItems(initShots);

        onStart(mode);
    }

    public abstract void onStart(@Mode String mode);

    public abstract void onDataLoaded(@Mode String mode, List<? extends SaidItem> data);

    public void loadPopular() {
        if(shotDataManager == null) {
            shotDataManager = new ShotDataManager(mContext) {

                @Override
                public void onDataLoaded(List<Shot> items) {

                    mFeedAdapter.addShots(items);
                    isModeChanging = false;
                    Router.this.onDataLoaded(mode, items);
                }
            };
        }
        resetDataManager(MODE_POPULAR);
        shotDataManager.loadPopular();
    }

    public void search(String keyword){
        //mTvSearchKeyword.setText(Html.fromHtml("Search by <a href=\"\"'>" + mQuerySearched + "</href>"));
        mQuerySearched = keyword;
        if(searchDataManager == null) {
            searchDataManager = new SearchDataManager(mContext) {
                @Override
                public void onDataLoaded(List<Shot> items) {

                    List<Shot> shots = new ArrayList<>();
                    for (Shot shot : items) {
                        shots.add(shot);
                    }
                    if (!mode.equals(MODE_SEARCH) ||
                            (mode.equals(MODE_SEARCH) && mPreviousQuerySearched.equals(mQuerySearched))) {
                        mFeedAdapter.addShots(shots);
                        mPreviousQuerySearched = mQuerySearched;
                        mode = MODE_SEARCH;
                        mRvFeeds.scrollBy(0, -mRvFeeds.computeVerticalScrollOffset());
                    } else {
                        mFeedAdapter.addShots(shots);
                    }
                    isModeChanging = false;
                    Router.this.onDataLoaded(mode, shots);
                }
            };
        }
        resetDataManager(MODE_SEARCH);
        searchDataManager.createDribbbleSearchApi();
        searchDataManager.search(mQuerySearched);
    }

    public void loadProjects() {
        if(projectDataManager == null) {
            projectDataManager = new ProjectDataManager(mContext) {

                @Override
                public void onDataLoaded(Projects items) {

                    List<Project> projects = items.projects;

                    mFeedAdapter.addShots(projects);
                    isModeChanging = false;
                    Router.this.onDataLoaded(mode, projects);
                }
            };
        }
        resetDataManager(MODE_PROJECTS);
        projectDataManager.loadProject();
    }
}
