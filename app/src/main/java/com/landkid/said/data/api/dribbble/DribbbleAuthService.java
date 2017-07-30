package com.landkid.said.data.api.dribbble;

import com.landkid.said.data.api.model.dribbble.AccessToken;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by landkid on 2017. 6. 1..
 */

public interface DribbbleAuthService {
    String ENDPOINT = "https://dribbble.com/";

    @POST("/oauth/token")
    Call<AccessToken> getAccessToken(@Query("client_id") String client_id,
                                     @Query("client_secret") String client_secret,
                                     @Query("code") String code);
}
