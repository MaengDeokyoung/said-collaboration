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

}
