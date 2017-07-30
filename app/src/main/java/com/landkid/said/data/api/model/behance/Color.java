package com.landkid.said.data.api.model.behance;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by landkid on 2017. 7. 29..
 */

public class Color implements Parcelable {

    public Color(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    @SuppressWarnings("unused")
    public static final Creator<Color> CREATOR = new Creator<Color>() {
        @Override
        public Color createFromParcel(Parcel in) {
            return new Color(in);
        }

        @Override
        public Color[] newArray(int size) {
            return new Color[size];
        }
    };
}
