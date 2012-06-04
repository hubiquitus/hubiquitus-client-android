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

/**
 * @version 0.3
 * Enumeration of different message acknoledgements state.
 * For more information see Hubiquitus reference
 */

public enum HAckValue {
	UNKNOWN(""),
	RECV("recv"),
	READ("read");
	
	private String value;
	
	private HAckValue(String value) {
		this.value = value;
	}
	
	/**
	 * @return string equivalent.
	 */
	public String value() {
		return value;
	}
	
	/**
	 * Get constant for value
	 * @param value
	 * @return
	 */
	public static HAckValue constant(String value) {
		HAckValue [] _values = HAckValue.values();
		HAckValue _value = HAckValue.UNKNOWN;
		for (int i = 0; i < _values.length; i++) {
			if (_values[i].value.equals(value)) {
				_value = _values[i];
			}
		}
		return _value;
	}
}
