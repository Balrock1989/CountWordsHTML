package com.api.tests;

import api.RequestHelper;
import com.api.helpers.WireMockService;
import handlers.DbHandler;
import handlers.TextHandler;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.Log;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class TextHandlerTest extends RequestHelper {
    private final DbHandler db = DbHandler.getInstance();
    private WireMockService wm;


    @BeforeClass
    public void setUp() throws IOException {
        Log.configLogger();
        RequestHelper.initClient();
        wm = WireMockService.getInstance();
        wm.start();
    }

    @Test(description = "Проверка что в БД увеличивается счетчик для уникальных слов")
    public void test1() throws IOException, InterruptedException {
        String[] words = new String[]{"one", "two", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять", "10"};
        int countBefore = db.getCountForWords(words);
        int countWordInText = wm.prepareGetWithText();
        TextHandler textHandler = new TextHandler(wm.getBaseUrl() + "/text");
        textHandler.start();
        textHandler.join();
        int countAfter = db.getCountForWords(words);
        assertThat(countBefore + countWordInText, equalTo(countAfter));
    }
    //TODO добавить кейсы как поведет себя программа с битым УРЛ, с ответом без слов, с ответом с JSON
    //TODO добавить датапровайдер куда нибудь может

    @Test(description = "Проверка что в БД увеличивается счетчик для уникальных слов")
    public void getTest() throws IOException {
//        wm.prepare();
        RequestHelper.get(wm.getBaseUrl() + "/getTest");
    }

//    @Test(description = "URL не указан")
//    public void test2() throws IOException {
//        TextHandler textHandler = new TextHandler("");
//        textHandler.start();
//    }
//
//    @Test(description = "URL возвращает пустую строку")
//    public void test3() throws IOException {
//        TextHandler textHandler = new TextHandler("https://www.simbirsoft.com/");
//        textHandler.start();
//    }
}
