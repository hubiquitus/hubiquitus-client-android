package org.hubiquitus.hapi.transport;

import org.hubiquitus.hapi.structures.JabberID;

public class HTransportOptions {
	private JabberID jid = null;
	private String password = null;
	private String serverHost = null;
	private int serverPort = 0;
	private String endpointHost = null;
	private int endpointPort = 0;
	private String endpointPath = null;
	
	
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
	 * user jid (ie : my_user@domain.com/resource)
	 * @return
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
	 * server host (ie : localhost)
	 * @return
	 */
	public String getServerHost() {
		return serverHost;
	}


	public void setServerHost(String serverHost) {
		if (serverHost == null || serverHost.equals("")) {
			this.serverHost = null;
		} else {
			this.serverHost = serverHost;
		}
	}

	/**
	 * server port (ie : 5222 for xmpp)
	 * @return
	 */
	public int getServerPort() {
		return serverPort;
	}


	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}


	/**
	 * endpoint host (ie : localhost)
	 * @return
	 */
	public String getEndpointHost() {
		return endpointHost;
	}


	public void setEndpointHost(String endpointHost) {
		this.endpointHost = endpointHost;
	}
	
	/** 
	 * endpoint port (ie : 8080)
	 * @return
	 */
	public int getEndpointPort() {
		return endpointPort;
	}


	public void setEndpointPort(int endpointPort) {
		this.endpointPort = endpointPort;
	}

	/**
	 * endpoint path (ie my_path) without begin /
	 * @return
	 */
	public String getEndpointPath() {
		return endpointPath;
	}


	public void setEndpointPath(String endpointPath) {
		this.endpointPath = endpointPath;
	}

	/* overrides */
	
	@Override
	public String toString() {
		return "HTransportOptions [jid=" + jid + ", serverHost=" + serverHost
				+ ", serverPort=" + serverPort + ", endpointHost="
				+ endpointHost + ", endpointPort=" + endpointPort
				+ ", endpointPath=" + endpointPath + "]";
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
		result = prime * result + ((jid == null) ? 0 : jid.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((serverHost == null) ? 0 : serverHost.hashCode());
		result = prime * result + serverPort;
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
		if (serverHost == null) {
			if (other.serverHost != null)
				return false;
		} else if (!serverHost.equals(other.serverHost))
			return false;
		if (serverPort != other.serverPort)
			return false;
		return true;
	}
	
}
