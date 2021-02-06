package main;

import handlers.TextHandler;
import util.Log;

import java.io.IOException;


public class Run {

    public static void main(String[] args) throws IOException {
        Log.configLogger();
//        new TextHandler("https://www.simbirsoft.commmm/").start();
        new TextHandler("https://www.simbirsoft.com/").start();
//        new TextHandler("https://codengineering.ru/").start();

    }
}
