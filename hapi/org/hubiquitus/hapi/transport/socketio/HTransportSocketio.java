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
import org.hubiquitus.hapi.transport.CheckConnectivity;
import org.hubiquitus.hapi.transport.HTransport;
import org.hubiquitus.hapi.transport.HTransportDelegate;
import org.hubiquitus.hapi.transport.HTransportOptions;
import org.hubiquitus.hapi.util.MyApplication;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @cond internal
 * @version 0.5
 * HTransportSocketIO is the socketio transport layer of the hubiquitus hAPI client
 */

public class HTransportSocketio extends CheckConnectivity implements HTransport, IOCallback {

	final Logger logger = LoggerFactory.getLogger(HTransportSocketio.class);
	private HTransportDelegate callback = null;
	private HTransportOptions options = null;
	private SocketIO socketio = null;
	private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
	private Timer timeoutTimer = null;
	private Timer autoReconnectTimer = new Timer();
	private ReconnectTask autoReconnectTask = null;
	
	private class ReconnectTask extends TimerTask{

		@Override
		public void run() {
			try {
				connect(callback, options);
			} catch (Exception e) {
				updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.TECH_ERROR, e.getMessage());
			}
			
		}
		
	}
	
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
		}, 30000);
		
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
	 */
	public void disconnect() {
		this.connectionStatus = ConnectionStatus.DISCONNECTING;
		if(autoReconnectTask != null){
			autoReconnectTask.cancel();
			autoReconnectTask = null;
		}
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

	public void sendObject(JSONObject object) {
		if( connectionStatus == ConnectionStatus.CONNECTED) {
			socketio.emit("hMessage",object);
		} else {
			logger.warn("message: Not connected");
		}		
	}
	
	/* Socket io  delegate callback*/
	public void on(String type, IOAcknowledge arg1, Object... arg2) {
		//switch for type
		if (type.equalsIgnoreCase("hStatus") && arg2 != null && arg2[0].getClass() == JSONObject.class) {
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
		} else if (type.equalsIgnoreCase("hMessage") && arg2 != null && arg2[0].getClass() == JSONObject.class){
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
	
	public void onConnect() {
		//try to log in once connected
		String publisher = options.getJid().getFullJID();
		String password = options.getPassword();
		
		//prepare data to be sent
		JSONObject data = new JSONObject();
		try {
			data.put("publisher", publisher);
			data.put("password", password);
            data.put("sent", DateTime.now());
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
		if(hasConnectivity)
			this.reconnect();
	}


	public void onMessage(String arg0, IOAcknowledge arg1) {
		logger.info("socketio" + arg0);
	}


	public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
		logger.info("socketio" + arg0.toString());
	}

	public String toString() {
		return "HTransportSocketio [callback=" + callback + ", options="
				+ options + ", socketio=" + socketio + ", connectionStatus="
				+ connectionStatus + ", timeoutTimer=" + timeoutTimer + "]";
	}
	
	/**
	 * Called in onError. try to reconnect in 5s. if socketio can't connect, it will be called every 5s. 
	 */
	public void reconnect(){
		updateStatus(connectionStatus, ConnectionError.NOT_CONNECTED, "Lost connection, try to reconnect in 5 s");
		if(autoReconnectTask != null){
			autoReconnectTask.cancel();
		}
		autoReconnectTask = new ReconnectTask();
		autoReconnectTimer.schedule(autoReconnectTask, 5000);
	}
	
//	//check the connectivity of the device
//	public static boolean checkConn(Context ctx){
//		
//		ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
//		if(conMgr == null){
//			return false;
//		}else{
//			NetworkInfo i = conMgr.getActiveNetworkInfo();
//			if(i==null){
//				return false;
//			}
//			if(!i.isConnected()){
//				return false;
//			}
//			if(!i.isAvailable()){
//				return false;
//			}
//			return true;
//		}
//	}
		
}

/**
 * @endcond
 */