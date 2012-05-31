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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @version 0.3
 * Describes a measure payload
 * Message acknowledgements
 * acknowledgements are used to identify the participants that have received or not received, read or not read a message 
 * Note, when a hMessage contains a such kind of payload, the convid must be provided with the same value has the acknowledged hMessage.
 */

public class HAck implements HJsonObj{

	private JSONObject hack = new JSONObject();
		
	public HAck() {};
	
	public HAck(JSONObject jsonObj){
		fromJSON(jsonObj);
	}
	
	/* HJsonObj interface */
	
	public JSONObject toJSON() {
		return hack;
	}
	
	public void fromJSON(JSONObject jsonObj) {
		if(jsonObj != null) {
			this.hack = jsonObj; 
		} else {
			this.hack = new JSONObject();
		}
	}
	
	public String getHType() {
		return "hack";
	}
	
	@Override
	public String toString() {
		return hack.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return hack.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return hack.hashCode();
	}
	
	/* Getters & Setters */
	
	/**
	 * Mandatory.
	 * The “msgid” of the message to which this acknowledgment refers.
	 * @return ackid. NULL if undefined
	 */
	public String getAckid() {
		String ackid;
		try {
			ackid = hack.getString("ackid");
		} catch (Exception e) {
			ackid = null;			
		}
		return ackid;
	}

	public void setAckid(String ackid) {
		try {
			if(ackid == null) {
				hack.remove("ackid");
			} else {
				hack.put("ackid", ackid);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * The status of the acknowledgement.
	 * @return acknowledgement status. NULL if undefined
	 */
	public HAckValue getAck() {
		HAckValue ack;
		try {
			String ackString = hack.getString("ack");
			ack = HAckValue.constant(ackString);
		} catch (Exception e) {
			ack = null;			
		}
		return ack;
	}

	public void setAck(HAckValue ack) {
		try {
			if(ack == null) {
				hack.remove("ack");
			} else {
				hack.put("value", ack.value());
			}
		} catch (JSONException e) {
		}
	}	
}