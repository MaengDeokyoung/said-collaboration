package com.landkid.said.data.api.model.behance;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by landkid on 2017. 7. 29..
 */

public class Feature implements Parcelable {

    public Feature(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    @SuppressWarnings("unused")
    public static final Creator<Feature> CREATOR = new Creator<Feature>() {
        @Override
        public Feature createFromParcel(Parcel in) {
            return new Feature(in);
        }

        @Override
        public Feature[] newArray(int size) {
            return new Feature[size];
        }
    };
}
