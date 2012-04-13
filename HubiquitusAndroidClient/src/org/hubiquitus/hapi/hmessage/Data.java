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

	@Override
	public String toString(){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{\"data\":{");
		if(getStatus() != null) 
			stringBuilder.append("\"status\":\"").append(getStatus().getValue()).append("\",");
//		if(getStatus() != null && getType() != null)
//			stringBuilder.append(",");
		if(getType() != null) 
			stringBuilder.append("\"type\":\"").append(getType().getValue()).append("\",");
//		if(getType() != null && getError() != null)
//			stringBuilder.append(",");
		if(getError() != null) 
			stringBuilder.append("\"code\":").append(getError()).append(","); 
//		if(getError() != null && getChannel() != null)
//			stringBuilder.append(",");
		if(getChannel()!= null) 
			stringBuilder.append("\"channel\":\"").append(getChannel()).append("\",");
//		if(getChannel() != null && getMsgid() != null)
//			stringBuilder.append(",");
		if(getMsgid() != null) 
			stringBuilder.append("\"msgid\":\"").append(getMsgid()).append("\",");
//		if(getMsgid() != null && getMessage() != null)
//			stringBuilder.append(",");
		if(getMessage() != null) 
			stringBuilder.append("\"message\":\"").append(getMessage()).append("\"");
		stringBuilder.append("}}");	
		return stringBuilder.toString();
	}
	
	public JSONArray toJSON(){
		return null;
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
