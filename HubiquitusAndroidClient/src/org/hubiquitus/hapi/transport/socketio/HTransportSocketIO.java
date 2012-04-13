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
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HTransportSocketIO implements HTransport, HCallback {

	private SocketIO socket = null;
	
	private HIOCallback hioCallback = new HIOCallback(this);
	
	private HClient client;
	
	private HOptions options;
	
	private Attributes attributes;
	
	private boolean isAuthenticated = false;
	
	/**
	 * default constructor
	 */
	public HTransportSocketIO(HClient client){
		this.client = client;
	}
	
	@Override
	public void connect(HOptions options) {
		this.options = options;
		
		// initialize socket 
		try {
        	// Opening the socket
			socket = new SocketIO(options.getEndpoint());
		} catch (MalformedURLException e) {
			Log.i(getClass().getCanonicalName(),"Connection failed");
			Log.i(getClass().getCanonicalName(), e.getMessage());
		}
		
		// connection
		socket.connect(hioCallback);
			
	}

	@Override
	public void disconnect() {
		client.hCallbackConnection(Context.LINK, 
				new Data(Status.DISCONNECTING, Error.NO_ERROR, null, null, null, null));
		
		socket.disconnect();
		
		client.hCallbackConnection(Context.LINK, 
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
			publish.put("msgid", "0a15f1hrd");
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
	
	@Override
	public void hCallback(Context context, Status status, JSONObject json) {
		if(json == null){
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
				
				isAuthenticated = true;
				client.hCallbackConnection(Context.LINK, 
						new Data(Status.CONNECTING, null, null, null, null, null));
			}
			else if(context == Context.LINK){
				if(status == Status.CONNECTED){
					client.hCallbackConnection(context, 
							new Data(status, null, null, null, null, null));
				}
				else if(status == Status.DISCONNECTED){
					client.hCallbackConnection(context, 
							new Data(status, Error.NO_ERROR, null, null, null, null));
				}
			}
			else if(context == Context.ERROR){
				client.hCallbackConnection(context, 
						new Data(status, Error.UNKNOWN_ERROR, null, null, null, null));
			}
		}
		else{
			Log.i("test", "context : " + context);
			Log.i("test", "json : " + json);
			if(context == Context.LINK){
				
				try {
					if((Status.setValue((String) json.get("status")).getValue())
							.equals(Status.ERROR)){
						
						client.hCallbackConnection(context, 
								new Data(Status.setValue((String) json.get("status")), 
										Error.setValue(json.getInt("code")), 
										null, null, null, null));
						
					}
					else client.hCallbackConnection(context, 
							new Data(Status.setValue((String) json.get("status")), 
									null, null, null, null, null));
				} catch (JSONException e) {
					Log.i(getClass().getCanonicalName(),"JSON exception");
					Log.i(getClass().getCanonicalName(), e.getMessage());
				}
			}
			else if(context == Context.RESULT){
				
				try {
					client.hCallbackConnection(context, 
							new Data(null, null, 
									Type.setValue((String)json.get("type")), 
									json.getString("channel"), 
									json.getString("msgid"), 
									null));
				} catch (JSONException e) {
					Log.i(getClass().getCanonicalName(),"JSON exception");
					Log.i(getClass().getCanonicalName(), e.getMessage());
				}
			}
			else if(context == Context.MESSAGE){
				
				try {
					client.hCallbackConnection(context, 
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
					client.hCallbackConnection(context, 
							new Data(null, 
									Error.setValue(json.getInt("code")), 
									Type.setValue((String)json.get("type")), 
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
