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
 * @author j.desousag
 * @version 0.3
 * hResult status codes. Returned on all action
 * For more information see Hubiquitus reference
 */

public enum ResultStatus {
	
	NO_ERROR (0),
	TECH_ERROR (1),
	CHAN_INACTIVE (2),
	NOT_CONNECTED (3),
	CHAN_INVALID (4),
	NOT_AUTHORIZED (5),
	MISSING_ATTR (6),
	INVALID_ATTR (7),
	ADMIN_REQUIRED (8),
	NOT_AVAILABLE (9),
	EXEC_TIMEOUT (10);
	
	private int value;
	
	private ResultStatus(int value) {
		this.value = value;
	}
	
	/**
	 * Method to get the value of ErrorCode
	 * @return ErrorCode's value
	 */
	public int value() {
		return value;
	}
	
	/**
	 * Get constant for value
	 * @param value
	 * @return
	 */
	public static ResultStatus constant(int value) {
		ResultStatus [] _values = ResultStatus.values();
		return _values[value];
	}
}