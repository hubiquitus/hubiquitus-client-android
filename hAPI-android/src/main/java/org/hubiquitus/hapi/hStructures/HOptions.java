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

package org.hubiquitus.hapi.hStructures;

import org.hubiquitus.hapi.transport.socketio.HAuthCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 0.6 hAPI options. For more info, see Hubiquitus reference
 */

public class HOptions extends JSONObject {
	
	final Logger logger = LoggerFactory.getLogger(HOptions.class);
	private HAuthCallback authCB = null;

	public HAuthCallback getAuthCB() {
		return authCB;
	}

	public void setAuthCB(HAuthCallback authCB) {
		this.authCB = authCB;
	}

	public HOptions() {
		super();
	}

	public HOptions(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}

	public HOptions(HOptions options) {
		this.setEndpoints(options.getEndpoints());
		this.setTransport(options.getTransport());
		this.setTimeout(options.getTimeout());
	}

	/* Getters & Setters */

	/**
	 * Transport layer used to connect to hNode (ie : socketio)
	 */
	public String getTransport() {
		String transport;
		try {
			transport = this.getString("transport");
		} catch (Exception e) {
			transport = "socketio";
		}
		return transport;
	}

	public void setTransport(String transport) {

		try {
			if (transport != null && transport.length() > 0){
				this.put("transport", transport);
			}else{
				this.put("transport", "socketio");
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}

	}

	
	public JSONArray getEndpoints() {
		JSONArray endpoints;
		try {
			endpoints = this.getJSONArray("endpoints");
		} catch (Exception e) {
			logger.warn("message: endpoints is null or empty while it is mandatory. so an endpoints with http://localhost:8080 is returned");
			endpoints = new JSONArray();
			endpoints.put("http://localhost:8080");
		}
		return endpoints;
	}

	public void setEndpoints(JSONArray endpoints) {
		try {
			if (endpoints != null && endpoints.length() > 0){
				this.put("endpoints", endpoints);
			}else{
				logger.warn("message: endpoints in HOptions is mandatory. It can not be null or empty");
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public int getTimeout(){
		int timeout;
		try {
			timeout = this.getInt("timeout");
		} catch (Exception e) {
			timeout = 30000;
		}
		return timeout;
	}
	
	public void setTimeout(int timeout){
		try {
			if(timeout >= 0){
				this.put("timeout", timeout);
			}else{
				this.put("timeout", 30000);
			}
		} catch (Exception e) {
			logger.warn("message: ", e);
		}
	}


}