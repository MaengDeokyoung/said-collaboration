package com.landkid.said.data.api.model.behance;

import android.content.Context;
import android.text.SpannableStringBuilder;

import com.landkid.said.data.api.model.SaidItem;

import java.util.List;
import java.util.Map;

/**
 * Created by landkid on 2017. 7. 29..
 */

public class Project extends SaidItem {

    public Project project;

    public String name;
    public long published_on;
    public long created_on;
    public long modified_on;
    public String privacy;
    public List<String> fields;
    public List<String> tags;
    public Map<String, String> covers;
    public int mature_content;
    public String mature_access;
    public List<User> owners;
    public Stats stats;
    public int conceived_on;
    public List<Feature> features;
    public List<Color> colors;
    public String description;
    public List<Module> modules;
    public Styles styles;

    public String getTags(){
        SpannableStringBuilder tagsSpannableStr = new SpannableStringBuilder();
        for(String tag : tags){
            tagsSpannableStr.append("#");
            tagsSpannableStr.append(tag.replace(" ", ""));
            tagsSpannableStr.append("  ");
        }
        return tagsSpannableStr.toString();
    }

    public String getStylesheetForHtml(Context context){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<style type=\"text/css\">");
        for (String className : styles.text.keySet()){

            if(className.equals("link")){
                stringBuilder.append("a");
                stringBuilder.append("{");
                for(String attrName : styles.text.get(className).keySet()){
                    if(attrName.equals("font_family")
                            || attrName.equals("text_decoration")
                            || attrName.equals("color")
                            || attrName.equals("font_style")){
                        stringBuilder.append(attrName);
                        stringBuilder.append(":");
                        stringBuilder.append(styles.text.get(className).get(attrName));
                        stringBuilder.append(";");
                    }
                }
            } else {
                stringBuilder.append("." );
                stringBuilder.append(className);
                stringBuilder.append("{");
                for(String attrName : styles.text.get(className).keySet()){
                    if(attrName.equals("font_family")
                            || attrName.equals("font_weight")
                            || attrName.equals("color")
                            || attrName.equals("font_size")
                            || attrName.equals("font_style")){
                        stringBuilder.append(attrName);
                        stringBuilder.append(":");
                        stringBuilder.append(styles.text.get(className).get(attrName));
                        stringBuilder.append(";");
                    }
                }
            }

            stringBuilder.append("}");
        }
        stringBuilder.append("</style>");

        return stringBuilder.toString().replace("_", "-");
    };

    public Project(long id, String name, String url) {
        super(id, name, url);
    }

}