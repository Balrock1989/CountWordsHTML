package com.api.tests;

import api.RequestHelper;
import com.api.helpers.WireMockService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.Log;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class RequestHelperTest extends RequestHelper {
    private WireMockService wm;


    @BeforeClass
    public void setUp() throws IOException {
        Log.configLogger();
        RequestHelper.initClient();
        wm = WireMockService.getInstance();
        wm.start();
    }

    @Test
    public void getWithTextTest() throws IOException {
        wm.prepareOkGetWithBody();
        String responseBody = RequestHelper.get(wm.getBaseUrl() + "/read/987654");
        assertThat(responseBody, equalTo("Some content"));
    }

    @Test
    public void getWithoutTextTest() throws IOException {
        wm.prepareOkGetWithoutBody();
        String responseBody = RequestHelper.get(wm.getBaseUrl() + "/read/456382");
        assertThat(responseBody, equalTo(""));
    }

    @Test
    public void getToIncorrectURLTest() throws IOException {
        String responseBody = RequestHelper.get(wm.getBaseUrl() + "/incorrect.commm");
        assertThat(responseBody, equalTo(""));
    }

    @Test
    public void getToEmptyURLTest() throws IOException {
        try{
            RequestHelper.get("");
        } catch (IllegalArgumentException e){
            assertThat(e.getLocalizedMessage(), equalTo("Expected URL scheme 'http' or 'https' but no colon was found"));
        }
    }

    @AfterClass
    public void tearDown() {
        wm.stop();
    }
}
