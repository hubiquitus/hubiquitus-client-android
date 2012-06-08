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

/**
 * @version 0.3
 * This structure describe the connection status
 */

public class HStatus implements HJsonObj{

	private JSONObject hstatus = new JSONObject();
		
	public HStatus() {};
	
	public HStatus(JSONObject jsonObj) {
		fromJSON(jsonObj);
	}
	
	public HStatus(ConnectionStatus status ,ConnectionError errorCode ,String errorMsg) {
		setStatus(status);
		setErrorCode(errorCode);
		setErrorMsg(errorMsg);
	}

	/* HJsonObj interface */
	
	public JSONObject toJSON() {
		return hstatus;
	}
	
	public void fromJSON(JSONObject jsonObj) {
		if(jsonObj != null) {
			this.hstatus = jsonObj; 
		} else {
			this.hstatus = new JSONObject();
		}
	}
	
	public String getHType() {
		return "hstatus";
	}
	
	@Override
	public String toString() {
		return hstatus.toString();
	}
	
	/**
	 * Check are made on : status, errorcode and errormsg. 
	 * @param HStatus 
	 * @return Boolean
	 */
	public boolean equals(HStatus obj) {
		if(obj.getStatus().value() != this.getStatus().value())
			return false;
		if(obj.getErrorCode().value() != this.getErrorCode().value())
			return false;
		if(obj.getErrorMsg() != this.getErrorMsg())
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return hstatus.hashCode();
	}
	
	/* Getters & Setters */
	
	/**
	 * Mandatory. Connection status.
	 * @return status. NULL if undefined
	 */
	public ConnectionStatus getStatus() {
		ConnectionStatus status;
		try {
			status = ConnectionStatus.constant(hstatus.getInt("status"));
		} catch (Exception e) {
			status = null;			
		}
		return status;
	}

	public void setStatus(ConnectionStatus status) {
		try {
			if(status == null) {
				hstatus.remove("status");
			} else {
				hstatus.put("status", status.value());
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Mandatory. Valid only if status = error
	 * @return error code. NULL if undefined
	 */
	public ConnectionError getErrorCode() {
		ConnectionError errorCode;
		try {
			errorCode = ConnectionError.constant(hstatus.getInt("errorCode"));
		} catch (Exception e) {
			errorCode = null;
		}
		return errorCode;
	}

	
	public void setErrorCode(ConnectionError errorCode) {
		try {
			if(errorCode == null) {
				hstatus.remove("errorCode");
			} else {
				hstatus.put("errorCode", errorCode.value());
			}
		} catch (JSONException e) {
		}
	}
	
	/**
	 * Error message. Platform dependent (low level layer messages)
	 * Should only be used for debug
	 * @return error message. NULL if undefined
	 */
	public String getErrorMsg() {
		String errorMsg;
		try {
			errorMsg = hstatus.getString("errorMsg");
		} catch (JSONException e) {
			errorMsg = null;
		}
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		try {
			if(errorMsg == null) {
				hstatus.remove("errorMsg");
			} else {
				hstatus.put("errorMsg", errorMsg);
			}
		} catch (JSONException e) {
		}
	}
}
