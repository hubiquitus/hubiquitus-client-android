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

public class HCondition extends JSONObject {
	final Logger logger = LoggerFactory.getLogger(HCondition.class);
	

	public HCondition(){
		super();
	}
	
	public HCondition(JSONObject jsonObj) throws JSONException{
		super(jsonObj.toString());
	}
	
	public HCondition(String jsonString) throws JSONException{
		super(jsonString);
	}
	
	public HValue getValue(OperandNames operand){
		if(operand != OperandNames.EQ && operand != OperandNames.NE && operand != OperandNames.GT
				&& operand != OperandNames.GTE && operand != OperandNames.LT && operand != OperandNames.LTE){
			logger.error("message: \"" + operand.toString() + "\" can not apply on hValue.");
			return null;
		}
		HValue value;
		try {
			JSONObject jsonObj = this.getJSONObject(operand.toString());
			if(jsonObj instanceof HValue){
				value = new HValue(this.getJSONObject(operand.toString()));
			}else{
				return null;
			}
		} catch (Exception e) {
			value = null;
		}
		return value;
	}
	
	public void setValue(OperandNames operand, HValue value){
		if(operand != OperandNames.EQ && operand != OperandNames.NE && operand != OperandNames.GT
				&& operand != OperandNames.GTE && operand != OperandNames.LT && operand != OperandNames.LTE){
			logger.error("message: \"" + operand.toString() + "\" can not apply on hValue.");
			return;
		}
		try {
			if(value == null){
				this.remove(operand.toString());
			}else{
				this.put(operand.toString(), value);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public HArrayOfValue getArrayOfValue(OperandNames operand){
		if(operand != OperandNames.IN && operand != OperandNames.NIN){
			logger.error("message: \"" + operand.toString() + "\" can not apply on hArrayOfValue.");
			return null;
		}
		HArrayOfValue values;
		try {
			JSONObject jsonObj = this.getJSONObject(operand.toString());
			if(jsonObj instanceof HArrayOfValue){
				values = new HArrayOfValue(jsonObj);
			}else{
				return null;
			}
		} catch (Exception e) {
			values = null;
		}
		return values;
	}
	
	public void setValueArray(OperandNames operand, HArrayOfValue values){
		if(operand != OperandNames.IN && operand != OperandNames.NIN){
			logger.error("message: \"" + operand.toString() + "\" can not apply on hArrayOfValue.");
			return;
		}
		try {
			if(values == null){
				this.remove(operand.toString());
			}else{
				this.put(operand.toString(), values);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public HCondition getCondition(OperandNames operand){
		if(operand != OperandNames.NOT){
			logger.error("message: \"" + operand.toString() + "\" can not apply on HCondition.");
			return null;
		}
		HCondition condition;
		try {
			JSONObject jsonObj = this.getJSONObject(operand.toString());
			if(jsonObj instanceof HCondition){
				condition = new HCondition(jsonObj);
			}else{
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		return condition;
	}
	
	public void setCondition(OperandNames operand, HCondition condition){
		if(operand != OperandNames.NOT){
			logger.error("message: \"" + operand.toString() + "\" can not apply on HCondition.");
			return;
		}
		try {
			if(condition == null){
				this.remove(operand.toString());
			}else{
				this.put(operand.toString(), condition);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public JSONArray getConditionArray(OperandNames operand){
		if(operand != OperandNames.AND && operand != OperandNames.OR && operand != OperandNames.NOR){
			logger.error("message: \"" + operand.toString() + "\" can not apply on HCondition Array.");
			return null;
		}
		try {
			JSONArray jsonArray = this.getJSONArray(operand.toString());
			if(jsonArray == null){
				return null;
			}else{
				JSONArray conditionArray = new JSONArray();
				for(int i=0; i<jsonArray.length(); i++){
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					if(jsonObj instanceof HCondition){
						HCondition condition = new HCondition(jsonObj);
						conditionArray.put(condition);
					}else{
						return null;
					}
				}
				return conditionArray;
			}
		} catch (Exception e) {
			return null;
		}
	}
		
	public void setConditionArray(OperandNames operand, JSONArray conditionArray){
		if(operand != OperandNames.AND && operand != OperandNames.OR && operand != OperandNames.NOR){
			logger.error("message: \"" + operand.toString() + "\" can not apply on HCondition Array.");
			return;
		}
		try {
			if(conditionArray == null){
				this.remove(operand.toString());
			}else{
				this.put(operand.toString(), conditionArray);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public Boolean getRelevant(){
		Boolean relevant;
		try {
			relevant = this.getBoolean("relevant");
		} catch (Exception e) {
			relevant = null;
		}
		return relevant;
	}
	
	public void setRelevant(Boolean relevant){
		try {
			if(relevant == null){
				this.remove("relevant");
			}else{
				this.put("relevant", relevant);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public HPos getGeo(){
		HPos geo;
		try {
			geo = new HPos(this.getJSONObject("geo"));
		} catch (Exception e) {
			geo = null;
		}
		return geo;
	}
	
	public void setGeo(HPos geo){
		try {
			if(geo == null){
				this.remove("geo");
			}else{
				this.put("geo", geo);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

}
