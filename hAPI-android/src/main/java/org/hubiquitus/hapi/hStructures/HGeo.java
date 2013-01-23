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

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  @version v0.6
 *  Specifies the exact longitude and latitude of the location
 */
public class HGeo extends JSONObject {

	final Logger logger = LoggerFactory.getLogger(HGeo.class);

	public HGeo(JSONObject jsonObj) throws JSONException{
		super(jsonObj.toString());
	}
	
	//ac lat and lng is mandatory
	public HGeo(double lng, double lat) {
		super();
		setLng(lng);
		setLat(lat);
	}

	/* Setter & Getter */
/**
 * @return Longitude of the location. Null if undefined.
 */
	public double getLng() {
		double lng;
		try {
			lng = this.getDouble("lng");
		} catch (Exception e) {
			lng = 0;
		}
		return lng;
	}

	/**
	 * Set the longitude of the location.
	 * @param lng
	 */
	public void setLng(double lng) {
		try {
			this.put("lng", lng);
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return Latitude of the location. Null if undefined.
	 */
	public double getLat() {
		double lat;
		try {
			lat = this.getDouble("lat");
		} catch (Exception e) {
			lat = 0;
		}
		return lat;
	}

	/**
	 * Set the latitude of the location.
	 * @param lat
	 */
	public void setLat(double lat) {
		try {
			this.put("lat", lat);
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
}
