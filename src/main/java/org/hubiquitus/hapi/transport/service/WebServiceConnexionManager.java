package org.hubiquitus.hapi.transport.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Web service connection manager singleton
 *
 * @author j.varin
 */
public class WebServiceConnexionManager {
    private static final int CONNEXION_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 15000;


    public static HttpURLConnection getUrlConnection (String url) throws IOException, NoSuchAlgorithmException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setConnectTimeout(CONNEXION_TIMEOUT);
        connection.setReadTimeout(SOCKET_TIMEOUT);
        connection.setUseCaches(false);
        connection.setChunkedStreamingMode(0);
        connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
        connection.setRequestProperty("Expect", "100-continue");

        if (connection instanceof HttpsURLConnection) {
            SSLContext context = SSLContext.getInstance("TLS");

            try {
                context.init(null, new TrustManager[]{new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }},null);
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            ((HttpsURLConnection) connection).setSSLSocketFactory(context.getSocketFactory());
        }
        return connection;
    }
}
