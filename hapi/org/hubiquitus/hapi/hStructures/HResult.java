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

import org.hubiquitus.hapi.structures.HJSONSerializable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author j.desousag
 * @version 0.3
 * hAPI result. For more info, see Hubiquitus reference
 */

public class HResult implements HJSONSerializable {
	
	private String cmd = null;
	private String reqid = null;
	private ResultStatus status = null;
	private Object result = null;
	
	public HResult() {	}
	
	public HResult(String reqid, String cmd, Object result) {
		this.reqid = reqid;
		this.cmd = cmd;
		this.result = result;
	}
	
	public HResult(JSONObject jsonObj) {
		try {
			this.fromJSON(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("erreur HCommand fromJSON");
		}
	}
	
	@Override
	public String toString() {
		return "HResult [cmd=" + cmd + ", reqid=" + reqid + ", status="
				+ status + ", result=" + result + "]";
	}

	public JSONObject toJSON() {
		JSONObject jsonObj = new JSONObject();
		
		try {
			jsonObj.put("cmd", this.cmd);
			jsonObj.put("requid",this.reqid);
			jsonObj.put("status",this.status.value());
			jsonObj.put("result",this.result);
		} catch (JSONException e) {
			e.printStackTrace();
			jsonObj = null;
		}
		
		return jsonObj;
	}
	
	public void fromJSON(JSONObject jsonObj) throws Exception {
		try {
			if (jsonObj.has("cmd") && jsonObj.has("reqid") && jsonObj.has("status")) {
				this.cmd = jsonObj.getString("cmd");
				this.reqid = jsonObj.getString("reqid");
				this.result = jsonObj.get("result");
				if(jsonObj.has("status")) {
					this.status = ResultStatus.constant(jsonObj.getInt("status"));
				}
			} else {
				throw new Exception(this.getClass().toString() + " JSon object mal formated");
			}
		} catch (Exception e) {
			throw new Exception(this.getClass().toString() + " JSon object mal formated : " + e.getMessage());
		}	
	}
	
	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getReqid() {
		return reqid;
	}

	public void setReqid(String reqid) {
		this.reqid = reqid;
	}

	public ResultStatus getStatus() {
		return status;
	}

	public void setStatus(ResultStatus status) {
		this.status = status;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}