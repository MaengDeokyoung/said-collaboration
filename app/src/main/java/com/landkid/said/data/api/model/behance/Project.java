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

    public Project(long id,
                   String name,
                   String url,
                   Project project,
                   long published_on,
                   long created_on,
                   long modified_on,
                   String privacy,
                   List<String> fields,
                   List<String> tags,
                   Map<String, String> covers,
                   int mature_content,
                   String mature_access,
                   List<User> owners,
                   Stats stats,
                   int conceived_on,
                   List<Feature> features,
                   List<Color> colors,
                   String description,
                   List<Module> modules,
                   Styles styles) {
        super(id, name, url);
        this.project = project;
        this.name = name;
        this.published_on = published_on;
        this.created_on = created_on;
        this.modified_on = modified_on;
        this.privacy = privacy;
        this.fields = fields;
        this.tags = tags;
        this.covers = covers;
        this.mature_content = mature_content;
        this.mature_access = mature_access;
        this.owners = owners;
        this.stats = stats;
        this.conceived_on = conceived_on;
        this.features = features;
        this.colors = colors;
        this.description = description;
        this.modules = modules;
        this.styles = styles;
    }
}
