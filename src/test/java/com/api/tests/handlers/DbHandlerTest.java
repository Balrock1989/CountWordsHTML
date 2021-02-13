package com.api.tests.handlers;

import com.api.BaseTest;
import handlers.DbHandler;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static util.RandomGenerator.randomString;


public class DbHandlerTest extends BaseTest {
    private final DbHandler db = DbHandler.getInstance();
    private String tempTableName;

    @BeforeMethod
    public void createTempTable() {
        tempTableName = randomString.nextString();
        db.createTempTable(tempTableName);
    }

    @Test
    public void getAllWordsTest() {
        Map<String, Integer> result = db.getAllWords(tempTableName);
        assertThat(result.size(), equalTo(0));
        String randomWord = randomString.nextString();
        db.addProduct(tempTableName, randomWord);
        db.addProduct(tempTableName, "test123");
        db.addProduct(tempTableName, "test123");
        result = db.getAllWords(tempTableName);
        assertThat(result.keySet(), containsInAnyOrder("test123", randomWord));
        assertThat(result.values(), containsInAnyOrder(1, 2));
        db.clearTempTable(tempTableName);
    }

    @Test
    public void getCountForWordsTest() {
        String[] words = new String[]{randomString.nextString(), randomString.nextString(), randomString.nextString()};
        assertThat(db.getCountForWords(words), equalTo(0));
        for (String word : words) {
            db.addProduct(tempTableName, word);
        }
        assertThat(db.getCountForWords(words), equalTo(3));
        db.clearTempTable(tempTableName);
    }

    @AfterMethod
    public void clearTempTable() {
        db.clearTempTable(tempTableName);
    }
}
