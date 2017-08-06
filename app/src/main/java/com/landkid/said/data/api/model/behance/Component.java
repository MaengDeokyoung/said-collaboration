package com.landkid.said.data.api.model.behance;

import java.util.Map;

/**
 * Created by landkid on 2017. 8. 6..
 */

public class Component {

    public long id;
    public long project_id;
    public String source_filename;
    public int source_width;
    public int source_height;
    public int position;
    public double flex_width;
    public double flex_height;
    public Map<String, Map<String, Integer>> dimensions;
    public Map<String, String> sizes;
    public String src;


    public Component(long id,
                     long project_id,
                     String source_filename,
                     int source_width,
                     int source_height,
                     int position,
                     double flex_width,
                     double flex_height,
                     Map<String, Map<String, Integer>> dimensions,
                     Map<String, String> sizes,
                     String src) {
        this.id = id;
        this.project_id = project_id;
        this.source_filename = source_filename;
        this.source_width = source_width;
        this.source_height = source_height;
        this.position = position;
        this.flex_width = flex_width;
        this.flex_height = flex_height;
        this.dimensions = dimensions;
        this.sizes = sizes;
        this.src = src;
    }
}
