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
 * This kind of payload is used to describe the status of a thread of correlated messages identified by its convid. 
 * Multiple hConvStates with the same convid can be published into a channel, specifying the evolution of the state of the thread during time.
 */

public class HConvState extends JSONObject {

	final Logger logger = LoggerFactory.getLogger(HConvState.class);

	public HConvState() {
		super();
	};

	public HConvState(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}

	/* Getters & Setters */

	/**
	 * The status of the thread
	 * 
	 * @return topic description. NULL if undefined
	 */
	public String getStatus() {
		String status;
		try {
			status = this.getString("status");
		} catch (Exception e) {
			status = null;
		}
		return status;
	}

	public void setStatus(String status) throws MissingAttrException {
		try {
			if (status == null || status.length()<=0) {
				throw new MissingAttrException("status");
			} else {
				this.put("status", status);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

}
