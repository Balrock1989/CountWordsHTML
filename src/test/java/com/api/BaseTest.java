package com.api;

import org.testng.annotations.BeforeSuite;
import util.Log;

public class BaseTest {

    @BeforeSuite(alwaysRun = true)
    public void setupBeforeTests() {
        Log.configLogger();
    }
}
