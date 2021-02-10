package api;

import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

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
        Buffer buffer = new Buffer();
        if (response.isSuccessful()){
            buffer.writeUtf8(response.body().string());
        }
        return buffer.readUtf8();
    }
}
