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

/** 
 * @cond internal
 * @version 0.5
 * options used for transport layers
 */

public class HTransportOptions {
	private String urn = null;
	private String fullUrn = null;
	private String domain = null;
	private String resource = null;
	private String username = null;
	private String password = null;
	private String endpointHost = null;
	private int endpointPort = 0;
	private String endpointPath = null;
	private String hserver = "hnode";
	private HAuthCallback authCB = null;
	

	public HTransportOptions() {
		super();
	}
	
	/* getters and setters */


	/**
	 * @return hserver service name (by default it should be "hnode.domain")
	 */
	public String getHserverService() {
		String nodeService = null;
		
		if(this.urn != null) {
			nodeService = this.hserver + "@" + this.getDomain();
		}
		return nodeService;
	}
	
	/**
	 * @return user urn (ie : urn:domain:username)
	 */
	public String getUrn() {
		return urn;
	}


	public void setUrn(String urn) {
		this.urn = urn;
		setDomain(urn.split(":")[1]);
		setUsername(urn.split(":")[2]);
	}
	
	

	public String getFullUrn() {
		return fullUrn;
	}

	public void setFullUrn(String fullUrn) {
		this.fullUrn = fullUrn;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return pubsub service name (by default it should be "pubsub")
	 */
	public String getPubsubService() {
		return "pubsub" + "." + this.getDomain();
	}
	
	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}




	/**
	 * @return endpoint host (ie : localhost)
	 */
	public String getEndpointHost() {
		return endpointHost;
	}


	public void setEndpointHost(String endpointHost) {
		this.endpointHost = endpointHost;
	}
	
	/** 
	 * @return endpoint port (ie : 8080)
	 */
	public int getEndpointPort() {
		return endpointPort;
	}


	public void setEndpointPort(int endpointPort) {
		this.endpointPort = endpointPort;
	}

	/**
	 * @return endpoint path (ie my_path) without begin /
	 */
	public String getEndpointPath() {
		return endpointPath;
	}


	public void setEndpointPath(String endpointPath) {
		this.endpointPath = endpointPath;
	}

	public String getHserver() {
		return hserver;
	}

	public void setHserver(String hserver) {
		this.hserver = hserver;
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