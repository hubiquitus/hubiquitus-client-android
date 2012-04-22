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




/**
 * @version 0.3
 * This structure describe the connection status
 */

public class HStatus {

	
	private ConnectionStatus status;
	private ConnectionError errorCode;
	private String errorMsg;
	
	/**
	 * Constructor 
	 */
	public HStatus() {};
	
	/**
	 * @param status
	 * @param errorCode
	 * @param errorMsg
	 */
	public HStatus(ConnectionStatus status ,ConnectionError errorCode ,String errorMsg) {
		this.status = status;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;		
	};

	
	/* Getters & Setters */

	/**
	 * Connection status
	 */
	public ConnectionStatus getStatus() {
		return status;
	}

	public void setStatus(ConnectionStatus status) {
		this.status = status;
	}

	/**
	 * Error code. For more info, see Hubiquitus Ref
	 * Valid only if status = error
	 */
	public ConnectionError getErrorCode() {
		return errorCode;
	}

	
	public void setErrorCode(ConnectionError errorCode) {
		this.errorCode = errorCode;	
	}
	
	/**
	 * Error message
	 * Platform dependent (low level layer messages)
	 * Should only be used for debug
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	/* override section */
	@Override
	public String toString() {
		return "HStatus [status=" + status + ", errorCode=" + errorCode
				+ ", errorMsg=" + errorMsg + "]";
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((errorCode == null) ? 0 : errorCode.hashCode());
		result = prime * result
				+ ((errorMsg == null) ? 0 : errorMsg.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HStatus other = (HStatus) obj;
		if (errorCode != other.errorCode)
			return false;
		if (errorMsg == null) {
			if (other.errorMsg != null)
				return false;
		} else if (!errorMsg.equals(other.errorMsg))
			return false;
		if (status != other.status)
			return false;
		return true;
	}
}
