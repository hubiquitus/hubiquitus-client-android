package org.hubiquitus.hapi.transport.service;

import android.util.Log;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.security.KeyStore;

/**
 * Web service connection manager singleton
 *
 * @author j.varin
 */
public class WebServiceConnexionManager {
    private static final int CONNEXION_TIMEOUT = 15000;
    private static final int SOCKET_TIMEOUT = 20000;

    private static WebServiceConnexionManager SINGLETON = new WebServiceConnexionManager();

    private ClientConnectionManager mConnexionManager;
    private HttpParams mHttpParameters;

    public static WebServiceConnexionManager getConnexionService() {
        return SINGLETON;
    }

    private WebServiceConnexionManager() {

        try {

            mHttpParameters = new BasicHttpParams();
            mHttpParameters.setBooleanParameter("http.protocol.expect-continue", false);

            HttpProtocolParams.setVersion(mHttpParameters, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(mHttpParameters, "utf-8");

            HttpConnectionParams.setConnectionTimeout(mHttpParameters, CONNEXION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(mHttpParameters, SOCKET_TIMEOUT);

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sslSocketFactory = new MySSLSocketFactory(trustStore);
            sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sslSocketFactory, 443));

            mConnexionManager = new ThreadSafeClientConnManager(mHttpParameters, registry);

        } catch (Exception e) {
            Log.w(getClass().getCanonicalName(), e);
        }
    }

    /**
     * @return a new {@link org.apache.http.client.HttpClient}
     */
    public HttpClient getHttpClient() {
        return new DefaultHttpClient(this.mConnexionManager, this.mHttpParameters);
    }
}
