package api;

import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/*** Класс для работы с запросами*/
public class RequestHelper extends HttpClient {
    private final HttpClient HTTP_CLIENT = new HttpClient();

    public void initClient() throws IOException {
        HTTP_CLIENT.initProperties();
        HTTP_CLIENT.initClient();
    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(HTTP_CLIENT.headers)
                .get()
                .build();
        Response response = HTTP_CLIENT.client.newCall(request).execute();
        return response.body().string();
    }
}
