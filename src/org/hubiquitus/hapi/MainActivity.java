package org.hubiquitus.hapi;

import java.util.Timer;
import java.util.TimerTask;

import org.hubiquitus.hapi.listener.HubiquitusListener;
import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.transport.callback.ReplyCallback;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

/**
 * Hubiquitus connection test activity
 * 
 * @author t.bourgeois
 *
 */
public class MainActivity extends Activity implements HubiquitusListener {

	/**
	 * Hubiquitus object
	 */
	private Hubiquitus hubiquitus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initConnection();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	/**
	 * Initializes the connection
	 */
	private void initConnection() {

		hubiquitus = new Hubiquitus(this);

		JSONObject authData = new JSONObject();
		try {
			authData.put("username", "max");
		} catch (JSONException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		}
		
		hubiquitus.connect("http://192.168.2.105:8888/hubiquitus",
				authData);
		
		// Disconnect
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
		}, 30000);
	}

	@Override
	public void onConnect() {
		if (BuildConfig.DEBUG) {
			Log.d(getClass().getCanonicalName(), "Connected");
		}
		try {
			hubiquitus.send("ping", "PING", 5000, new ResponseListener() {
				@Override
				public void onResponse(Object err, String from, Object content) {
					if (err != null) {
						if (BuildConfig.DEBUG) {
							StringBuilder sb = new StringBuilder();
							sb.append("onResponse error : ").append(err);
							Log.d(getClass().getCanonicalName(), sb.toString());
						}
					} else {
						if (BuildConfig.DEBUG) {
							StringBuilder sb = new StringBuilder();
							sb.append("onResponse : ")
									.append(content.toString())
									.append(" from ").append(from);
							Log.d(getClass().getCanonicalName(), sb.toString());
						}
					}
				}
			});
		} catch (TransportException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		}
	}

	@Override
	public void onDisconnect() {
		if (BuildConfig.DEBUG) {
			Log.d(getClass().getCanonicalName(), "Disconnected");
		}
	}

	@Override
	public void onMessage(String from, Object content,
			ReplyCallback replyCallback) {
		if (BuildConfig.DEBUG) {
			StringBuilder sb = new StringBuilder();
			sb.append("onMessage : ").append(content.toString())
					.append(" from ").append(from);
			Log.d(getClass().getCanonicalName(), sb.toString());
		}
	}

	@Override
	public void onError(String message) {
		if (BuildConfig.DEBUG) {
			StringBuilder sb = new StringBuilder();
			sb.append("onError : ").append(message);
			Log.d(getClass().getCanonicalName(), sb.toString());
		}
	}

}
