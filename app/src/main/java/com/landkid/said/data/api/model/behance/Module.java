package com.landkid.said.data.api.model.behance;

import java.util.Map;

/**
 * Created by landkid on 2017. 7. 30..
 */

public class Module {

    public String type;
    public String text;
    public String src;
    public String embed;
    public String text_plain;
    public int width;
    public int height;
    public Map<String, String> sizes;


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
