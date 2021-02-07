package api;

import com.cedarsoftware.util.io.JsonIoException;
import com.cedarsoftware.util.io.JsonWriter;

import java.io.IOException;
import java.util.logging.Logger;

import okhttp3.*;

import okhttp3.internal.concurrent.TaskRunner;
import okio.Buffer;

/** Конфигурация перехватчика запросов*/
public final class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Logger logger = TaskRunner.Companion.getLogger();

        Request request = chain.request();
        long t1 = System.nanoTime();
        Response response = chain.proceed(request);
        logger.info(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
        long t2 = System.nanoTime();
        MediaType contentType = null;
        String bodyString = null;
        if (response.body() != null) {
            contentType = response.body().contentType();
            bodyString = response.body().string();
        }


        double time = (double) (t2 - t1) / 1000000.00;
        switch (request.method()) {
            case "GET":
                logger.info(String.format("GET  %s in %.1fms %n%s %nResponse: %d %nRequest body: %s %n %n------------------------------------------- %n", request.url(), time, request.headers(), response.code(), this.stringifyResponseBody(bodyString)));
                break;
            case "PUT":
                logger.info(String.format("PUT  %s in %.1fms %n%sRequest body: %s %n %nResponse: %d %nRequest body: %s %n %n------------------------------------------- %n", request.url(), time, request.headers(), FormatRequest.stringifyRequestBody(request), response.code(), this.stringifyResponseBody(bodyString)));
                break;
            case "POST":
                logger.info(String.format("POST  %s in %.1fms %n%sRequest body: %s %n %nResponse: %d %nRequest body: %s %n %n------------------------------------------- %n", request.url(), time, request.headers(), FormatRequest.stringifyRequestBody(request), response.code(), this.stringifyResponseBody(bodyString)));
                break;
            case "PATCH":
                logger.info(String.format("PATCH  %s in %.1fms %n%sRequest body: %s %n %nResponse: %d %nRequest body: %s %n %n------------------------------------------- %n", request.url(), time, request.headers(), FormatRequest.stringifyRequestBody(request), response.code(), this.stringifyResponseBody(bodyString)));
                break;
            case "DELETE":
                logger.info(String.format("DELETE  %s in %.1fms %n%sRequest body: %s %n %nResponse: %d %nRequest body: %s %n %n------------------------------------------- %n", request.url(), time, request.headers(), FormatRequest.stringifyRequestBody(request), response.code(), this.stringifyResponseBody(bodyString)));
                break;
            default:
                logger.severe("Unknown request: " + request.method());
                break;
        }

        if (response.body() != null) {
            ResponseBody body = ResponseBody.create(bodyString, contentType);
            return response.newBuilder().body(body).build();
        } else {
            return response;
        }
    }

    private String stringifyResponseBody(String responseBody) {
        try {
            return JsonWriter.formatJson(responseBody);
        } catch (JsonIoException var4) {
            TaskRunner.Companion.getLogger().severe("The response body contains invalid JSON");
            return responseBody;
        }
    }

    public static final class FormatRequest {
        private static final String F_BREAK = " %n";
        private static final String F_URL = " %s";
        private static final String F_TIME = " in %.1fms";
        private static final String F_HEADERS = "%s";
        private static final String F_RESPONSE = " %nResponse: %d";
        private static final String F_BODY = "Request body: %s";
        private static final String F_BREAKER = " %n------------------------------------------- %n";
        private static final String F_REQUEST_WITHOUT_BODY = " %s in %.1fms %n%s";
        private static final String F_RESPONSE_WITHOUT_BODY = " %nResponse: %d %n %n------------------------------------------- %n";
        private static final String F_REQUEST_WITH_BODY = " %s in %.1fms %n%sRequest body: %s %n";
        private static final String F_RESPONSE_WITH_BODY = " %nResponse: %d %nRequest body: %s %n %n------------------------------------------- %n";

        private static String stringifyRequestBody(Request request) throws IOException {
            Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            Logger logger = TaskRunner.Companion.getLogger();
            try {
                copy.body().writeTo(buffer);
                return JsonWriter.formatJson(buffer.readUtf8());
            } catch (JsonIoException | IOException var6) {
                logger.severe("The request body contains invalid JSON");
                copy.body().writeTo(buffer);
                return buffer.readUtf8();
            }
        }
    }
}

