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

package org.hubiquitus.hapi.codes;

public enum Status {
	
	CONNECTED("connected"),
	CONNECTING("connecting"),
	DISCONNECTING("disconnecting"),
	DISCONNECTED("disconnected"),
	ATTACHING("attaching"),
	ATTACHED("attached"),
	ERROR("error");
	
	private String value;

	private Status(String value) {
		this.value = value;
	}

	/**
	 * Method to get the value of Status
	 * @return Status' value
	 */

	public String getValue() {
		return value;
	}
	
	/**
	 * Method to set the value of Status
	 * @return Status' value
	 */
	public static Status setValue(String value){
		Status status = null;
		if(value.equals(CONNECTED.getValue())) status = CONNECTED;
		else if(value.equals(CONNECTING.getValue())) status = CONNECTING;
		else if(value.equals(DISCONNECTING.getValue())) status = DISCONNECTING;
		else if(value.equals(DISCONNECTED.getValue())) status = DISCONNECTED;
		else if(value.equals(ATTACHING.getValue())) status = ATTACHING;
		else if(value.equals(ATTACHED.getValue())) status = ATTACHED;
		else if(value.equals(ERROR.getValue())) status = ERROR;
		return status;
	}
	
}
