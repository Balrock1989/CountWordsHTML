package main;

import handlers.DbHandler;
import handlers.TextHandler;
import util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Run {

    public static void main(String[] args) throws IOException, InterruptedException {
        Log.configLogger();
        startCountWords("https://www.simbirsoft.com/", "https://codengineering.ru/");
    }

    public static void startCountWords(String... urls) throws IOException, InterruptedException {
        List<TextHandler> thread = new ArrayList<>();
        for (String url : urls) {
            thread.add(new TextHandler(url));
        }
        for (TextHandler textHandler : thread) {
            textHandler.start();
            textHandler.join();
        }
        DbHandler.getInstance().printAllStatistics(10);
    }
}
