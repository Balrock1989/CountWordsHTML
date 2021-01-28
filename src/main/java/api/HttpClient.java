package api;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import java.security.cert.X509Certificate;
import java.util.Properties;

/*** Конфигурация HTTP клиента*/
public class HttpClient {

    public final MediaType mediaType = MediaType.Companion.get("application/json; charset=utf-8");;
    public final Headers headers = (new okhttp3.Headers.Builder()).add("Accept-Encoding", "identity").build();
    public static Proxy proxy = new Proxy(Type.HTTP, (new InetSocketAddress("127.0.0.1", 8888)));
    public static Properties appProps;
    public static OkHttpClient client;

    public static void initClient() throws IOException {
        String appConfigPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "myProperties.properties";
        appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        client = Boolean.parseBoolean(appProps.getProperty("config.proxy")) ? getUnsafeOkHttpClient() : getOkHTTPClient();
//        client = Boolean.parseBoolean(System.getProperty("config.proxy")) ? getUnsafeOkHttpClient() : getOkHTTPClient();
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{(new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {

                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            })};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            Builder builder = new Builder();
            builder.addInterceptor(new LoggingInterceptor());
            if (Boolean.parseBoolean(appProps.getProperty("config.logger"))) {
                builder.addNetworkInterceptor((new LoggingInterceptor()));
            }
            builder.proxy(proxy);
            return builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static OkHttpClient getOkHTTPClient() {
        try {
            Builder builder = new Builder();
            if (Boolean.parseBoolean(appProps.getProperty("config.logger"))) {
                builder.addNetworkInterceptor((new LoggingInterceptor()));
            }
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException((e));
        }
    }
}
