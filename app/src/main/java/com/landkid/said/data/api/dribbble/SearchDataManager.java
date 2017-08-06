package com.landkid.said.data.api.dribbble;

import android.content.Context;

import com.landkid.said.data.api.BaseDataManager;
import com.landkid.said.data.api.model.dribbble.Shot;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Created by landkid on 2017. 6. 4..
 */

public abstract class SearchDataManager extends BaseDataManager<List<Shot>> {

    private DribbbleSearchService dribbbleSearchService;
    private Context mContext;
    private int page = 1;

    @Override
    public void resetNextPageIndexes() {
        page = 1;
    }

    protected SearchDataManager(){
        resetNextPageIndexes();
    }


    public void createDribbbleSearchApi() {

        dribbbleSearchService = new Retrofit.Builder()
                .baseUrl(DribbbleSearchService.ENDPOINT)
                .addConverterFactory(new DribbbleSearchConverter.Factory())
                .build()
                .create((DribbbleSearchService.class));
    }


    public void search(String query){
        loadStarted();
        final Call<List<Shot>> searchCall = dribbbleSearchService.search(
                query,
                page++,
                DribbbleSearchService.SORT_POPULAR);
        searchCall.enqueue(new Callback<List<Shot>>() {

            @Override
            public void onResponse(Call<List<Shot>> call, Response<List<Shot>> response) {
                if (response.isSuccessful()) {
                    onDataLoaded(response.body());
                }
                loadFinished();
                inflight.remove(call);
            }

            @Override
            public void onFailure(Call<List<Shot>> call, Throwable t) {
                loadFinished();
                inflight.remove(call);
            }
        });
        inflight.add(searchCall);

    }
}