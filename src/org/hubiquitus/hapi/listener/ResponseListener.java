package org.hubiquitus.hapi.listener;

import org.hubiquitus.hapi.message.Message;


public interface ResponseListener {

	void onResponse(Object err, Message message);
	
}
