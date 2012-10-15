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

import org.hubiquitus.hapi.exceptions.MissingAttrException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 0.5 
 * Describes a measure payload
 */

public class HMeasure extends JSONObject {
	
	final Logger logger = LoggerFactory.getLogger(HMeasure.class);

	public HMeasure() {
		super();
	};

	public HMeasure(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}


	/* Getters & Setters */

	/**
	 * Specifies the unit in which the measure is expressed, should be in
	 * lowercase.
	 * @return unit. NULL if undefined
	 */
	public String getUnit() {
		String unit;
		try {
			unit = this.getString("unit");
		} catch (Exception e) {
			unit = null;
		}
		return unit;
	}

	public void setUnit(String unit) throws MissingAttrException {
		try {
			if (unit == null || unit.length()<=0) {
				throw new MissingAttrException("unit");
			} else {
				this.put("unit", unit);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * Specify the value of the measure (ie : 31.2)
	 * @return value. NULL if undefined
	 */
	public String getValue() {
		String value;
		try {
			value = this.getString("value");
		} catch (Exception e) {
			value = null;
		}
		return value;
	}

	public void setValue(String value) throws MissingAttrException {
		try {
			if (value == null || value.length()<=0) {
				throw new MissingAttrException("value");
			} else {
				this.put("value", value);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
}
