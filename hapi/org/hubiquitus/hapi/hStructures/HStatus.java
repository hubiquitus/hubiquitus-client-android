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
 * This structure describe the connection status
 */

public class HStatus extends JSONObject {

	final Logger logger = LoggerFactory.getLogger(HStatus.class);

	public HStatus() {
		super();
	};

	public HStatus(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}

	public HStatus(ConnectionStatus status, ConnectionError errorCode,
			String errorMsg) throws MissingAttrException {
		setStatus(status);
		setErrorCode(errorCode);
		setErrorMsg(errorMsg);
	}

	/* Getters & Setters */

	/**
	 * Mandatory. Connection status.
	 * @return status. NULL if undefined
	 */
	public ConnectionStatus getStatus() {
		ConnectionStatus status;
		try {
			status = ConnectionStatus.constant(this.getInt("status"));
		} catch (Exception e) {
			status = null;
		}
		return status;
	}

	public void setStatus(ConnectionStatus status) throws MissingAttrException {
		try {
			if (status == null) {
				throw new MissingAttrException("status");
			} else {
				this.put("status", status.value());
			}
		} catch (JSONException e) {
			logger.error("message: ", e);
		}
	}

	/**
	 * Mandatory. Valid only if status = error
	 * @return error code. NULL if undefined
	 */
	public ConnectionError getErrorCode() {
		ConnectionError errorCode;
		try {
			errorCode = ConnectionError.constant(this.getInt("errorCode"));
		} catch (Exception e) {
			errorCode = null;
		}
		return errorCode;
	}

	public void setErrorCode(ConnectionError errorCode) throws MissingAttrException {
		try {
			if (errorCode == null) {
				throw new MissingAttrException("errorCode");
			} else {
				this.put("errorCode", errorCode.value());
			}
		} catch (JSONException e) {
			logger.error("message: ", e);
		}
	}

	/**
	 * Error message. Platform dependent (low level layer messages) Should only be used for debug
	 * @return error message. NULL if undefined
	 */
	public String getErrorMsg() {
		String errorMsg;
		try {
			errorMsg = this.getString("errorMsg");
		} catch (JSONException e) {
			errorMsg = null;
		}
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		try {
			if (errorMsg == null) {
				this.remove("errorMsg");
			} else {
				this.put("errorMsg", errorMsg);
			}
		} catch (JSONException e) {
			logger.error("message: ", e);
		}
	}
}
