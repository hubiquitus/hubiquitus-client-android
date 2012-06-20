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


package exceptions;

/**
 * @version 0.4
 * Exception to notify a missing attribute (ONLY used in builders)
 */


public class MissingAttrException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String attrName = null;
	
	/**
	 * Name of the missing attribute
	 * @param attrName
	 */
	public MissingAttrException(String attrName) {
		this.attrName = attrName;
	}
	
	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getMessage() {
		return "Attribute " + this.attrName + " is required but missing";
	}
	
	public String getLocalizedMessage() {
		return getMessage();
	}
	
	public String toString() {
		return "Attribute " + this.attrName + " is required but missing";
	}
}
