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

import org.hubiquitus.hapi.exceptions.MissingAttrException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * @version 0.5 
 * hAPI Command. For more info, see Hubiquitus reference
 */

public class HCommand extends JSONObject {
	final Logger logger = LoggerFactory.getLogger(HCommand.class);

	public HCommand() {
		super();
	}

	public HCommand(String cmd, JSONObject params){
		this();
		try {
			setCmd(cmd);
		} catch (MissingAttrException e) {
			logger.error("message: ", e);
		}
		setParams(params);
	}

	public HCommand(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}

	/* Getters & Setters */

	/**
	 * @return command. NULL if undefined
	 */
	public String getCmd() {
		String cmd;
		try {
			cmd = this.getString("cmd");
		} catch (JSONException e) {
			cmd = null;
		}
		return cmd;
	}

	public void setCmd(String cmd) throws MissingAttrException {
		try {
			if (cmd == null || cmd.length()<=0) {
				throw new MissingAttrException("cmd");
			} else {
				this.put("cmd", cmd);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return params throws to the hserver. NULL if undefined
	 */
	public JSONObject getParams() {
		JSONObject params;
		try {
			params = this.getJSONObject("params");
		} catch (JSONException e) {
			params = null;
		}
		return params;
	}

	public void setParams(JSONObject params) {
		try {
			if (params == null) {
				this.remove("params");
			} else {
				this.put("params", params);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

}