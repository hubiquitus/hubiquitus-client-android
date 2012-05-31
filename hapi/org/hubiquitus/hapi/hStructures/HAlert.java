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
 * Alert message payload
 */

public class HAlert implements HJsonObj{

	private JSONObject halert = new JSONObject();
		
	public HAlert() {};
	
	public HAlert(JSONObject jsonObj){
		fromJSON(jsonObj);
	}
	
	/* HJsonObj interface */
	
	public JSONObject toJSON() {
		return halert;
	}
	
	public void fromJSON(JSONObject jsonObj) {
		if(jsonObj != null) {
			this.halert = jsonObj; 
		} else {
			this.halert = new JSONObject();
		}
	}
	
	public String getHType() {
		return "halert";
	}
	
	@Override
	public String toString() {
		return halert.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return halert.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return halert.hashCode();
	}
	
	/* Getters & Setters */
	
	/**
	 * The message provided by the author to describe the alert. 
	 * @return alert message. NULL if undefined
	 */
	public String getAlert() {
		String alert;
		try {
			alert = halert.getString("alert");
		} catch (Exception e) {
			alert = null;			
		}
		return alert;
	}

	public void setAlert(String alert) {
		try {
			if(alert == null) {
				halert.remove("alert");
			} else {
				halert.put("alert", alert);
			}
		} catch (JSONException e) {
		}
	}
}