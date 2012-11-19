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

public class HPos extends JSONObject {

	final Logger logger = LoggerFactory.getLogger(HPos.class);
	public HPos() {
		super();
	};

	public HPos(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}
	
	/* Getters & Setters */

	/**
	 * The latitude . Mandatory
	 */
	public Double getLat() {
		Double lat;
		try {
			lat = this.getDouble("lat");
		} catch (Exception e) {
			logger.error("messag: lat is mandatory in HPos.");
			lat = null;
		}
		return lat;
	}

	public void setLat(double lat) {
		try {
				this.put("lat", lat);
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	/**
	 * The longitude. Mandatory
	 */
	public Double getLng() {
		Double lng;
		try {
			lng = this.getDouble("lng");
		} catch (Exception e) {
			logger.error("messag: lng is mandatory in HPos.");
			lng = null;
		}
		return lng;
	}

	public void setLng(double lng) {
		try {
				this.put("lng", lng);
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	/**
	 * The radius expressed in meters. Mandatory
	 */
	public Double getRadius() {
		Double radius;
		try {
			radius = this.getDouble("radius");
		} catch (Exception e) {
			logger.error("messag: lat is mandatory in HPos.");
			radius = null;
		}
		return radius;
	}

	public void setRadius(double radius) {
		try {
				this.put("radius", radius);
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
}
