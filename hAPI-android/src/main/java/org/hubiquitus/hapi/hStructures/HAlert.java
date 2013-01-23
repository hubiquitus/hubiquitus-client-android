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
 * Alert message payload
 */

public class HAlert extends JSONObject {

	final Logger logger = LoggerFactory.getLogger(HAlert.class);

	public HAlert() {
		super();
	};

	public HAlert(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}



	/* Getters & Setters */

	/**
	 * The message provided by the author to describe the alert. (Eg : Power
	 * Failure)
	 * @return alert message. NULL if undefined
	 */
	public String getAlert() {
		String alert;
		try {
			alert = this.getString("alert");
		} catch (Exception e) {
			alert = null;
		}
		return alert;
	}

	public void setAlert(String alert) throws MissingAttrException {
		try {
			if (alert == null || alert.length()<=0) {
				throw new MissingAttrException("alert");
			} else {
				this.put("alert", alert);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

}