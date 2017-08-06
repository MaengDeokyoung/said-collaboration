package com.landkid.said.data.api.model.behance;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by landkid on 2017. 7. 29..
 */

public class Stats {

    public int views;
    public int appreciations;
    public int comments;
    public int followers;
    public int following;

    public Stats(int views,
                 int appreciations,
                 int comments,
                 int followers,
                 int following) {
        this.views = views;
        this.appreciations = appreciations;
        this.comments = comments;
        this.followers = followers;
        this.following = following;
    }
}
