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
 * hAPI allows to attach acknowledgements to each message.
 * Acknowledgements are used to identify the participants that have received or not received, read or not read a message Note, 
 * when a hMessage contains a such kind of payload, the convid must be provided with the same value has the acknowledged hMessage.
 */

public class HAck extends JSONObject {

	final Logger logger = LoggerFactory.getLogger(HAck.class);
	public HAck() {
		super();
	};

	public HAck(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}
	
	/* Getters & Setters */

	/**
	 * The status of the acknowledgement.
	 * @return acknowledgement status. NULL if undefined
	 */
	public HAckValue getAck() {
		HAckValue ack;
		try {
			String ackString = this.getString("ack");
			ack = HAckValue.constant(ackString);
		} catch (Exception e) {
			ack = null;
		}
		return ack;
	}

	public void setAck(HAckValue ack) throws MissingAttrException {
		try {
			if (ack == null) {
				throw new MissingAttrException("ack");
			} else {
				this.put("ack", ack.value());
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
}
