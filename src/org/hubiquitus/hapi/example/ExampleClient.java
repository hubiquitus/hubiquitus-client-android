package org.hubiquitus.hapi.example;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import android.util.Log;

public class ExampleClient extends WebSocketClient {

	public ExampleClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public ExampleClient(URI serverURI) {
		super(serverURI);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		Log.d(getClass().getCanonicalName(), "opened connection");
	}

	@Override
	public void onMessage(String message) {
		Log.d(getClass().getCanonicalName(), "received: " + message);
	}

	public void onFragment(Framedata fragment) {
		Log.d(getClass().getCanonicalName(), "received fragment: "
				+ new String(fragment.getPayloadData().array()));
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		Log.d(getClass().getCanonicalName(), "Connection closed by "
				+ (remote ? "remote peer" : "us"));
	}

	@Override
	public void onError(Exception ex) {
		Log.e(getClass().getCanonicalName(), ex.getMessage());
	}

}
