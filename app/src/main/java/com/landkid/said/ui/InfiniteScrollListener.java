/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.landkid.said.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.landkid.said.data.api.BaseDataManager;
import com.landkid.said.data.api.SearchDataManager;
import com.landkid.said.data.api.ShotDataManager;

import java.util.ArrayList;

/**
 * A scroll listener for RecyclerView to load more items as you approach the end.
 *
 * Adapted from https://gist.github.com/ssinss/e06f12ef66c51252563e
 */
public abstract class InfiniteScrollListener extends RecyclerView.OnScrollListener {

    // The minimum number of items remaining before we should loading more.
    private static final int VISIBLE_THRESHOLD = 5;

    private final LinearLayoutManager layoutManager;
    private ArrayList<BaseDataManager> dataManagers;

//    private final ShotDataManager dataLoading;
//    private final SearchDataManager searchDataLoading;


//    public InfiniteScrollListener(@NonNull LinearLayoutManager layoutManager,
//                                  @NonNull ShotDataManager dataLoading,
//                                  @NonNull SearchDataManager searchDataManager) {
//        this.layoutManager = layoutManager;
//        this.dataLoading = dataLoading;
//        this.searchDataLoading = searchDataManager;
//        dataManagers = new ArrayList<>();
//    }

    public InfiniteScrollListener(@NonNull LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        dataManagers = new ArrayList<>();
    }

    public void addDataManager(BaseDataManager dataManager){
        dataManagers.add(dataManager);
    }

    public void removeDataManager(BaseDataManager dataManager){
        dataManagers.remove(dataManager);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        // bail out if scrolling upward or already loading data
        //if (dy < 0 || dataLoading.isDataLoading() || (searchDataLoading != null && searchDataLoading.isDataLoading())) return;
        if (dy < 0)
            return;
        for (BaseDataManager dataManager : dataManagers) {
            if (dataManager.isDataLoading())
                return;
        }


        final int visibleItemCount = recyclerView.getChildCount();
        final int totalItemCount = layoutManager.getItemCount();
        final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
            onLoadMore();
        }
    }

    public abstract void onLoadMore();

}
