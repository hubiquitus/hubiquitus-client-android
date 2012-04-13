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

import java.util.List;

import org.hubiquitus.hapi.HClient;
import org.hubiquitus.hapi.callback.HTransportCallback;
import org.hubiquitus.hapi.codes.Context;
import org.hubiquitus.hapi.codes.Error;
import org.hubiquitus.hapi.codes.Status;
import org.hubiquitus.hapi.codes.Type;
import org.hubiquitus.hapi.hmessage.Data;
import org.hubiquitus.hapi.options.HOptions;
import org.hubiquitus.hapi.transport.HTransport;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.Subscription;

import android.util.Log;

public class HTransportXMPP implements HTransport, HTransportCallback {

	/**
	 * Connection object
	 */
	private Connection connection;
	
	/**
	 * The domain name
	 */
	private String domain;
	
	/**
	 * The address of the pubsub service
	 */
	private String pubSubAdress;
	
	/**
	 * Connection status
	 */
	private Status status;
	
	/**
	 * Error code
	 */
	private Error error;
	
	/**
	 * the pubsub manager
	 */
	private PubSubManager pubSubManager;
	
	/**
	 * the client
	 */
	private HClient hClient;
	
	/**
	 * the class constructor
	 * @param hCallback
	 */
	public HTransportXMPP(HClient hCallback){
		this.hClient = hCallback;
	}
	
	@Override
	public void connect(HOptions options) {
		// Connection process executed by a secondary thread
		new Thread (new HTransportXMPPConnectionThread(options, this)).start();
	}

	@Override
	public void disconnect() {
		status = Status.DISCONNECTING;
		error = Error.NO_ERROR;
		hCallbackConnection(Context.LINK, new Data(status, error, null, null, null, null));
		
		connection.disconnect();
		
		if(!connection.isConnected()){
			status = Status.DISCONNECTED;
			error = Error.NO_ERROR;
			hCallbackConnection(Context.LINK, new Data(status, error, null, null, null, null));
		}
	}

	@Override
	public void subscribe(String channelToSubscribeTo) {
		
		LeafNode leaf = getLeaf(channelToSubscribeTo, Type.SUBSCRIBE); 
		
		if(leaf != null){
		
			 // Subscribe to the node 
			String user = connection.getUser();
			List<Subscription> subs = null;
			try {
				subs = leaf.getSubscriptions();
			} catch (XMPPException e) {
				Log.i(getClass().getCanonicalName(),"Failed to get subscription list from node "+ channelToSubscribeTo + " : ");
				Log.i(getClass().getCanonicalName(), e.getMessage());
				
				error = Error.GET_SUBS_FAILED;
				hCallbackConnection(Context.ERROR, new Data(null, error, Type.SUBSCRIBE, channelToSubscribeTo, null, null));
			}
			boolean find = false;
			for (int i=0; i<subs.size(); i++) {
				Subscription sub = subs.get(i);
				String jid = sub.getJid();
				if (user.equals(jid)) {
					Log.i(getClass().getCanonicalName(),"The user " + user + " has already suscribe to the node " + channelToSubscribeTo);
					error = Error.ALREADY_SUBSCRIBED;
					hCallbackConnection(Context.ERROR, new Data(null, error, Type.SUBSCRIBE, channelToSubscribeTo, null, null));
					find = true;
				}
			}
			if (!find) {
				try {
					leaf.subscribe(user);
					hCallbackConnection(Context.RESULT, new Data(null, null, Type.SUBSCRIBE, channelToSubscribeTo, null, null));
				} catch (XMPPException e) {
					Log.i(getClass().getCanonicalName(),"Failed to subscribe to node "+ channelToSubscribeTo + " : ");
					Log.i(getClass().getCanonicalName(), e.getMessage());
					
					error = Error.UNKNOWN_ERROR;
					hCallbackConnection(Context.ERROR, new Data(null, error, Type.SUBSCRIBE, channelToSubscribeTo, null, null));
				}
				Log.i(getClass().getCanonicalName(),"The user " + user + " suscribed to node " + channelToSubscribeTo);
			}
		}
	}

