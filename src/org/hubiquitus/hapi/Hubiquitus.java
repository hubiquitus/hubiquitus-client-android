package org.hubiquitus.hapi;

import java.io.IOException;

import org.hubiquitus.hapi.listener.HubiquitusListener;
import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.message.Request;
import org.hubiquitus.hapi.transport.Transport;
import org.hubiquitus.hapi.transport.WebSocketTransport;
import org.hubiquitus.hapi.transport.XhrTransport;
import org.hubiquitus.hapi.transport.callback.AuthCallback;
import org.hubiquitus.hapi.transport.callback.ConnectCallback;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.hubiquitus.hapi.transport.service.ServiceManager;
import org.hubiquitus.hapi.transport.service.ServiceResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

/**
 * Hubiquitus main file
 * 
 * @author teabow
 * 
 */
public class Hubiquitus implements TransportListener {
	
	/**
	 * Urn constant
	 */
	private static final String URN = "urn";
	/**
	 * Ticket constant
	 */
	private static final String TICKET = "ticket";
	/**
	 * Info request constant
	 */
	private static final String INFO = "/info";
	
	/**
	 * Boolean checking if a connection has been attempted or not
	 */
	private boolean connectAttempt = false;
	
	/**
	 * Delay for next reconnection try
	 */
	private static final int reconnectDelay = 3000;

	/**
	 * Default send timeout 
	 */
	private static final int defaultSendTimeout = 30000;

	/**
	 * Endpoint of the hubiquitus connection
	 */
	private String endpoint;
	/**
	 * Authentication data
	 */
	private JSONObject authData;
	
	/**
	 * Hubiquitus data transport
	 */
	private Transport transport;

	/**
	 * Hubiquitus events listener
	 */
	private HubiquitusListener hubiquitusListener;
	/**
	 * Hubiquitus authentication listener
	 */
	private AuthCallback authCallback;
	/**
	 * Auto reconnect boolean value
	 */
	private boolean autoReconnect;
	/**
	 * Checks if should reconnect or not
	 */
	private boolean shouldReconnect;
	/**
	 * Handler used for reconnect task
	 */
	private Handler handler;
	/**
	 * Reconnect task
	 */
	private Runnable task;
	
	private enum State {
		CONNECTING, CONNECTED, DISCONNECTED, ERROR
	}
	
	private State state;

	
	/**
	 * Constructor
	 * @param hubiquitusListener hubiquitus listener
	 */
	public Hubiquitus(HubiquitusListener hubiquitusListener, Handler handler) {
		this.hubiquitusListener = hubiquitusListener;
		this.handler = handler;
		this.autoReconnect = false;
	}
	
	/**
	 * Constructor
	 * @param hubiquitusListener hubiquitus listener
	 */
	public Hubiquitus(HubiquitusListener hubiquitusListener, Handler handler, AuthCallback authCallback) {
		this.hubiquitusListener = hubiquitusListener;
		this.handler = handler;
		this.authCallback = authCallback;
		this.autoReconnect = false;
	}

	/**
	 * Constructor
	 * @param hubiquitusListener hubiquitus listener
	 * @param handler handler used for reconnection task
	 */
	public Hubiquitus(HubiquitusListener hubiquitusListener, Handler handler, boolean autoReconnect) {
		this.hubiquitusListener = hubiquitusListener;
		this.handler = handler;
		if (autoReconnect) {
			this.autoReconnect = true;
		}
		else {
			this.autoReconnect = false;
		}
	}
	
