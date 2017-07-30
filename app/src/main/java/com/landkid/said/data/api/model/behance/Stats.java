package com.landkid.said.data.api.model.behance;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by landkid on 2017. 7. 29..
 */

public class Stats implements Parcelable {
    public int views;
    public int appreciations;
    public int comments;
    public int followers;
    public int following;


    public Stats(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    @SuppressWarnings("unused")
    public static final Creator<Stats> CREATOR = new Creator<Stats>() {
        @Override
        public Stats createFromParcel(Parcel in) {
            return new Stats(in);
        }

        @Override
        public Stats[] newArray(int size) {
            return new Stats[size];
        }
    };
}
