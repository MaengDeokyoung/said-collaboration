package com.landkid.said.data.api.dribbble;

import android.support.annotation.StringDef;

import com.landkid.said.data.api.model.SaidItem;
import com.landkid.said.data.api.model.Shot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by landkid on 2017. 6. 3..
 */

public interface DribbbleSearchService {

    String ENDPOINT = "https://dribbble.com/";
    String SORT_POPULAR = "";
    String SORT_LATEST = "latest";

    @GET("search")
    Call<List<Shot>> search(@Query("q") String query,
                            @Query("page") Integer page,
                            @Query("s") @SortOrder String sortOrder);

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            SORT_POPULAR,
            SORT_LATEST
    })
    @interface SortOrder{}

}
