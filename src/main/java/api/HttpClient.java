package api;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.Properties;

/*** Конфигурация HTTP клиента*/
public class HttpClient {

    public Headers headers = (new okhttp3.Headers.Builder()).add("Accept-Encoding", "identity").build();
    public Proxy proxy = new Proxy(Type.HTTP, (new InetSocketAddress("127.0.0.1", 8877)));
    public OkHttpClient client;
    private static boolean needProxy;
    private static boolean needLogger;

    public void initProperties() throws IOException {
        String appConfigPath = Paths.get(System.getProperty("user.dir"),"target", "classes", "config.properties").toString();
        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        needProxy = Boolean.parseBoolean(appProps.getProperty("config.proxy"));
        needLogger = Boolean.parseBoolean(appProps.getProperty("config.logger"));
    }

    public void initClient() throws IOException {
        client = needProxy ? getUnsafeOkHttpClient() : getOkHTTPClient();
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{(new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) { }
                public void checkServerTrusted(X509Certificate[] chain, String authType) { }
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            })};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            Builder builder = new Builder();
            if (needLogger) {
                builder.addNetworkInterceptor((new LoggingInterceptor()));
            }
            builder.proxy(proxy);
            return builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private OkHttpClient getOkHTTPClient() {
        try {
            Builder builder = new Builder();
            if (needLogger) {
                builder.addNetworkInterceptor((new LoggingInterceptor()));
            }
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException((e));
        }
    }
}
