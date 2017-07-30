package com.landkid.said.data.api.model.behance;

/**
 * Created by landkid on 2017. 7. 30..
 */

import java.util.Map;
//"styles":{
//        "text":{
//        "title":{
//        "font_family":"arial,helvetica,sans-serif",
//        "font_weight":"bold",
//        "color":"#191919",
//        "text_align":"left",
//        "line_height":"1.1em",
//        "font_size":"36px",
//        "text_decoration":"none",
//        "font_style":"normal",
//        "display":"inline",
//        "text_transform":"none"
//        },
//        "subtitle":{
//        "font_family":"arial,helvetica,sans-serif",
//        "font_weight":"normal",
//        "color":"#a4a4a4",
//        "text_align":"left",
//        "line_height":"1.4em",
//        "font_size":"20px",
//        "text_decoration":"none",
//        "font_style":"normal",
//        "display":"inline",
//        "text_transform":"none"
//        },
//        "paragraph":{
//        "font_family":"arial,helvetica,sans-serif",
//        "font_weight":"normal",
//        "color":"#696969",
//        "text_align":"left",
//        "line_height":"1.4em",
//        "font_size":"20px",
//        "text_decoration":"none",
//        "font_style":"normal",
//        "display":"inline",
//        "text_transform":"none"
//        },
//        "caption":{
//        "font_family":"arial,helvetica,sans-serif",
//        "font_weight":"normal",
//        "color":"#a4a4a4",
//        "text_align":"left",
//        "line_height":"1.4em",
//        "font_size":"14px",
//        "text_decoration":"none",
//        "font_style":"italic",
//        "display":"block",
//        "text_transform":"none"
//        },
//        "link":{
//        "font_family":"arial,helvetica,sans-serif",
//        "font_weight":"normal",
//        "color":"#1769FF",
//        "text_align":"left",
//        "line_height":"1.4em",
//        "font_size":"12px",
//        "text_decoration":"none",
//        "font_style":"normal",
//        "display":"inline",
//        "text_transform":"none"
//        }
//        },
//        "background":{
//        "color":"000000"
//        },
//        "spacing":{
//        "project":{
//        "top_margin":80
//        },
//        "modules":{
//        "bottom_margin":60
//        }
//        },
//        "dividers":{
//        "display":"none"
//        }
//        },

public class Styles {
    public Map<String, String> background;
    public Map<String, Map<String, String>> text;
}
