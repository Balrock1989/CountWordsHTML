package api;

import okhttp3.Request;
import okhttp3.Response;
import org.hamcrest.MatcherAssert;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;


public class RequestHelper extends HttpClient {
//    private HttpClient httpClient = new HttpClient();
//
//    public void initClient() {
//        httpClient.
//    }

    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        MatcherAssert.assertThat(response.code(), equalTo(200));
        return response.body().string();
    }
}
