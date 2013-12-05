package org.hubiquitus.hapi.transport;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Web socket transport class
 * 
 * @author teabow
 * 
 */
public class WebSocketTransport extends Transport {
	
	private static final String WSS = "wss";

	/**
	 * Web socket client
	 */
	private WebSocketClient webSocketClient;
	/**
	 * Authentication data
	 */
	private JSONObject authData;

	/**
	 * Constructor
	 * 
	 * @param transportListener
	 *            transport listener
	 */
	public WebSocketTransport(TransportListener transportListener) {
		super(transportListener);
	}

	/**
	 * Initialize the socket
	 * 
	 * @param endpoint
	 *            endpoint
	 */
	private void initSocket(String endpoint) {

		URI endpointURI;

		try {
			endpointURI = new URI(endpoint);

			this.webSocketClient = new WebSocketClient(endpointURI) {

				@Override
				public void onOpen(ServerHandshake arg0) {
					JSONObject authDataMessage = null;
					try {
						authDataMessage = buildAuthData(authData);
						this.send(authDataMessage.toString());
					} catch (JSONException e) {
						Log.e(getClass().getCanonicalName(), e.getMessage());
					}
				}

				@Override
				public void onMessage(String mesage) {
					JSONObject jsonMessage = null;
					try {
						jsonMessage = new JSONObject(mesage);
						WebSocketTransport.this.handleMessage(jsonMessage);
					} catch (JSONException e) {
						Log.e(getClass().getCanonicalName(), e.getMessage());
					}
				}

				@Override
				public void onError(Exception arg0) {
					WebSocketTransport.this.transportListener.onError(arg0.getMessage());
				}

				@Override
				public void onClose(int arg0, String arg1, boolean arg2) {
					WebSocketTransport.this.transportListener.onDisconnect();
				}
			};
			
			if (endpoint.startsWith(WSS)) {
				SSLContext sslContext = null;
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, null);

                SSLSocketFactory factory = sslContext.getSocketFactory();
                this.webSocketClient.setSocket(factory.createSocket());
			}
			
		} catch (URISyntaxException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		} catch (KeyManagementException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		} catch (IOException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		}
	}

	@Override
	public void connect(String endpoint, JSONObject authData) {
		this.authData = authData;
		if (this.webSocketClient == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(endpoint).append("/websocket");
			this.initSocket(sb.toString());
			this.webSocketClient.connect();
		}
	}

	@Override
	public JSONObject send(String to, Object content, int timeout,
			ResponseListener responseListener) throws TransportException {
		JSONObject jsonMessage = super.send(to, content, timeout, responseListener);
		if (this.webSocketClient == null) {
			throw new TransportException("webSocketClient is null");
		}
		this.webSocketClient.send(jsonMessage.toString());
		try {
			this.responseQueue.put(jsonMessage.getString(ID), responseListener);
		} catch (JSONException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		}
		return jsonMessage;
	}
	
	@Override
	protected void send(JSONObject jsonObject) throws TransportException {
		if (this.webSocketClient == null) {
			throw new TransportException("webSocketClient is null");
		}
		this.webSocketClient.send(jsonObject.toString());
	}

	@Override
	public void disconnect() throws TransportException {
		if (this.webSocketClient == null) {
			throw new TransportException("webSocketClient is null");
		}
		this.webSocketClient.close();
		this.webSocketClient = null;
	}

}
