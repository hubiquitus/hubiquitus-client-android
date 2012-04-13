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

package org.hubiquitus.hapi.tranport.xmpp;

import org.hubiquitus.hapi.codes.Context;
import org.hubiquitus.hapi.codes.Error;
import org.hubiquitus.hapi.codes.Status;
import org.hubiquitus.hapi.hmessage.Data;
import org.hubiquitus.hapi.options.HOptions;
import org.hubiquitus.hapi.utils.ConfigureProviderManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.util.Log;

public class HTransportXMPPConnectionThread implements Runnable {	
	
	/**
	 * Connection object
	 */
	private Connection connection;
	
	/**
	 * to check if the connection has been set or not
	 */
	private boolean isConnectionSet = false;
	
	/**
	 * Connection options
	 */
	private HOptions options;
	
	/**
	 * Connection status
	 */
	private Status status;
	
	/**
	 * Error code
	 */
	private Error error;
	
	/**
	 * the transport class
	 */
	private HTransportXMPP hTransportXMPP;
	
	/**
	 * the class constructor
	 * @param connection
	 * @param status
	 * @param options
	 */
	public HTransportXMPPConnectionThread(HOptions options, HTransportXMPP hCallback){
		this.options = options;
		this.hTransportXMPP = hCallback;
	}
	
	@Override
	public void run() {
		//configure provider manager (patch ajouté suite au problème de classcastexception)
        ConfigureProviderManager.configureProviderManager();
        
        // Creates a new ConnectionConfiguration for a connection that will connect to the desired host and port.
        ConnectionConfiguration config = new ConnectionConfiguration(options.getDomain(), options.getPorts()[0]);
        
        config.setSecurityMode(SecurityMode.disabled);
        // Sets whether the client will use SASL authentication when logging into the server.
//        config.setSASLAuthenticationEnabled(true);
//        
//        // Sets the TLS security mode used when making the connection.
//        config.setSecurityMode(SecurityMode.required);
//        
//        // Encrypt connection
//        config.setTruststorePath("/system/etc/security/cacerts.bks");
//        config.setTruststoreType("bks");
  
        // Creates a connection with the options specified above
        connection = new XMPPConnection(config);
       
		try {
			status = Status.CONNECTING;
			error = Error.NO_ERROR;
			hCallback(status, error);
			// Establishes a connection to the XMPP server and performs an automatic login 
	    	// only if the previous connection state was logged (authenticated).
			connection.connect();
			Log.i(getClass().getCanonicalName(), "Host : " + connection.getHost());
			Log.i(getClass().getCanonicalName(), "Service : " + connection.getServiceName());
			Log.i(getClass().getCanonicalName(), "Port : " + connection.getPort());
		} catch (XMPPException e) {
			Log.i(getClass().getCanonicalName(),"Connection failed to host : "+ options.getDomain());
			Log.i(getClass().getCanonicalName(), e.getMessage());
		}
		
		if(connection.isConnected()){
			
			try {
				 // Logs in to the server using the strongest authentication mode supported by the server, 
				 // then sets presence to available.
				connection.login(options.getUsername(), options.getPassword());
			
				// Set the status to available
	           Presence presence = new Presence(Presence.Type.available);
	           connection.sendPacket(presence);
	           Log.i(getClass().getCanonicalName(), "User : " + connection.getUser());
	           
	           status = Status.CONNECTED;
	           error = Error.NO_ERROR;
	           hCallback(status, error);
			} catch (XMPPException e) {
				Log.i(getClass().getCanonicalName(),"Failed to log as "+ options.getUsername() +" :");
				Log.i(getClass().getCanonicalName(), e.getMessage());
				
				status = Status.ERROR;
		        error = Error.AUTH_FAILED;
		        hCallback(status, error);
			}
		}
		else {
			// TODO the reconnection process
			
			status = Status.DISCONNECTED;
			error = Error.CONNECTION_FAILED;
			hCallback(status, error);
		}
		
		// Creation of a packet filter
		PacketFilter packetFilter = new PacketFilter() {
			
			@Override
			public boolean accept(Packet arg0) {
				return true;
			}
		};
		
		// Creation of a packet listener to get incoming message
		PacketListener packetListener = new PacketListener() {
			
			@Override
			public void processPacket(Packet arg0) {
				Log.i(getClass().getCanonicalName(), "Received a packet");
				Log.i(getClass().getCanonicalName(), arg0.toXML());
				hTransportXMPP.hCallbackConnection(Context.MESSAGE, new Data(null, null, null , arg0.getFrom(), null, arg0.toXML()));
			}
		};
		
		connection.addPacketListener(packetListener, packetFilter);
		
		/* packet listener: listen for incoming messages of type IQ on the connection (whatever the buddy) */
//	    PacketFilter filter = new IQTypeFilter(IQ.Type.SET); // or IQ.Type.GET etc. according to what you like to filter. 
//
//	    connection.addPacketListener(new PacketListener() { 
//	        public void processPacket(Packet packet) {
//	        	Log.i(getClass().getCanonicalName(), "Received an IQ packet");
//				Log.i(getClass().getCanonicalName(), packet.toXML());
//	        }
//	    }, filter);  

	}
	
	/**
	 * Method that notify to the transport class that the connection is complete
	 */
	public void hCallback(Status status, Error error){
		if(!isConnectionSet){
			hTransportXMPP.setConnection(connection);
			isConnectionSet = true;
		}
	
		Data data = new Data();
		data.setStatus(status);
		if(error != Error.NO_ERROR) data.setError(error);
		hTransportXMPP.hCallbackConnection(Context.LINK, data);
	}

}
