package handlers;

import api.RequestHelper;
import org.jsoup.Jsoup;
import util.Log;

import java.io.IOException;
import java.sql.Statement;
import java.util.*;

public class TextHandler extends Thread {
    private final RequestHelper REQUEST_HELPER = new RequestHelper();
    private final String URL;
    private static DbHandler db;

    public TextHandler(String url) throws IOException {
        REQUEST_HELPER.initClient();
        db = DbHandler.getInstance();
        this.URL = url;
    }

    public void run() {
        try {
            splitTextIntoWords(Jsoup.parse(REQUEST_HELPER.get(this.URL)).text());
        } catch (IOException e) {
            e.printStackTrace();
            Log.severe(this, e.toString());
        }
        if (db.notEmpty()) {
            Map<String, Integer> result = db.getAllWords();
            System.out.println(this.URL + "\nВсего слов: " + result.size());
            result.forEach((k, v) -> System.out.println(k + " : " + v));
            System.out.println(new String(new char[50]).replace("\0", "-"));
        } else {
            Log.info(this, String.format("На сайте %s не найден текст", URL));
        }
        db.clearLastStatistics();
    }

    private void splitTextIntoWords(String allText) {
        Statement st = db.createNewStatement();
        String regSplit = "[ \t\n\r,/.!?\\\"\\':;\\(\\)\\[\\]@#\\$%\\^&\\*\\-\\+\\=\\|\\{\\}\\«\\»\\<\\>]";
        Arrays.stream(allText.split(regSplit))
                .filter(s -> !s.equals(""))
                .map(String::toLowerCase)
                .forEach(s -> db.addProduct(st, s));
        db.commit();
    }
}
