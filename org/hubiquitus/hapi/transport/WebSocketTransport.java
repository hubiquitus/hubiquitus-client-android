package org.hubiquitus.hapi.transport;

import java.net.URI;
import java.net.URISyntaxException;

import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

/**
 * Web socket transport class
 * 
 * @author teabow
 * 
 */
public class WebSocketTransport extends Transport {

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
					JSONObject authDataMessage = buildAuthData(authData);
					this.send(authDataMessage.toString());
				}

				@Override
				public void onMessage(String mesage) {
					JSONObject jsonMessage = new JSONObject(mesage);
					WebSocketTransport.this.handleMessage(jsonMessage);
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
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connect(String endpoint, JSONObject authData) {
		this.authData = authData;
		if (this.webSocketClient == null) {
			this.initSocket(endpoint);
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
		this.responseQueue.put(jsonMessage.getString(ID), responseListener);
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
