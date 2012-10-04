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
 * Enumeration of different status of connection take by the client.
 * For more information see Hubiquitus reference
 */

public enum ConnectionStatus {
	CONNECTING(1),
	CONNECTED(2),
	DISCONNECTING(5),
	DISCONNECTED(6);
	
	private int value;
	
	private ConnectionStatus(int value) {
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
	public static ConnectionStatus constant(int value) {
		ConnectionStatus [] _values = ConnectionStatus.values();
		ConnectionStatus _value = ConnectionStatus.DISCONNECTED;
		for (int i = 0; i < _values.length; i++) {
			if(_values[i].value == value){
				_value = _values[i];
			}
		}
		return _value;
	}
}
