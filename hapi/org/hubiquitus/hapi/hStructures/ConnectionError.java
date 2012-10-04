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
 * @version 0.5
 * Enumeration of different connection errors take by the client.
 * For more information see Hubiquitus reference
 */

public enum ConnectionError {
	NO_ERROR(0),
	JID_MALFORMAT(1),
	CONN_TIMEOUT(2),
	AUTH_FAILED(3),
	ATTACH_FAILED(4),
	ALREADY_CONNECTED(5),
	TECH_ERROR(6),
	NOT_CONNECTED(7),
	CONN_PROGRESS(8);
	
	private int value;
	
	private ConnectionError(int value) {
		this.value = value;
	}
	
	/**
	 * @return int equivalent.
	 */
	public int value() {
		return value;
	}
	
	/**
	 * Get constant for value
	 * @param value
	 * @return
	 */
	public static ConnectionError constant(int value) {
		ConnectionError [] _values = ConnectionError.values();
		return _values[value];
	}
	
}
