package org.hubiquitus.hapi.listener;

import org.hubiquitus.hapi.message.Message;
import org.json.JSONObject;


public interface ResponseListener {

	void onResponse(JSONObject err, Message message);
	
}
