package org.hubiquitus.hapi.transport;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

import android.os.Handler;
import android.util.Log;

/**
 * Transport class
 * 
 * @author teabow
 * 
 */
public abstract class Transport {
	
	/**
	 * Transport endpoint
	 */
	protected String endpoint;
	/**
	 * Authentication data
	 */
	protected JSONObject authData;

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
	protected static final String AUTHTIMEOUT = "AUTHTIMEOUT";
	protected static final String HBTIMEOUT = "HBTIMEOUT";
	protected static final String HEARTBEAT_FREQUENCY = "heartbeatFreq";
	protected static final String HB = "hb";
	/**
	 * Server id value
	 */
	protected static String serverId;
	/**
	 * Session id value
	 */
	protected static String sessionId;

	/**
	 * Response message queue
	 */
	protected HashMap<String, ResponseListener> responseQueue;

	/**
	 * Transport listener
	 */
	protected TransportListener transportListener;
	
	private int authTimeout = 20000;
	
	protected boolean authentified = false;
	
	private Timer sendTimeoutTimer;
	
	private long lastHeartbeat;
	
	private int heartbeatFreq = 15000;
	
	private Handler handler;
	
	protected boolean disconnected = true;
	
	private Runnable checkConnectionRunnable;
	
	/**
	 * Constructor
	 * 
	 * @param transportListener
	 *            transport listener
	 */
	public Transport(TransportListener transportListener) {
		this.transportListener = transportListener;
		this.responseQueue = new HashMap<String, ResponseListener>();
		this.checkConnectionRunnable = new Runnable() {
			
			@Override
			public void run() {
				checkConnection();
			}
		};
	}
	
	private void checkConnection() {
		
		Log.d("DEBUG", "checkConnection");
		
		if (new Date().getTime() - (Transport.this.lastHeartbeat + Transport.this.heartbeatFreq + (0.5 * Transport.this.heartbeatFreq)) > 0) {
			
			Log.d("DEBUG", "=======> checkConnection LOST");
			
			try {
				Transport.this.disconnect();
				Transport.this.transportListener.onError(Transport.this.buildErrorMessage(HBTIMEOUT));
			} catch (TransportException e) {
				Log.d(getClass().getCanonicalName(), e.getMessage());
			} catch (JSONException e) {
				Log.d(getClass().getCanonicalName(), e.getMessage());
			}
		}
		else {
			// Remove any scheduled runnable to avoid several threads to do the same thing
			handler.removeCallbacks(checkConnectionRunnable);
			handler.postDelayed(checkConnectionRunnable, heartbeatFreq);
		}
	}

	/**
	 * Connect to endpoint
	 * 
	 * @param endpoint
	 *            endpoint
	 * @param authData
	 *            authentication data
	 */
	public void connect(String endpoint, JSONObject authData) {
		
		Log.d("DEBUG", "Transport connect to " + endpoint + " with " + authData);
		
		Timer authTimeoutTimer = new Timer();
		authTimeoutTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!Transport.this.authentified) {
					JSONObject jsonErr = new JSONObject();
					JSONObject jsonCode = new JSONObject();
					try {
						jsonCode.put(CODE, AUTHTIMEOUT);
						jsonErr.put(ERR, jsonCode);
					} catch (JSONException e) {
						Log.e(getClass().getCanonicalName(), e.getMessage());
					}
					Transport.this.transportListener.onError(jsonErr);
				}
			}
		}, this.authTimeout);
	}

	/**
	 * Disconnect from endpoint
	 * 
	 * @throws TransportException
	 */
	public abstract void disconnect() throws TransportException;
	
	/**
	 * Disconnect from endpoint
	 * 
	 * @throws TransportException
	 */
	public abstract void silentDisconnect() throws TransportException;

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
							jsonErr.put(ERR, jsonCode);
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
	 * Handler for json messages
	 * 
	 * @param jsonMessage
	 *            a json message
	 * @throws JSONException
	 */
	protected void handleMessage(String stringMessage) throws JSONException, IOException {
		
		if (HB.equals(stringMessage)) {
				
			Log.d("DEBUG", "handle hb message : " + stringMessage);
			
			lastHeartbeat = new Date().getTime();
		}
		
		else {
			
			JSONObject jsonMessage = new JSONObject(stringMessage);
		
			Log.d("DEBUG", "handle json message : " + jsonMessage.toString());
			
			String messageId = null;
			String from = null;
			JSONObject err = null;
			Object content = null;
			if (jsonMessage.has(ID)) {
				messageId = jsonMessage.getString(ID);
			}
			if (jsonMessage.has(FROM)) {
				from = jsonMessage.getString(FROM);
			}
			if (jsonMessage.has(CONTENT)) {
				content = jsonMessage.get(CONTENT);
			}
			if (jsonMessage.has(ERR) && !jsonMessage.isNull(ERR)) {
				err = jsonMessage.getJSONObject(ERR);
			}
			if (jsonMessage.has(TYPE)) {
	
				String mesageType = jsonMessage.getString(TYPE);
	
				switch (MessageType.valueOf(mesageType.toUpperCase(Locale.US))) {
				case LOGIN:
					this.authentified = true;
					this.transportListener.onConnect();
					break;
				case NEGOTIATE:
					if (this instanceof WebSocketTransport) {
						((WebSocketTransport)this).cancelPingTimeout();
						this.transportListener.OnWebSocketReady();
					}
					this.heartbeatFreq = jsonMessage.getInt(HEARTBEAT_FREQUENCY);
					// Remove any scheduled runnable to avoid several threads to do the same thing
					this.handler.removeCallbacks(checkConnectionRunnable);
					this.handler.postDelayed(checkConnectionRunnable, this.heartbeatFreq);
					break;
				case REQ:
					Request request = buildRequest(from, content, messageId);
					transportListener.onMessage(request);
					break;
				case RES:
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
	
	//TODO extract methods in a buildMessageFactory class
	
	/**
	 * Build an authentication message
	 * 
	 * @param authData
	 *            authentication data
	 * @return the builded message
	 * @throws JSONException
	 */
	public JSONObject buildAuthData(JSONObject authData)
			throws JSONException {
		JSONObject authDataMessage = new JSONObject();
		authDataMessage.put(TYPE, MessageType.LOGIN.format());
		authDataMessage.put(AUTH_DATA, authData);
		return authDataMessage;
	}
	
	/**
	 * Build a negociateMessage
	 * @return the builded negotiate message
	 * @throws JSONException
	 */
	protected JSONObject buildNegotiateMessage() throws JSONException {
		JSONObject negociateMessage = new JSONObject();
		negociateMessage.put(TYPE, MessageType.NEGOTIATE.format());
		return negociateMessage;
	}
	
	/**
	 * Build an error message
	 * @param error the error message
	 * @return the builded error message
	 * @throws JSONException
	 */
	protected JSONObject buildErrorMessage(String error) throws JSONException {
		JSONObject errorMessage = new JSONObject();
		errorMessage.put(CODE, HBTIMEOUT);
		return errorMessage;
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
		message.put(TYPE, MessageType.REQ.format());
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
		response.put(TYPE, MessageType.RES.format());
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

	public void setHandler(Handler handler) {
		this.handler = handler;	
	}
	
}
