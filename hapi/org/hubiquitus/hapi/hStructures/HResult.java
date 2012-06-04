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

import org.hubiquitus.hapi.util.HJsonDictionnary;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author j.desousag
 * @version 0.3
 * hAPI result. For more info, see Hubiquitus reference
 */

public class HResult implements HJsonObj {
	
	private JSONObject hresult = new JSONObject();
		
	public HResult() {	}
	
	public HResult(String reqid, String cmd, HJsonObj result) {
		setReqid(reqid);
		setCmd(cmd);
		setResult(result);
	}
	
	public HResult(JSONObject jsonObj) {
		this.fromJSON(jsonObj);
	}
	
	/* HJsonObj interface */
	
	public JSONObject toJSON() {
		return this.hresult;
	}
	
	public String getHType() {
		return "hresult";
	}
	
	public void fromJSON(JSONObject jsonObj) {
		if( jsonObj != null) {
			this.hresult = jsonObj;
		} else {
			this.hresult = new JSONObject();
		}
	}
	
	@Override
	public String toString() {
		return hresult.toString();
	}
	
	/**
	 * Check are made on : cmd, reqid and status. 
	 * @param HResult 
	 * @return Boolean
	 */
	public boolean equals(HResult obj) {
		if(obj.getCmd() != this.getCmd()) 
			return false;
		if(obj.getReqid() != this.getReqid())
			return false;
		if(obj.getStatus().value() != this.getStatus().value())
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return hresult.hashCode();
	}
	
	/* Getters & Setters */
	
	/**
	 * Mandatory.
	 * @return command. NULL if undefined
	 */
	public String getCmd() {
		String cmd;
		try {
			cmd = hresult.getString("cmd");
		} catch (JSONException e) {
			cmd = null;
		}
		return cmd;
	}

	public void setCmd(String cmd) {
		try {
			if(cmd == null) {
				hresult.remove("cmd");
			} else {
				hresult.put("cmd", cmd);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Mandatory. Filled by the hApi
	 * @return reqid. NULL if undefined
	 */
	public String getReqid() {
		String reqid;
		try {
			reqid = hresult.getString("reqid");
		} catch (JSONException e) {
			reqid = null;
		}
		return reqid;
	}

	public void setReqid(String reqid) {
		try {
			if(reqid == null) {
				hresult.remove("reqid");
			} else {
				hresult.put("reqid", reqid);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Mandatory. Execution status.
	 * @return status. NULL if undefined
	 */
	public ResultStatus getStatus() {
		ResultStatus reqid;
		try {
			reqid = ResultStatus.constant(hresult.getInt("status"));
		} catch (Exception e1) {
			reqid = null;
		}
		return reqid;
	}

	public void setStatus(ResultStatus status) {
		try {
			if(status == null) {
				hresult.remove("status");
			} else {
				hresult.put("status", status.value());
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Only if result type is a JsonObject
	 * @see getResultString()
	 * @see getResultArray()
	 * @return result of a command operation or a subscriptions operation. 
	 */
	public HJsonObj getResult() {
		HJsonObj result;
		try {
			result = new HJsonDictionnary(hresult.getJSONObject("result"));
		} catch (JSONException e) {
			result = null;
		}
		return result;
	}
	
	/**
	 * Only if result type is a String
	 * @return result of a command operation or a subscriptions operation. 
	 */
	public String getResultString() {
		String result;
		try {
			result = hresult.getString("result");
		} catch (JSONException e) {
			result = null;
		}
		return result;
	}
	
	/**
	 * Only if result type is a JsonArray
	 * @return result of a command operation or a subscriptions operation. 
	 */
	public JSONArray getResultArray() {
		JSONArray result;
		try {
			result = hresult.getJSONArray("result");
		} catch (JSONException e) {
			result = null;
		}
		return result;
	}

	public void setResult(HJsonObj result) {
		try {
			if(result == null) {
				hresult.remove("result");
			} else {
				hresult.put("result", result.toJSON());
			}
		} catch (JSONException e) {
		}
	}

	
}