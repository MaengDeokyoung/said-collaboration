package com.landkid.said.data.api.dribbble;

import com.landkid.said.data.api.model.dribbble.Comment;
import com.landkid.said.data.api.model.dribbble.Like;
import com.landkid.said.data.api.model.dribbble.Shot;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Dribbble API - http://developer.dribbble.com/v1/
 */
public interface DribbbleService {

    String ENDPOINT = "https://api.dribbble.com/";
    String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss Z";
    int PER_PAGE_MAX = 100;
    int PER_PAGE_DEFAULT = 15;


    /* Shots */

    @GET("v1/shots")
    Call<List<Shot>> getPopular(@Query("page") Integer page,
                                @Query("per_page") Integer pageSize);

    @GET("v1/shots/{id}")
    Call<Shot> getShot(@Path("id") long shotId);

    @GET("v1/shots/{id}/like")
    Call<Like> checkLiked(@Path("id") long shotId);

    @POST("v1/shots/{id}/like")
    Call<Like> like(@Path("id") long shotId);

    @DELETE("v1/shots/{id}/like")
    Call<Void> unlike(@Path("id") long shotId);

    @GET("v1/shots/{id}/comments")
    Call<List<Comment>> getComments(@Path("id") long shotId);
}
