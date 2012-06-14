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

import java.util.Timer;
import java.util.TimerTask;

import org.hubiquitus.hapi.hStructures.ConnectionError;
import org.hubiquitus.hapi.hStructures.ConnectionStatus;
import org.hubiquitus.hapi.transport.HTransport;
import org.hubiquitus.hapi.transport.HTransportDelegate;
import org.hubiquitus.hapi.transport.HTransportOptions;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemsExtension;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @cond internal
 * @version 0.3
 * HTransportXMPP is the xmpp transport layer of the hubiquitus hAPI client
 */

public class HTransportXMPP implements HTransport, ConnectionListener,PacketListener {

	private HTransportDelegate callback = null;
	private HTransportOptions options = null;
	private Connection connection = null;
	private ConnectionConfiguration config = null;
	private Thread connectionThread = null;
	private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
	
	public HTransportXMPP() {
		//patch for android to add xmpp providers
		SmackConfiguration.setPacketReplyTimeout(10000);
		SmackConfigureProviderManager.configureProviderManager();
	};	
	
	/**
	 * connect to an xmpp server
	 * @param callback - see HTransportCallback for more informations
	 * @param options - transport options
	 */
	public void connect(HTransportDelegate callback, HTransportOptions options){	
		if (connection != null && connection.isConnected()) {
			connection.disconnect();
		}
		
		this.connectionStatus = ConnectionStatus.CONNECTING;
		
		this.callback = callback;
		this.options = options;
		
		String serverHost = options.getServerHost();
		int serverPort = options.getServerPort();
		String serviceName = options.getJid().getDomain();

		//each time it connect we create a new configuration and connection objects
		//because smack doesn't allow setting host, or configuration on existing objects
		//@todo check if config has changed rather than create a new one
		this.config = new ConnectionConfiguration(serverHost, serverPort, serviceName);
//		//patch for android to support security
		config.setTruststorePath("/system/etc/security/cacerts.bks");
	    config.setTruststoreType("bks");

	    // Sets whether the client will use SASL authentication when logging into the server.
	    //config.setSASLAuthenticationEnabled(true);
			        
	    // Sets the TLS security mode used when making the connection.
	    //config.setSecurityMode(SecurityMode.required);
	    
	    config.setReconnectionAllowed(false);
	    config.setSendPresence(true);
	    
	  //add a timer to monitor connection thread. If it takes too long kill it and return conn timeout
	    final Timer timer = new Timer();
	    
	    try {
	    	this.connection = new XMPPConnection(config);
	        
		    final HTransportOptions localOptions = options;
		    final HTransportXMPP outerClass = this;
		    
		    //create a thread to connect async
		    this.connectionThread = new Thread(new Runnable() {

				public void run() {
					try {
						//launch connection and add connection listener for errors
						connection.connect();
						connection.addConnectionListener(outerClass);
						try {
							//try to login and update status
							connection.login(localOptions.getUsername(), localOptions.getPassword(), localOptions.getResource());
							updateStatus(ConnectionStatus.CONNECTED, ConnectionError.NO_ERROR, null);
							PacketFilter hserverPacketFilter = new FromContainsFilter(localOptions.getHserverService());
							PacketFilter pubsubPacketFilter = new FromContainsFilter(localOptions.getPubsubService());
							PacketFilter packetFilter = new OrFilter(hserverPacketFilter, pubsubPacketFilter);
							connection.addPacketListener(outerClass,packetFilter);
						} catch(Exception e) { //login failed
							boolean wasConnected = false;
							if (connection.isConnected()) {
								wasConnected = true;
								connection.disconnect();
							}
							connection = null;
							
							//update status only if connected, because if not, it's a network error not a login error
							if(wasConnected) {
								updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.AUTH_FAILED, e.getMessage());
							}
						}
						
					} catch(Exception e) { //in case connection failed
						connection = null;
						updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.TECH_ERROR , e.getMessage());
					}	
					timer.cancel();
				}
			});
		    this.connectionThread.start(); //start async thread
	    } catch(Exception e) { //in case thread creation failed or it was interrupted
	    	if (connection.isConnected()) {
				connection.disconnect();
			}
	    	//e.printStackTrace();
	    	this.connection = null;
	    	this.updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.TECH_ERROR, e.getMessage());
	    }
	    
	    
	    //set timer task to add a connection timeout
	    timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if (connectionThread != null && connectionThread.isAlive()) {
					connectionThread.interrupt();
					if(connection.isConnected()) {
						connection.disconnect();
					}
				}
				updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.CONN_TIMEOUT, null);
			}
		}, SmackConfiguration.getPacketReplyTimeout());
	}
	
	/**
	 * change current status and notify delegate through callback
	 * @param status - connection status
	 * @param error - error code
	 * @param errorMsg - a low level description of the error
	 */
	public void updateStatus(ConnectionStatus status, ConnectionError error, String errorMsg) {
		this.connectionStatus = status;
		if (callback != null) {
			callback.onStatus(status, error, errorMsg);
			if(this.connectionStatus == ConnectionStatus.DISCONNECTED) {
				callback = null;
			}
		}
	}

	/**
	 * Disconnect from server 
	 * @param callback
	 * @param options
	 */
	public void disconnect() {
		this.connectionStatus = ConnectionStatus.DISCONNECTING;
		
		//stop connection thread if running and to try to disconnect through usual way
		//we should resend disconnected if connection thread is alive
		if(connectionThread != null && connectionThread.isAlive()) {
			connectionThread.interrupt(); 
			if(connection != null && connection.isConnected()) {
				connection.disconnect();
			}
			updateStatus(ConnectionStatus.DISCONNECTED, null, null);
		} else {
			try {
			    //create a thread to disconnect async
			    new Thread(new Runnable() {

					public void run() {
						try {
							connection.disconnect();
							updateStatus(ConnectionStatus.DISCONNECTED, null, null);
						} catch(Exception e) {
							connection = null;
							updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.TECH_ERROR, e.getMessage());			
						}
					}
				}).start();
		    } catch(Exception e) {
		    	this.connection = null;
		    	this.updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.TECH_ERROR, e.getMessage());
		    }
		}
	}

	@Override
	public void sendObject(JSONObject object) {
		if( connectionStatus == ConnectionStatus.CONNECTED) {
			Message msg = new Message(options.getHserverService());
			HMessageXMPP packet = new HMessageXMPP("hcommand",object.toString());
			msg.addExtension(packet);
			connection.sendPacket(msg);
		} else {
			System.out.println("Not connected");
		}		
	}

	@Override
	public void processPacket(Packet receivePacket) {
		if(receivePacket.getClass().equals(Message.class)) {
			if(receivePacket.getFrom().equalsIgnoreCase(options.getHserverService())) {
				HMessageXMPP packetExtention = (HMessageXMPP)receivePacket.getExtension("hbody","");
				if(packetExtention != null) {
					JSONObject jsonObj = null;
					try {
						jsonObj = new JSONObject(packetExtention.getContent());
					} catch (JSONException e) {
						e.printStackTrace();
						System.out.println("Received malformted JSon object from hserver in :" + this.getClass());
					}
					if(jsonObj != null) {
						callback.onData(packetExtention.getType(), jsonObj);
					} else {
						System.out.println("Received malformted JSon object from hserver in :" + this.getClass());
					}
				}else {
					System.out.println("erreur lors de la reception : PacketExtension erreur");
				}
			} else if(receivePacket.getFrom().equalsIgnoreCase(options.getPubsubService())) {
				Message message = (Message)receivePacket;
				if(message.getExtension("http://jabber.org/protocol/pubsub#event") instanceof EventElement){

					// get the event extension
					EventElement event = (EventElement)message.getExtension("http://jabber.org/protocol/pubsub#event");

					if(event.getEventType().name().equals("items")){ // get the type of the event to check if it actually contains items
						// get the event
						ItemsExtension extension = (ItemsExtension)event.getEvent();

						// get the items
						for(int i = 0; i < extension.getItems().size(); i++){
							Item item = (Item)extension.getItems().get(i);
							if (item instanceof PayloadItem) {
							
								JSONObject jsonObj = null;
								try {
									@SuppressWarnings("unchecked")
									PayloadItem<HXMPPPubsubEntry> payloadItem = (PayloadItem<HXMPPPubsubEntry>)item;
									jsonObj = new JSONObject(payloadItem.getPayload().getContent());
								} catch (JSONException e) {
									e.printStackTrace();
									System.out.println("Received malformted JSon object from pubsub in :" + this.getClass());
									System.err.println("error message :" + e.getMessage());
								}
								if(jsonObj != null) {
									callback.onData("hmessage", jsonObj);
								} else {
									System.out.println("Received malformted JSon object from pubsub in :" + this.getClass());
								}
							}
						}
					}
				}
			}
		}
	}

	/* Connection listener interface */
	@Override
	public void connectionClosed() {

	}

	@Override
	public void connectionClosedOnError(Exception e) {
		this.connectionThread.interrupt();
		this.connectionThread = null;
		this.connection = null;
		this.updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.TECH_ERROR, e.getMessage());

	}

	//as we use our reconnection system, this shouldn't be called
	@Override
	public void reconnectingIn(int arg0) {
	}

	//as we use our reconnection system, this shouldn't be called
	@Override
	public void reconnectionFailed(Exception arg0) {
	}

	//as we use our reconnection system, this shouldn't be called
	@Override
	public void reconnectionSuccessful() {
	}


}

/**
 * @endcond
 */