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


package org.hubiquitus.hapi.exceptions;

/**
 * @version 0.5
 * Exception to notify a missing attribute (used in builders and services with callback)
 */


public class MissingAttrException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String attrName = null;
	
	/**
	 * Name of the missing attribute
	 * @param attrName  the name of the missing attribute
	 */
	public MissingAttrException(String attrName) {
        super();
		this.attrName = attrName;
	}
    /**
     * Name of the missing attribute
     * @param attrName the name of the missing attribute
     * @param t the cause of the error...
     */
    @SuppressWarnings("unused")
    public MissingAttrException(String attrName, Throwable t) {
        super(t);
        this.attrName = attrName;
    }

    /**
     *
     * @return the attribute's name
     */
    @SuppressWarnings("unused")
	public String getAttrName() {
		return attrName;
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
