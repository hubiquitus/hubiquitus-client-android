package org.hubiquitus.hapi.transport.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

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

import android.util.Log;

/**
 * Service qui gère les connexions au Web Service de Fnac.com
 * 
 * @author j.varin
 * 
 */
public class ConnexionService {

	/**
	 * L'instance unique du service.
	 */
	private static ConnexionService SINGLETON = new ConnexionService();

	/**
	 * Le gestionnaire de connexion.
	 */
	private ClientConnectionManager connexionManager;
	
	/**
	 * Les paramètres HTTP.
	 */
	private HttpParams httpParameters;

	/**
	 * Méthode statique qui retourne l'instance unique du service.
	 * 
	 * @return Le service de connexion.
	 */
	public static ConnexionService getConnexionService() {
		return SINGLETON;
	}

	/**
	 * Le constructeur privé du service.
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnrecoverableKeyException 
	 * @throws KeyManagementException 
	 */
	private ConnexionService() {
		
		try {

			this.httpParameters = new BasicHttpParams();
			HttpProtocolParams
					.setVersion(this.httpParameters, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(this.httpParameters, "utf-8");
			this.httpParameters.setBooleanParameter(
					"http.protocol.expect-continue", false);
			int timeoutConnection = 30000;
			HttpConnectionParams.setConnectionTimeout(this.httpParameters,
					timeoutConnection);
			int timeoutSocket = 30000;
			
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);
			final SSLSocketFactory sslSocketFactory = new MySSLSocketFactory(trustStore);
			sslSocketFactory
					.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			
			HttpConnectionParams.setSoTimeout(this.httpParameters, timeoutSocket);
	
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sslSocketFactory, 443));
	
			this.connexionManager = new ThreadSafeClientConnManager(httpParameters,
					registry);
			
		} catch(Exception e) {
			Log.w(getClass().getCanonicalName(), e);
		}
	}

	/**
	 * Retourne un client de connexion issue du pool du connexion.
	 * 
	 * @return Un client HTTP issue du pool de connexion.
	 */
	public HttpClient getHttpClient() {
		return new DefaultHttpClient(this.connexionManager, this.httpParameters);
	}
}
