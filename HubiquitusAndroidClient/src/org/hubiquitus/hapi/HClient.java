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

package org.hubiquitus.hapi;

import org.hubiquitus.hapi.callback.HTransportCallback;
import org.hubiquitus.hapi.codes.Context;
import org.hubiquitus.hapi.codes.Error;
import org.hubiquitus.hapi.hmessage.Data;
import org.hubiquitus.hapi.options.HOptions;
import org.hubiquitus.hapi.transport.HTransport;
import org.hubiquitus.hapi.transport.socketio.HTransportSocketIO;
import org.hubiquitus.hapi.transport.xmpp.HTransportXMPP;

public class HClient implements HTransportCallback {

	/**
	 * Transport used 
	 */
	private HTransport hTransport;
	
	/**
	 * the callback
	 */
	private HTransportCallback client;
	
	/**
	 * the class constructor
	 * @param client
	 */
	public HClient(HTransportCallback client, android.content.Context context){
		connect(new HOptions(), context);
		this.client = client;
	}
	
	/**
	 * The constructor with options
	 * @param username
	 * @param password
	 * @param client
	 * @param hOptions
	 */
	public HClient(String username, String password, HTransportCallback client, HOptions hOptions, android.content.Context context){
		hOptions.setUsername(username);
		hOptions.setPassword(password);
		connect(hOptions, context);
		this.client = client;
	}
	
	/**
	 * the connection method
	 * @param options
	 */
	public void connect(HOptions options, android.content.Context context){
		if(options.getTransport() == "bosh"){
			hTransport = new HTransportXMPP(this);
			hTransport.connect(options, context);
		}
		else if(options.getTransport() == "socketio"){
			hTransport = new HTransportSocketIO(this);
			hTransport.connect(options, context);
		}
		else hCallbackConnection(Context.ERROR, new Data(null, Error.UNKNOWN_ERROR, null, null, null, null));
		
	}
	
	public void disconnect(){
		hTransport.disconnect();
	}
	
	public void subscribe(String channelTosubscribeTo){
		hTransport.subscribe(channelTosubscribeTo);
	}
	
	public void publish(String channelToPublishTo, String message ){
		hTransport.publish(channelToPublishTo, message);
	}
	
	public void unsubscribe(String channelToUnsubscribeFrom){
		hTransport.unsubscribe(channelToUnsubscribeFrom);
	}
	
	public void getMessages(String channelToGetMessageFrom){
		hTransport.getMessages(channelToGetMessageFrom);
	}

	@Override
	public void hCallbackConnection(Context context, Data data) {
		client.hCallbackConnection(context, data);
	}


}
