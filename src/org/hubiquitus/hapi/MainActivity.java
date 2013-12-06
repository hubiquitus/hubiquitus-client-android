package org.hubiquitus.hapi;

import java.util.Timer;
import java.util.TimerTask;

import org.hubiquitus.hapi.listener.HubiquitusListener;
import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.message.Message;
import org.hubiquitus.hapi.message.Request;
import org.hubiquitus.hapi.transport.exception.TransportException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

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
	
	private void setStatusText(final String status) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView textView = (TextView) findViewById(R.id.status);
				textView.setText(status);
			}
		});
	}
	
	private void setResponseText(final String response) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView textView = (TextView) findViewById(R.id.response);
				textView.setText(response);
			}
		});
	}
	
	private void setRequestText(final String request) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView textView = (TextView) findViewById(R.id.request);
				textView.setText(request);
			}
		});
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

		hubiquitus = new Hubiquitus(this, new Handler());

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
		}, 5000);
	}

	@Override
	public void onConnect() {
		if (BuildConfig.DEBUG) {
			Log.d(getClass().getCanonicalName(), "Connected");
		}
		setStatusText("Connected");
		try {
			hubiquitus.send("ping", "PING", new ResponseListener() {
				@Override
				public void onResponse(Object err, Message message) {
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
									.append(message.getContent().toString())
									.append(" from ").append(message.getFrom());
							Log.d(getClass().getCanonicalName(), sb.toString());
						}
					}
					if (message != null) {
						setResponseText(message.toString());
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
		setStatusText("Disconnected");
	}

	@Override
	public void onMessage(Request request) {
		if (BuildConfig.DEBUG) {
			StringBuilder sb = new StringBuilder();
			sb.append("onMessage : ").append(request.getContent())
					.append(" from ").append(request.getFrom());
			Log.d(getClass().getCanonicalName(), sb.toString());
			
			setRequestText(request.toString());
			
			request.getReplyCallback().reply(null, "PING PONG");
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
