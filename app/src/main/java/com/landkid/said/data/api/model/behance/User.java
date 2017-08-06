package com.landkid.said.data.api.model.behance;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

/**
 * Created by landkid on 2017. 7. 29..
 */
public class User implements Parcelable {

    public long id;
    public String first_name;
    public String last_name;
    public String username;
    public String city;
    public String state;
    public String country;
    public String location;
    public String company;
    public String occupation;
    public long created_on;
    public String url;
    public Map<String, String> images;
    public String display_name;
    public List<String> fields;
    public int has_default_image;
    public String website;
    public Stats stats;

    public String getFullName(){
        return first_name + " " + last_name;
    }

    public User(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    @SuppressWarnings("unused")
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(long id,
                String first_name,
                String last_name,
                String username,
                String city,
                String state,
                String country,
                String location,
                String company,
                String occupation,
                long created_on,
                String url,
                Map<String, String> images,
                String display_name,
                List<String> fields,
                int has_default_image,
                String website,
                Stats stats) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.city = city;
        this.state = state;
        this.country = country;
        this.location = location;
        this.company = company;
        this.occupation = occupation;
        this.created_on = created_on;
        this.url = url;
        this.images = images;
        this.display_name = display_name;
        this.fields = fields;
        this.has_default_image = has_default_image;
        this.website = website;
        this.stats = stats;
    }
}
