package api;

import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import util.Log;

import java.io.IOException;
import java.rmi.UnexpectedException;

/*** Класс для работы с запросами*/
public class RequestHelper extends HttpClient {

    public static void initClient() throws IOException {
        HttpClient.initClient();
    }

    public static String get(String url) throws IOException {
        Buffer buffer = new Buffer();
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .headers(HttpClient.headers)
                    .get()
                    .build();
            Response response = HttpClient.client.newCall(request).execute();
            if (response.isSuccessful()){
                buffer.writeUtf8(response.body().string());
            }
        } catch (IllegalArgumentException | UnexpectedException e){
            e.printStackTrace();
            Log.severe(RequestHelper.class, e);
        }
        return buffer.readUtf8();
    }
}
