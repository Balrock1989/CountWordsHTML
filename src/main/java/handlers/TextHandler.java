package handlers;

import api.RequestHelper;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class TextHandler extends RequestHelper {
    private final String REG_SPLIT = "[\" \"|\",\"|\".\"|\"!\"|\"?\"|\"\"\"|\";\"|\":\"|\"\\[\"|\"\\]\"|\"(\"/\")\"|\"\n\"|\"\r\"|\"\t\"|\"\\s+\"]";
    private final String url;
    public TextHandler(String url){
        this.url = url;
    }

    public void findUniqueWord() throws IOException {
        initProperties();
        String allText = getAllText();
        if (!allText.equals("")){
            List<String> allWords = splitTextIntoWords(allText);
            HashMap<String, Integer> wordToCount = countWords(allWords);
            Map<String, Integer> result = prepareResult(wordToCount);
            result.forEach((k, v) -> System.out.println(k + " :" + v));
        } else {
            System.out.printf("На сайте %s не найден текст", url);
        }
    }
    private String getAllText() throws IOException {
        Document parse;
        try {
            parse = Jsoup.parse(get(this.url));
        }catch (UnknownHostException e){
            // TODO залогировать e.fillInStackTrace()
            throw new RuntimeException(e.fillInStackTrace());//TODO не завершать приложении при ошибке в url
            // TODO Добавить многопоточность

        }
        return parse.text();
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
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return result;
    }
}
