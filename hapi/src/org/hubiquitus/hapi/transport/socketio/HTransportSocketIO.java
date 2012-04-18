/*
 * Copyright (c) Novedia Group 2012.
 *
 *     This file is part of Hubiquitus.
 *
 *     Hubiquitus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Hubiquitus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Hubiquitus.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.hubiquitus.hapi.transport.socketio;

import io.socket.SocketIO;

import java.net.MalformedURLException;

import org.hubiquitus.hapi.HClient;
import org.hubiquitus.hapi.callback.HCallback;
import org.hubiquitus.hapi.codes.Context;
import org.hubiquitus.hapi.codes.Error;
import org.hubiquitus.hapi.codes.Status;
import org.hubiquitus.hapi.codes.Type;
import org.hubiquitus.hapi.hmessage.Data;
import org.hubiquitus.hapi.options.HOptions;
import org.hubiquitus.hapi.transport.HTransport;
import org.hubiquitus.hapi.utils.HTimer;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

public class HTransportSocketIO implements HTransport, HCallback {

	/**
	 * the socket
	 */
	private SocketIO socket = null;
	
	/**
	 * the callback for the socket
	 */
	private HIOCallback hioCallback = new HIOCallback(this);
	
	/**
	 * the client from which the callback is called
	 */
	private HClient hClient;
	
	/**
	 * the connection options
	 */
	private HOptions options;
	
	/**
	 * the connection attributes (RID, SID, JID)
	 */
	private Attributes attributes;
	
	/**
	 * to check if the user is authenticated on the XMPP Server or not
	 */
	private boolean isAuthenticated = false;
	
	/**
	 * the main activity context
	 */
	private android.content.Context context;
	
	/**
	 * the timer
	 */
	private HTimer timer; 
	
	/**
	 * default constructor
	 */
	public HTransportSocketIO(HClient client){
		this.hClient = client;
	}
	
	@Override
	public void connect(HOptions options, android.content.Context context) {
		this.options = options;
		this.context = context;
		this.timer = HTimer.getHTimer(options, hClient);
		
		 // Check if the phone/pad is connected to a network
        ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		// not connected
		if ((info == null) || (!info.isConnected())) {
			Log.i(getClass().getCanonicalName(),"No connection detected, can not connect");
			hCallback(Context.ERROR, Status.DISCONNECTED, null);
		}
		else {
		
			// initialize the socket 
			try {
	        	// Opening the socket
				socket = new SocketIO(options.getEndpoint());
			} catch (MalformedURLException e) {
				Log.i(getClass().getCanonicalName(),"Connection failed");
				Log.i(getClass().getCanonicalName(), e.getMessage());
				
				hClient.hCallbackConnection(Context.LINK, new Data(Status.DISCONNECTED, Error.CONNECTION_FAILED, null, null, null, null));
			}
			
			// connection
			socket.connect(hioCallback);
		}
			
	}

	@Override
	public void disconnect() {
		hClient.hCallbackConnection(Context.LINK, 
				new Data(Status.DISCONNECTING, Error.NO_ERROR, null, null, null, null));
		
		socket.disconnect();
		
		hClient.hCallbackConnection(Context.LINK, 
				new Data(Status.DISCONNECTED, Error.NO_ERROR, null, null, null, null));

	}

	@Override
	public void subscribe(String nodeTosubscribeTo) {
		JSONObject subscribe = new JSONObject();
		
		try {
			subscribe.put("channel", nodeTosubscribeTo);
			subscribe.put("msgid", 0);
		} catch (JSONException e) {
			Log.i(getClass().getCanonicalName(),"JSON exception");
			Log.i(getClass().getCanonicalName(), e.getMessage());
		}
		
		socket.emit("subscribe", subscribe);
	}

	@Override
	public void unsubscribe(String nodeToUnsubscribeFrom) {
		JSONObject unsubscribe = new JSONObject();
				
		try {
			unsubscribe.put("channel", nodeToUnsubscribeFrom);
			unsubscribe.put("msgid", 0);
		} catch (JSONException e) {
			Log.i(getClass().getCanonicalName(),"JSON exception");
			Log.i(getClass().getCanonicalName(), e.getMessage());
		}
		
		socket.emit("unsubscribe", unsubscribe);
	}

	@Override
	public void publish(String nodeToPublishTo, String message) {
		JSONObject publish = new JSONObject();
		
		try {
			publish.put("channel", nodeToPublishTo);
			publish.put("message", message);
			publish.put("msgid", "");
		} catch (JSONException e) {
			Log.i(getClass().getCanonicalName(),"JSON exception");
			Log.i(getClass().getCanonicalName(), e.getMessage());
		}
		
		socket.emit("publish", publish);
	}

	@Override
	public void getMessages(String nodeToGetMessageFrom) {
		JSONObject getMessages = new JSONObject();
	
		try {
			getMessages.put("channel", nodeToGetMessageFrom);
			getMessages.put("msgid", 0);
		} catch (JSONException e) {
			Log.i(getClass().getCanonicalName(),"JSON exception");
			Log.i(getClass().getCanonicalName(), e.getMessage());
		}
		
		socket.emit("hMessage", getMessages);
	}
	
	/**
	 * the reconnection process
	 * @return the connection state
	 */
	public boolean tryToReconnect() throws InterruptedException{
		int nbTrial = 0;
		while (nbTrial < options.getRetryInterval().length && !socket.isConnected()){
			Thread.sleep(options.getRetryInterval()[nbTrial]);
			hCallback(Context.LINK, Status.CONNECTING, null);
			connect(options, context);
			nbTrial++;
		}
		return socket.isConnected();
	}
	
	@Override
	public void hCallback(Context context, Status status, JSONObject json){
		// stopping the timer because we got an answer from server
		if(timer.isAlive()){
			timer.interrupt();
			Log.e(getClass().getCanonicalName(),"timer stopped");
		}
		// json is at null when the callback is called when the state of the connection changes
		if(json == null){
			// when connected to hubiquitus node, connection to the XMPP server
			if(context == null && status == Status.CONNECTED && !isAuthenticated){
				// hConnect process
				JSONObject connect = new JSONObject();
				try {
					connect.put("userid", options.getUsername());
					connect.put("password", options.getPassword());
					connect.put("host", options.getDomain());
					connect.put("port", options.getServerPorts()[0]);	
				} catch (JSONException e) {
					Log.i(getClass().getCanonicalName(),"JSON exception");
					Log.i(getClass().getCanonicalName(), e.getMessage());
				}
				
				socket.emit("hConnect", connect);
				
				// timer launched to check if server is running
				timer.start();
				
				isAuthenticated = true;
				hClient.hCallbackConnection(Context.LINK, 
						new Data(Status.CONNECTING, null, null, null, null, null));
			}
			else if(context == Context.LINK){
				if(status == Status.CONNECTED){
					hClient.hCallbackConnection(context, 
							new Data(status, null, null, null, null, null));
				}
				else if(status == Status.CONNECTING){
					hClient.hCallbackConnection(context, 
							new Data(status, null, null, null, null, null));
				}
				else if(status == Status.DISCONNECTED){
					hClient.hCallbackConnection(context, 
							new Data(status, Error.NO_ERROR, null, null, null, null));
					isAuthenticated = false;
				}
			}
			else if(context == Context.ERROR){
				isAuthenticated = false;
				 // Check if the phone/pad is connected to a network
		        ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = cm.getActiveNetworkInfo();
				// not connected
				if ((info == null) || (!info.isConnected())) {
					Log.i(getClass().getCanonicalName(),"No connection detected, can not connect");
					hClient.hCallbackConnection(context, 
							new Data(status, Error.UNKNOWN_ERROR, null, null, null, null));
				}
				else {
					// server got disconnected, reconnection process
					if(!socket.isConnected()){
						try {
							tryToReconnect();
						} catch (InterruptedException e) {
							Log.i(getClass().getCanonicalName(),"InterruptedException");
							Log.i(getClass().getCanonicalName(), e.getMessage());
						}
					}
				}
			}
		}
		// the following lines are called when a message is received
		else{
//			Log.i("test", "context : " + context);
//			Log.i("test", "json : " + json);
			if(context == Context.LINK){
				try {	
					if((Status.setValue((String) json.get("status")).getValue())
							.equals(Status.ERROR)){
						
						hClient.hCallbackConnection(context, 
								new Data(Status.setValue(json.getString("status")), 
										Error.setValue(json.getInt("code")), 
										null, null, null, null));
					}
					else {
						hClient.hCallbackConnection(context, 
							new Data(Status.setValue(json.getString("status")), 
									null, null, null, null, null));
					}
				} catch (JSONException e) {
					Log.i(getClass().getCanonicalName(),"JSON exception");
					Log.i(getClass().getCanonicalName(), e.getMessage());
				}
			}
			else if(context == Context.RESULT){
				try {	
					hClient.hCallbackConnection(context, 
							new Data(null, null, 
									Type.setValue(json.getString("type")), 
									json.getString("channel"), 
									json.getString("msgid"), 
									null));
				} catch (JSONException e) {
					Log.i(getClass().getCanonicalName(),"JSON exception");
					Log.i(getClass().getCanonicalName(), e.getMessage());
				}

			}
			else if(context == Context.MESSAGE){
				try	{	
					hClient.hCallbackConnection(context, 
							new Data(null, null, null, 
									json.getString("channel"),
									null, 
									json.getString("message")));
				} catch (JSONException e) {
					Log.i(getClass().getCanonicalName(),"JSON exception");
					Log.i(getClass().getCanonicalName(), e.getMessage());
				}

			}
			else if(context == Context.ERROR){
				
				try {
					hClient.hCallbackConnection(context, 
							new Data(null, 
									Error.setValue(json.getInt("code")), 
									Type.setValue(json.getString("type")), 
									json.getString("channel"), 
									json.getString("id"), 
									null));
				} catch (JSONException e) {
					Log.i(getClass().getCanonicalName(),"JSON exception");
					Log.i(getClass().getCanonicalName(), e.getMessage());
				}
				
			}
		}
	}
	
	/*****   Getters et Setters   *****/
	
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the attributes
	 */
	public Attributes getAttributes() {
		return attributes;
	}

}
