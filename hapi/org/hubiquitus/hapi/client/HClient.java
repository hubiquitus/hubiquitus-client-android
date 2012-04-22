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

import org.hubiquitus.hapi.hStructures.ConnectionError;
import org.hubiquitus.hapi.hStructures.ConnectionStatus;
import org.hubiquitus.hapi.hStructures.HStatus;
import org.hubiquitus.hapi.structures.JabberID;
import org.hubiquitus.hapi.transport.HTransport;
import org.hubiquitus.hapi.transport.HTransportCallback;
import org.hubiquitus.hapi.transport.HTransportOptions;
import org.hubiquitus.hapi.transport.xmpp.HTransportXMPP;
import org.hubiquitus.hapi.util.HUtil;

/**
 * @author j.desousag
 * @version 0.3
 * Hubiquitus client, public api
 */

public class HClient implements HTransportCallback {
	
	private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED; /* only connecting, connected, diconnecting, disconnected */
	private HOptions options = null;
	private HTransportOptions transportOptions = null;
	private HCallback callback = null;
	private HTransport transport;
	
	public HClient() {
		transportOptions = new HTransportOptions();
	}

	/**
	 * Connect to server
	 * 
	 * @param publisher - user jid (ie : my_user@domain/resource)
	 * @param password
	 * @param callback - client callback to get api notifications
	 * @param options
	 */
	public void connect(String publisher, String password, HCallback callback, HOptions options) {
		boolean shouldConnect = false;
		boolean connInProgress = false;
		boolean disconInProgress = false;
		
		//synchronize connection status updates to make sure, we have one connect at a time
		synchronized (this) {
			if (this.connectionStatus == ConnectionStatus.DISCONNECTED) {
				shouldConnect = true;
				
				//update connection status
				connectionStatus = ConnectionStatus.CONNECTING;
			} else if(this.connectionStatus == ConnectionStatus.CONNECTING) {
				connInProgress = true;
			} else if(this.connectionStatus == ConnectionStatus.DISCONNECTING) {
				disconInProgress = true;
			}
		}
		
		if (shouldConnect) { //if not connected, then connect
			
			this.callback = callback;
			
			//notify connection
			this.updateStatus(ConnectionStatus.CONNECTING, null, null);
			
			//fill HTransportOptions
			try {
				this.fillHTransportOptions(publisher, password, options);
			} catch (Exception e) { 
				//stop connecting if filling error
				this.updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.JID_MALFORMAT, e.getMessage());
				return;
			}
			
			//choose transport layer
			//if(options.getTransport().equals("xmpp")) {
				if (this.transport == null || (this.transport.getClass() != HTransportXMPP.class)) {
					this.transport = new HTransportXMPP();
				}
				
				this.transport.connect(this, this.transportOptions);
			//}
		} else {
			if (connInProgress) {
				updateStatus(ConnectionStatus.CONNECTING, ConnectionError.CONN_PROGRESS, null);
			} else if (disconInProgress) {
				updateStatus(ConnectionStatus.DISCONNECTING, ConnectionError.ALREADY_CONNECTED, null);
			} else {
				updateStatus(ConnectionStatus.CONNECTED, ConnectionError.ALREADY_CONNECTED, null);
			}	
		}
	}

	/**
	 * fill htransport, randomly pick an endpoint from availables endpoints.
	 * By default it uses options server host to fill serverhost field and as fallback jid domain
	 * @param publisher - publisher as jid format (my_user@serverhost.com/my_resource)
	 * @param password
	 * @param options 
	 * @throws Exception - in case jid is malformatted, it throws an exception
	 */
	public void fillHTransportOptions(String publisher, String password, HOptions options) throws Exception {
		JabberID jid = new JabberID(publisher);
		
		this.transportOptions.setJid(jid);
		this.transportOptions.setPassword(password);
		
		//by default we user server host rather than publish host if defined
		if (options.getServerHost() != null) {
			this.transportOptions.setServerHost(options.getServerHost());
		} else { 
			this.transportOptions.setServerHost(jid.getDomain());
		}
		
		this.transportOptions.setServerPort(options.getServerPort());
		
		//for endpoints, pick one randomly and fill htransport options
		if (options.getEndpoints().size() > 0) {
			int endpointIndex = HUtil.pickIndex(options.getEndpoints()); 
			String endpoint = options.getEndpoints().get(endpointIndex);
			
			this.transportOptions.setEndpointHost(HUtil.getHost(endpoint));
			this.transportOptions.setEndpointPort(HUtil.getPort(endpoint));
			this.transportOptions.setEndpointPath(HUtil.getPath(endpoint));
		} else {
			this.transportOptions.setEndpointHost(null);
			this.transportOptions.setEndpointPort(0);
			this.transportOptions.setEndpointPath(null);
		}
	}
	
	/**
	 * change current status and notify delegate through callback
	 * @param status - connection status
	 * @param error - error code
	 * @param errorMsg - a low level description of the error
	 */
	public void updateStatus(ConnectionStatus status, ConnectionError error, String errorMsg) {
		if (callback != null) {
			connectionStatus = status;
			
			//create structure 
			HStatus hstatus = new HStatus();
			hstatus.setStatus(status);
			hstatus.setErrorCode(error);
			hstatus.setErrorMsg(errorMsg);
			
			callback.hCallback("hstatus", hstatus);
		} else {
			throw new NullPointerException("Error : " + this.getClass().getName() + " requires a callback");
		}
	}
	
	/**
	 * Disconnect
	 */
	public void disconnect() {
		boolean shouldDisconnect = false;
		synchronized (this) {
			if (this.connectionStatus == ConnectionStatus.CONNECTED 
					|| this.connectionStatus == ConnectionStatus.CONNECTING) {
				shouldDisconnect = true;
				
				//update connection status
				connectionStatus = ConnectionStatus.DISCONNECTING;
			}
		}
		
		if(shouldDisconnect) {
			updateStatus(ConnectionStatus.DISCONNECTING, null, null);
			transport.disconnect();
		} else {
			updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.NOT_CONNECTED, null);
		}
	}

	/* HTransportCallback functions */
	
	/**
	 * @internal
	 * see HTransportCallback for more informations
	 */
	public void connectionCallback(ConnectionStatus status,
			ConnectionError error, String errorMsg) {
		this.updateStatus(status, error, errorMsg);
	}
}
