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

package com.landkid.said.data.api.behance;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.landkid.said.data.api.dribbble.DenvelopingConverter;

import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Storing dribbble user state.
 */
public class BehancePreferences {

    private static final String BEHANCE_PREF = "BEHANCE_PREF";

    private static volatile BehancePreferences instance;
    private final SharedPreferences prefs;

    private BehanceService api;


    public static BehancePreferences get(Context context) {
        if (instance == null) {
            synchronized (BehancePreferences.class) {
                instance = new BehancePreferences(context);
            }
        }
        return instance;
    }

    private BehancePreferences(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(BEHANCE_PREF, Context.MODE_PRIVATE);
    }

    public BehanceService getApi() {
        if (api == null) createApi();
        return api;
    }

    private void createApi() {

        final OkHttpClient client = new OkHttpClient.Builder().build();
        final Gson gson = new GsonBuilder()
                .setDateFormat(BehanceService.DATE_FORMAT)
                .create();
        api = new Retrofit.Builder()
                .baseUrl(BehanceService.ENDPOINT)
                .client(client)
                .addConverterFactory(new DenvelopingConverter(gson))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create((BehanceService.class));
    }
}
