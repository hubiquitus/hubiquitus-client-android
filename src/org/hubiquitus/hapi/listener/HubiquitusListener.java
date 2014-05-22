package org.hubiquitus.hapi.listener;

import org.hubiquitus.hapi.message.Request;

public interface HubiquitusListener {

	void onConnect();
	
	void onDisconnect();
	
	void onMessage(Request request);

	void onError(Object message);
	
}
