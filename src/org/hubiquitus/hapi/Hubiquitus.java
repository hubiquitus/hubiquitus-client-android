package org.hubiquitus.hapi;

import org.hubiquitus.hapi.listener.HubiquitusListener;
import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.message.Request;
import org.hubiquitus.hapi.transport.Transport;
import org.hubiquitus.hapi.transport.WebSocketTransport;
import org.hubiquitus.hapi.transport.XhrTransport;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.hubiquitus.hapi.transport.listener.TransportListener;
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
	/**
	 * Websocket transport used if websocket supported
	 */
	private WebSocketTransport tmpWebSocketTransport;
	/**
	 * Web socket transport listener
	 */
	private TransportListener tmpTransportListener;
	/**
	 * Checks if client is connected or not
	 */
	private boolean isConnected;

	
	/**
	 * Constructor
	 * @param hubiquitusListener hubiquitus listener
	 */
	public Hubiquitus(HubiquitusListener hubiquitusListener) {
		this.hubiquitusListener = hubiquitusListener;
		initTransport();
		this.autoReconnect = false;
	}

	/**
	 * Constructor
	 * @param hubiquitusListener hubiquitus listener
	 * @param handler handler used for reconnection task
	 */
	public Hubiquitus(HubiquitusListener hubiquitusListener, Handler handler) {
		this.hubiquitusListener = hubiquitusListener;
		initTransport();
		this.handler = handler;
		this.autoReconnect = true;
	}
	
	/**
	 * Iniatializes transport
	 */
	private void initTransport() {
		this.transport = new XhrTransport(this);
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
		this.endpoint = endpoint;
		this.authData = authData;
		this.shouldReconnect = true;
		this.transport.connect(endpoint, authData);
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
		this.transport.disconnect();
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
		this.transport.send(to, content, defaultSendTimeout, responseListener);
		Log.d(getClass().getCanonicalName(), "Hubiquitus send via " + this.transport);
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
		this.transport.send(to, content, timeout, responseListener);
	}
	
	
	/**
	 * Transport listener implementation
	 */

	@Override
	public void onConnect() {
		if (!Hubiquitus.this.isConnected) {
			this.removeReconnectionHandler();
			this.hubiquitusListener.onConnect();
			this.isConnected = true;
		}
	}

	@Override
	public void onMessage(Request request) {
		this.hubiquitusListener.onMessage(request);
	}

	@Override
	public void onDisconnect() {
		this.hubiquitusListener.onDisconnect();
		if (this.autoReconnect && this.shouldReconnect) {
			this.initReconnectionHandler();
		}
		this.isConnected = false;
	}

	@Override
	public void onError(String message) {
		this.hubiquitusListener.onError(message);
	}

	@Override
	public void onWebSocketSupported() {
		Log.d(getClass().getCanonicalName(),"onWebSocketSupported");
		if (this.transport instanceof XhrTransport) {
			this.tmpTransportListener = new TransportListener() {
				@Override
				public void onXhrClosed() {}
				
				@Override
				public void onWebSocketSupported() {}
				
				@Override
				public void onWebSocketReady() {
					Log.d(getClass().getCanonicalName(), "onWebSocketReady");
					if (Hubiquitus.this.transport instanceof XhrTransport) {
						((XhrTransport) Hubiquitus.this.transport).closeConnection();
					}
				}
				
				@Override
				public void onConnect() {
					Log.d(getClass().getCanonicalName(),"Switch to web socket");
					if (!Hubiquitus.this.isConnected) {
						Hubiquitus.this.removeReconnectionHandler();
						Hubiquitus.this.hubiquitusListener.onConnect();
						Hubiquitus.this.isConnected = true;
					}
				}

				@Override
				public void onMessage(Request request) {
					Hubiquitus.this.hubiquitusListener.onMessage(request);
				}

				@Override
				public void onDisconnect() {
					Hubiquitus.this.hubiquitusListener.onDisconnect();
					if (Hubiquitus.this.autoReconnect && Hubiquitus.this.shouldReconnect) {
						Hubiquitus.this.initReconnectionHandler();
					}
				}

				@Override
				public void onError(String message) {
					Hubiquitus.this.hubiquitusListener.onError(message);
				}
			};
			this.tmpWebSocketTransport = new WebSocketTransport(this.tmpTransportListener);
			this.tmpWebSocketTransport.connect(this.endpoint, this.authData);
		}
	}

	@Override
	public void onWebSocketReady() {}

	@Override
	public void onXhrClosed() {
		Log.d(getClass().getCanonicalName(), "onXhrClosed");
		this.transport = this.tmpWebSocketTransport;
	}

}
