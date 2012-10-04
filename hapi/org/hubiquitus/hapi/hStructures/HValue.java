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
package org.hubiquitus.hapi.hStructures;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version v0.5
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
