package org.hubiquitus.hapi;

import org.hubiquitus.hapi.listener.HubiquitusListener;
import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.message.Request;
import org.hubiquitus.hapi.transport.Transport;
import org.hubiquitus.hapi.transport.XhrTransport;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.json.JSONObject;

import android.os.Handler;

/**
 * Hubiquitus main file
 * 
 * @author teabow
 * 
 */
public class Hubiquitus {
	
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
	
	private Handler handler;
	
	private Runnable task;

	
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
		// TODO : select available transport
		this.transport = new XhrTransport(new TransportListener() {

			@Override
			public void onConnect() {
				Hubiquitus.this.removeReconnectionHandler();
				Hubiquitus.this.hubiquitusListener.onConnect();
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
		});
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
	
	
	public void initReconnectionHandler() {
		task = new Runnable() {
			public void run() {
				runReconnectionTask();
			}
		};
		handler.postDelayed(task, reconnectDelay);
	}
	
	
	private void runReconnectionTask() {
		Hubiquitus.this.connect(endpoint, authData);
		handler.postDelayed(task, reconnectDelay);
	}
	
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

}
