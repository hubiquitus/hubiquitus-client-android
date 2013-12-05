package org.hubiquitus.hapi.listener;

import org.hubiquitus.hapi.transport.callback.ReplyCallback;

public interface HubiquitusListener {

	void onConnect();
	
	void onDisconnect();
	
	void onMessage(String from, Object content, ReplyCallback replyCallback);

	void onError(String message);
	
}
