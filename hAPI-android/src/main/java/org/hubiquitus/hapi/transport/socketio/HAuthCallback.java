package org.hubiquitus.hapi.transport.socketio;

public interface HAuthCallback {
	public void authCb(String username, ConnectedCallback connectedCB);
}