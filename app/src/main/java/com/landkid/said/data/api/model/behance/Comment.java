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

}
