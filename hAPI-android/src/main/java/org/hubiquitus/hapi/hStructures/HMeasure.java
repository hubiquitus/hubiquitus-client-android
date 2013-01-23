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

import org.hubiquitus.hapi.exceptions.MissingAttrException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 0.6 
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
