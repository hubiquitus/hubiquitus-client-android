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
 */

public class HMeasure implements HJsonObj{

	private JSONObject hmeasure = new JSONObject();
		
	public HMeasure() {};
	
	public HMeasure(JSONObject jsonObj){
		fromJSON(jsonObj);
	}
	
	/* HJsonObj interface */
	
	public JSONObject toJSON() {
		return hmeasure;
	}
	
	public void fromJSON(JSONObject jsonObj) {
		if(jsonObj != null) {
			this.hmeasure = jsonObj; 
		} else {
			this.hmeasure = new JSONObject();
		}
	}
	
	public String getHType() {
		return "hmeasure";
	}
	
	@Override
	public String toString() {
		return hmeasure.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return hmeasure.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return hmeasure.hashCode();
	}
	
	/* Getters & Setters */
	
	/**
	 * Specifies the unit in which the measure is expressed, should be in lowercase. 
	 * @return unit. NULL if undefined
	 */
	public String getUnit() {
		String unit;
		try {
			unit = hmeasure.getString("unit");
		} catch (Exception e) {
			unit = null;			
		}
		return unit;
	}

	public void setUnit(String unit) {
		try {
			if(unit == null) {
				hmeasure.remove("unit");
			} else {
				hmeasure.put("unit", unit);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Specify the value of the measure (ie : 31.2)
	 * @return value. NULL if undefined
	 */
	public String getValue() {
		String value;
		try {
			value = hmeasure.getString("value");
		} catch (Exception e) {
			value = null;			
		}
		return value;
	}

	public void setValue(String value) {
		try {
			if(value == null) {
				hmeasure.remove("value");
			} else {
				hmeasure.put("value", value);
			}
		} catch (JSONException e) {
		}
	}	
}
