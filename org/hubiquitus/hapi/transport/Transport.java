package org.hubiquitus.hapi.transport;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.message.MessageType;
import org.hubiquitus.hapi.transport.callback.ReplyCallback;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.json.JSONObject;

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
	protected static final String ID = "id";
	protected static final String DATE = "date";
	protected static final String CONTENT = "content";
	protected static final String ERR = "err";
	protected static final String TIMEOUT = "TIMEOUT";

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
	 */
	public JSONObject send(String to, Object content, int timeout,
			ResponseListener responseListener) throws TransportException {
		JSONObject jsonMessage = buildMessage(to, content);
		final String messageId = jsonMessage.getString(ID);
		this.sendTimeoutTimer = new Timer();
		this.sendTimeoutTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				ResponseListener responseListener = responseQueue
						.get(messageId);
				if (responseListener != null) {
					JSONObject jsonErr = new JSONObject();
					jsonErr.put(ERR, TIMEOUT);
					responseListener.onResponse(jsonErr, null, null);
					responseQueue.remove(messageId);
				}
			}
		}, timeout);
		return jsonMessage;
	}

	/**
	 * Build an authentication message
	 * 
	 * @param authData
	 *            authentication data
	 * @return the builded message
	 */
	protected JSONObject buildAuthData(JSONObject authData) {
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
	 * @return the builded message
	 */
	protected JSONObject buildMessage(String to, Object content) {
		JSONObject message = new JSONObject();
		message.put(TO, to);
		message.put(ID, UUID.randomUUID().toString());
		message.put(DATE, new Date().getTime());
		message.put(TYPE, MessageType.message.name());
		JSONObject jsonContent = new JSONObject();
		jsonContent.put(CONTENT, content);
		message.put(PAYLOAD, jsonContent);
		return message;
	}

	/**
	 * Build a hubiquitus response message
	 * 
	 * @param from
	 *            recipient of the message
	 * @param messageId
	 *            the message id of the message
	 * @param payload
	 *            the payload of the message
	 * @return
	 */
	protected JSONObject buildResponse(String from, String messageId,
			Object err, Object content) {
		JSONObject response = new JSONObject();
		response.put(TYPE, MessageType.response.name());
		response.put(ID, messageId);
		response.put(TO, from);
		JSONObject jsonContent = new JSONObject();
		jsonContent.put(CONTENT, content);
		response.put(PAYLOAD, jsonContent);
		JSONObject jsonErr = new JSONObject();
		jsonErr.put(ERR, err);
		response.put(PAYLOAD, jsonErr);
		return response;
	}

	/**
	 * Handler for json messages
	 * 
	 * @param jsonMessage
	 *            a json message
	 */
	protected void handleMessage(JSONObject jsonMessage) {

		JSONObject jsonPayload = null;
		String messageId = null;
		String from = null;
		Object err = null, content = null;
		if (jsonMessage.has(ID)) {
			messageId = jsonMessage.getString(ID);
		}
		if (jsonMessage.has(FROM)) {
			from = jsonMessage.getString(FROM);
		}
		if (jsonMessage.has(PAYLOAD)) {
			jsonPayload = jsonMessage.getJSONObject(PAYLOAD);
			if (jsonPayload.has(ERR)) {
				err = jsonPayload.get(ERR);
				if (err == JSONObject.NULL) {
					err = null;
				}
			}
			if (jsonPayload.has(CONTENT)) {
				content = jsonPayload.get(CONTENT);
			}
		}

		if (jsonMessage.has(TYPE)) {

			String mesageType = jsonMessage.getString(TYPE);

			switch (MessageType.valueOf(mesageType)) {
			case login:
				this.transportListener.onConnect();
				break;
			case message:
				final String finalFrom = from,
				finalMessageId = messageId;
				transportListener.onMessage(from, content, new ReplyCallback() {
					@Override
					public void reply(Object err, Object content) {
						JSONObject response = buildResponse(finalFrom,
								finalMessageId, err, content);
						try {
							Transport.this.send(response);
						} catch (TransportException e) {
							e.printStackTrace();
						}
					}
				});
				break;
			case response:
				ResponseListener responseListener = responseQueue
						.get(messageId);
				if (responseListener != null) {
					responseListener.onResponse(err, from, content);
					responseQueue.remove(messageId);
				}
				break;
			default:
				break;
			}
		}
	}
}
