package com.landkid.said.data.api.behance;

import android.content.Context;

import com.landkid.said.data.api.BaseDataManager;
import com.landkid.said.data.api.model.behance.Projects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Created by landkid on 2017. 7. 29..
 */

public abstract class ProjectDataManager extends BaseDataManager<Projects> {
    Context mContext;
    BehanceService api;
    static final String SOURCE_DRIBBBLE_POPULAR = "SOURCE_DRIBBBLE_POPULAR";


    public ProjectDataManager(Context context){
        super();
        mContext = context;
        api = BehancePreferences.get(mContext).getApi();
        resetNextPageIndexes();
    }

    public void loadProject() {
        loadStarted();
        final Call<Projects> projectCall = api.getProjects(getNextPageIndexes(), "", "", BehanceService.FEATURED_DATE);
        projectCall.enqueue(new Callback<Projects>() {
            @Override
            public void onResponse(Call<Projects> call, Response<Projects> response) {
                if (response.isSuccessful()) {
                    updatePageIndexes();
                    onDataLoaded(response.body());
                }
                loadFinished();
                inflight.remove(call);
            }

            @Override
            public void onFailure(Call<Projects> call, Throwable t) {
                loadFinished();
                inflight.remove(call);
            }
        });
        inflight.add(projectCall);
    }
}
