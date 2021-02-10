package com.api.helpers;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockService {
    public static WireMockServer wm;
    public static WireMockService instance;

    public static synchronized WireMockService getInstance() {
        if (instance == null)
            instance = new WireMockService();

        return instance;
    }

    private WireMockService() {
        wm = new WireMockServer(wireMockConfig()
                .bindAddress("127.0.0.1")
                .dynamicPort()
                .dynamicHttpsPort()
        );
    }

    public void start(){
        wm.start();
    }

    public void stop(){
        wm.stop();
    }

    public void resetRequests(){
        wm.resetRequests();
    }

    public String getBaseUrl(){
        return "http://" + wm.getOptions().bindAddress() + ":" + wm.port();
    }

    public void prepareOkGetWithBody(){
        wm.stubFor(WireMock.get(urlMatching("/read/[1-9]{6}"))
                .withHeader("Accept-Encoding", WireMock.equalTo("identity"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("Some content")));
    }

    public void prepareLoginPostWithToken(){
        wm.stubFor(WireMock.post(urlEqualTo("/login/"))
                .withRequestBody(matching(".*login.*password.*"))
                .withHeader("Accept-Encoding", WireMock.equalTo("identity"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withBody("{\"data\": {\"bearer_token\":\"q1w2e3r4t5\",}}")));
    }

    public int prepareGetWithText(){
        String[] words = {"one", "two", "ТРИ", "чеТЫре", "пять", "шесть", "семь", "восемь", "восемь", "девять", "10", "one"};
        String body = String.format("%s \"%s\",>%s<;[%s]\t:{%s}\r(%s)\\?%s&%s\n.%s,«%s»-%s№#*<excludeTag>@%s&", (Object[])words);
        wm.stubFor(WireMock.get(urlEqualTo("/text"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(body)));
        return words.length;
    }
}
