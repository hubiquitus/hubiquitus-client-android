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

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version v0.6
 * This structure defines a simple condition value for the available operand
 */
public class HValue extends JSONObject {
	final Logger logger = LoggerFactory.getLogger(HValue.class);
	
	private String name;
	
	public HValue(){
		super();
	}
	/**
	 * @param name : The name of the attribute to compare with.
	 * @param value : The value of the attribute to compare with.
	 */
	public HValue(String name, Object value){
		super();
		this.name = name;
		setValue(value);
	}
	
	public HValue(JSONObject jsonObj) throws JSONException{
		super(jsonObj.toString());
	}
	
	public HValue(String jsonString) throws JSONException{
		super(jsonString);
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
	 * @return The value of the attribute to compare with.
	 */
	public Object getValue() {
		if(this.name == null || this.name.length() <= 0){
			logger.error("message: The name of the attribute in HValue is null or empty");
			return null;
		}
		Object value;
		try {
			value  = this.get(this.name);
		} catch (Exception e) {
			value = null;
		}
		return value;
	}
	/**
	 * Set the value of the attribute to compare with.
	 * @param value
	 */
	public void setValue(Object value) {
		if(this.name == null || this.name.length() <= 0){
			logger.error("message: The name of the attribute in HValue is null or empty");
			return;
		}
		try {
			if(value == null){
				this.remove(this.name);
			}else{
				this.put(this.name, value);
			}
		} catch (JSONException e) {
			logger.warn("message : ", e);
		}
	}
	
	
}
