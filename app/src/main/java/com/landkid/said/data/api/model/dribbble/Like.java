package com.landkid.said.data.api.model.dribbble;

import android.support.annotation.Nullable;

import java.util.Date;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Models a like of a Dribbble shot.
 */
public class Like {

    public final long id;
    public final Date created_at;
    public final @Nullable User user; // some calls do not populate the user field
    public final @Nullable Shot shot; // some calls do not populate the shot field

    public Like(long id, Date created_at, User user, Shot shot) {
        this.id = id;
        this.created_at = created_at;
        this.user = user;
        this.shot = shot;
    }

    public User getPlayer() {
        return user;
    }

    public long getId() {
        return id;
    }

    public Date getDateCreated() {
        return created_at;
    }
}
