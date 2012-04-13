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

package org.hubiquitus.hapi.utils;

public class JabberID {

	/**
	 * Contain the username
	 */
	private String username;

	/**
	 * Contain the domain
	 */
	private String domain;

	/**
	 * Contain ressources
	 */
	private String ressources;
	/**
	 * Contain the bareJID
	 */
	private String bareJID;

	/* Function */

	/**
	 * Construct the JabberID from a string
	 * @param String
	 */
	public JabberID(String login) {

		this.username = null;
		this.domain = null;
		this.ressources = null;
		this.setBareJID(null);

		if(login != null) {
			this.username = login.split("@")[0];
			if (login.contains("@")) {
				this.domain = login.split("@")[1];
				if (login.contains("/")) {
					this.domain = this.domain.split("/")[0];
					this.ressources =  login.split("/")[1];
				}
			}

			this.setBareJID(this.username + "@" +this.domain);
		}
	}

	@Override
	public String toString() {
		return "JabberID [username=" + username + ", domain=" + domain
				+ ", ressources=" + ressources + ", bareJID=" + bareJID + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bareJID == null) ? 0 : bareJID.hashCode());
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result
				+ ((ressources == null) ? 0 : ressources.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		JabberID other = (JabberID) obj;
		if (bareJID == null) {
			if (other.bareJID != null)
				return false;
		} else if (!bareJID.equals(other.bareJID))
			return false;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (ressources == null) {
			if (other.ressources != null)
				return false;
		} else if (!ressources.equals(other.ressources))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/*****   Getters et Setters   *****/


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getRessources() {
		return ressources;
	}

	public void setRessources(String ressources) {
		this.ressources = ressources;
	}

	public String getBareJID() {
		return bareJID;
	}

	public void setBareJID(String bareJID) {
		this.bareJID = bareJID;
	}



}