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

package org.hubiquitus.hapi.util;

import java.net.URI;

import org.json.JSONArray;

/**
 * @cond internal
 * @version 0.5 Contain some utils function
 */
public class HUtil {

	/**
	 * Pick the index of a random element in a list
	 * @param list
	 * @return one number
	 */
	public static  int pickIndex(JSONArray jsonArray) {
		int index = 0;

		int size = jsonArray.length();
		index = (int) (Math.random() * size);
		return index;
	}

	/**
	 * get host fragment from an endpoint
	 * @param endpoint
	 * @return host in the
	 */
	public static String getHost(String endPoint) {
		String host = null;

		try {
			URI uri = new URI(endPoint);
			host = uri.getHost();
		} catch (Exception e) {
			host = null;
		}

		return host;
	}

	/**
	 * get port fragment from an endpoint
	 * @param endpoint
	 * @return
	 */
	public static int getPort(String endpoint) {
		int port = 0;

		try {
			URI uri = new URI(endpoint);
			port = uri.getPort();
		} catch (Exception e) {
			port = 0;
		}

		return port;
	}

	/**
	 * get path from an endpoint
	 * @param endpoint
	 * @return
	 */
	public static String getPath(String endpoint) {
		String path = null;

		try {
			URI uri = new URI(endpoint);
			path = uri.getPath();
		} catch (Exception e) {
			path = null;
		}

		return path;
	}

	/**
	 * @params hserver
	 * @params domain
	 * @return HServer Jid
	 */
	public static String getHserverJid(String hserver, String domain) {
		return hserver + "@" + domain;
	}

	/**
	 * Help to get the hApi part of a ref in hmessage.
	 * @param ref
	 * @return
	 */
	public static String getApiRef(String ref) {
		if (ref != null) {
			return ref.split("#")[0];
		} else {
			return null;
		}
	}

}

/**
 * @endcond
 */
