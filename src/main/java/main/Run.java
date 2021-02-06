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
//        new TextHandler("https://www.simbirsoft.commmm/").start();
        List<TextHandler> thread = new ArrayList<>();
        thread.add(new TextHandler("https://www.simbirsoft.com/"));
        thread.add(new TextHandler("https://codengineering.ru/"));
        for (TextHandler textHandler : thread) {
            textHandler.start();
            textHandler.join();
        }
        DbHandler.getInstance().commit();
//        DbHandler.getInstance().printAllStatistics();
    }
}
