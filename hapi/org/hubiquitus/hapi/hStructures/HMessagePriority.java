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
 * Enumeration of different Message priority.
 * For more information see Hubiquitus reference
 */
public enum HMessagePriority {
	TRACE(0),
	INFO(1),
	WARNING(2),
	ALERT(3),
	CRITICAL(4),
	PANIC(5);
	
	private int value;
	
	private HMessagePriority(int value) {
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
	public static HMessagePriority constant(int value) {
		HMessagePriority [] _values = HMessagePriority.values();
		return _values[value];
	}
}