	/**
	 * Iniatializes transport
	 */
	private void initTransport() {
		/** Test XHR
		//Hubiquitus.this.transport = new XhrTransport(Hubiquitus.this);
		//Hubiquitus.this.onTransportSet();
		//Hubiquitus.this.transport.setHandler(Hubiquitus.this.handler);
		 **/
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ServiceResponse responseWSSupported = ServiceManager.requestService(endpoint, Hubiquitus.INFO, ServiceManager.Method.GET, null);
					if (parseWebSocketSupported(responseWSSupported)) {
						Hubiquitus.this.transport = new WebSocketTransport(Hubiquitus.this);
					}
					else {
						Hubiquitus.this.transport = new XhrTransport(Hubiquitus.this);
					}
					Hubiquitus.this.onTransportSet();
					Hubiquitus.this.transport.setHandler(Hubiquitus.this.handler);
				} catch (IOException e) {
					Hubiquitus.this.onError(e.getMessage());
					Log.e(getClass().getCanonicalName(), e.getMessage());
				}
			}
		}).start();
	}
	
	/**
	 * Parse the supported websocket request
	 * @param response
	 * @return
	 */
	private boolean parseWebSocketSupported(ServiceResponse response) {
		if (response == null) {
			return false;
		}
		String text = response.getText();
		if (text != null) {
			try {
				JSONObject jsonObject = new JSONObject(text);
				if (jsonObject.has("websocket")) {
					return jsonObject.getBoolean("websocket");
				}
			} catch (JSONException e) {
				Log.e(getClass().getCanonicalName(), e.getMessage());
				e.printStackTrace();
			}
			
		}
		return false;
	}
	
	/**
	 * Handler called when transport set
	 */
	private void onTransportSet() {
		if (connectAttempt) {
			authConnect(endpoint, authData);
			connectAttempt = false;
		}
	}

	/**
	 * Connect to hubiquitus endpoint
	 * 
	 * @param endpoint
	 *            endpoint
	 * @param authData
	 *            authentication data
	 * @throws TransportException 
	 */
	public void connect(final String endpoint, final JSONObject authData) {
		this.state = State.CONNECTING;
		this.endpoint = endpoint;
		this.authData = authData;
		this.shouldReconnect = true;
		
		Log.d("DEBUG", "Hubiquitus connect");
		
		if (this.transport != null) {
			connectAttempt = false;
			authConnect(endpoint, authData);
		}
		else {
			connectAttempt = true;
			initTransport();
		}
	}
	
	private void authConnect(String endpoint, JSONObject authData) {
		
		Log.d("DEBUG", "Hubiquitus authConnect");
		
		if (this.authCallback != null) {
			this.authCallback.onAuthentication(this.authData, new ConnectCallback() {
				@Override
				public void connect(String urn, String password) {
					try {
						JSONObject authData = new JSONObject();
						authData.put(URN, urn);
						authData.put(TICKET, password);
						Hubiquitus.this.transport.connect(Hubiquitus.this.endpoint, authData);
					} catch (JSONException e) {
						Log.e(getClass().getCanonicalName(), e.getMessage());
					}
				}
			});
		}
		else {
			this.transport.connect(endpoint, authData);
		}
	}
	
	/**
	 * Initializes the reconnection handler
	 */
	public void initReconnectionHandler() {
		task = new Runnable() {
			public void run() {
				runReconnectionTask();
			}
		};
		handler.postDelayed(task, reconnectDelay);
	}
	
	/**
	 * Runs the reconnection task
	 */
	private void runReconnectionTask() {
		Hubiquitus.this.connect(endpoint, authData);
		handler.postDelayed(task, reconnectDelay);
	}
	
	/**
	 * Removes the reconnection handler pending tasks
	 */
	private void removeReconnectionHandler() {
		if (handler != null && task != null) {
			handler.removeCallbacks(task);
		}
	}

	/**
	 * Disconnect from hubiquitus endpoint
	 * @throws TransportException
	 */
	public void disconnect() throws TransportException {
		this.shouldReconnect = false;
		if (this.transport != null) {
			this.transport.silentDisconnect();
		}
	}

	/**
	 * Send a hubiquitus message
	 * 
	 * @param to
	 *            recipient of the message
	 * @param content
	 *            content of the message
	 * @param responseListener
	 *            response listener
	 * @throws TransportException 
	 */
	public void send(String to, Object content,
			ResponseListener responseListener) throws TransportException {
		Log.d(getClass().getCanonicalName(), "Hubiquitus send via " + this.transport + " : " + content + " => " + to);
		this.transport.send(to, content, defaultSendTimeout, responseListener);
	}
	
	/**
	 * Send a hubiquitus message
	 * 
	 * @param to
	 *            recipient of the message
	 * @param content
	 *            content of the message
	 * @throws TransportException 
	 */
	public void send(String to, Object content) throws TransportException {
		Log.d(getClass().getCanonicalName(), "Hubiquitus send via " + this.transport + " : " + content + " => " + to);
		this.transport.send(to, content, defaultSendTimeout, null);
	}

	/**
	 * Send a hubiquitus message
	 * 
	 * @param to
	 *            recipient of the message
	 * @param content
	 *            content of the message
	 * @param timeout
	 *            timeout of the message
	 * @param responseListener
	 *            response listener
	 * @throws TransportException 
	 */
	public void send(String to, Object content, int timeout,
			ResponseListener responseListener) throws TransportException {
		Log.d(getClass().getCanonicalName(), "Hubiquitus send via " + this.transport + " : " + content + " => " + to);
		if (this.transport != null) {
			this.transport.send(to, content, timeout, responseListener);
		}
		else {
			onError("Transport is null");
		}
	}
	
	
	/**
	 * Transport listener implementation
	 */

	@Override
	public void onConnect() {
		if (!State.CONNECTED.equals(this.state)) {
			this.state = State.CONNECTED;
			this.removeReconnectionHandler();
			this.hubiquitusListener.onConnect();
		}
	}

	@Override
	public void onDisconnect() {
		
		if (State.ERROR.equals(state) || State.DISCONNECTED.equals(state)) {
			return;
		}
		
		this.state = State.DISCONNECTED;
		
		this.hubiquitusListener.onDisconnect();
		if (this.autoReconnect && this.shouldReconnect) {
			this.initReconnectionHandler();
		}
	}
	
	public boolean isConnected() {
		return State.CONNECTED.equals(state);
	}
	
	public boolean isConnecting() {
		return State.CONNECTING.equals(state);
	}

	@Override
	public void onError(Object message) {
		
		if (State.ERROR.equals(this.state) || State.DISCONNECTED.equals(state)) {
			return;
		}
		
		this.state = State.ERROR;
		this.transport = null;
		this.hubiquitusListener.onError(message);
	}

	@Override
	public void onMessage(Request request) {
		this.hubiquitusListener.onMessage(request);
	}
	
	@Override
	public void onWebSocketPingTimeout() {
		this.transport = new XhrTransport(this);
		this.transport.connect(endpoint, authData);
	}

	@Override
	public void OnWebSocketReady() {
		// It appears sometimes the transport is not a WebSocketTransport
		if (!(this.transport instanceof WebSocketTransport)) {
			return;
		}
		if (this.authCallback != null) {
			this.authCallback.onAuthentication(this.authData, new ConnectCallback() {
				@Override
				public void connect(String urn, String password) {
					try {
						JSONObject authData = new JSONObject();
						authData.put(URN, urn);
						authData.put(TICKET, password);
						Log.d("DEBUG", "WebsocketClient? " + ((WebSocketTransport) Hubiquitus.this.transport).getWebSocketClient());
						Log.d("DEBUG", "BuildAuthData? " + Hubiquitus.this.transport.buildAuthData(authData));
						if (((WebSocketTransport) Hubiquitus.this.transport).getWebSocketClient() != null &&
								((WebSocketTransport) Hubiquitus.this.transport).getWebSocketClient().isOpen()) {
							((WebSocketTransport) Hubiquitus.this.transport).getWebSocketClient().send(Hubiquitus.this.transport.buildAuthData(authData).toString());
						}
						else {
							Hubiquitus.this.onError("Websocket null or closed");
						}
					} catch (JSONException e) {
						Log.e(getClass().getCanonicalName(), e.getMessage());
					}
				}
			});
		}
		else {
			try {
				((WebSocketTransport) Hubiquitus.this.transport).getWebSocketClient().send(Hubiquitus.this.transport.buildAuthData(authData).toString());
			} catch (JSONException e) {
				Log.e(getClass().getCanonicalName(), e.getMessage());
			}
		}
	}
}
