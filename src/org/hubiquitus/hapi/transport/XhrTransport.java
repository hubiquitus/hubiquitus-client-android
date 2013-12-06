package org.hubiquitus.hapi.transport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.client.ClientProtocolException;
import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.hubiquitus.hapi.transport.model.Message;
import org.hubiquitus.hapi.transport.service.ServiceManager;
import org.hubiquitus.hapi.transport.service.ServiceResponse;
import org.hubiquitus.hapi.transport.utils.TransportUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Xhr transport class
 * 
 * @author t.bourgeois
 *
 */
public class XhrTransport extends Transport {
	
	private static final String SOCKJS_START_MESSAGE = "a";
	private static final String XHR = "/xhr";
	private static final String XHR_SEND = "/xhr_send";
	
	private String serverId;
	private String sessionId;
	
	private JSONObject authDataMessage;
	private String fullUrl;
	
	private boolean isConnected;
	
	/**
	 * Poll thread
	 */
	private PollThread pollThread;
	
	private ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<Message>();
	
	public XhrTransport(TransportListener transportListener) {
		super(transportListener);
	}

	@Override
	public void connect(String endpoint, JSONObject authData) {
		
		this.serverId = TransportUtils.getServerId();
		this.sessionId = TransportUtils.getSessionId();
		
		StringBuilder sb = new StringBuilder();
		sb.append(endpoint).append("/").append(this.serverId).append("/").append(this.sessionId);
		
		this.fullUrl = sb.toString();
		
		try {
			authDataMessage = buildAuthData(authData);
		} catch (JSONException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					ServiceResponse responseConnect = ServiceManager.requestService(XhrTransport.this.fullUrl, XHR, null);
					if (responseConnect.getStatus() == 200) {
						ServiceResponse responseAuth = ServiceManager.requestService(XhrTransport.this.fullUrl, XHR_SEND, authDataMessage);
						if (responseAuth.getStatus() == 204) {
							isConnected = true;
							pollThread = new PollThread();
							pollThread.start();
						}
						else {
							XhrTransport.this.transportListener.onError("Authentication failed");
						}
					}
					else {
						XhrTransport.this.transportListener.onError("Can't connect to host");
					}
				} catch (ClientProtocolException e) {
					Log.e(getClass().getCanonicalName(), e.getMessage());
				} catch (IOException e) {
					Log.e(getClass().getCanonicalName(), e.getMessage());
				}
			}
		}).start();
		
	}

	@Override
	public void disconnect() throws TransportException {
		isConnected = false;
		XhrTransport.this.transportListener.onDisconnect();
	}

	@Override
	protected void send(JSONObject jsonObject) throws TransportException {
		
		Message message = new Message();
		message.setJsonContent(jsonObject);
		queue.add(message);
	}
	
	@Override
	public JSONObject send(String to, Object content, int timeout,
			ResponseListener responseListener) throws TransportException {
		
		JSONObject jsonMessage = super.send(to, content, timeout, responseListener);
			
		Message message = new Message();
		message.setJsonContent(jsonMessage);
		message.setResponseListener(responseListener);
		queue.add(message);
			
		return jsonMessage;
	}
	
	/**
	 * Extracts a json object from 
	 * @param text the text to parse
	 * @return the parsed json object
	 * @throws JSONException
	 */
	private List<JSONObject> extractJSON(String text) throws JSONException {
		
		JSONArray jsonArray = new JSONArray(text);
		
		if (jsonArray.length() > 0) {
			List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
				jsonObjects.add(jsonObject);
			}
			return jsonObjects;
		}
		return null;
	}
	
	/**
	 * Handler for managing poll errors
	 * @param e the raised exception
	 */
	private void handlerPollError(Exception e) {
		XhrTransport.this.transportListener.onError(e.getMessage());
		XhrTransport.this.transportListener.onDisconnect();
		isConnected = false;
		Log.e(getClass().getCanonicalName(), e.getMessage());
		e.printStackTrace();
	}
	
	/**
	 * Thread used for xhr-polling
	 * 
	 * @author t.bourgeois
	 *
	 */
	private class PollThread extends Thread {
		
		@Override
		public void run() {
			
			while (isConnected) {
				
				try {
					
					if (!queue.isEmpty()) {
						if (queue.size() == 1) {
							Message message = queue.poll();
							ServiceManager.requestService(XhrTransport.this.fullUrl, XHR_SEND, message.getJsonContent());
							if (message.getResponseListener() != null) {
								XhrTransport.this.responseQueue.put(message.getJsonContent().getString(ID), message.getResponseListener());
							}
						}
						else {
							Iterator<Message> iter = queue.iterator();
							while (iter.hasNext()) {
								Message message = iter.next();
								ServiceManager.requestService(XhrTransport.this.fullUrl, XHR_SEND, message.getJsonContent());
								if (message.getResponseListener() != null) {
									XhrTransport.this.responseQueue.put(message.getJsonContent().getString(ID), message.getResponseListener());
								}
								iter.remove();
							}
						}
						
					}
					
					else {
					
						ServiceResponse response = ServiceManager.requestService(XhrTransport.this.fullUrl, XHR, null);
						String text = response.getText();
						
						if (text.startsWith(SOCKJS_START_MESSAGE)) {
							try {
								List<JSONObject> jsonObjects = extractJSON(text.replaceFirst(SOCKJS_START_MESSAGE, ""));
								if (jsonObjects != null) {
									for (JSONObject jsonObject : jsonObjects) {
										XhrTransport.this.handleMessage(jsonObject);
									}
 								}
							} catch (JSONException e) {
								Log.e(getClass().getCanonicalName(), e.getMessage());
							}
						}
					}
				
				} catch (IOException e) {
					handlerPollError(e);
				} catch (JSONException e) {
					handlerPollError(e);
				}
				
				try {
					sleep(100);
				} catch (InterruptedException e) {
					handlerPollError(e);
				}
				
			}
			
		}
		
	}
}
