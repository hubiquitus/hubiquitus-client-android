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

import java.util.ArrayList;
import java.util.List;


/**
 * @author j.desousag
 * @version 0.3
 * hAPI options. For more info, see Hubiquitus reference
 */

public class HOptions implements Cloneable {

	
	private String serverHost = null;
	private int serverPort = 5222;
	private String transport = "xmpp";
	private List<String> endpoints = null;
	private int nbLastMessage = 10;
	
	/**
	 * Constructor 
	 */
	public HOptions() {
	}
	
	public HOptions(HOptions options) {
		this.setServerHost(options.getServerHost());
		this.setServerPort(options.serverPort);
		this.setEndpoints(options.getEndpoints());
		this.setTransport(options.getTransport());
		this.setNbLastMessage(options.getNbLastMessage());
	}
	
	
	/**
	 * Constructor
	 * @param serverHost
	 * @param serverPort
	 * @param transport
	 */
	public HOptions(String serverHost ,int serverPort ,String transport){
		this.setServerHost(serverHost);
		this.setServerPort(serverPort);
		this.setTransport(transport);
	}
	
	/* Getters & Setters */
	
	/**
	 * server host (localhost by default for xmpp host)
	 */
	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		if (serverHost == null || serverHost.equals("")) {
			serverHost = null;
		} else {
			this.serverHost = serverHost;
		}
	}

	/**
	 * server port (by default 5222 for xmpp)
	 */
	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		if(serverPort == 0)
			this.serverPort = 5222;
		else
			this.serverPort = serverPort;
	}
	
	public void setServerPort(String serverPort) {
		try {
			this.serverPort = Integer.valueOf(serverPort);
		} catch (Exception e) {
			setServerPort(0);
		}
	}
	
	/**
	 * Transport layer used to connect to hNode (ie : xmpp, socketio)
	 */
	public String getTransport() {
		return transport;
	}	

	public void setTransport(String transport) {
		if(transport.equals("xmpp") || transport.equals("socketio")) {
			this.transport = transport;
		} else {
			this.transport = "xmpp";
		}
	}

	/**
	 * Only valid if transport = xmpp
	 * hNode gateway endpoints formated as domain:port/path (by default : localhost:8080)
	 */
	public List<String> getEndpoints() {
		return new ArrayList<String>(endpoints);
	}

	public void setEndpoints(List<String> endpoints) {
		if(endpoints != null && endpoints.size() > 0)
			this.endpoints = new ArrayList<String>(endpoints);
		else {
			this.endpoints = new ArrayList<String>();
			this.endpoints.add("http://localhost:8080/");
		}	
	}

	/**
	 * max number of messages that should be return by a call to getLastMessages
	 */
	public int getNbLastMessage() {
		return nbLastMessage;
	}

	public void setNbLastMessage(int nbLastMessage) {
		if(nbLastMessage >=1)
			this.nbLastMessage = nbLastMessage;
		else
			this.nbLastMessage = 10;
	}

	
	/* overrides */
	
	@Override
	public String toString() {
		return "HOption [serverHost=" + serverHost + ", serverPort="
				+ serverPort + ", transport=" + transport + ", endpoints="
				+ endpoints + ", nbLastMessage=" + nbLastMessage + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endpoints == null) ? 0 : endpoints.hashCode());
		result = prime * result + nbLastMessage;
		result = prime * result
				+ ((serverHost == null) ? 0 : serverHost.hashCode());
		result = prime * result + serverPort;
		result = prime * result
				+ ((transport == null) ? 0 : transport.hashCode());
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
		HOptions other = (HOptions) obj;
		if (endpoints == null) {
			if (other.endpoints != null)
				return false;
		} else if (!endpoints.equals(other.endpoints))
			return false;
		if (nbLastMessage != other.nbLastMessage)
			return false;
		if (serverHost == null) {
			if (other.serverHost != null)
				return false;
		} else if (!serverHost.equals(other.serverHost))
			return false;
		if (serverPort != other.serverPort)
			return false;
		if (transport == null) {
			if (other.transport != null)
				return false;
		} else if (!transport.equals(other.transport))
			return false;
		return true;
	}	

}
