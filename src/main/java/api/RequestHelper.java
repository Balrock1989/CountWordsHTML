package api;

import okhttp3.Request;
import okhttp3.Response;
import org.hamcrest.MatcherAssert;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;


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
        MatcherAssert.assertThat(response.code(), equalTo(200));
        return response.body().string();
    }
}
