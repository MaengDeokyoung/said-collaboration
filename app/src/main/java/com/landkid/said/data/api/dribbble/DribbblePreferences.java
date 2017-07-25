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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.landkid.said.BuildConfig;
import com.landkid.said.data.api.AuthInterceptor;
import com.landkid.said.data.api.CachingControlInterceptor;
import com.landkid.said.data.api.DenvelopingConverter;
import com.landkid.said.data.api.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Storing dribbble user state.
 */
public class DribbblePreferences {

    public static final String LOGIN_CALLBACK = "dribbble-auth-callback";
    public static final String LOGIN_URL = "https://dribbble.com/oauth/authorize?client_id="
            + BuildConfig.DRIBBBLE_CLIENT_ID
            + "&redirect_uri=said%3A%2F%2F" + LOGIN_CALLBACK
            + "&scope=public+write+comment+upload";
    private static final String DRIBBBLE_PREF = "DRIBBBLE_PREF";

    private static final String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_USER_NAME = "KEY_USER_NAME";
    private static final String KEY_USER_USERNAME = "KEY_USER_USERNAME";
    private static final String KEY_USER_AVATAR = "KEY_USER_AVATAR";
    private static final String KEY_USER_TYPE = "KEY_USER_TYPE";

    private static final List<String> CREATIVE_TYPES = Arrays.asList(new String[] { "Player", "Team" });

    private static volatile DribbblePreferences instance;
    private final SharedPreferences prefs;

    private String accessToken;
    private boolean isLoggedIn = false;
    private long userId;
    private String userName;
    private String userUsername;
    private String userAvatar;
    private String userType;
    private DribbbleService api;
    private DribbbleService apiWithCache;
    private List<DribbbleLoginStatusListener> loginStatusListeners;

    public static DribbblePreferences get(Context context) {
        if (instance == null) {
            synchronized (DribbblePreferences.class) {
                instance = new DribbblePreferences(context);
            }
        }
        return instance;
    }

    private DribbblePreferences(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(DRIBBBLE_PREF, Context.MODE_PRIVATE);
        accessToken = prefs.getString(KEY_ACCESS_TOKEN, null);
        isLoggedIn = !TextUtils.isEmpty(accessToken);
        if (isLoggedIn) {
            userId = prefs.getLong(KEY_USER_ID, 0);
            userName = prefs.getString(KEY_USER_NAME, null);
            userUsername = prefs.getString(KEY_USER_USERNAME, null);
            userAvatar = prefs.getString(KEY_USER_AVATAR, null);
            userType = prefs.getString(KEY_USER_TYPE, null);
        }
    }

    public interface DribbbleLoginStatusListener {
        void onDribbbleLogin();
        void onDribbbleLogout();
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setAccessToken(String accessToken) {
        if (!TextUtils.isEmpty(accessToken)) {
            this.accessToken = accessToken;
            isLoggedIn = true;
            prefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
            createApi();
            dispatchLoginEvent();
        }
    }

    public void setLoggedInUser(User user) {
        if (user != null) {
            userName = user.name;
            userUsername = user.username;
            userId = user.id;
            userAvatar = user.avatar_url;
            userType = user.type;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(KEY_USER_ID, userId);
            editor.putString(KEY_USER_NAME, userName);
            editor.putString(KEY_USER_USERNAME, userUsername);
            editor.putString(KEY_USER_AVATAR, userAvatar);
            editor.putString(KEY_USER_TYPE, userType);
            editor.apply();
        }
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public boolean userCanPost() {
        return CREATIVE_TYPES.contains(userType);
    }

    public User getUser() {
        return new User.Builder()
                .setId(userId)
                .setName(userName)
                .setUsername(userUsername)
                .setAvatarUrl(userAvatar)
                .setType(userType)
                .build();
    }

    public DribbbleService getApi() {
        if (api == null) createApi();
        return api;
    }

    public DribbbleService getApiWithCache(Context context){
        if (apiWithCache == null) createApiWithCache(context);
        return apiWithCache;
    }

    public void logout() {
        isLoggedIn = false;
        accessToken = null;
        userId = 0l;
        userName = null;
        userUsername = null;
        userAvatar = null;
        userType = null;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, null);
        editor.putLong(KEY_USER_ID, 0l);
        editor.putString(KEY_USER_NAME, null);
        editor.putString(KEY_USER_AVATAR, null);
        editor.putString(KEY_USER_TYPE, null);
        editor.apply();
        createApi();
        dispatchLogoutEvent();
    }

    public void login(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(LOGIN_URL)));
    }

    public void addLoginStatusListener(DribbbleLoginStatusListener listener) {
        if (loginStatusListeners == null) {
            loginStatusListeners = new ArrayList<>();
        }
        loginStatusListeners.add(listener);
    }

    public void removeLoginStatusListener(DribbbleLoginStatusListener listener) {
        if (loginStatusListeners != null) {
            loginStatusListeners.remove(listener);
        }
    }

    private void dispatchLoginEvent() {
        if (loginStatusListeners != null && !loginStatusListeners.isEmpty()) {
            for (DribbbleLoginStatusListener listener : loginStatusListeners) {
                listener.onDribbbleLogin();
            }
        }
    }

    private void dispatchLogoutEvent() {
        if (loginStatusListeners != null && !loginStatusListeners.isEmpty()) {
            for (DribbbleLoginStatusListener listener : loginStatusListeners) {
                listener.onDribbbleLogout();
            }
        }
    }

    private void createApiWithCache(Context context) {
        long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(new File(context.getCacheDir(), "http"), SIZE_OF_CACHE);

        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(getAccessToken()))
                .addNetworkInterceptor(new CachingControlInterceptor(context))
                .cache(cache)
                .build();

        final Gson gson = new GsonBuilder()
                .setDateFormat(DribbbleService.DATE_FORMAT)
                .create();
        apiWithCache = new Retrofit.Builder()
                .baseUrl(DribbbleService.ENDPOINT)
                .client(client)
                .addConverterFactory(new DenvelopingConverter(gson))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create((DribbbleService.class));
    }

    private void createApi() {

        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(getAccessToken()))
                .build();
        final Gson gson = new GsonBuilder()
                .setDateFormat(DribbbleService.DATE_FORMAT)
                .create();
        api = new Retrofit.Builder()
                .baseUrl(DribbbleService.ENDPOINT)
                .client(client)
                .addConverterFactory(new DenvelopingConverter(gson))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create((DribbbleService.class));
    }

    private String getAccessToken() {
        return !TextUtils.isEmpty(accessToken) ? accessToken
                : BuildConfig.DRIBBBLE_CLIENT_ACCESS_TOKEN;
    }

}
