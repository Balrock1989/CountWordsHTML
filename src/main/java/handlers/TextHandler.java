package handlers;

import api.RequestHelper;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.*;

public class TextHandler extends Thread{
    private Logger log = Logger.getLogger(TextHandler.class.getName());
    private final RequestHelper REQUEST_HELPER = new RequestHelper();
    private final String REG_SPLIT = "[\" \"|\",\"|\".\"|\"!\"|\"?\"|\"\"\"|\";\"|\":\"|\"\\[\"|\"\\]\"|\"(\"/\")\"|\"\n\"|\"\r\"|\"\t\"|\"\\s+\"|\"-\"]";
    private final String URL;

    public TextHandler(String url)  throws IOException {

        REQUEST_HELPER.initClient();
        this.URL = url;
    }

    public void run()  {
        String allText = "";
        try {
            Document document = Jsoup.parse(REQUEST_HELPER.get(this.URL));
            allText = document.text();
        } catch (IOException e) {
            log.severe(e.toString());

        }
        if (!allText.equals("")) {
            List<String> allWords = splitTextIntoWords(allText);
            HashMap<String, Integer> wordToCount = countWords(allWords);
            Map<String, Integer> result = prepareResult(wordToCount);
            System.out.println("Всего слов: " + result.size());
            result.forEach((k, v) -> System.out.println(k + " : " + v));
        } else {
            log.info(String.format("На сайте %s не найден текст", URL));
        }
    }

    @NotNull
    private List<String> splitTextIntoWords(String allText) {
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
        Map<String, Integer> result = wordToCount.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return result;
    }
}
