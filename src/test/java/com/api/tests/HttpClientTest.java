package com.api.tests;

import api.HttpClient;
import api.RequestHelper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import handlers.TextHandler;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.api.helpers.ParseHelper.parseJsonAsString;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class HttpClientTest extends RequestHelper {

    private WireMockServer wireMockServer;

    @BeforeClass
    public void setUp() throws IOException {
        HttpClient.initClient();
        wireMockServer = new WireMockServer(wireMockConfig()
                .bindAddress("127.0.0.1")
                .dynamicPort()
                .dynamicHttpsPort()
        );
        wireMockServer.start();
    }

    @Test(description = "Проверка get запроса")
    public void getRequestTest() throws IOException {
        wireMockServer.stubFor(WireMock.get(urlMatching("/read/[1-9]{6}"))
                .withHeader("Accept-Encoding", WireMock.equalTo("identity"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("Some content")));
        Request request = new Request.Builder()
                .url("http://localhost:" + wireMockServer.port() + "/read/123456")
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
        wireMockServer.stubFor(WireMock.post(urlMatching("/login/"))
                .withHeader("Accept-Encoding", WireMock.equalTo("identity"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withBody("{\"data\": {\"bearer_token\":\"q1w2e3r4t5\",}}")));
        RequestBody body = new FormBody.Builder()
                .add("login", "admin")
                .add("password", "qwerty")
                .build();
        Request request = new Request.Builder()
                .url("http://localhost:" + wireMockServer.port() + "/login/")
                .headers(HttpClient.headers)
                .post(body)
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        assertThat(response.code(), equalTo(202));
        String responseBody = response.body().string();
        assertThat(parseJsonAsString(new JSONObject(responseBody), "data.bearer_token"), equalTo("q1w2e3r4t5"));
    }

    @Test(description = "хедеры?")
    public void test3() throws IOException {
        TextHandler textHandler = new TextHandler("https://www.simbirsoft.com/");
        textHandler.start();
    }

    @AfterClass
    public void tearDown() {
        wireMockServer.stop();
    }
}
