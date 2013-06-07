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

package org.hubiquitus.hapi.transport;

import org.hubiquitus.hapi.transport.socketio.HAuthCallback;
import org.json.JSONObject;

/** 
 * @cond internal
 * @version 0.6
 * options used for transport layers
 */

public class HTransportOptions {
	private String login = null;
	private String fullUrn = null;
	private String domain = null;
	private String resource = null;
	private String password = null;
	private String endpoint = null;
	private HAuthCallback authCB = null;
	private JSONObject context = null;
	


	public HTransportOptions() {
		super();
	}
	
	/* getters and setters */


	/**
	 * @return user urn (ie : urn:domain:username)
	 */
	public String getLogin() {
		return login;
	}


	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getBareURN(){
    		String splitted = fullUrn.split("/")[0];
    		return splitted;
    	}

	public String getFullUrn() {
		return fullUrn;
	}

	public void setFullUrn(String fullUrn) {
		this.fullUrn = fullUrn;
		setDomain(fullUrn.split(":")[1]);
		setResource(fullUrn.split(":")[2].split("/")[1]);
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	

	public JSONObject getContext() {
		return context;
	}

	public void setContext(JSONObject context) {
		this.context = context;
	}
	
	
	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}




	/**
	 * @return endpoint path (ie my_path) without begin /
	 */
	public String getEndpoint() {
		return endpoint;
	}


	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public HAuthCallback getAuthCB() {
		return authCB;
	}

	public void setAuthCB(HAuthCallback authCB) {
		this.authCB = authCB;
	}
	

}

/**
 * @endcond
 */
