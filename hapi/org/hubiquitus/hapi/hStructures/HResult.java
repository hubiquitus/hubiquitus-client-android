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

import org.hubiquitus.hapi.structures.HJsonObj;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author j.desousag
 * @version 0.3
 * hAPI result. For more info, see Hubiquitus reference
 */

public class HResult implements HJsonObj {
	
	private JSONObject hresult;
	
	public HResult() {	}
	
	public HResult(String reqid, String cmd, HJsonObj result) {
		setReqid(reqid);
		setCmd(cmd);
		setResult(result);
	}
	
	public HResult(JSONObject jsonObj) {
		this.fromJSON(jsonObj);
	}
	
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
			System.out.println("erreur hresult");
		}
	}
	
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

	public Object getResult() {
		HJsonObj result;
		try {
			result = (HJsonObj) hresult.get("result");
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
				hresult.put("result", result);
			}
		} catch (JSONException e) {
		}
	}

	@Override
	public String toString() {
		return hresult.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return hresult.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return hresult.hashCode();
	}
}