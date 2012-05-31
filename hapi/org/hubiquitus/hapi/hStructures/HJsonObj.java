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

import org.json.JSONObject;

/**
 * @version 0.3
 * JSON serializable interface for hstructures
 * Should be implemented by all hstructures
 */

public interface HJsonObj {
	
	/**	
	 * @return Object serialize to JSon
	 */
	public JSONObject toJSON();
	
	/**
	 * Deserialize object from Json
	 * @param jsonObj
	 */
	public void fromJSON(JSONObject jsonObj);
	
	/**
	 * @return Type of the HJsonObj
	 */
	public String getHType();
	
	public boolean equals(Object obj);
	public int hashCode();
	public String toString();
}
