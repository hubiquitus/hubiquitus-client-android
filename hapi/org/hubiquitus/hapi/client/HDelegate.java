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

package org.hubiquitus.hapi.client;

import org.hubiquitus.hapi.structures.HJsonObj;

/**
 * 
 * @author j.desousag
 * @version 0.3
 * Interface HCallback
 */

public interface HDelegate {
	
	/**
	 * hAPI callback.
	 * called asynchronyously to notify an update
	 * @param type - hubiquitus structure (hresult, hstatus, hmessage...)
	 * @param data - the structure defined by the type
	 */
	public void hDelegate(String type, HJsonObj data);

}
