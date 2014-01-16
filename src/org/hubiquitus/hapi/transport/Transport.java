package org.hubiquitus.hapi.transport;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.message.Message;
import org.hubiquitus.hapi.message.MessageType;
import org.hubiquitus.hapi.message.Request;
import org.hubiquitus.hapi.transport.callback.ReplyCallback;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Transport class
 * 
 * @author teabow
 * 
 */
public abstract class Transport {

	protected static final String TYPE = "type";
	protected static final String AUTH_DATA = "authData";
	protected static final String TO = "to";
	protected static final String FROM = "from";
	protected static final String PAYLOAD = "payload";
	protected static final String CB = "cb";
	protected static final String ID = "id";
	protected static final String DATE = "date";
	protected static final String CONTENT = "content";
	protected static final String CODE = "code";
	protected static final String ERR = "err";
	protected static final String TIMEOUT = "TIMEOUT";
	
	protected static String serverId;
	protected static String sessionId;

	/**
	 * Response message queue
	 */
	protected HashMap<String, ResponseListener> responseQueue;

	/**
	 * Transport listener
	 */
	protected TransportListener transportListener;

	private Timer sendTimeoutTimer;

	/**
	 * Constructor
	 * 
	 * @param transportListener
	 *            transport listener
	 */
	public Transport(TransportListener transportListener) {
		this.transportListener = transportListener;
		this.responseQueue = new HashMap<String, ResponseListener>();
	}

	/**
	 * Connect to endpoint
	 * 
	 * @param endpoint
	 *            endpoint
	 * @param authData
	 *            authentication data
	 */
	public abstract void connect(String endpoint, JSONObject authData);

	/**
	 * Disconnect from endpoint
	 * 
	 * @throws TransportException
	 */
	public abstract void disconnect() throws TransportException;

	/**
	 * Send a hubiquitus message
	 * 
	 * @param jsonObject
	 *            the json object describing the message
	 * @throws TransportException
	 */
	protected abstract void send(JSONObject jsonObject)
			throws TransportException;

	/**
	 * Send a hubiquitus message
	 * 
	 * @param to
	 *            the recipient of the message
	 * @param content
	 *            the content of the message
	 * @param timeout
	 *            the timeout of the message
	 * @param responseListener
	 *            the response listener
	 * @return a json object describing the message
	 * @throws TransportException
	 * @throws JSONException
	 */
	public JSONObject send(String to, Object content, int timeout,
			ResponseListener responseListener) throws TransportException {

		JSONObject jsonMessage = null;
		try {
			jsonMessage = buildMessage(to, content, responseListener != null);
			final String messageId = jsonMessage.getString(ID);
			this.sendTimeoutTimer = new Timer();
			this.sendTimeoutTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					ResponseListener responseListener = responseQueue
							.get(messageId);
					if (responseListener != null) {
						JSONObject jsonErr = new JSONObject();
						JSONObject jsonCode = new JSONObject();
						try {
							jsonCode.put(CODE, TIMEOUT);
							jsonErr.put(ERR, CODE);
						} catch (JSONException e) {
							Log.e(getClass().getCanonicalName(), e.getMessage());
						}
						responseListener.onResponse(jsonErr, null);
						responseQueue.remove(messageId);
					}
				}
			}, timeout);
		} catch (JSONException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		}
		return jsonMessage;
	}

	/**
	 * Build an authentication message
	 * 
	 * @param authData
	 *            authentication data
	 * @return the builded message
	 * @throws JSONException
	 */
	protected JSONObject buildAuthData(JSONObject authData)
			throws JSONException {
		JSONObject authDataMessage = new JSONObject();
		authDataMessage.put(TYPE, MessageType.login.name());
		authDataMessage.put(AUTH_DATA, authData);
		return authDataMessage;
	}

	/**
	 * Build a hubiquitus message to send
	 * 
	 * @param to
	 *            the recipient of the message
	 * @param content
	 *            the content of the message
	 * @param cb
	 *            true if callback needed
	 * @return the builded message
	 * @throws JSONException
	 */
	protected JSONObject buildMessage(String to, Object content, boolean cb)
			throws JSONException {
		JSONObject message = new JSONObject();
		message.put(TO, to);
		message.put(ID, UUID.randomUUID().toString());
		message.put(DATE, new Date().getTime());
		message.put(TYPE, MessageType.req.name());
		message.put(CONTENT, content);
		if (cb) {
			message.put(CB, cb);
		}
		return message;
	}

	/**
	 * Builds a hubiquitus response object
	 * 
	 * @param from
	 *            recipient of the message
	 * @param messageId
	 *            the message id of the message
	 * @param err
	 *            the error
	 * @param content
	 *            the content of the message
	 * @return
	 * @throws JSONException
	 */
	protected JSONObject buildResponse(String from, String messageId,
			Object err, Object content) throws JSONException {
		JSONObject response = new JSONObject();
		response.put(TYPE, MessageType.res.name());
		response.put(ID, messageId);
		response.put(TO, from);
		response.put(CONTENT, content);
		response.put(ERR, err);
		return response;
	}

	/**
	 * Build a hubiquitus request response object
	 * 
	 * @param from
	 *            recipient of the message
	 * @param content
	 *            the content of the message
	 * @param messageId
	 *            the message id of the message
	 * @return
	 */
	private Request buildRequest(final String from, Object content,
			final String messageId) {
		Request request = new Request();
		request.setContent(content);
		request.setFrom(from);
		request.setReplyCallback(new ReplyCallback() {
			@Override
			public void reply(Object err, Object content) {
				try {
					JSONObject response = buildResponse(from, messageId, err,
							content);
					Transport.this.send(response);
				} catch (TransportException e) {
					Log.e(getClass().getCanonicalName(), e.getMessage());
				} catch (JSONException e) {
					Log.e(getClass().getCanonicalName(), e.getMessage());
				}
			}
		});
		return request;
	}

	/**
	 * Handler for json messages
	 * 
	 * @param jsonMessage
	 *            a json message
	 * @throws JSONException
	 */
	protected void handleMessage(JSONObject jsonMessage) throws JSONException {
		
		String messageId = null;
		String from = null;
		Object err = null, content = null;
		if (jsonMessage.has(ID)) {
			messageId = jsonMessage.getString(ID);
		}
		if (jsonMessage.has(FROM)) {
			from = jsonMessage.getString(FROM);
		}
		if (jsonMessage.has(CONTENT)) {
			content = jsonMessage.get(CONTENT);
			if (jsonMessage.has(ERR)) {
				err = jsonMessage.get(ERR);
				if (err == JSONObject.NULL) {
					err = null;
				}
			}
		}

		if (jsonMessage.has(TYPE)) {

			String mesageType = jsonMessage.getString(TYPE);

			switch (MessageType.valueOf(mesageType)) {
			case login:
				this.transportListener.onConnect();
				if (this instanceof WebSocketTransport) {
					this.transportListener.onWebSocketReady();
				}
				break;
			case req:
				Request request = buildRequest(from, content, messageId);
				transportListener.onMessage(request);
				break;
			case res:
				
				ResponseListener responseListener = responseQueue
						.get(messageId);
				if (responseListener != null) {
					Message message = new Message();
					message.setContent(content);
					message.setFrom(from);
					responseListener.onResponse(err, message);
					responseQueue.remove(messageId);
				}
				break;
			default:
				break;
			}
		}
	}
}
