package main;

import handlers.TextHandler;

import java.io.IOException;



public class Run {

    public static void main(String[] args) throws IOException {
        TextHandler textHandler = new TextHandler();
        textHandler.findUniqueWord();
    }
}
