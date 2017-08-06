package com.landkid.said.data.api.model.behance;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;

/**
 * Created by landkid on 2017. 7. 30..
 */

public class Module {

    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    public static final String EMBED = "embed";
    public static final String MEDIA_COLLECTION = "media_collection";


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            TEXT,
            IMAGE,
            EMBED,
            MEDIA_COLLECTION
    })
    public @interface Type{}

    @Type public String type;
    public String text;
    public String src;
    public String embed;
    public String text_plain;
    public int width;
    public int height;
    public Map<String, String> sizes;
    public List<Component> components;
    public String alignment;
    public String caption_alignment;
    public String caption;
    public String sort_type;
    public String collection_type;


    public String getParsedText(String backgroundColor){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<body>");

//        stringBuilder.append("<body style=\"background-color:#");
//        stringBuilder.append(backgroundColor);
//        stringBuilder.append(";\"/>");
        stringBuilder.append(text);
        stringBuilder.append("</body>");
        return stringBuilder.toString();
    }
}
