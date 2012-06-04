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
package org.hubiquitus.hapi.phonegap;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.hubiquitus.hapi.client.HDelegate;
import org.hubiquitus.hapi.client.HClient;
import org.hubiquitus.hapi.hStructures.HCommand;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.hubiquitus.hapi.hStructures.HOptions;
import org.hubiquitus.hapi.hStructures.HJsonObj;
import org.hubiquitus.hapi.hStructures.HResult;
import org.hubiquitus.hapi.hStructures.HStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HClientPhoneGapPlugin extends Plugin implements HDelegate {

	private HClient hclient = null;
	private String jsHClientCallback = null;
	
	/**
	 * Receive actions from phonegap and dispatch them to the corresponding function
	 */
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackid) {
		//First of all, create hclient instance
		if(hclient == null)  {
			hclient = new HClient();
		}
		
		//do work depending on action
		if (action.equalsIgnoreCase("connect")) {
			this.connect(action, data, callbackid);
		} else if(action.equalsIgnoreCase("disconnect")) {
			this.disconnect(action, data, callbackid);
		} else if(action.equalsIgnoreCase("command")) {
			this.command(action, data, callbackid);
		} else if(action.equalsIgnoreCase("subscribe")) {
			this.subscribe(action, data, callbackid);
		} else if(action.equalsIgnoreCase("unsubscribe")) {
			this.unsubscribe(action, data, callbackid);
		} else if(action.equalsIgnoreCase("publish")) {
			this.publish(action, data, callbackid);
		} else if(action.equalsIgnoreCase("getlastmessages")) {
			this.getLastMessages(action, data, callbackid);
		} else if(action.equalsIgnoreCase("getsubscriptions")) {
			this.getSubscriptions(action, data, callbackid);
		}
		
		return null;
	}

	/**
	 * Bridge to HClient.getSubcriptions
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void getSubscriptions(String action, JSONArray data, String callbackid) {
		hclient.getSubscriptions();
	}
	
	/**
	 * Bridge to HClient.getLastMessages
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void getLastMessages(String action, JSONArray data, String callbackid) {
		JSONObject jsonObj = null;
		String chid = null;
		int nbLastMsg = -1;
		try {
			jsonObj = data.getJSONObject(0);
			chid = (String)jsonObj.getString("chid");
			
			if(jsonObj.has("nbLastMsg")) {
				nbLastMsg = jsonObj.getInt("nbLastMsg");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (nbLastMsg < 0) {
			hclient.getLastMessages(chid);
		} else {
			hclient.getLastMessages(chid, nbLastMsg);
		}
	}
	
	/**
	 * Bridge to HClient unsubscribe
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void unsubscribe(String action, JSONArray data, String callbackid) {
		JSONObject jsonObj = null;
		String chid = null;
		try {
			jsonObj = data.getJSONObject(0);
			chid = (String)jsonObj.getString("chid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		hclient.unsubscribe(chid);
	}

	/**
	 * Bridge to HClient.publish
	 * Convert json message to hmessage
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void publish(String action, JSONArray data, String callbackid) {
		JSONObject jsonObj = null;
		JSONObject jsonMsg = null;
		HMessage msg = null;
		try {
			jsonObj = data.getJSONObject(0);
			jsonMsg = (JSONObject)jsonObj.get("hmessage");
			msg = new HMessage(jsonMsg);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		
		hclient.publish(msg);
	}
	
	/**
	 * Bridge to HClient.subscribe
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void subscribe(String action, JSONArray data, String callbackid) {
		JSONObject jsonObj = null;
		String chid = null;
		try {
			jsonObj = data.getJSONObject(0);
			chid = (String)jsonObj.getString("chid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		hclient.subscribe(chid);
	}

	/**
	 * Bridge to HClient.command
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void command(String action, JSONArray data, String callbackid) {
		JSONObject jsonObj = null;
		JSONObject jsonCmd = null;
		HCommand cmd = null;
		try {
			jsonObj = data.getJSONObject(0);
			jsonCmd = (JSONObject)jsonObj.get("hcommand");
			cmd = new HCommand(jsonCmd);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		Log.i("Debug","Command : " + cmd);
		hclient.command(cmd);
	}
	
	/**
	 * Bridge to HClient.disconnect
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void disconnect(String action, JSONArray data, String callbackid) {
		hclient.disconnect();
	}
	
	/**
	 * bridge to HClient.connect
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void connect(String action, JSONArray data, String callbackid) {
		String publisher = null;
		String password = null;
		String callback = null;
		HOptions options = null;
		try {
			//get vars
			JSONObject jsonObj = data.getJSONObject(0); 
			publisher = jsonObj.getString("publisher");
			password = jsonObj.getString("password");
			callback = jsonObj.getString("callback");
			JSONObject jsonOptions = (JSONObject) jsonObj.get("options");
			options = new HOptions(jsonOptions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//set callback
		jsHClientCallback = callback;
		hclient.connect(publisher, password, this, options);
		//hclient.connect(publisher, password, this, new HOptions());
	}
	
	/** HDelegate interface */
	
	/**
	 * Receives HClient callbacks, convert them to JSONObject and send them throught javascript to js callback
	 */
	@Override
	public void hDelegate(final String type, final HJsonObj data) {
		//do callback on main thread
		this.webView.post(new Runnable() {
			
			public void run() {
				//update javascript connection status if needed
				if (type.equalsIgnoreCase("hstatus")) {
					HStatus hStatusData = (HStatus)data;
					String jsConnStatusCallback = "hClient._connectionStatus = " + hStatusData.getStatus().value() + ";"; 
					sendJavascript(jsConnStatusCallback);
				}
				
				//send callback through javascript
			 	JSONObject jsonCallback = data.toJSON();
			 	String jsCallbackFct = "var tmpcallback = " + jsHClientCallback + "; tmpcallback({type: \"" + type + "\",data: " + jsonCallback.toString() + "});";
			 	sendJavascript(jsCallbackFct);
			}
		});	
		
	}

}
