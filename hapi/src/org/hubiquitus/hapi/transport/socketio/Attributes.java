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

package org.hubiquitus.hapi.transport.socketio;

public class Attributes {

	/**
	 * full jid
	 */
	private String JID;
	
	/**
	 * session ID
	 */
	private int SID;
	
	/**
	 * request ID
	 */
	private int RID;
	
	/**
	 * default constructor
	 */
	public Attributes(){}
	
	public Attributes(String jid, int sid, int rid){
		this.JID = jid;
		this.SID = sid;
		this.RID = rid;
	}
	
	@Override
	public String toString(){
		String str = "{\"userid\" : \"" + getJID() + "\",";
		str += "\"rid\" : " + getRID() + ",";
		str += "\"sid\" : " + getSID() + "}";
		return str;
	}

	/*****   Getters et Setters   *****/
	
	/**
	 * @return the jID
	 */
	public String getJID() {
		return JID;
	}

	/**
	 * @param jID the jID to set
	 */
	public void setJID(String jID) {
		JID = jID;
	}

	/**
	 * @return the sID
	 */
	public int getSID() {
		return SID;
	}

	/**
	 * @param sID the sID to set
	 */
	public void setSID(int sID) {
		SID = sID;
	}

	/**
	 * @return the rID
	 */
	public int getRID() {
		return RID;
	}

	/**
	 * @param rID the rID to set
	 */
	public void setRID(int rID) {
		RID = rID;
	}
}
