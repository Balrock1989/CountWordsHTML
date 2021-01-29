package main;

import handlers.TextHandler;
import logger.Log;

import java.io.IOException;


public class Run implements Log {

    public static void main(String[] args) throws IOException {
        Log.configLogger();
        TextHandler textHandler = new TextHandler("https://www.simbirsoft.commmm/");
//        TextHandler textHandler = new TextHandler("https://www.simbirsoft.com/");
//        TextHandler textHandler = new TextHandler("https://codengineering.ru/q/how-to-monitor-java-memory-usage-2537");
        textHandler.start();
    }
}
