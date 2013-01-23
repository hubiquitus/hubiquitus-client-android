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
 * This kind of payload is used to describe the status of a thread of correlated messages identified by its convid. 
 * Multiple hConvStates with the same convid can be published into a channel, specifying the evolution of the state of the thread during time.
 */

public class HConvState extends JSONObject {

	final Logger logger = LoggerFactory.getLogger(HConvState.class);

	public HConvState() {
		super();
	};

	public HConvState(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}

	/* Getters & Setters */

	/**
	 * The status of the thread
	 * 
	 * @return topic description. NULL if undefined
	 */
	public String getStatus() {
		String status;
		try {
			status = this.getString("status");
		} catch (Exception e) {
			status = null;
		}
		return status;
	}

	public void setStatus(String status) throws MissingAttrException {
		try {
			if (status == null || status.length()<=0) {
				throw new MissingAttrException("status");
			} else {
				this.put("status", status);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

}
