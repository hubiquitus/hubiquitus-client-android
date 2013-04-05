/*
 * Copyright (c) Novedia Group 2012.
 *
 *    This file is part of Hubiquitus
 *
 *    Permission is hereby granted, free of charge, to any person obtaining a copy
 *    of this software and associated documentation files (the "Software"), to deal
 *    in the Software without restriction, including without limitation the rights
 *    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *    of the Software, and to permit persons to whom the Software is furnished to do so,
 *    subject to the following conditions:
 *
 *    The above copyright notice and this permission notice shall be included in all copies
 *    or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *    INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *    PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 *    FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *    You should have received a copy of the MIT License along with Hubiquitus.
 *    If not, see <http://opensource.org/licenses/mit-license.php>.
 */

package org.hubiquitus.hapi.transport;

import org.hubiquitus.hapi.hStructures.ConnectionError;
import org.hubiquitus.hapi.hStructures.ConnectionStatus;
import org.hubiquitus.hapi.util.ErrorMsg;
import org.hubiquitus.hapi.util.MyApplication;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public class HTransportManager {
	final Logger logger = LoggerFactory.getLogger(HTransportManager.class);
	private HTransport transport;
	private HTransportDelegate callback;
	private TransportManagerDelegate innerCallback = new TransportManagerDelegate();
	private HTransportOptions options;
	private ConnectionStatus connStatus = ConnectionStatus.DISCONNECTED;
	private boolean hasConnectivity = true;
	private boolean shouldConnect = false;
	
	class TransportManagerDelegate implements HTransportDelegate{

		public void onStatus(ConnectionStatus status, ConnectionError error,
				String errorMsg) {
			connStatus = status;
			if(error == ConnectionError.TECH_ERROR){//when tech error issues, reconnect.
				if(callback != null)
					callback.onStatus(status, error, errorMsg + ". " + ErrorMsg.reconnIn5s);
					try {
						Thread.sleep(5000);
						tryToConnectDisconnect();
					} catch (InterruptedException e) {
						logger.error("Message : " + e);
					}
			}else{
				if(callback != null)
					callback.onStatus(status, error, errorMsg);
			}
		}

		public void onData(String type, JSONObject jsonData) {
			callback.onData(type, jsonData);
		}
	}
	
	public HTransportManager(){
		registerReceivers();
	}
	
	public void connect(){
		shouldConnect = true;
		if(connStatus != ConnectionStatus.CONNECTED && connStatus != ConnectionStatus.CONNECTING){
			tryToConnectDisconnect();
		}else if(connStatus == ConnectionStatus.CONNECTED){
			callback.onStatus(connStatus, ConnectionError.ALREADY_CONNECTED, ErrorMsg.alreadyConn);
		}else if(connStatus == ConnectionStatus.CONNECTING){
			callback.onStatus(connStatus, ConnectionError.CONN_PROGRESS, ErrorMsg.connWhileConnecting);
		}
	}
	
	public void disconnect(){
		shouldConnect = false;
		if(connStatus != ConnectionStatus.DISCONNECTING && connStatus != ConnectionStatus.DISCONNECTED){
			tryToConnectDisconnect();
		}else if(connStatus == ConnectionStatus.DISCONNECTED){
			callback.onStatus(connStatus, ConnectionError.NOT_CONNECTED, ErrorMsg.alreadyDisconn);
		}else if(connStatus == ConnectionStatus.DISCONNECTING){
			callback.onStatus(connStatus, ConnectionError.NOT_CONNECTED, ErrorMsg.disconnWhileDisconnecting);
		}
	}
	
	public void sendObject(JSONObject object){
		transport.sendObject(object);
	}
	

	private void tryToConnectDisconnect(){
		logger.debug(">> tryToConnectDisconnect : shouldConnect = " + shouldConnect + " status = " + connStatus.toString());
		
		if (hasConnectivity){
			if(shouldConnect && connStatus != ConnectionStatus.CONNECTED && connStatus != ConnectionStatus.CONNECTING){
				logger.debug(">> tryToConnectDisconnect : transport.connect ...");
				connStatus = ConnectionStatus.CONNECTING;
				transport.connect(innerCallback, options);
			}else if(!shouldConnect && connStatus != ConnectionStatus.DISCONNECTED && connStatus != ConnectionStatus.DISCONNECTING){
				logger.debug(">> tryToConnectDisconnect : transport.disconnect ...");
				connStatus = ConnectionStatus.DISCONNECTING;
				transport.disconnect();
			}else if(shouldConnect && connStatus == ConnectionStatus.CONNECTED){
				logger.debug(">> tryToConnectDisconnect : already connected, I do nothing...");
			}else if(!shouldConnect && connStatus == ConnectionStatus.DISCONNECTED){
				logger.debug(">> tryToConnectDisconnect : already disconnected, I do nothing...");
			}
		}else{
			innerCallback.onStatus(ConnectionStatus.DISCONNECTED, ConnectionError.NOT_CONNECTED, ErrorMsg.noConnectivity);
		}
	}
	
	public HTransportDelegate getCallback() {
		return callback;
	}

	public void setCallback(HTransportDelegate callback) {
		this.callback = callback;
	}
	
	public HTransport getTransport() {
		return transport;
	}

	public void setTransport(HTransport transport) {
		this.transport = transport;
	}

	public HTransportOptions getOptions() {
		return options;
	}

	public void setOptions(HTransportOptions options) {
		this.options = options;
	}



	//listen to the connectivity of the device
	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
               
            if(noConnectivity){
            	hasConnectivity = false;
            	innerCallback.onStatus(ConnectionStatus.DISCONNECTED, ConnectionError.NOT_CONNECTED, ErrorMsg.noConnectivity);
            }else{
            	hasConnectivity = true;
            	tryToConnectDisconnect();
            }
        }
    };
    
    /*
     * method to be invoked to register the receiver
     */
    private void registerReceivers() {    
        MyApplication.getAppContext().registerReceiver(mConnReceiver, 
            new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
	
}
