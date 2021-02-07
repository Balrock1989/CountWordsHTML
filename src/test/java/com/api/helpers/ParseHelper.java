package com.api.helpers;

import com.jayway.jsonpath.JsonPath;
import org.json.JSONObject;

import java.util.List;

public class ParseHelper {

    public static String parseJsonAsString(JSONObject jsonObject, String path) {
        return JsonPath.parse(jsonObject.toString()).read("$." + path);
    }
    public static List<String> parseJsonAsList(JSONObject jsonObject, String path) {
        return JsonPath.parse(jsonObject.toString()).read("$." + path);
    }


}
