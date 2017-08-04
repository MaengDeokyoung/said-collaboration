package com.landkid.said.data.api.behance;

import android.support.annotation.StringDef;

import com.landkid.said.BuildConfig;
import com.landkid.said.data.api.model.behance.Comment;
import com.landkid.said.data.api.model.behance.Project;
import com.landkid.said.data.api.model.behance.Projects;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Created by landkid on 2017. 7. 29..
 */

public interface BehanceService {

    String ENDPOINT = "http://www.behance.net/";
    String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss Z";

    String FEATURED_DATE = "featured_date";
    String APPRECIATIONS = "appreciations";
    String VIEWS = "views";
    String COMMENTS = "comments";
    String PUBLISHED_DATE = "published_date";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            FEATURED_DATE,
            APPRECIATIONS,
            VIEWS,
            COMMENTS,
            PUBLISHED_DATE
    })
    @interface SortType{}


    @GET("v2/projects?time=month&client_id=" + BuildConfig.BEHANCE_CLIENT_ID)
    Call<Projects> getProjects(@Query("page") Integer page,
                               @Query("q") String query,
                               @Query("tags") String tags,
                               @Query("sort") @SortType String sort);

    @GET("v2/projects/{id}?api_key=" + BuildConfig.BEHANCE_CLIENT_ID)
    Call<Project> getProject(@Path("id") long projectId);

    @GET("v2/projects/{id}/comments?client_id=" + BuildConfig.BEHANCE_CLIENT_ID)
    Call<Comment> getComments(@Path("id") long projectId);

}
