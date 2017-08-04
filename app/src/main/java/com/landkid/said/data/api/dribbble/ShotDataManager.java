package com.landkid.said.data.api.dribbble;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.landkid.said.BuildConfig;
import com.landkid.said.data.api.BaseDataManager;
import com.landkid.said.data.api.model.dribbble.Shot;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Created by landkid on 2017. 6. 3..
 */

public abstract class ShotDataManager extends BaseDataManager<List<Shot>> {

    DribbbleService dribbbleService;
    Context mContext;
    static final String SOURCE_DRIBBBLE_POPULAR = "SOURCE_DRIBBBLE_POPULAR";


    public ShotDataManager(Context context){
        super();
        mContext = context;
        dribbbleService = DribbblePreferences.get(context).getApi();
        resetNextPageIndexes();
    }

    public void loadPopular(){
        loadStarted();
        final Call<List<Shot>> popularCall = dribbbleService.getPopular(getNextPageIndexes(),
                DribbbleService.PER_PAGE_DEFAULT);
        popularCall.enqueue(new Callback<List<Shot>>() {
            @Override
            public void onResponse(Call<List<Shot>> call, Response<List<Shot>> response) {
                if (response.isSuccessful()) {
                    updatePageIndexes();
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
        inflight.add(popularCall);
    }
}
