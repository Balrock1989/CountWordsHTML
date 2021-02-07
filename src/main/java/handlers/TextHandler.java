package handlers;

import api.RequestHelper;
import org.jsoup.Jsoup;
import util.Log;
import util.RandomGenerator;

import java.io.IOException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;

/*** Класс для подсчета кол-ва уникальных слов на заданном URL*/
public class TextHandler extends Thread implements RandomGenerator {
    private final DbHandler db = DbHandler.getInstance();
    private final String URL;
    private final String tempTableName;
    private final Statement st;

    public TextHandler(String url) throws IOException {
        RequestHelper.initClient();
        this.URL = url;
        this.tempTableName = randomString.nextString().toLowerCase();
        this.st = db.createNewStatement(tempTableName);
    }

    public void run() {
        try {
            splitTextIntoWords(Jsoup.parse(RequestHelper.get(this.URL)).text());
        } catch (IOException e) {
            e.printStackTrace();
            Log.severe(this, e.toString());
        }
        if (db.notEmpty(st, tempTableName)) {
            Map<String, Integer> result = db.getAllWords(st, tempTableName);
            printResult(result);
        } else {
            Log.info(this, String.format("На сайте %s не найден текст", URL));
        }
        db.clearTempTable(st, tempTableName);
    }

    private void printResult(Map<String, Integer> result) {
        Log.info(this, URL + "\nВсего найдено уникальных слов: " + result.size());
        result.forEach((k, v) -> System.out.println(k + " : " + v));
        System.out.println(new String(new char[50]).replace("\0", "-"));
    }

    private void splitTextIntoWords(String allText) {
        String regSplit = "[ \t\n\r,/.!?\\\"\\“\\':;\\(\\)\\[\\]@#\\$%\\^&\\*\\-\\+\\=\\|\\{\\}\\«\\»\\<\\>©×–]";
        Arrays.stream(allText.split(regSplit))
                .filter(s -> !s.equals(""))
                .map(String::toLowerCase)
                .forEach(s -> db.addProduct(st, tempTableName, s));
    }
}
