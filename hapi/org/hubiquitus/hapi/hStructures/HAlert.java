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
 * Alert message payload
 */

public class HAlert extends JSONObject {

	final Logger logger = LoggerFactory.getLogger(HAlert.class);

	public HAlert() {
		super();
	};

	public HAlert(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}



	/* Getters & Setters */

	/**
	 * The message provided by the author to describe the alert. (Eg : Power
	 * Failure)
	 * @return alert message. NULL if undefined
	 */
	public String getAlert() {
		String alert;
		try {
			alert = this.getString("alert");
		} catch (Exception e) {
			alert = null;
		}
		return alert;
	}

	public void setAlert(String alert) throws MissingAttrException {
		try {
			if (alert == null || alert.length()<=0) {
				throw new MissingAttrException("alert");
			} else {
				this.put("alert", alert);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

}