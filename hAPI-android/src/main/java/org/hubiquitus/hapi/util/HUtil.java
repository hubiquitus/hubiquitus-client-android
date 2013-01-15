/*
 * Copyright (c) Novedia Group 2012.
 *
 *    This file is part of Hubiquitus
 *
 *    Permission is hereby granted, free of charge, to any person obtaining a copy
 *    of this software and associated documentation files (the "Software"), to deal
 *    in the Software without restriction, including without limitation the rights
 *    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *    of the Software, and to permit persons to whom the Software is furnished to do so,
 *    subject to the following conditions:
 *
 *    The above copyright notice and this permission notice shall be included in all copies
 *    or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *    INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *    PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 *    FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *    You should have received a copy of the MIT License along with Hubiquitus.
 *    If not, see <http://opensource.org/licenses/mit-license.php>.
 */

package org.hubiquitus.hapi.util;

import java.net.URI;

import org.json.JSONArray;

/**
 * @cond internal
 * @version 0.5 Contain some utils function
 */
public class HUtil {

	public static String URN_REGEX = "^urn:[a-z0-9][a-z0-9-]{0,31}:([a-z0-9()+,\\-.:=@;$_!*']|%[0-9a-f]{2})+$";
	
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
