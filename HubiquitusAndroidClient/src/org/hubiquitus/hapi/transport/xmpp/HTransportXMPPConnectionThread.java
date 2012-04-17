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

package org.hubiquitus.hapi.transport.xmpp;

import java.util.ArrayList;
import java.util.List;

import org.hubiquitus.hapi.codes.Context;
import org.hubiquitus.hapi.codes.Error;
import org.hubiquitus.hapi.codes.Status;
import org.hubiquitus.hapi.hmessage.Data;
import org.hubiquitus.hapi.options.HOptions;
import org.hubiquitus.hapi.utils.ConfigureProviderManager;
import org.hubiquitus.hapi.utils.Parser;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.Header;
import org.jivesoftware.smackx.packet.HeadersExtension;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemsExtension;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.SimplePayload;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
	 * the main activity context
	 */
	private android.content.Context context;
	
	/**
	 * the class constructor
	 * @param connection
	 * @param status
	 * @param options
	 */
	public HTransportXMPPConnectionThread(HOptions options, HTransportXMPP hCallback, android.content.Context context){
		this.options = options;
		this.hTransportXMPP = hCallback;
		this.context = context;
	}
	
	@Override
	public void run() {
		//configure provider manager (patch ajouté suite au problème de classcastexception)
        ConfigureProviderManager.configureProviderManager();
        
        // Creates a new ConnectionConfiguration for a connection that will connect to the desired host and port.
        ConnectionConfiguration config = new ConnectionConfiguration(options.getDomain(), options.getPorts()[0]);
        
        // Sets whether the client will use SASL authentication when logging into the server.
        config.setSASLAuthenticationEnabled(true);
        
        // Sets the TLS security mode used when making the connection.
        config.setSecurityMode(SecurityMode.required);
        
        // Encrypt connection
        config.setTruststorePath("/system/etc/security/cacerts.bks");
        config.setTruststoreType("bks");
        
        // Creates a connection with the options specified above
        connection = new XMPPConnection(config);
        
        
        // Check if the phone/pad is connected to o a network
        ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		// not connected
		if ((info == null) || (!info.isConnected())) {
			Log.i(getClass().getCanonicalName(),"No connection detected, can not connect");
			status = Status.DISCONNECTED;
			error = Error.CONNECTION_FAILED;
			hCallback(status, error);
		}
		else {
			try {
				status = Status.CONNECTING;
				error = Error.NO_ERROR;
				hCallback(status, error);
				// Establishes a connection to the XMPP server and performs an automatic login 
		    	// only if the previous connection state was logged (authenticated).
				connection.connect();
			} catch (XMPPException e) {
				Log.i(getClass().getCanonicalName(),"Connection failed to host : "+ options.getDomain());
				Log.i(getClass().getCanonicalName(), e.getMessage());
				
				status = Status.DISCONNECTED;
				error = Error.CONNECTION_FAILED;
				hCallback(status, error);
			}
			
			
			try {
				// Logs in to the server using the strongest authentication mode supported by the server, 
				// then sets presence to available.
				connection.login(options.getUsername(), options.getPassword());
			
				// Set the status to available
				Presence presence = new Presence(Presence.Type.available);
	           	connection.sendPacket(presence);
	           
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
			
			if(!connection.isConnected() || !connection.isAuthenticated())
				try {
					tryToReconnect();
				} catch (InterruptedException e) {
					Log.i(getClass().getCanonicalName(),"InterruptedException");
					Log.i(getClass().getCanonicalName(), e.getMessage());
				}
				
			if(connection.isConnected() && connection.isAuthenticated()){
				
				
				// Creation of a packet listener to get incoming message
				PacketListener packetListener = new PacketListener() {
					
					@Override
					public void processPacket(Packet arg0) {
						if(arg0 instanceof Message){
							Message message = (Message) arg0;
							
							if(message.getExtension("http://jabber.org/protocol/pubsub#event") instanceof EventElement){
								// get the event extension
								EventElement event = (EventElement)message.getExtension("http://jabber.org/protocol/pubsub#event");
								
								if(event.getEventType().name().equals("items")){ // get the type of the event to check if it actually contains items
									// get the event
									ItemsExtension extension = (ItemsExtension)event.getEvent();
									String channelName = extension.getNode();
									//Log.i("packet listener (event name) : ", extension.getElementName());
									
									// get the items
									for(int i=0; i<extension.getItems().size(); i++){
										Item item = (Item)extension.getItems().get(i);
										//Log.i("packet listener (event item)", item.getElementName());
										hTransportXMPP.hCallbackConnection(Context.MESSAGE, new Data(null, null, null , channelName, null, Parser.parseItem(item.toXML())));

									}
								}
							}

						}
						else{
							Log.i("instanceof : ", "type "+arg0.getClass().getSimpleName());
							Log.i("packet listener (packet) : ", arg0.toXML());
						}
					}
				};
				
				PacketFilter packetFilter = new PacketFilter() {
					
					@Override
					public boolean accept(Packet arg0) {
						return true;
					}
				};
				
				PacketListener packetSender = new PacketListener() {
					
					@Override
					public void processPacket(Packet arg0) {
						Log.i(getClass().getCanonicalName(), "Send a packet : ");
						Log.i(getClass().getCanonicalName(), arg0.toXML());
					}
				};
			
				connection.addPacketListener(packetListener, packetFilter);
				connection.addPacketSendingListener(packetSender, packetFilter);
				
			}
			else {
				status = Status.DISCONNECTED;
				error = Error.CONNECTION_FAILED;
				hCallback(status, error);
				
			}
		}
		
	}
	
	/**
	 * Launch the connection process if disconnected
	 * @return the connection state
	 */
	public boolean tryToReconnect() throws InterruptedException{
		int nbTrial = 0;
		while(nbTrial < options.getRetryInterval().length && !connection.isAuthenticated()){
			// disconnect if connected
//			if(connection.isConnected()){
//				status = Status.DISCONNECTING;
//				error = Error.NO_ERROR;
//				hCallback(status, error);
//				
//				connection.disconnect();
//				
//				if(!connection.isConnected()){
//					status = Status.DISCONNECTED;
//					error = Error.NO_ERROR;
//					hCallback(status, error);
//				}
//			}
			
			// wait
			Thread.sleep(options.getRetryInterval()[nbTrial]);

			if (!connection.isConnected()) {

		        // Check if the phone/pad is connected to a network
		        ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = cm.getActiveNetworkInfo();
				// not connected
				if ((info == null) || (!info.isConnected())) {
					Log.i(getClass().getCanonicalName(),"No connection detected, can not connect");
					status = Status.DISCONNECTED;
					error = Error.CONNECTION_FAILED;
					hCallback(status, error);
					// No need to try to reconnect
					nbTrial = options.getRetryInterval().length;
				}
				else {
					// retry to connect
					try {
						status = Status.CONNECTING;
						error = Error.NO_ERROR;
						hCallback(status, error);
						
						connection.connect();
					} catch (XMPPException e) {
						Log.i(getClass().getCanonicalName(),"Connection failed to host : "+ options.getDomain());
						Log.i(getClass().getCanonicalName(), e.getMessage());
						
						status = Status.DISCONNECTED;
						error = Error.CONNECTION_FAILED;
						hCallback(status, error);
					}
				}
			}
				
			if (connection.isConnected() && !connection.isAuthenticated()) {
				// retry login
				try {
					status = Status.CONNECTED;
					error = Error.NO_ERROR;
					hCallback(status, error);
					
					connection.login(options.getUsername(), options.getPassword());
				} catch (XMPPException e) {
					Log.i(getClass().getCanonicalName(),"Failed to log as "+ options.getUsername() +" :");
					Log.i(getClass().getCanonicalName(), e.getMessage());
					
					status = Status.ERROR;
			        error = Error.AUTH_FAILED;
			        hCallback(status, error);
				}
			}
			
			nbTrial++;
		}

		return connection.isConnected();
	}
	
	/**
	 * Method that notify to the transport class that the connection is complete
	 */
	public void hCallback(Status status, Error error){
		//connection transfered to HTransportXMPP class
		if(!isConnectionSet){
			hTransportXMPP.setConnection(connection);
			isConnectionSet = true;
		}
	
		// set the data to send
		Data data = new Data();
		data.setStatus(status);
		if(error != Error.NO_ERROR) data.setError(error);
		hTransportXMPP.hCallbackConnection(Context.LINK, data);
	}

}
