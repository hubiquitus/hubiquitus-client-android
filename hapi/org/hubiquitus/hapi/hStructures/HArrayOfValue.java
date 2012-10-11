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