	@Override
	public void unsubscribe(String channelToUnsubscribeFrom) {
		
		LeafNode leaf = getLeaf(channelToUnsubscribeFrom, Type.UNSUBSCRIBE);
		
		if(leaf != null){
			
			 // Subscribe to the node 
			String user = connection.getUser();
			List<Subscription> subs = null;
			try {
				subs = leaf.getSubscriptions();
			} catch (XMPPException e) {
				Log.i(getClass().getCanonicalName(),"Failed to get subscription list from node "+ channelToUnsubscribeFrom + " : ");
				Log.i(getClass().getCanonicalName(), e.getMessage());
				
				error = Error.GET_SUBS_FAILED;
				hCallbackConnection(Context.ERROR, new Data(null, error, Type.UNSUBSCRIBE, channelToUnsubscribeFrom, null, null));
			}
			boolean find = false;
			for (int i=0; i<subs.size(); i++) {
				Subscription sub = subs.get(i);
				String jid = sub.getJid();
				if (user.equals(jid)) {
					try {
						leaf.unsubscribe(user);
						hCallbackConnection(Context.RESULT, new Data(null, null, Type.UNSUBSCRIBE, channelToUnsubscribeFrom, null, null));
					} catch (XMPPException e) {
						Log.i(getClass().getCanonicalName(),"Failed to unsubscribe from node "+ channelToUnsubscribeFrom + " : ");
						Log.i(getClass().getCanonicalName(), e.getMessage());
						
						error = Error.NOT_SUBSCRIBED;
						hCallbackConnection(Context.ERROR, new Data(null, error, Type.UNSUBSCRIBE, channelToUnsubscribeFrom, null, null));
					}
					Log.i(getClass().getCanonicalName(),"The user " + user + " successfully unsuscribed from the node " + channelToUnsubscribeFrom);
					find = true;
				}
			}
			if (!find) {
				Log.i(getClass().getCanonicalName(),"Failed to unsubscribe to node "+ channelToUnsubscribeFrom + " because no existing subscription.");
				error = Error.NOT_SUBSCRIBED;
				hCallbackConnection(Context.ERROR, new Data(null, error, Type.UNSUBSCRIBE, channelToUnsubscribeFrom, null, null));
			}
		}	
	}

	@Override
	public void publish(String channelToPublishTo, String message) {
		LeafNode leaf = getLeaf(channelToPublishTo, Type.PUBLISH);

		if(leaf != null){
			// Prepare the message to send
			String content = "<entry xmlns=\"org.hubiquitus.hapi.entry\">"+message+"</entry>";
			PayloadItem<SimplePayload> payloadItem = new PayloadItem<SimplePayload>(new SimplePayload(domain, pubSubAdress, content));
			
			// Send the message
			leaf.publish(payloadItem);
			hCallbackConnection(Context.RESULT, new Data(null, null, Type.PUBLISH, channelToPublishTo, null, null));
			Log.i(getClass().getCanonicalName(),"publish to node " + channelToPublishTo + " the following message : " + content);
		}
	}

	@Override
	public void getMessages(String channelToGetMessageFrom) {
		
		// Get the node
		LeafNode leaf = getLeaf(channelToGetMessageFrom, null);
		List<PayloadItem<SimplePayload>> items = null;
	    
		if(leaf != null){
		      
			// Get persistent messages
			try {
				// attention noeud configuré avec un seul persistent item
				items = leaf.getItems();
			} catch (XMPPException e) {
				Log.i(getClass().getCanonicalName(),"Failed to get persistent items on node "+ channelToGetMessageFrom + " : ");
				Log.i(getClass().getCanonicalName(), e.getMessage());
				
				error = Error.UNKNOWN_ERROR;
				//hCallbackConnection(Context.ERROR, new Data(null, error, null, channelToGetMessageFrom, null, null));
			}
		     
			if(items != null){
				for(int i = 0; i<items.size(); i++){
					Log.i(getClass().getCanonicalName(), "item " + i + " : " + items.get(i).toString());
					hCallbackConnection(Context.MESSAGE, new Data(null, null, null, channelToGetMessageFrom, null, items.get(i).toXML()));
				}
			}
		}
		
	}
	
	public LeafNode getLeaf(String channel, Type type){
		
		LeafNode leaf = null; 
		
		try {
			//  Reload list of existing pubsub nodes on the server
			leaf =  (LeafNode) pubSubManager.getNode(channel);
			/** 
			 ** LeafNode : The main class for the majority of pubsub functionality. 
			 ** In general almost all pubsub capabilities are related to the concept of a node. 
			 ** All items are published to a node, and typically subscribed to by other users. 
			 ** These users then retrieve events based on this subscription.
			 **/
		} catch (XMPPException e) {
			Log.i(getClass().getCanonicalName(),"Failed to get node "+ channel + " : ");
			Log.i(getClass().getCanonicalName(), e.getMessage());
			
			error = Error.UNKNOWN_ERROR;
			if(type != null) hCallbackConnection(Context.ERROR, new Data(null, error, type, channel, null, null));
			
			return null;
		}
		return leaf;
	}
	
	@Override
	public void hCallbackConnection(Context context, Data data) {
		
		Log.i(getClass().getCanonicalName(), "Context : " + context.getValue());
		Log.i(getClass().getCanonicalName(), "Data : " + data.toString());
//		if(status != null) this.status = status;
//		this.error = error;
		
		if(data.getStatus() == Status.CONNECTED && pubSubManager == null){
			
			// Create the pubsub manager
			domain = connection.getServiceName();
			pubSubAdress = "pubsub."+domain;
			// Create a pubsub manager associated to the specified connection where the pubsub requests 
			// require a specific address to send packets.
			pubSubManager = new PubSubManager(connection, "pubsub." + connection.getServiceName());
			Log.i(getClass().getCanonicalName(), "pubSubManager created");
			
			//getLeaf("testandroid2");
			//subscribe("testandroid2");
			//publish("testandroid2", "blabla");
		}
		
		hClient.hCallbackConnection(context, data);
	}
	
	/*****   Getters et Setters   *****/

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}


}
