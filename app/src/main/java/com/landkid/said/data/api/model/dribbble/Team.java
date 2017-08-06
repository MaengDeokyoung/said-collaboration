package com.landkid.said.data.api.model.dribbble;

import android.os.Parcel;
import android.os.Parcelable;

import com.landkid.said.util.ParcelUtils;

import java.util.Map;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Models a Dribbble team.
 */
public class Team implements Parcelable {

    @SuppressWarnings("unused")
    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };
    public final long id;
    public final String name;
    public final String username;
    public final String html_url;
    public final String avatar_url;
    public final String bio;
    public final String location;
    public final Map<String, String> links;


    public Team(long id,
                String name,
                String username,
                String html_url,
                String avatar_url,
                String bio,
                String location,
                Map<String, String> links) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.html_url = html_url;
        this.avatar_url = avatar_url;
        this.bio = bio;
        this.location = location;
        this.links = links;
    }

    protected Team(Parcel in) {
        id = in.readLong();
        name = in.readString();
        username = in.readString();
        html_url = in.readString();
        avatar_url = in.readString();
        bio = in.readString();
        location = in.readString();
        links = ParcelUtils.readStringMap(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(html_url);
        dest.writeString(avatar_url);
        dest.writeString(bio);
        dest.writeString(location);
        ParcelUtils.writeStringMap(links, dest);
    }
}
