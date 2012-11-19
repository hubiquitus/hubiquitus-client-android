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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HArrayOfValue extends JSONObject {
	final Logger logger = LoggerFactory.getLogger(HArrayOfValue.class);
	
	private String name;
	
	public HArrayOfValue() {
		super();
	}
	
	public HArrayOfValue(String name, JSONArray values){
		super();
		this.name = name;
	}
	
	public HArrayOfValue(String jsonString) throws JSONException{
		super(jsonString);
	}
	
	public HArrayOfValue(JSONObject jsonObj) throws JSONException{
		super(jsonObj.toString());
	}
	/**
	 * @return The name of the attribute to compare with.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Set the name of the attribute to compare with.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return The Array of the values of the attribute to compare with.
	 */
	public JSONArray getValues() {
		if(this.name == null || this.name.length() <= 0){
			logger.error("message: The name of the attribute in HArrayOfValue is null or empty");
			return null;
		}
		JSONArray values;
		try {
			values = this.getJSONArray(this.name);
		} catch (Exception e) {
			values = null;
		}
		return values;
	}
	/**
	 * Set the values of the attribute to compare with; 
	 * @param values
	 */
	public void setValues(JSONArray values) {
		if(this.name == null || this.name.length() <= 0){
			logger.error("message: The name of the attribute in HArrayOfValue is null or empty");
			return;
		}
		try {
			if(values == null){
				this.remove(this.name);
			}else{
				this.put(this.name, values);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
}
