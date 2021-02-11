package com.api.helpers;

import com.jayway.jsonpath.JsonPath;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class ParseHelper {

    public static String parseJsonAsString(JSONObject jsonObject, String path) {
        return JsonPath.parse(jsonObject.toString()).read("$." + path);
    }

    public static List<String> parseJsonAsList(JSONObject jsonObject, String path) {
        return JsonPath.parse(jsonObject.toString()).read("$." + path);
    }

    public static String readLastLine(File file, String prefixMessage) throws IOException {
        String result = null;
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long startIdx = file.length();
            while (startIdx >= 0 && (result == null || result.length() == 0)) {
                raf.seek(startIdx);
                if (startIdx > 0)
                    raf.readLine(); // считываем 2 строки с конца файла. Если вторая не null, значит она последняя
                result = raf.readLine();
                if (result != null && !result.startsWith(prefixMessage)){
                    result = null;
                }
                startIdx--;
            }
        }
        return result != null ? new String(result.getBytes("ISO-8859-1")) : null;
    }

}
