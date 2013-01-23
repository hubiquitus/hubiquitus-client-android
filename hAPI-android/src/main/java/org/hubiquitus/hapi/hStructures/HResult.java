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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 0.6 
 * hAPI result. For more info, see Hubiquitus reference
 */

public class HResult extends JSONObject {

	final Logger logger = LoggerFactory.getLogger(HResult.class);

	public HResult() {super();
	}

	public HResult(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}


	/* Getters & Setters */

	/**
	 * Mandatory. Execution status.
	 * @return status. NULL if undefined
	 */
	public ResultStatus getStatus() {
		ResultStatus reqid;
		try {
			reqid = ResultStatus.constant(this.getInt("status"));
		} catch (Exception e1) {
			reqid = null;
		}
		return reqid;
	}

	public void setStatus(ResultStatus status) throws MissingAttrException {
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
	 * if we don't know the result type.
	 * @return result of a command operation or a subscriptions operation.
	 */
	public Object getResult() {
		Object result;
		try {
			result = this.get("result");
		} catch (JSONException e) {
			result = null;
		}
		return result;
	}
	
	/**
	 * if result type is a JSONObject
	 * @return result of a command operation or a subscriptions operation.
	 */
	public JSONObject getResultAsJSONObject() {
		JSONObject result;
		try {
			result = this.getJSONObject("result");
		} catch (JSONException e) {
			result = null;
		}
		return result;
	}

	/**
	 * if result type is a JSONArray
	 */
	public JSONArray getResultAsJSONArray() {
		JSONArray result;
		try {
			result = this.getJSONArray("result");
		} catch (JSONException e) {
			result = null;
		}
		return result;
	}

	/**
	 * if result type is a String
	 * @return result of a command operation or a subscriptions operation.
	 */
	public String getResultAsString() {
		String result;
		try {
			result = this.getString("result");
		} catch (JSONException e) {
			result = null;
		}
		return result;
	}

	/**
	 * if result type is Boolean
	 */
	public Boolean getResultAsBoolean() {
		Boolean result;
		try {
			result = this.getBoolean("result");
		} catch (JSONException e) {
			result = null;
		}
		return result;
	}

	/**
	 * if result type is Integer
	 */
	public Integer getResultAsInt() {
		Integer result;
		try {
			result = this.getInt("result");
		} catch (JSONException e) {
			result = null;
		}
		return result;
	}

	/**
	 * if result type is Double
	 */
	public Double getResultAsDouble() {
		Double result;
		try {
			result = this.getDouble("result");
		} catch (JSONException e) {
			result = null;
		}
		return result;
	}

	
	/**
	 * The result type could be JSONObject, JSONArray, String, Boolean, Number.
	 * @param result
	 */
	public void setResult(Object result) {
		try {
			if (result == null) {
				this.remove("result");
			} else {
				this.put("result", result);
			}
		} catch (JSONException e) {
			logger.error("message: ", e);
		}
	}
	
	public void setResult(JSONObject result) {
		try {
			if (result == null) {
				this.remove("result");
			} else {
				this.put("result", result);
			}
		} catch (JSONException e) {
			logger.error("message: ", e);
		}
	}
	
	public void setResult(JSONArray result) {
		try {
			if (result == null) {
				this.remove("result");
			} else {
				this.put("result", result);
			}
		} catch (JSONException e) {
			logger.error("message: ", e);
		}
	}
	
	public void setResult(String result) {
		try {
			if (result == null) {
				this.remove("result");
			} else {
				this.put("result", result);
			}
		} catch (JSONException e) {
			logger.error("message: ", e);
		}
	}
	
	public void setResult(Boolean result) {
		try {
			if (result == null) {
				this.remove("result");
			} else {
				this.put("result", result);
			}
		} catch (JSONException e) {
			logger.error("message: ", e);
		}
	}
	
	public void setResult(Integer result) {
		try {
			if (result == null) {
				this.remove("result");
			} else {
				this.put("result", result);
			}
		} catch (JSONException e) {
			logger.error("message: ", e);
		}
	}
	
	public void setResult(Double result) {
		try {
			if (result == null) {
				this.remove("result");
			} else {
				this.put("result", result);
			}
		} catch (JSONException e) {
			logger.error("message: ", e);
		}
	}

}
