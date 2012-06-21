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
 * This kind of payload is used to describe the status of a thread of correlated messages identified by its convid.
 * Multiple hConvStates with the same convid can be published into a channel, specifying the evolution of the state of the thread during time.
 */

public class HConvState implements HJsonObj{

	private JSONObject hconvstate = new JSONObject();
		
	public HConvState() {};
	
	public HConvState(JSONObject jsonObj){
		fromJSON(jsonObj);
	}
	
	/* HJsonObj interface */
	
	public JSONObject toJSON() {
		return hconvstate;
	}
	
	public void fromJSON(JSONObject jsonObj) {
		if(jsonObj != null) {
			this.hconvstate = jsonObj; 
		} else {
			this.hconvstate = new JSONObject();
		}
	}
	
	public String getHType() {
		return "hconv";
	}
	
	@Override
	public String toString() {
		return hconvstate.toString();
	}
	
	/**
	 * Check are made on : status. 
	 * @param HConvState 
	 * @return Boolean
	 */
	public boolean equals(HConvState obj) {
		if(obj.getStatus() != this.getStatus()) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return hconvstate.hashCode();
	}
	
	/* Getters & Setters */
	
	/**
	 * The status of the thread
	 * @return topic description. NULL if undefined
	 */
	public String getStatus() {
		String status;
		try {
			status = hconvstate.getString("status");
		} catch (Exception e) {
			status = null;			
		}
		return status;
	}

	public void setStatus(String status) {
		try {
			if(status == null) {
				hconvstate.remove("status");
			} else {
				hconvstate.put("status", status);
			}
		} catch (JSONException e) {
		}
	}
	
}

