package org.hubiquitus.hapi;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Hubiquitus connection test activity
 * 
 * @author t.bourgeois
 *
 */
public class MainActivity extends Activity implements HubiquitusListener, OnClickListener {

	/**
	 * Hubiquitus object
	 */
	private Hubiquitus hubiquitus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
		initConnection();
	}
	
	@Override
	protected void onDestroy() {
		if (hubiquitus != null) {
			try {
				hubiquitus.disconnect();
			} catch (TransportException e) {
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}
	
	/**
	 * Initializes the view
	 */
	private void initView() {
		Button btnDisconnect = (Button) findViewById(R.id.btn_disconnect);
		Button btnSend = (Button) findViewById(R.id.btn_send);
		
		btnDisconnect.setOnClickListener(this);
		btnSend.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_disconnect:
			try {
				if (hubiquitus != null) {
					hubiquitus.disconnect();
				}
			} catch (TransportException e) {
				e.printStackTrace();
			}
			break;
		case R.id.btn_send:
			try {
				hubiquitus.send("ping", "PING", new ResponseListener() {
					@Override
					public void onResponse(Object err, Message message) {
						onResponseHandler(err, message);
					}
				});
			} catch (TransportException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
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
		
		hubiquitus.connect("http://192.168.2.98:8888/hubiquitus",
				authData);
	}

	@Override
	public void onConnect() {
		Log.d("DEBUG", "onConnect");
		setStatusText("Connected");
	}

	@Override
	public void onDisconnect() {
		Log.d("DEBUG", "onDisconnect");
		setStatusText("Disconnected");
	}

	@Override
	public void onMessage(Request request) {
		if (BuildConfig.DEBUG) {
			StringBuilder sb = new StringBuilder();
			sb.append("onMessage : ").append(request.getContent())
					.append(" from ").append(request.getFrom());
			Log.d(getClass().getCanonicalName(), sb.toString());
		}
		setRequestText(request.toString());
		request.getReplyCallback().reply(null, "PING PONG");
	}

	@Override
	public void onError(String message) {
		if (BuildConfig.DEBUG) {
			StringBuilder sb = new StringBuilder();
			sb.append("onError : ").append(message);
			Log.d(getClass().getCanonicalName(), sb.toString());
		}
	}
	
	private void onResponseHandler(Object err, Message message) {
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
	

}
