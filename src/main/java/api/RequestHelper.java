package api;

import okhttp3.*;
import org.hamcrest.MatcherAssert;

import java.io.IOException;

import static org.hamcrest.Matchers.*;


public class RequestHelper extends HttpClient {

        public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        public static String get(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            MatcherAssert.assertThat(response.code(), equalTo(200));
            return response.body().string();
        }
}
