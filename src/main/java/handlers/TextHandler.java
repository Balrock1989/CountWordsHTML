package handlers;

import api.RequestHelper;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import util.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class TextHandler extends Thread {
    private final RequestHelper REQUEST_HELPER = new RequestHelper();
    private final String URL;
    private DbHandler dbHandler;

    public TextHandler(String url) throws IOException {
        REQUEST_HELPER.initClient();
        this.URL = url;
    }

    public void run() {
        try {
            dbHandler = DbHandler.getInstance();
        } catch (SQLException e) {
            Log.severe(this, e.toString());
        }
        //TODO сделать проверку, если слово уже есть в базе, то вовзаращать его count, и прибавлять
        dbHandler.addProduct("test");
        dbHandler.addProduct("test");
        dbHandler.addProduct("test2");
//        dbHandler.commit();


        List<String> allWords = new ArrayList<>();
        try {
            allWords = splitTextIntoWords(Jsoup.parse(REQUEST_HELPER.get(this.URL)).text());
        } catch (IOException e) {
            Log.severe(this, e.toString());
        }
        if (allWords.size() > 0) {
            HashMap<String, Integer> wordToCount = countWords(allWords);
            Map<String, Integer> result = prepareResult(wordToCount);
            System.out.println(this.URL + "\nВсего слов: " + result.size());
            result.forEach((k, v) -> System.out.println(k + " : " + v));
            System.out.println(new String(new char[50]).replace("\0", "-"));
        } else {
            Log.info(this, String.format("На сайте %s не найден текст", URL));
        }
    }

    @NotNull
    private List<String> splitTextIntoWords(String allText) {
        String regSplit = "[ \t\n\r,/.!?\\\"\\':;\\(\\)\\[\\]@#\\$%\\^&\\*\\-\\+\\=\\|\\{\\}\\«\\»\\<\\>]";
        return Arrays.stream(allText.split(regSplit))
                .filter(s -> !s.equals(""))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private HashMap<String, Integer> countWords(List<String> allWords) {
        HashMap<String, Integer> wordToCount = new HashMap<>();
        for (String word : allWords) {
            if (!wordToCount.containsKey(word)) {
                wordToCount.put(word, 0);
            }
            wordToCount.put(word, wordToCount.get(word) + 1);
        }
        return wordToCount;
    }

    @NotNull
    private Map<String, Integer> prepareResult(HashMap<String, Integer> wordToCount) {
        return wordToCount.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
