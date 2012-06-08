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

package org.hubiquitus.hapi.transport;

import org.hubiquitus.hapi.hStructures.ConnectionError;
import org.hubiquitus.hapi.hStructures.ConnectionStatus;
import org.json.JSONObject;

/**
 * @cond internal
 * @version 0.3
 * Interface of HTransportCallback
 */

public interface HTransportCallback {
	
	/**
	 * transport layer callback to return an update of connection status
	 * should only return status updates on connected and disconnected
	 * @param status connection status
	 * @param error hapi error code
	 * @param errorMsg low level error message
	 */
	public void connectionCallback(ConnectionStatus status, ConnectionError error, String errorMsg);
	
	/**
	 * transport layer callback to get received serialized hapi objects
	 * @param jsonData serialized hapi object
	 */
	public void dataCallback(String type, JSONObject jsonData); 
}

/**
 * @endcond
 */