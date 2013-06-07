/*
 * Copyright (c) Novedia Group 2012.
 *
 *    This file is part of Hubiquitus
 *
 *    Permission is hereby granted, free of charge, to any person obtaining a copy
 *    of this software and associated documentation files (the "Software"), to deal
 *    in the Software without restriction, including without limitation the rights
 *    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *    of the Software, and to permit persons to whom the Software is furnished to do so,
 *    subject to the following conditions:
 *
 *    The above copyright notice and this permission notice shall be included in all copies
 *    or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *    INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *    PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 *    FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *    You should have received a copy of the MIT License along with Hubiquitus.
 *    If not, see <http://opensource.org/licenses/mit-license.php>.
 */
package org.hubiquitus.hapi.phonegap;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.hubiquitus.hapi.client.HClient;
import org.hubiquitus.hapi.client.HMessageDelegate;
import org.hubiquitus.hapi.client.HStatusDelegate;
import org.hubiquitus.hapi.hStructures.ConnectionStatus;
import org.hubiquitus.hapi.hStructures.HCondition;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.hubiquitus.hapi.hStructures.HOptions;
import org.hubiquitus.hapi.hStructures.HStatus;
import org.hubiquitus.hapi.transport.socketio.ConnectedCallback;
import org.hubiquitus.hapi.transport.socketio.HAuthCallback;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @cond internal
 */

public class HClientPhoneGapPlugin extends Plugin implements HStatusDelegate, HMessageDelegate {

	final Logger logger = LoggerFactory.getLogger(HClientPhoneGapPlugin.class);
	private HClient hclient = null;
	private ConnectedCallback connectCb = null;
	private boolean isFullJidSet = false;
	
