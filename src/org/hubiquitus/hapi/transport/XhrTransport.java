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
	
	private static final String HB_ARRAY = "[\"hb\"]";
	
	private static final String XHR = "/xhr";
	private static final String XHR_SEND = "/xhr_send";
	
	private String fullUrl;
	
	private boolean isConnected;
	
	private boolean close = false;
	
	/**
	 * Poll thread
	 */
	private PollThread pollThread;
	
	private ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<Message>();
	
	public XhrTransport(TransportListener transportListener) {
		super(transportListener);
	}

	@Override
	public void connect(final String endpoint, JSONObject authDataObject) {
		
		super.connect(endpoint, authData);
		
		serverId = TransportUtils.getServerId();
		sessionId = TransportUtils.getSessionId();
		
		StringBuilder sb = new StringBuilder();
		sb.append(endpoint).append("/").append(serverId).append("/").append(sessionId);
		
		this.fullUrl = sb.toString();
		this.authData = authDataObject;
		
		Log.d("DEBUG", "XHR connect to " + endpoint + " with " + authDataObject);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					ServiceResponse responseConnect = ServiceManager.requestService(XhrTransport.this.fullUrl, XHR, ServiceManager.Method.POST, null);
					if (responseConnect.getStatus() == 200) {
						ServiceResponse responseAuth = ServiceManager.requestService(XhrTransport.this.fullUrl, XHR_SEND, ServiceManager.Method.POST, buildAuthData(authData));
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
					e.printStackTrace();
				} catch (IOException e) {
					Log.e(getClass().getCanonicalName(), e.getMessage());
					e.printStackTrace();
				} catch (JSONException e) {
					Log.e(getClass().getCanonicalName(), e.getMessage());
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	@Override
	public void disconnect() throws TransportException {
		isConnected = false;
		this.transportListener.onDisconnect();
	}
	
	@Override
	public void silentDisconnect() throws TransportException {
		isConnected = false;
	}

	@Override
	protected void send(JSONObject jsonObject) throws TransportException {
		
		Message message = new Message();
		message.setJsonContent(jsonObject);
		queue.add(message);
		
		handleQueueMessages();
	}
	
	@Override
	public JSONObject send(String to, Object content, int timeout,
			ResponseListener responseListener) throws TransportException {
		
		JSONObject jsonMessage = super.send(to, content, timeout, responseListener);
			
		Message message = new Message();
		message.setJsonContent(jsonMessage);
		message.setResponseListener(responseListener);
		
		queue.add(message);
		
		handleQueueMessages();
		
		return jsonMessage;
	}

    @Override
    protected void sendHeartBeat() throws TransportException {
        try {
            send(new JSONObject().put(Transport.HB,Transport.HB));
        } catch (JSONException e) {
            Log.e(getClass().getCanonicalName(),"Unable to create json HeartBeat message");
        }
    }

    /**
	 * Handler for messages to send
	 */
	private void handleQueueMessages() {

		new Thread(new Runnable() {
			@Override
			public void run() {
		
				if (!queue.isEmpty()) {
					
					try {
					
					if (queue.size() == 1) {
						
						Message message = queue.poll();
						
						if (message.getResponseListener() != null) {
							XhrTransport.this.responseQueue.put(message.getJsonContent().getString(ID), message.getResponseListener());
						}
						
						ServiceManager.requestService(XhrTransport.this.fullUrl, XHR_SEND,ServiceManager.Method.POST, message.getJsonContent());
					}
					else {
						Iterator<Message> iter = queue.iterator();
						while (iter.hasNext()) {
							Message message = iter.next();
							
							if (message.getResponseListener() != null) {
								XhrTransport.this.responseQueue.put(message.getJsonContent().getString(ID), message.getResponseListener());
							}
							
							ServiceManager.requestService(XhrTransport.this.fullUrl, XHR_SEND, ServiceManager.Method.POST, message.getJsonContent());
							iter.remove();
						}
					}
					
					} catch (JSONException e) {
						Log.e(getClass().getCanonicalName(), e.getMessage());
					} catch (IOException e) {
						Log.e(getClass().getCanonicalName(), e.getMessage());
					}
				}
			}
		}).start();
		
	}

	/**
	 * Extracts a json object from 
	 * @param text the text to parse
	 * @return the parsed json object
	 * @throws JSONException
	 * @throws IOException 
	 */
	private List<JSONObject> extractJSON(String text) throws JSONException, IOException {
		
		JSONArray jsonArray = new JSONArray(text);
		
		if (jsonArray.length() > 0) {
			List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
			for (int i = 0; i < jsonArray.length(); i++) {
				if (HB.equals(jsonArray.getString(i))) {
					handleMessage(HB);
				}
				else {
					JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
					jsonObjects.add(jsonObject);
				}
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
		XhrTransport.this.authentified = false;
		isConnected = false;
		Log.e(getClass().getCanonicalName(), e.getMessage());
		e.printStackTrace();
	}
	
	public void closeConnection() {
		this.close = true;
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
					
					if (close) {
						isConnected = false;
						break;
					}
					
					ServiceResponse response = ServiceManager.requestService(XhrTransport.this.fullUrl, XHR, ServiceManager.Method.POST, null);
					String text = response.getText();
					
					if (text.startsWith(SOCKJS_START_MESSAGE)) {
						
						Log.d("DEBUG", "SOCKJS MESSAGE => " + text);
						
						try {
							String stripText = text.replaceFirst(SOCKJS_START_MESSAGE, "");
							if (HB_ARRAY.equals(stripText)) {
								XhrTransport.this.handleMessage(HB);
							}
							else {
								List<JSONObject> jsonObjects = extractJSON(stripText);
								if (jsonObjects != null) {
									for (JSONObject jsonObject : jsonObjects) {
										XhrTransport.this.handleMessage(jsonObject.toString());
									}
								}
							}
						} catch (JSONException e) {
							Log.e(getClass().getCanonicalName(), e.getMessage());
						}
					}
				
				} catch (IOException e) {
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
