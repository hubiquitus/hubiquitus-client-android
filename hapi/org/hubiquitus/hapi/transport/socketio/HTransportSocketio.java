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

import java.util.Timer;
import java.util.TimerTask;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import org.hubiquitus.hapi.hStructures.ConnectionError;
import org.hubiquitus.hapi.hStructures.ConnectionStatus;
import org.hubiquitus.hapi.hStructures.HStatus;
import org.hubiquitus.hapi.transport.HTransport;
import org.hubiquitus.hapi.transport.HTransportDelegate;
import org.hubiquitus.hapi.transport.HTransportOptions; 
import org.json.JSONObject;

/**
 * @cond internal
 * @version 0.3
 * HTransportSocketIO is the socketio transport layer of the hubiquitus hAPI client
 */

public class HTransportSocketio implements HTransport, IOCallback {

	private HTransportDelegate callback = null;
	private HTransportOptions options = null;
	private SocketIO socketio = null;
	private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
	private Timer timeoutTimer = null;
	
	public HTransportSocketio() {
	};	
	
	/**
	 * connect to a socket io hnode gateway
	 * @param callback - see HTransportCallback for more informations
	 * @param options - transport options
	 */
	public void connect(HTransportDelegate callback, HTransportOptions options){	
		this.connectionStatus = ConnectionStatus.CONNECTING;
		
		this.callback = callback;
		this.options = options;
		
		String endpointHost = options.getEndpointHost();
		int endpointPort = options.getEndpointPort();
		String endpointPath = options.getEndpointPath();
		
		String endpointAdress = toEndpointAdress(endpointHost, endpointPort, endpointPath);
		//add a timer to make sure it doesn't go over timeout
		timeoutTimer = new Timer();
		//set timer task to add a connection timeout
	    timeoutTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.CONN_TIMEOUT, null);
				if(socketio.isConnected()) {
					socketio.disconnect();
				}
				
				socketio = null;
			}
		}, 10000);
		
		//init socketio component
		try {
			socketio = new SocketIO();
		} catch (Exception e) {
			if (timeoutTimer != null) {
				timeoutTimer.cancel();
				timeoutTimer = null;
			}
			updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.TECH_ERROR, e.getMessage());
			socketio = null;
		}
		
		//connect
		try {
			socketio.connect(endpointAdress, this);
		} catch (Exception e) {
			if (timeoutTimer != null) {
				timeoutTimer.cancel();
				timeoutTimer = null;
			}
			updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.TECH_ERROR, e.getMessage());
		}
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
		} else {
			throw new NullPointerException("Error : " + this.getClass().getName() + " requires a callback");
		}
	}

	/**
	 * Disconnect from server 
	 * @param callback
	 * @param options
	 */
	public void disconnect() {
		this.connectionStatus = ConnectionStatus.DISCONNECTING;
		try {
			socketio.disconnect();
		} catch (Exception e) {
		}
		
	}
	
	/* helper functions */
	
	/**
	 * make an endpoint adress from endpoints components (ie http://host:port/path)
	 */
	public String toEndpointAdress(String endpointHost, int endpointPort, String endpointPath) {
		String endpointAdress = "";
		endpointAdress += "http://";
		if (endpointHost != null) {
			endpointAdress += endpointHost;
		}
		
		if (endpointPort != 0) {
			endpointAdress += ":" + endpointPort;
		}
		
		//endpointAdress += "/";
		
		if(endpointPath != null) {
			endpointAdress += endpointPath;
		}
		
		return endpointAdress;
	}

	@Override
	public void sendObject(JSONObject object) {
		if( connectionStatus == ConnectionStatus.CONNECTED) {
			socketio.emit("hCommand",object);
		} else {
			System.out.println("Not connected");
		}		
	}
	
	/* Socket io  delegate callback*/
	public void on(String type, IOAcknowledge arg1, Object... arg2) {
		//switch for type
		if (type.equals("hStatus") && arg2 != null && arg2[0].getClass() == JSONObject.class) {
			JSONObject data = (JSONObject)arg2[0];
			try {
				HStatus status = new HStatus(data);
				if (timeoutTimer != null) {
					timeoutTimer.cancel();
					timeoutTimer = null;
				}
				updateStatus(status.getStatus(), status.getErrorCode(), status.getErrorMsg());
			} catch (Exception e) {
				e.printStackTrace();
				
				if (timeoutTimer != null) {
					timeoutTimer.cancel();
					timeoutTimer = null;
				}
				socketio.disconnect();
				updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.TECH_ERROR, e.getMessage());
			}
		} else {
			JSONObject data = (JSONObject)arg2[0];
			try {
				if (timeoutTimer != null) {
					timeoutTimer.cancel();
					timeoutTimer = null;
				}
				callback.onData(type, data);
			} catch (Exception e) {
				if (timeoutTimer != null) {
					timeoutTimer.cancel();
					timeoutTimer = null;
				}
			}
		}
	}
	
	@Override
	public void onConnect() {
		//try to log in once connected
		String publisher = options.getJid().getFullJID();
		String password = options.getPassword();
		String serverHost = options.getServerHost();
		int serverPort = options.getServerPort();
		
		//prepare data to be sent
		JSONObject data = new JSONObject();
		try {
			data.put("publisher", publisher);
			data.put("password", password);
			data.put("serverHost", serverHost);
			data.put("serverPort", serverPort);

			//send the event
			socketio.emit("hConnect", data);
		} catch (Exception e) {
			if(socketio != null) {
				socketio.disconnect();
			}
			if (timeoutTimer != null) {
				timeoutTimer.cancel();
				timeoutTimer = null;
			}
			updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.TECH_ERROR, e.getMessage());
		}
	}

	@Override
	public void onDisconnect() {
		if (timeoutTimer != null) {
			timeoutTimer.cancel();
			timeoutTimer = null;
		}
		
		if (this.connectionStatus != ConnectionStatus.DISCONNECTED) {
//			while(socketio.isConnected()) {
//				socketio.disconnect();
//			}
			updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.NO_ERROR, null);
		}
	}

	@Override
	public void onError(SocketIOException arg0) {
		if (socketio != null && socketio.isConnected()) {
			socketio.disconnect();
		}
		socketio = null;
		String errorMsg = null;
		if (arg0 != null) {
			errorMsg = arg0.getMessage();
		}
		if (timeoutTimer != null) {
			timeoutTimer.cancel();
			timeoutTimer = null;
		}
		updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.TECH_ERROR, errorMsg);
	}

	@Override
	public void onMessage(String arg0, IOAcknowledge arg1) {
		System.out.println("socketio" + arg0);
	}

	@Override
	public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
		System.out.println("socketio" + arg0.toString());
	}

	@Override
	public String toString() {
		return "HTransportSocketio [callback=" + callback + ", options="
				+ options + ", socketio=" + socketio + ", connectionStatus="
				+ connectionStatus + ", timeoutTimer=" + timeoutTimer + "]";
	}
}

/**
 * @endcond
 */