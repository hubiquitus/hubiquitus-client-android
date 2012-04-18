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

public enum Type {
	PUBLISH("publish"),
	SUBSCRIBE("subscribe"),
	UNSUBSCRIBE("unsubscibe");
	
	private String value;

	private Type(String value) {
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
	 * Method to set the value of Type
	 * @return Type's value
	 */
	public static Type setValue(String value){
		Type type = null;
		if(value.equals(PUBLISH.getValue())) type = PUBLISH;
		else if(value.equals(SUBSCRIBE.getValue())) type = SUBSCRIBE;
		else if(value.equals(UNSUBSCRIBE.getValue())) type = UNSUBSCRIBE;
		return type;
	}
}