	/**
	 * Receive actions from phonegap and dispatch them to the corresponding function
	 */
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackid) {
		//First of all, create hclient instance
		if(hclient == null)  {
			hclient = new HClient();
			hclient.onStatus(this);
			hclient.onMessage(this);
		}
		
		//do work depending on action
		if (action.equalsIgnoreCase("connect")) {
			this.connect(action, data, callbackid);
		} else if(action.equalsIgnoreCase("disconnect")) {
			this.disconnect(action, data, callbackid);
		} else if(action.equalsIgnoreCase("subscribe")) {
			this.subscribe(action, data, callbackid);
		} else if(action.equalsIgnoreCase("unsubscribe")) {
			this.unsubscribe(action, data, callbackid);
		} else if(action.equalsIgnoreCase("send")) {
			this.send(action, data, callbackid);
		} else if(action.equalsIgnoreCase("getsubscriptions")) {
			this.getSubscriptions(action, data, callbackid);
		} else if(action.equalsIgnoreCase("setfilter")) {
			this.setFilter(action, data, callbackid);
		}else if(action.equalsIgnoreCase("login")){
			this.login(action, data, callbackid);
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
		JSONObject jsonObj = null;
		String jsonCallback = null;
		try {
			jsonObj = data.getJSONObject(0);
			
			jsonCallback = jsonObj.getString("callback");
			
			final String msgCallback = jsonCallback;
			
			//set the callback
			HMessageDelegate messageDelegate = new MessageDelegate(msgCallback);
			hclient.getSubscriptions(messageDelegate);
		} catch (Exception e) {
			logger.error("message: ",e);
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
		String jsonCallback = null;
		try {
			jsonObj = data.getJSONObject(0);
			String actor = jsonObj.getString("actor");
			jsonCallback = jsonObj.getString("callback");
			
			final String msgCallback = jsonCallback;
			
			//set the callback
			HMessageDelegate messageDelegate = new MessageDelegate(msgCallback);
			hclient.unsubscribe(actor,messageDelegate);
		} catch (Exception e) {
			logger.error("message: ",e);
		} 
	}

	/**
	 * Bridge to HClient.send
	 * Convert json message to hmessage
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void send(String action, JSONArray data, String callbackid) {
		JSONObject jsonObj = null;
		JSONObject jsonMsg = null;
		String jsonCallback = null;
		HMessage msg = null;
		try {
			jsonObj = data.getJSONObject(0);
			jsonMsg = jsonObj.getJSONObject("hmessage");
			
			msg = new HMessage(jsonMsg);
			jsonCallback = jsonObj.getString("callback");
			
			final String msgCallback = jsonCallback;
			//set the callback
			HMessageDelegate messageDelegate = new MessageDelegate(msgCallback);
			
			hclient.send(msg, messageDelegate);
		} catch (Exception e) {
			logger.error("message: ",e);
		}
	}
	
	/**
	 * Bridge to HClient.subscribe
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void subscribe(String action, JSONArray data, String callbackid) {
		JSONObject jsonObj = null;
		String actor = null;
		String jsonCallback = null;
		try {
			jsonObj = data.getJSONObject(0);
			
			actor = jsonObj.getString("actor");
			jsonCallback = jsonObj.getString("callback");
			
			final String msgCallback = jsonCallback;
			
			//set the callback
			HMessageDelegate messageDelegate = new MessageDelegate(msgCallback);
			
			hclient.subscribe(actor, messageDelegate);
		} catch (Exception e) {
			logger.error("message: ",e);
		}
	}
	
	/**
	 * Bridge to HClient.setFilter
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void setFilter(String action, JSONArray data, String callbackid){
		JSONObject jsonObj = null;
		HCondition filter = null;
		String jsonCallback = null;
		try {
			jsonObj = data.getJSONObject(0);
			filter = new HCondition(jsonObj.getJSONObject("filter"));
			jsonCallback = jsonObj.getString("callback");
			final String msgCallback = jsonCallback;
			//set the callback
			HMessageDelegate messageDelegate = new MessageDelegate(msgCallback);
			hclient.setFilter(filter, messageDelegate);
		} catch (Exception e) {
			logger.error("message: ",e);
		}
	}
	
	/**
	 * Bridge to HClient.disconnect
	 * @param action
	 * @param data
	 * @param callbackid
	 */
	public void disconnect(String action, JSONArray data, String callbackid) {
		isFullJidSet = false;
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
		HOptions options = null;
		String jsonAuthCB = null;
		try {
			//get vars
			JSONObject jsonObj = data.getJSONObject(0); 
			publisher = jsonObj.getString("publisher");
			password = jsonObj.getString("password");
			JSONObject jsonOptions = (JSONObject) jsonObj.get("options");
			jsonAuthCB = jsonObj.getString("authCB");
			options = new HOptions(jsonOptions);
		} catch (Exception e) {
			logger.error("message: ",e);
		}
		if(!"undefined".equalsIgnoreCase(jsonAuthCB)&&jsonAuthCB!= null){
			options.setAuthCB(new AuthCb(jsonAuthCB, publisher));
		}
		
		//set callback
		hclient.connect(publisher, password, options);
	}
	
	class AuthCb implements HAuthCallback{
		private String authCbName;
		private String publisher;
		
		public AuthCb(String authCbName, String publisher){
			this.authCbName = authCbName;
			this.publisher = "'"+publisher+"'";
		}
		
		@Override
		public void authCb(String arg0, ConnectedCallback arg1) {
			notifyJsCallback("var tmpcallback = " + authCbName + "; tmpcallback", publisher, "window.plugins.hClient.login" );		
			connectCb = arg1;
		}
	}
	
	public void login(String action, JSONArray data, String callbackid){
		String jid = null;
		String password = null;
		try {
			JSONObject json = data.getJSONObject(0);
			jid = json.getString("publisher");
			password = json.getString("password");
			if(connectCb != null){
				connectCb.connect(jid, password);
			}
		} catch (Exception e) {
			logger.error("message: ",e);
		}
	}

	/**
	 * Helper function, that will call a jsCallback with an argument (model used in hapi);
	 * @param callback
	 * @param arg0
	 * @param arg1
	 */
	private void notifyJsCallback(final String jsCallback, final String arg0, final String arg1) {
		if (jsCallback != null && jsCallback.length() > 0) {
			
			//do callback on main thread
			this.webView.post(new Runnable() {

				public void run() {
					//send callback through javascript
					String jsCallbackFct = jsCallback + "(" + arg0 + ", " + arg1 + ");";
					logger.debug("HClientPhoneGapPlugiin::jsCallback: " + jsCallbackFct);
					sendJavascript(jsCallbackFct);
				}
			});	
		}
	}
	
	/**
	 * Helper function, that will call a jsCallback with an argument (model used in hapi);
	 * @param callback
	 * @param arg
	 */
	private void notifyJsCallback(final String jsCallback, final String arg) {
		if (jsCallback != null && jsCallback.length() > 0) {
			
			//do callback on main thread
			this.webView.post(new Runnable() {

				public void run() {
					//send callback through javascript
					String jsCallbackFct = jsCallback + "(" + arg + ");";
					logger.debug("HClientPhoneGapPlugiin::jsCallback: " + jsCallbackFct);
					sendJavascript(jsCallbackFct);
				}
			});	
		}
	}

	
	/**
	 * Help to update the connection state in js.
	 * @param status
	 */
	private void notifyJsUpdateConnState(final HStatus status){
		if(status != null){
			this.webView.post(new Runnable() {
				
				@Override
				public void run() {
					sendJavascript("window.plugins.hClient._connectionStatus=" + status.getStatus().value());
				}
			});
		}
	}
	
	private void setFullUrnAndResource(){
		if(!isFullJidSet){
			this.webView.post(new Runnable() {
				@Override
				public void run() {
					sendJavascript("window.plugins.hClient.fullUrn=" +"'"+ hclient.getFullUrn()+"'");
					sendJavascript("window.plugins.hClient.resource=" + "'"+hclient.getResource()+"'");
					sendJavascript("window.plugins.hClient.domain=" + "'"+hclient.getFullUrn().split(":")[1]+"'");
					isFullJidSet = true;
				}
			});
		}
	}
	
	@Override
	public void onStatus(HStatus status) {
		logger.debug("HClientPhoneGapPlugiin::onStatus: " + status);
		if(status.getStatus() == ConnectionStatus.CONNECTED)
			setFullUrnAndResource();
		notifyJsUpdateConnState(status);
		notifyJsCallback("window.plugins.hClient.onStatus", status.toString());
	}

	@Override
	public void onMessage(HMessage message) {
		notifyJsCallback("window.plugins.hClient.onMessage", message.toString());		
	}
	
	/**
	 * Message delegate for all js messages. call the right js callback
	 *
	 */
	private class MessageDelegate implements HMessageDelegate {

		private String msgCallback = null;
		
		/**
		 * Init with js callback function
		 * @param msgCallback
		 */
		public MessageDelegate(String msgCallback) {
			this.msgCallback = msgCallback;
		}
		
		@Override
		public void onMessage(HMessage message) {
			logger.debug("HClientPhoneGapPlugiin::onMessage: " + message);
			notifyJsCallback("var tmpcallback = " + this.msgCallback + "; tmpcallback", message.toString());	
		}
		
	}
}

/**
 * @endcond
 */