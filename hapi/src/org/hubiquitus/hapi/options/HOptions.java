package org.hubiquitus.hapi.options;

import android.util.Log;

public class HOptions implements Cloneable{
	
	/**
	 * the user login
	 */
	private String username = "username";
	
	/**
	 * the user password
	 */
	private String password = "password";
	
	/**
	 * the server domain
	 */
	private String domain;
	
	/**
	 * the server route
	 */
	private String route; // domain + port
	
	/**
	 * the retry interval to connect to server when disconnected
	 */
	private int[] retryInterval = new int[]{600, 300,  60, 30, 10, 2};
	
	/**
	 * the interval to wait to get a timeout exception
	 */
	private long timeOut = 15000;
	
	/**
	 * the interval of rid to reattach successfully
	 */
	private int ridInterval = 10;
	
	/**
	 * the way of connecting to server
	 */
	private String transport = "bosh";
	
	/**
	 * the port to connect to the hubiquitus node, there can be several ports
	 */
	private int[] ports;
	
	/**
	 * the port to connect to the server if different
	 */
	private int[] serverPorts;
	
	/**
	 * the address to connet to the server when using socketio
	 */
	private String endpoint = "http://localhost/"; 
	
	/**
	 * namespace for the connection in case the user wants to run several services using sockets on the same port
	 */
	private String namespace; // -> socketio
	
	/**
	 * default constructor
	 */
	public HOptions(){}
	
	/**
	 * the class constructor
	 * @param domain
	 * @param transport
	 * @param endpoint
	 */
	public HOptions(String domain, String transport, String endpoint){
		this.domain = domain;
		this.transport = transport;
		this.endpoint = endpoint;
		
		if(transport == "bosh"){
			this.ports = new int[]{5222};
		}
		else if(transport == "socketio"){
			this.ports = new int[]{5280};
			this.serverPorts = this.ports;
			// TODO faire le load balancing de port
			this.route = domain + ":" + String.valueOf(ports[0]);
			this.endpoint = "http://" + route + "/";
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HOptions other = (HOptions) obj;
		if (endpoint == null) {
			if (other.endpoint != null)
				return false;
		} else if (!endpoint.equals(other.endpoint))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (ports != other.ports)
			return false;
		if (transport == null) {
			if (other.transport != null)
				return false;
		} else if (!transport.equals(other.transport))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "HOptions [username=" + username + ", password=" + password
				+ ", serverHost=" + domain + ", serverPort=" + ports
				+ ", transport=" + transport + ", endpoint=" + endpoint
				+ " ]";
	}

	@Override
	public HOptions clone() {
		HOptions option = null;

		try {
			option = (HOptions) super.clone();
		} catch (CloneNotSupportedException e) {
			Log.i(getClass().getCanonicalName(), "Cloneable error : ");
			Log.i(getClass().getCanonicalName(), e.getMessage());
		}
		return option;
	}
	
	/*****   Getters et Setters   *****/

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return the route
	 */
	public String getRoute() {
		return route;
	}

	/**
	 * @param route the route to set
	 */
	public void setRoute(String route) {
		this.route = route;
	}

	/**
	 * @return the retryInterval
	 */
	public int[] getRetryInterval() {
		return retryInterval;
	}

	/**
	 * @param retryInterval the retryInterval to set
	 */
	public void setRetryInterval(int[] retryInterval) {
		this.retryInterval = retryInterval;
	}

	/**
	 * @return the timeOut
	 */
	public long getTimeOut() {
		return timeOut;
	}

	/**
	 * @param timeOut the timeOut to set
	 */
	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut*1000;
	}

	/**
	 * @return the ridInterval
	 */
	public int getRidInterval() {
		return ridInterval;
	}

	/**
	 * @param ridInterval the ridInterval to set
	 */
	public void setRidInterval(int ridInterval) {
		this.ridInterval = ridInterval;
	}

	/**
	 * @return the transport
	 */
	public String getTransport() {
		return transport;
	}

	/**
	 * @param transport the transport to set
	 */
	public void setTransport(String transport) {
		this.transport = transport;
	}

	/**
	 * @return the ports
	 */
	public int[] getPorts() {
		return ports;
	}

	/**
	 * @param ports the ports to set
	 */
	public void setPorts(int[] ports) {
		this.ports = ports;
		this.route = domain + ":" + String.valueOf(ports[0]);
		this.endpoint = "http://" + route + "/";
	}

	/**
	 * @return the serverPort
	 */
	public int[] getServerPorts() {
		return serverPorts;
	}

	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPorts(int[] serverPort) {
		this.serverPorts = serverPort;
	}

	/**
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
}
