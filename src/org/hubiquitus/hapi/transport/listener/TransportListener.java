package org.hubiquitus.hapi.transport.listener;

import org.hubiquitus.hapi.message.Request;


public interface TransportListener {

	void onConnect();
	
	void onDisconnect();
	
	void onMessage(Request request);
	
	void onError(String message);
	
}
