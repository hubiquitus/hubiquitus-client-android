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

package org.hubiquitus.hapi.hStructures;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 0.5 hAPI options. For more info, see Hubiquitus reference
 */

public class HOptions extends JSONObject {
	
	final Logger logger = LoggerFactory.getLogger(HOptions.class);

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