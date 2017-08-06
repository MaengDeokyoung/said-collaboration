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


    public String getParsedText(){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<body>");
        stringBuilder.append(text);
        stringBuilder.append("</body>");
        return stringBuilder.toString();
    }

    public Module(String type,
                  String text,
                  String src,
                  String embed,
                  String text_plain,
                  int width,
                  int height,
                  Map<String, String> sizes,
                  List<Component> components,
                  String alignment,
                  String caption_alignment,
                  String caption,
                  String sort_type,
                  String collection_type) {
        this.type = type;
        this.text = text;
        this.src = src;
        this.embed = embed;
        this.text_plain = text_plain;
        this.width = width;
        this.height = height;
        this.sizes = sizes;
        this.components = components;
        this.alignment = alignment;
        this.caption_alignment = caption_alignment;
        this.caption = caption;
        this.sort_type = sort_type;
        this.collection_type = collection_type;
    }
}
