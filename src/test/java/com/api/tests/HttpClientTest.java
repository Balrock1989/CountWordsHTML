package com.api.tests;

import api.HttpClient;
import api.RequestHelper;
import com.api.helpers.WireMockService;
import okhttp3.*;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.api.helpers.ParseHelper.parseJsonAsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class HttpClientTest extends RequestHelper {

    private WireMockService wm;

    @BeforeClass
    public void setUp() throws IOException {
        HttpClient.initClient();
        wm = WireMockService.getInstance();
        wm.start();
    }

    @Test(description = "Проверка get запроса")
    public void getRequestTest() throws IOException {
        wm.prepareOkGetWithBody();
        Request request = new Request.Builder()
                .url(wm.getBaseUrl() + "/read/123456")
                .headers(HttpClient.headers)
                .get()
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        assertThat(response.code(), equalTo(200));
        assertThat(response.body().string(), equalTo("Some content"));
        assertThat(response.header("Content-Type"), equalTo("text/xml"));
    }

    @Test(description = "Проверка post запроса")
    public void postRequestTest() throws IOException {
        RequestBody requestBody = RequestBody.create(JSON, "{\"login\":\"admin\",\"password\":\"qwerty\"}");
        wm.prepareLoginPostWithToken();
        Request request = new Request.Builder()
                .url(wm.getBaseUrl() + "/login/")
                .headers(HttpClient.headers)
                .post(requestBody)
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        JSONObject responseBody = new JSONObject(response.body().string());
        assertThat(response.code(), equalTo(202));
        assertThat(parseJsonAsString(responseBody, "data.bearer_token"), equalTo("q1w2e3r4t5"));
    }

    @Test(description = "хедеры?")
    public void test3() throws IOException {
//        TextHandler textHandler = new TextHandler("https://www.simbirsoft.com/");
//        textHandler.start();
    }

    @AfterClass
    public void tearDown() {
        wm.stop();
    }
}
