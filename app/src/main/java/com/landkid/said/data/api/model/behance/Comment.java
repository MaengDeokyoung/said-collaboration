package com.landkid.said.data.api.model.behance;

import java.util.List;

/**
 * Created by landkid on 2017. 7. 30..
 */

public class Comment {

    public List<Comment> comments;

    public User user;
    public String comment;
    public long created_on;

    public Comment(List<Comment> comments,
                   User user,
                   String comment,
                   long created_on) {
        this.comments = comments;
        this.user = user;
        this.comment = comment;
        this.created_on = created_on;
    }
}
