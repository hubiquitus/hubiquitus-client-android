package org.hubiquitus.hapi.transport.listener;

import org.hubiquitus.hapi.transport.callback.ReplyCallback;


public interface TransportListener {

	void onConnect();
	
	void onDisconnect();
	
	void onMessage(String from, Object content, ReplyCallback replyCallback);
	
	void onError(String message);
	
}
