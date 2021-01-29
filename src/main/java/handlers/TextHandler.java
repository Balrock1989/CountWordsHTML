package handlers;

import api.RequestHelper;
import com.github.ansell.jdefaultdict.JDefaultDict;
import helpers.DefaultDict;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sun.reflect.generics.tree.Tree;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextHandler extends RequestHelper {
    private final String REG_SPLIT = "[\" \"|\",\"|\".\"|\"!\"|\"?\"|\"\"\"|\";\"|\":\"|\"\\[\"|\"\\]\"|\"(\"/\")\"|\"\n\"|\"\r\"|\"\t\"]";

    public void findUniqueWord() throws IOException {
        initProperties();
        Document html = Jsoup.parse(get("https://www.simbirsoft.com/"));
        String allText = html.text();
        List<String> allWords = Arrays.stream(allText.split(REG_SPLIT))
                .filter(s -> !s.equals(""))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        ConcurrentMap<String, AtomicInteger> defaultDict = new JDefaultDict<>(k -> new AtomicInteger(0));
        allWords.forEach(word -> defaultDict.get(word).incrementAndGet());
        Map<String, Integer> tempMap = new TreeMap<>();
        defaultDict.forEach((k, v) -> tempMap.put(k, Integer.parseInt(String.valueOf(v))));
        Map<String, Integer> result = tempMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        result.forEach((k, v) -> System.out.println(k + " :" + v));
    }
}
