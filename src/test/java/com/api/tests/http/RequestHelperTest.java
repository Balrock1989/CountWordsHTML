package com.api.tests.http;

import api.RequestHelper;
import com.api.BaseTest;
import com.api.helpers.WireMockService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class RequestHelperTest extends BaseTest {
    private WireMockService wm;

    @BeforeClass
    public void setUp() throws IOException {
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
        String responseBody = RequestHelper.get(wm.getBaseUrl() + "/withoutBody/456382");
        assertThat(responseBody, equalTo(""));
    }

    @Test
    public void getToEmptyURLTest() throws IOException {
        try {
            RequestHelper.get("");
        } catch (IllegalArgumentException e) {
            assertThat(e.getLocalizedMessage(), equalTo("Expected URL scheme 'http' or 'https' but no colon was found"));
        }
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
