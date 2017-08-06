package com.landkid.said.data.api.model.dribbble;

import java.util.Date;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Models a commend on a Dribbble shot.
 */
public class Comment {

    public final long id;
    public final String body;
    public final String likes_url;
    public final Date created_at;
    public final Date updated_at;
    public final User user;
    public long likes_count;

    public Boolean liked;

    public Comment(long id,
                   String body,
                   long likes_count,
                   String likes_url,
                   Date created_at,
                   Date updated_at,
                   User user) {
        this.id = id;
        this.body = body;
        this.likes_count = likes_count;
        this.likes_url = likes_url;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.user = user;
    }
    @Override
    public String toString() {
        return body;
    }
}
