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

import org.hubiquitus.hapi.structures.JabberID;
import org.hubiquitus.hapi.transport.socketio.HAuthCallback;

/** 
 * @cond internal
 * @version 0.5
 * options used for transport layers
 */

public class HTransportOptions {
	private JabberID jid = null;
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
	 * Convenient function
	 * give username part of the jid
	 * @return username(without domain or resource)
	 */
	public String getUsername() {
		if (jid == null) {
			throw new NullPointerException("Error : " + this.getClass().getName() + " need a jid");
		}
		
		return jid.getUsername();
	}
	
	/**
	 * Convenient function
	 * give resource part of the jid
	 * @return resource
	 */
	public String getResource() {
		if (jid == null) {
			throw new NullPointerException("Error : " + this.getClass().getName() + " need a jid");
		}
		
		return jid.getResource();
	}
	
	/**
	 * @return hserver service name (by default it should be "hnode.domain")
	 */
	public String getHserverService() {
		String nodeService = null;
		
		if(this.jid != null) {
			nodeService = this.hserver + "@" + this.jid.getDomain();
		}
		
		return nodeService;
	}
	
	/**
	 * @return pubsub service name (by default it should be "pubsub")
	 */
	public String getPubsubService() {
		return "pubsub" + "." + this.jid.getDomain();
	}
	
	/**
	 * @return user jid (ie : my_user@domain.com/resource)
	 */
	public JabberID getJid() {
		return jid;
	}


	public void setJid(JabberID jid) {
		this.jid = jid;
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
	
	/* overrides */
	
	@Override
	public String toString() {
		return "HTransportOptions [jid=" + jid + ", password=" + password
				+ ", endpointHost=" + endpointHost + ", endpointPort="
				+ endpointPort + ", endpointPath=" + endpointPath + ", hNode="
				+ hserver + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endpointHost == null) ? 0 : endpointHost.hashCode());
		result = prime * result
				+ ((endpointPath == null) ? 0 : endpointPath.hashCode());
		result = prime * result + endpointPort;
		result = prime * result + ((hserver == null) ? 0 : hserver.hashCode());
		result = prime * result + ((jid == null) ? 0 : jid.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HTransportOptions other = (HTransportOptions) obj;
		if (endpointHost == null) {
			if (other.endpointHost != null)
				return false;
		} else if (!endpointHost.equals(other.endpointHost))
			return false;
		if (endpointPath == null) {
			if (other.endpointPath != null)
				return false;
		} else if (!endpointPath.equals(other.endpointPath))
			return false;
		if (endpointPort != other.endpointPort)
			return false;
		if (hserver == null) {
			if (other.hserver != null)
				return false;
		} else if (!hserver.equals(other.hserver))
			return false;
		if (jid == null) {
			if (other.jid != null)
				return false;
		} else if (!jid.equals(other.jid))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}
}

/**
 * @endcond
 */