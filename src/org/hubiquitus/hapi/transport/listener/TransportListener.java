package org.hubiquitus.hapi.transport.listener;

import org.hubiquitus.hapi.message.Request;


public interface TransportListener {

	void onConnect();
	
	void onDisconnect();
	
	void onMessage(Request request);
	
	void onWebSocketPingTimeout();
	
	void OnWebSocketReady();
	
	void onError(Object message);
	
}
