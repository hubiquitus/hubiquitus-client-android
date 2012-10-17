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
