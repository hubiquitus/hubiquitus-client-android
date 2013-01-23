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
