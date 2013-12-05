package org.hubiquitus.hapi;

import org.hubiquitus.hapi.listener.HubiquitusListener;
import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.transport.Transport;
import org.hubiquitus.hapi.transport.XhrTransport;
import org.hubiquitus.hapi.transport.callback.ReplyCallback;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.json.JSONObject;

/**
 * Hubiquitus main file
 * 
 * @author teabow
 * 
 */
public class Hubiquitus {

	private static final int defaultSendTimeout = 30000;

	/**
	 * Hubiquitus data transport
	 */
	private Transport transport;

	/**
	 * Hubiquitus events listener
	 */
	private HubiquitusListener hubiquitusListener;

	/**
	 * Hubiquitus constructor
	 */
	public Hubiquitus(HubiquitusListener hubiquitusListener) {
		this.hubiquitusListener = hubiquitusListener;
		// TODO : select available transport
		this.transport = new XhrTransport(new TransportListener() {

			@Override
			public void onConnect() {
				Hubiquitus.this.hubiquitusListener.onConnect();
			}

			@Override
			public void onMessage(String from, Object content, ReplyCallback replyCallback) {
				Hubiquitus.this.hubiquitusListener.onMessage(from, content, replyCallback);
			}

			@Override
			public void onDisconnect() {
				Hubiquitus.this.hubiquitusListener.onDisconnect();
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
		this.transport.connect(endpoint, authData);
	}

	/**
	 * Disconnect from hubiquitus endpoint
	 * @throws TransportException
	 */
	public void disconnect() throws TransportException {
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
