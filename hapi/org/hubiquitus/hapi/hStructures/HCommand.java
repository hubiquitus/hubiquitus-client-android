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

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.hubiquitus.hapi.structures.HJSONSerializable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author j.desousag
 * @version 0.3
 * 
 * hAPI Command. For more info, see Hubiquitus reference
 */

public class HCommand implements HJSONSerializable {
	
	private String reqid = null;
	private String requester = null;
	private String sender = null;
	private String entity = null;
	private Calendar sent = new GregorianCalendar();
	private String cmd = null;
	private JSONObject params = null;
	private Boolean _transient = null;
	
	
	public HCommand() {	}
	
	public HCommand(String entity, String cmd, JSONObject params) {
		this.entity = entity;
		this.cmd = cmd;
		this.params = params;
	}
	
	public HCommand(JSONObject jsonObj) {
		try {
			this.fromJSON(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("erreur HCommand fromJSON");
		}
	}
	
	public JSONObject toJSON() {
		JSONObject jsonObj = new JSONObject();
		
		try {
			jsonObj.put("reqid", this.reqid);
			jsonObj.put("requester", this.requester);
			jsonObj.put("sender", this.sender);
			jsonObj.put("entity", this.entity);
			jsonObj.put("sent", this.sent.getTime().toString());
			jsonObj.put("cmd",this.cmd);
			jsonObj.put("params", this.params);
			jsonObj.put("transient", this._transient);
		} catch (JSONException e) {
			e.printStackTrace();
			jsonObj = null;
		}
		
		return jsonObj;
	}
	
	public void fromJSON(JSONObject jsonObj) throws Exception {
		try {
			if (jsonObj.has("entity") && jsonObj.has("cmd") && jsonObj.has("params")) {
				this.entity = jsonObj.getString("entity");
				this.cmd = jsonObj.getString("cmd");
				this.params = jsonObj.getJSONObject("params");
				if(jsonObj.has("requid")) {
					this.reqid = jsonObj.getString("requid");
				}
				if(jsonObj.has("requester")) {
					this.reqid = jsonObj.getString("requester");
				}
				if(jsonObj.has("sender")) {
					this.reqid = jsonObj.getString("sender");
				}
				if(jsonObj.has("sent")) {
					this.sent = (Calendar) jsonObj.get("sent");
				}
				if(jsonObj.has("_transient")) {
					this.reqid = jsonObj.getString("_transient");
				}				
			} else {
				throw new Exception(this.getClass().toString() + " JSon object mal formated");
			}
		} catch (Exception e) {
			throw new Exception(this.getClass().toString() + " JSon object mal formated : " + e.getMessage());
		}	
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_transient == null) ? 0 : _transient.hashCode());
		result = prime * result + ((cmd == null) ? 0 : cmd.hashCode());
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((reqid == null) ? 0 : reqid.hashCode());
		result = prime * result
				+ ((requester == null) ? 0 : requester.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = prime * result + ((sent == null) ? 0 : sent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HCommand other = (HCommand) obj;
		if (_transient == null) {
			if (other._transient != null)
				return false;
		} else if (!_transient.equals(other._transient))
			return false;
		if (cmd == null) {
			if (other.cmd != null)
				return false;
		} else if (!cmd.equals(other.cmd))
			return false;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
			return false;
		if (reqid == null) {
			if (other.reqid != null)
				return false;
		} else if (!reqid.equals(other.reqid))
			return false;
		if (requester == null) {
			if (other.requester != null)
				return false;
		} else if (!requester.equals(other.requester))
			return false;
		if (sender == null) {
			if (other.sender != null)
				return false;
		} else if (!sender.equals(other.sender))
			return false;
		if (sent == null) {
			if (other.sent != null)
				return false;
		} else if (!sent.equals(other.sent))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HCommand [reqid=" + reqid + ", requester=" + requester
				+ ", sender=" + sender + ", entity=" + entity + ", sent="
				+ sent + ", cmd=" + cmd + ", params=" + params
				+ ", _transient=" + _transient + "]";
	}

	public String getReqid() {
		return reqid;
	}

	public void setReqid(String reqid) {
		this.reqid = reqid;
	}

	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Calendar getSent() {
		return sent;
	}

	public void setSent(Calendar sent) {
		this.sent = sent;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public JSONObject getParams() {
		return params;
	}

	public void setParams(JSONObject params) {
		this.params = params;
	}

	public Boolean get_transient() {
		return _transient;
	}

	public void set_transient(Boolean _transient) {
		this._transient = _transient;
	}	
}