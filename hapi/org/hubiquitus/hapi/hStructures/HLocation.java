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
 * This structure describe the connection status
 */

public class HLocation implements HJsonObj{

	private JSONObject hlocation = new JSONObject();
		
	public HLocation() {};
	
	public HLocation(JSONObject jsonObj){
		fromJSON(jsonObj);
	}
	
	/* HJsonObj interface */
	
	public JSONObject toJSON() {
		return hlocation;
	}
	
	public void fromJSON(JSONObject jsonObj) {
		if(jsonObj != null) {
			this.hlocation = jsonObj; 
		} else {
			this.hlocation = new JSONObject();
		}
	}
	
	public String getHType() {
		return "hlocation";
	}
	
	@Override
	public String toString() {
		return hlocation.toString();
	}
	
	/**
	 * Check are made on : lng, lat and zip. 
	 * @param HLocation 
	 * @return Boolean
	 */
	public boolean equals(HLocation obj) {
		if(obj.getLat() != this.getLat())
			return false;
		if(obj.getLng() != this.getLng())
			return false;
		if(obj.getZip() != this.getZip())
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return hlocation.hashCode();
	}
	
	/* Getters & Setters */
	
	/**
	 * @return latitude of the location. NULL if undefined
	 */
	public String getLat() {
		String lat;
		try {
			lat = hlocation.getString("lat");
		} catch (Exception e) {
			lat = null;			
		}
		return lat;
	}

	public void setLat(String lat) {
		try {
			if(lat == null) {
				hlocation.remove("lat");
			} else {
				hlocation.put("lat", lat);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * @return longitude of the location. NULL if undefined
	 */
	public String getLng() {
		String lng;
		try {
			lng = hlocation.getString("lng");
		} catch (Exception e) {
			lng = null;			
		}
		return lng;
	}

	public void setLng(String lng) {
		try {
			if(lng == null) {
				hlocation.remove("lng");
			} else {
				hlocation.put("lng", lng);
			}
		} catch (JSONException e) {
		}
	}
	
	/**
	 * @return zip code of the location. NULL if undefined
	 */
	public String getZip() {
		String zip;
		try {
			zip = hlocation.getString("zip");
		} catch (Exception e) {
			zip = null;			
		}
		return zip;
	}

	public void setZip(String zip) {
		try {
			if(zip == null) {
				hlocation.remove("zip");
			} else {
				hlocation.put("zip", zip);
			}
		} catch (JSONException e) {
		}
	}
	
}
