package com.api;

import org.testng.annotations.BeforeSuite;
import util.Log;

import java.io.IOException;

public class BaseTest {

    @BeforeSuite(alwaysRun = true)
    public void setupBeforeTests() throws IOException {
        Log.configLogger();
    }
}
