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

package com.landkid.said.data.api.dribbble;

import android.support.annotation.StringDef;

import com.landkid.said.data.api.model.Comment;
import com.landkid.said.data.api.model.Like;
import com.landkid.said.data.api.model.Shot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
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

    @GET("v1/shots/{id}/like")
    Call<Like> checkLiked(@Path("id") long shotId);

    @POST("v1/shots/{id}/like")
    Call<Like> like(@Path("id") long shotId);

    @DELETE("v1/shots/{id}/like")
    Call<Void> unlike(@Path("id") long shotId);

    @GET("v1/shots/{id}/comments")
    Call<List<Comment>> getComments(@Path("id") long shotId);
}
