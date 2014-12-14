package com.jxhobo.http.dataservice.http;

import android.content.Context;
import android.content.res.AssetManager;
import com.jxhobo.http.util.Log;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.concurrent.Executor;

/**
 * HTTPS支持
 *
 * @author zhaishixi
 */
public class HttpsService extends DefaultHttpService {
    private static final String TAG = "HttpsService";
    private static final int SERVER_PORT = 8443;
    private final static String SERVICE_CER = "tomcat.cer";
    //	private final static String ANDROID_CLIENT_BKS = "client.bks";
    private final static String CLIENT_TRUST = "client_130";
    private final static String CLIENT_P12 = "clientp12_130";
    private static final String CLIENT_KET_PASSWORD = "123456";
    private static final String trustPass = "123456";
    private static final String keyPass = "123456";

    /**
     *
     * https服务
     *
     * @param context
     * @param executor
     */
    public HttpsService(Context context, Executor executor) {
        super(context, executor);
    }

    public HttpClient getHttpClient() {
        HttpClient httpClient = httpClients.poll();
        if (httpClient == null)
            httpClient = createHttpClient();
        return httpClient;
    }

    /**
     * 创建httpclient
     *
     * @return
     */
    @Override
    public HttpClient createHttpClient() {

        DefaultHttpClient httpclient = new DefaultHttpClient();
        KeyStore ts = null;
        try {
            ts = KeyStore.getInstance("BKS");
            KeyStore ks = KeyStore.getInstance("PKCS12");
            AssetManager am = context.getAssets();
            InputStream tsStream = am.open(CLIENT_TRUST);
            InputStream ksStream = am.open(CLIENT_P12);
            try {
                ts.load(tsStream, trustPass.toCharArray());
                ks.load(ksStream, keyPass.toCharArray());
            } catch (Exception e) {
                Log.e(e.getMessage());
            } finally {
                tsStream.close();
                ksStream.close();
            }
            SSLSocketFactory socketFactory = new SSLSocketFactory(ks, keyPass, ts);
            Scheme sch = new Scheme("https", socketFactory, SERVER_PORT);
            httpclient.getConnectionManager().getSchemeRegistry().register(sch);
            HttpConnectionParams.setSoTimeout(httpclient.getParams(), timeout);
//            httpclient.setHttpRequestRetryHandler(retryHandler);
            return httpclient;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            Log.e(e.getMessage());
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
            Log.e(e.getMessage());
        } catch (KeyManagementException e) {
            e.printStackTrace();
            Log.e(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(e.getMessage());
        }
        return null;
    }

    /**
     * 设置处理请求异常机制
     *
     * @author zhaishixi
     */
    private HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {

        @Override
        public boolean retryRequest(IOException exception, int executionCount,
                                    HttpContext context) {

            if (executionCount >= 3) {
                log(TAG, "超过最大重连次数");
                // 如果超过最大重试次数，那么就不要继续了
                return false;
            }
            if (exception instanceof NoHttpResponseException) {
                log(TAG, exception.getLocalizedMessage());
                // 如果服务器丢掉了连接，那么就重试
                return true;
            }
            if (exception instanceof SSLHandshakeException) {
                log(TAG, exception.getLocalizedMessage());
                // 不要重试SSL握手异常
                return false;
            }
            HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                // 如果请求被认为是幂等的，那么就重试
                return true;
            }
            return false;
        }
    };
}
