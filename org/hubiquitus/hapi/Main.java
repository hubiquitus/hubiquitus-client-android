package org.hubiquitus.hapi;

import java.util.Timer;
import java.util.TimerTask;

import org.hubiquitus.hapi.listener.HubiquitusListener;
import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.transport.callback.ReplyCallback;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.json.JSONObject;

public class Main {
	
	private static Hubiquitus hubiquitus;

	public static void main(String[] args) {
		
		hubiquitus = new Hubiquitus(new HubiquitusListener() {
			
			@Override
			public void onConnect() {
				System.out.println("Hubiquitus connected");
				try {
					hubiquitus.send("ping", "PING", 5000, new ResponseListener() {
						@Override
						public void onResponse(Object err, String from, Object content) {
							if (err != null) {
								System.out.println("Hubiquitus onResponse error : " + err);
							}
							else {
								System.out.println("Hubiquitus onResponse : " + content.toString() + " from : " + from);
							}
						}
					});
				} catch (TransportException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onDisconnect() {
				System.out.println("Hubiquitus disconnected");
			}

			@Override
			public void onError(String message) {
				System.out.println("Hubiquitus onError : " + message);
			}

			@Override
			public void onMessage(String from, Object content,
					ReplyCallback replyCallback) {
				System.out.println("Hubiquitus onMessage : " + content.toString() + " from " + from);
			}

		});
				
		JSONObject authData = new JSONObject();
		authData.put("username", "max");
		hubiquitus.connect("ws://192.168.2.105:8888/hubiquitus/websocket", authData);
		
		
		Timer disconnectTimer = new Timer();
		disconnectTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					hubiquitus.disconnect();
				} catch (TransportException e) {
					e.printStackTrace();
				}
			}
		}, 10000);
	}

}
