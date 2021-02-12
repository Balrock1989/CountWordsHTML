package com.api.tests.handlers;

import com.api.BaseTest;
import com.api.helpers.WireMockService;
import handlers.DbHandler;
import handlers.TextHandler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.api.helpers.ParseHelper.readLastLine;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


public class TextHandlerTest extends BaseTest {
    private final DbHandler db = DbHandler.getInstance();
    private WireMockService wm;
    private String[] words = new String[]{"one", "two", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять", "10"};


    @BeforeClass
    public void setUp() {
        wm = WireMockService.getInstance();
        wm.start();
    }

    @Test(description = "Проверка что в БД увеличивается счетчик для уникальных слов если в ответе приходит текст")
    public void checkIncreasesCounterForTextTest() throws IOException, InterruptedException {
        int countBefore = db.getCountForWords(words);
        int countWordInText = wm.prepareGetWithText();
        TextHandler textHandler = new TextHandler(wm.getBaseUrl() + "/text");
        textHandler.start();
        textHandler.join();
        int countAfter = db.getCountForWords(words);
        assertThat(countBefore + countWordInText, equalTo(countAfter));
    }

    @Test(description = "URL возвращает пустую строку")
    public void returnEmptyStringTest() throws IOException, InterruptedException {
        wm.prepareOkGetWithoutBody();
        String url = wm.getBaseUrl() + "/withoutBody/432567";
        TextHandler textHandler = new TextHandler(url);
        textHandler.start();
        textHandler.join();
        assertThat(readLastLine("INFO"), containsString("INFO: handlers.TextHandler: Text not found in the site " + url));
    }

    @Test(description = "Проверка что в БД увеличивается счетчик для уникальных слов если в ответе приходит JSON")
    public void checkIncreasesCounterForJsonTest() throws IOException, InterruptedException {
        int countWordInText = wm.prepareGetWithJson();
        String[] words = new String[]{"admin", "login", "password", "qwerty"};
        int countBefore = db.getCountForWords(words);
        TextHandler textHandler = new TextHandler(wm.getBaseUrl() + "/json");
        textHandler.start();
        textHandler.join();
        int countAfter = db.getCountForWords(words);
        assertThat(countBefore + countWordInText, equalTo(countAfter));
    }

    @Test(description = "Проверка что в БД увеличивается счетчик для уникальных слов если в ответе приходит HTML")
    public void checkIncreasesCounterForHtmlTest() throws IOException, InterruptedException {
        int countBefore = db.getCountForWords(words);
        int countWordInText = wm.prepareGetWithHtml();
        TextHandler textHandler = new TextHandler(wm.getBaseUrl() + "/html");
        textHandler.start();
        textHandler.join();
        int countAfter = db.getCountForWords(words);
        assertThat(countBefore + countWordInText, equalTo(countAfter));
    }

    @Test(description = "Проверка ошибки, если URL не указан")
    public void checkEmptyUrlTest() throws IOException, InterruptedException {
        TextHandler textHandler = new TextHandler("");
        textHandler.start();
        textHandler.join();
        assertThat(readLastLine("SEVERE"), containsString("IllegalArgumentException: Expected URL scheme 'http' or 'https' but no colon was found"));
    }


    @Test(description = "Проверка ошибки, если URL с некорректный")
    public void checkInvalidUrlTest() throws IOException, InterruptedException {
        String url = "https://www.simbirsoft.commmm";
        TextHandler textHandler = new TextHandler(url);
        textHandler.start();
        textHandler.join();
        assertThat(readLastLine("SEVERE"), containsString("UnknownHostException: www.simbirsoft.commmm"));
        assertThat(readLastLine("INFO"), containsString("INFO: handlers.TextHandler: Text not found in the site " + url));
    }

    @BeforeMethod
    public void resetWm() {
        wm.resetAll();
    }

    @AfterClass
    public void tearDown() {
        wm.stop();
    }
}
