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

import org.hubiquitus.hapi.util.HJsonDictionnary;
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
	 * @return latitude of the location. 0 if undefined
	 */
	public double getLat() {
		double lat;
		try {
			lat = hlocation.getDouble("lat");
		} catch (Exception e) {
			lat = 0;			
		}
		return lat;
	}

	public void setLat(double lat) {
		try {
			if(lat == 0) {
				hlocation.remove("lat");
			} else {
				hlocation.put("lat", lat);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * @return longitude of the location. 0 if undefined
	 */
	public double getLng() {
		double lng;
		try {
			lng = hlocation.getDouble("lng");
		} catch (Exception e) {
			lng = 0;			
		}
		return lng;
	}

	public void setLng(double lng) {
		try {
			if(lng == 0) {
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
	
	/**
	 * @return address of the location. NULL if undefined
	 */
	public String getAddress() {
		String address;
		try {
			address = hlocation.getString("addr");
		} catch (Exception e) {
			address = null;			
		}
		return address;
	}

	public void setAddress(String address) {
		try {
			if(address == null) {
				hlocation.remove("addr");
			} else {
				hlocation.put("addr", address);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * @return city of the location. NULL if undefined
	 */
	public String getCity() {
		String city;
		try {
			city = hlocation.getString("city");
		} catch (Exception e) {
			city = null;			
		}
		return city;
	}

	public void setCity(String city) {
		try {
			if(city == null) {
				hlocation.remove("city");
			} else {
				hlocation.put("city", city);
			}
		} catch (JSONException e) {
		}
	}
	
	/**
	 * @return country of the location. NULL if undefined
	 */
	public String getCountry() {
		String country;
		try {
			country = hlocation.getString("country");
		} catch (Exception e) {
			country = null;			
		}
		return country;
	}

	public void setCountry(String country) {
		try {
			if(country == null) {
				hlocation.remove("country");
			} else {
				hlocation.put("country", country);
			}
		} catch (JSONException e) {
		}
	}
	
	/**
	 * @return params throws to the hserver. NULL if undefined
	 */
	public HJsonObj getExtra() {
		HJsonObj extra;
		try {
			extra = new HJsonDictionnary(hlocation.getJSONObject("extra"));
		} catch (JSONException e) {
			extra = null;
		}
		return extra;
	}

	public void setExtra(HJsonObj extra) {
		try {
			if(extra == null) {
				hlocation.remove("extra");
			} else {
				hlocation.put("extra", extra.toJSON());
			}
		} catch (JSONException e) {
		}
	}
}
