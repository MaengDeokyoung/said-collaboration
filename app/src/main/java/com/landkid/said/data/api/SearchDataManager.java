package com.landkid.said.data.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.landkid.said.data.api.dribbble.DribbbleSearchConverter;
import com.landkid.said.data.api.dribbble.DribbbleSearchService;
import com.landkid.said.data.api.dribbble.DribbbleService;
import com.landkid.said.data.api.model.Shot;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by landkid on 2017. 6. 4..
 */

public abstract class SearchDataManager extends BaseDataManager<List<Shot>> {

    DribbbleSearchService dribbbleSearchService;
    Context mContext;
    int page = 1;
    final static String SOURCE_DRIBBBLE_SEARCH = "SOURCE_DRIBBBLE_SEARCH";

    @Override
    public void resetNextPageIndexes() {
        page = 1;
    }

    public SearchDataManager(Context context){
        mContext = context;
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
                } else {

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
