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

package org.hubiquitus.hapi.hmessage;

import org.hubiquitus.hapi.codes.Error;
import org.hubiquitus.hapi.codes.Status;
import org.hubiquitus.hapi.codes.Type;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Data {
	
	/**
	 * The status of the connection
	 */
	private Status status = null;
	
	/**
	 * the code of the error if one happens
	 */
	private Error error = null;
	
	/**
	 * the type of action asked
	 */
	private Type type = null;
	
	/**
	 * the node on which we want to execute some actions
	 */
	private String channel = null;
	
	/**
	 * the message id
	 */
	private String msgid = null;
	 
	/**
	  * messages received 
	  */
	private String message = null;

	/**
	 * default constructor
	 */
	public Data(){}
	
	/**
	 * The class constructor
	 * @param status
	 * @param error
	 * @param type
	 * @param channel
	 * @param msgid
	 * @param message
	 */
	public Data(Status status, Error error, Type type, String channel,
			String msgid, String message) {
		this.status = status;
		this.error = error;
		this.type = type;
		this.channel = channel;
		this.msgid = msgid;
		this.message = message;
	}
	
	/**
	 * Turn Data into JSON
	 * @return a JSON object
	 */
	public JSONObject toJSON(){
		JSONObject data = new JSONObject();
		JSONObject json = new JSONObject();
		try {
			if(getStatus() != null)json.put("status", getStatus().getValue());
			if (getType() != null)json.put("type", getType().getValue());
			if (getError() != null)json.put("code", getError());
			if (getChannel() != null)json.put("channel", getChannel());
			if (getMsgid() != null)json.put("msgid", getMsgid());
			if (getMessage() != null)json.put("message", getMessage());
			
			
			data.put("data", json);
		} catch (JSONException e) {
			Log.i(getClass().getCanonicalName(),"JSON exception");
			Log.i(getClass().getCanonicalName(), e.getMessage());
		}
		
		return data;
	}

	
	/*****   Getters et Setters   *****/
	
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the error
	 */
	public Error getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(Error error) {
		this.error = error;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the msgid
	 */
	public String getMsgid() {
		return msgid;
	}

	/**
	 * @param msgid the msgid to set
	 */
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
