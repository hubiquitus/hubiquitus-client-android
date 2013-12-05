package org.hubiquitus.hapi.listener;


public interface ResponseListener {

	void onResponse(Object err, String from, Object content);
	
}
