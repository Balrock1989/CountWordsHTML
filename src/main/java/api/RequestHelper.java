package api;

import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/*** Класс для работы с запросами*/
public class RequestHelper extends HttpClient {

    public static void initClient() throws IOException {
        HttpClient.initClient();
    }

    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(HttpClient.headers)
                .get()
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        return response.body().string();
    }
}
