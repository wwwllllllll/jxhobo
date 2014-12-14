package com.jxhobo.http.dataservice.http;

import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * {@link org.apache.http.conn.ssl.SSLSocketFactory}
 *
 * @author zhaishixi
 */
public class SSLSocketFactoryEx extends SSLSocketFactory {

    SSLContext sslContext = SSLContext.getInstance("TLS");

    /**
     * 双向验证，只有已授权的客户端才能访问服务端。需要服务器支持
     *
     * @param truststore
     * @param kmf
     * @param tmf
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyManagementException
     * @throws java.security.KeyStoreException
     * @throws java.security.UnrecoverableKeyException
     */
    public SSLSocketFactoryEx(KeyStore truststore, KeyManagerFactory kmf,
                              TrustManagerFactory tmf) throws NoSuchAlgorithmException,
            KeyManagementException, KeyStoreException,
            UnrecoverableKeyException {
        super(truststore);
        // 客户端和服务端双向验证
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    }

    /**
     * 单项验证，服务端不验证客户端，所有客户端都可访问
     *
     * @param truststore
     * @throws java.security.KeyManagementException
     * @throws java.security.UnrecoverableKeyException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyStoreException
     */
    public SSLSocketFactoryEx(KeyStore truststore)
            throws KeyManagementException, UnrecoverableKeyException,
            NoSuchAlgorithmException, KeyStoreException {
        super(truststore);

        // 绕过证书（信任证书）的验证？
        TrustManager tm = new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;

            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

        };
        // init的第一个参数是验证客户端证书，第二个参数验证CA证书，由于私签的CA，所有绕过证书安全认正？
        sslContext.init(null, new TrustManager[]{tm}, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port,
                               boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port,
                autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}
