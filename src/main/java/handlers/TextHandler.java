package handlers;

import api.RequestHelper;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.*;

public class TextHandler extends Thread {
    private final Logger LOG = Logger.getLogger(TextHandler.class.getName());
    private final RequestHelper REQUEST_HELPER = new RequestHelper();
    private final String URL;

    public TextHandler(String url) throws IOException {
        REQUEST_HELPER.initClient();
        this.URL = url;
    }

    public void run() {
        List<String> allWords = new ArrayList<>();
        try {
            Document document = Jsoup.parse(REQUEST_HELPER.get(this.URL));
            allWords = splitTextIntoWords(document.text());
        } catch (IOException e) {
            LOG.severe(e.toString());
        }
        if (allWords.size() > 0) {
            HashMap<String, Integer> wordToCount = countWords(allWords);
            Map<String, Integer> result = prepareResult(wordToCount);
            System.out.println(this.URL + "\nВсего слов: " + result.size());
            result.forEach((k, v) -> System.out.println(k + " : " + v));
            System.out.println(new String(new char[50]).replace("\0", "-"));
        } else {
            LOG.info(String.format("На сайте %s не найден текст", URL));
        }
    }

    @NotNull
    private List<String> splitTextIntoWords(String allText) {
        String REG_SPLIT = "[ \t\n\r,/.!?\\\"\\':;\\(\\)\\[\\]@#\\$%\\^&\\*\\-\\+\\=\\|\\{\\}\\«\\»]";
        return Arrays.stream(allText.split(REG_SPLIT))
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
