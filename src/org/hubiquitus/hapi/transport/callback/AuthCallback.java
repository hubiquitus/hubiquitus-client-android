package org.hubiquitus.hapi.transport.callback;

import org.json.JSONObject;

public interface AuthCallback {

	void onAuthentication(JSONObject authData, ConnectCallback callback);
	
}
