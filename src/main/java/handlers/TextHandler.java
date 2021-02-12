package handlers;

import api.RequestHelper;
import org.jsoup.Jsoup;
import util.Log;
import util.RandomGenerator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/*** Класс для подсчета кол-ва уникальных слов на заданном URL*/
public class TextHandler extends Thread implements RandomGenerator {
    private final DbHandler db = DbHandler.getInstance();
    private final String URL;
    private final String tempTableName;


    public TextHandler(String url) throws IOException {
        RequestHelper.initClient();
        this.URL = url;
        this.tempTableName = randomString.nextString().toLowerCase();
        db.CreateTempTable(tempTableName);
    }

    public void run() {
        try {
            splitTextIntoWords(Jsoup.parse(RequestHelper.get(this.URL)).text());
        } catch (IOException e) {
            e.printStackTrace();
            Log.severe(TextHandler.class, e);
        }
        if (db.notEmpty(tempTableName)) {
            Map<String, Integer> result = db.getAllWords(tempTableName);
            printResult(result);
        } else {
            Log.info(TextHandler.class, String.format("Text not found in the site %s", URL));
        }
        db.clearTempTable(tempTableName);
    }

    private void printResult(Map<String, Integer> result) {
        Log.info(TextHandler.class, URL + "\nTotal unique words found: " + result.size());
        result.forEach((k, v) -> System.out.println(k + " : " + v));
        System.out.println(new String(new char[50]).replace("\0", "-"));
    }

    private void splitTextIntoWords(String allText) {
        String regSplit = "[ \t\n\r,/.!?\\\"\\“\\':;\\(\\)\\[\\]@#№\\$%\\^&\\*\\-\\+\\=\\|\\{\\}\\«\\»\\>\\<©×–\\\\]";
        Arrays.stream(allText.split(regSplit))
                .filter(s -> !s.equals(""))
                .map(String::toLowerCase)
                .forEach(s -> db.addProduct(tempTableName, s));
    }
}
