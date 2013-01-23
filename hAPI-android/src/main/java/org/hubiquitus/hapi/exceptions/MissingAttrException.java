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


package org.hubiquitus.hapi.exceptions;

/**
 * @version 0.6
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
