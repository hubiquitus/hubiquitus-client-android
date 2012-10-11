/*
 * Copyright (c) Novedia Group 2012.
 *
 *     This file is part of Hubiquitus.
 *
 *     Hubiquitus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Hubiquitus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Hubiquitus.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.hubiquitus.android.SimpleClient;

import java.util.Timer;
import java.util.TimerTask;

import org.hubiquitus.hapi.client.HClient;
import org.hubiquitus.hapi.client.HMessageDelegate;
import org.hubiquitus.hapi.client.HStatusDelegate;
import org.hubiquitus.hapi.exceptions.MissingAttrException;
import org.hubiquitus.hapi.hStructures.HArrayOfValue;
import org.hubiquitus.hapi.hStructures.HCondition;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.hubiquitus.hapi.hStructures.HMessageOptions;
import org.hubiquitus.hapi.hStructures.HOptions;
import org.hubiquitus.hapi.hStructures.HStatus;
import org.hubiquitus.hapi.hStructures.OperandNames;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class SimpleClientActivity extends Activity  implements HStatusDelegate, HMessageDelegate{
	/** Called when the activity is first created. */

	final Logger logger = LoggerFactory.getLogger(SimpleClientActivity.class );
	private String login;
	private String password;
	private String gateways;
	private String transport = "socketio";

	private Button connectionButton;
	private Button deconnectionButton;
	private Button clearButton;
	private Button subscribeButton;
	private Button unsubscribeButton;
	private Button sendButton;
	private Button getLastMessageButton;
	private Button getSubcriptionButton;
	private Button getThreadButton;
	private Button getThreadsButton;
	private Button pubConvState;
	private Button setFilterButton;
	private Button exitButton;
	private Button getRelevantMsgButton;

	private EditText loginEditText;
	private EditText passwordEditText;
	private EditText gatewaysEditText;
	private EditText channelIDText;
	private EditText timeOutText;
	private EditText nbLastMessageText;
	private EditText MessageEditText;
	private EditText relevantOffsetEditText;
	private EditText convidEditText;
	private EditText convstateEditText;

	private TextView outputTextArea;
	private RadioGroup transportRadioGroup;
	private RadioGroup messageRadioGroup;

	private ScrollView outputScroller;
	private TextView connectionStatusLabel;

	private HClient client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		initComponent();
		initListenerBoutonConnection();
		initListenerBoutonDeconnection();
		initListenerBoutonClear();
		initListenerSubscribeButton();
		initListenerUnsubscribeButton();
		initListenerSendButton();
		initListenerGetLastMessageButton();
		initListenerGetSubscriptionButton();
		initListenerGetThreadButton();
		initListenerGetThreadsButton();
		initListenerPubConvStateButton();
		initListenerSetFilterButton();
		initListenerGetRelevantMsgButton();
		initListenerExitButton();

		client = new HClient();
		client.onStatus(this);
		client.onMessage(this);
	}

	public void initComponent() {

		connectionButton = (Button) findViewById(R.id.ConnectionButton);
		deconnectionButton = (Button) findViewById(R.id.DeconnectionButton);
		clearButton = (Button) findViewById(R.id.ClearButton);
		subscribeButton = (Button) findViewById(R.id.SubscribeButton);
		unsubscribeButton = (Button) findViewById(R.id.UnsubscribeButton);
		sendButton = (Button) findViewById(R.id.SendButton);
		getLastMessageButton = (Button) findViewById(R.id.GetLastMessageButton);
		getSubcriptionButton = (Button) findViewById(R.id.GetSubcriptionButton);
		getThreadButton = (Button) findViewById(R.id.GetThreadButton);
		getThreadsButton = (Button) findViewById(R.id.GetThreadsButton);
		pubConvState = (Button) findViewById(R.id.PubConvStateButton);
		setFilterButton = (Button) findViewById(R.id.SetFilterButton);
		getRelevantMsgButton = (Button) findViewById(R.id.GetRelevantMsgButton);
		exitButton = (Button) findViewById(R.id.ExitButton);


		loginEditText = (EditText) findViewById(R.id.loginText);
		passwordEditText = (EditText) findViewById(R.id.passwordText);
		gatewaysEditText = (EditText) findViewById(R.id.gatewaysText); 
		channelIDText = (EditText) findViewById(R.id.ChannelIDText);
		timeOutText = (EditText) findViewById(R.id.timeOutText);
		nbLastMessageText = (EditText) findViewById(R.id.nbLastMessageText);
		relevantOffsetEditText = (EditText) findViewById(R.id.relevantOffsetText);
		MessageEditText = (EditText) findViewById(R.id.messageText);
		convidEditText = (EditText) findViewById(R.id.ConvidText);
		convstateEditText = (EditText) findViewById(R.id.ConvStateText);

		transportRadioGroup = (RadioGroup) findViewById(R.id.transportGroupbutton);
		messageRadioGroup = (RadioGroup) findViewById(R.id.MessageGroupbutton);
		outputTextArea = (TextView) findViewById(R.id.outputView);
		outputScroller = (ScrollView)findViewById(R.id.scrollview);
		connectionStatusLabel = (TextView)findViewById(R.id.connectionStatusLabel);

		loginEditText.setText("");
		passwordEditText.setText("");
		channelIDText.setText("");
		gatewaysEditText.setText("");
		MessageEditText.setText("");		
	}


	public void initListenerBoutonConnection() {
		final SimpleClientActivity parentClass = this;
		OnClickListener listener = new OnClickListener()
		{
			public void onClick(View v) {
				login = loginEditText.getText().toString();
				password = passwordEditText.getText().toString();
				gateways = gatewaysEditText.getText().toString();

				
				JSONArray endpoints = new JSONArray();
				endpoints.put(gateways);
				RadioButton temp = (RadioButton) findViewById(transportRadioGroup.getCheckedRadioButtonId());
				transport = temp.getText().toString();

			
				HOptions options = new HOptions();
				options.setTransport("socketio");
				options.setEndpoints(endpoints);
				options.setTimeout(3000);
				options.setTransport(transport);
				options.setEndpoints(endpoints);
				client.connect(login, password, options);
			}
		};
		connectionButton.setOnClickListener(listener);

	}

	public void initListenerBoutonDeconnection() {

		OnClickListener listener = new OnClickListener()
		{
			public void onClick(View v) {
				client.disconnect();
			}
		};
		deconnectionButton.setOnClickListener(listener);

	}


	public void initListenerBoutonClear() {

		OnClickListener listener = new OnClickListener()
		{
			public void onClick(View v) {

				TextView text = (TextView) findViewById(R.id.outputView);
				text.setText("");

			}

		};
		clearButton.setOnClickListener(listener);

	}
	
	public void initListenerSubscribeButton() {
		final SimpleClientActivity outerClass = this;
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				try {
					client.subscribe(channelIDText.getText().toString(), outerClass);
				} catch (MissingAttrException e) {
					logger.error("message: ", e);
				}
			}
		};
		subscribeButton.setOnClickListener(listener);
	}
	
	public void initListenerUnsubscribeButton() {
		final SimpleClientActivity outerClass = this;
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				try {
					client.unsubscribe(channelIDText.getText().toString(), outerClass);
				} catch (MissingAttrException e) {
					logger.error("message: ",e);
				}
			}
		};
		unsubscribeButton.setOnClickListener(listener);
	}
	
	public void initListenerSendButton() {
		final SimpleClientActivity outerClass = this;
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				JSONObject payload = new JSONObject();
				HMessage message = null;
				HMessageOptions option = new HMessageOptions();
				try {
					option.setRelevanceOffset(Integer.parseInt(relevantOffsetEditText.getText().toString()));
				} catch (Exception e) {
					logger.info("NO RELEVANT OFFSET!!");
				}
				try {
					payload.put("text",MessageEditText.getText().toString());
					message = client.buildMessage(channelIDText.getText().toString(), "obj", payload, option);
				} catch (Exception e) {
					logger.error("message: ",e);
				}
				RadioButton temp = (RadioButton) findViewById(messageRadioGroup.getCheckedRadioButtonId());
				try{
					if(temp.getText().toString().equalsIgnoreCase("Persistent")) {
						message.setPersistent(true);
					} else {
						message.setPersistent(false);
					}
				}catch(Exception e){
					logger.error("message: ",e);
				}
				int timeout = 0;
				try {
					timeout = Integer.parseInt(timeOutText.getText().toString());
				} catch (Exception e) {
					timeout = 0;
				}
				if(timeout > 0){
					message.setTimeout(timeout);
				}
				message.setPayload(payload);
				client.send(message, outerClass);
			}
		};
		sendButton.setOnClickListener(listener);
	}
	
	public void initListenerGetLastMessageButton() {
		final SimpleClientActivity outerClass = this;
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				String actor = channelIDText.getText().toString();
				int nbLastMessage = 0;
				try{
					try {
						nbLastMessage = Integer.parseInt(nbLastMessageText.getText().toString());
					} catch (Exception e) {
						nbLastMessage = 0;
					}
					if(nbLastMessage > 0) {
						client.getLastMessages(actor, nbLastMessage, outerClass);
					} else {
						client.getLastMessages(actor, outerClass);
					}
				} catch (Exception e) {
					logger.error("message: ",e);
				}
			}
		};
		getLastMessageButton.setOnClickListener(listener);
	}
	
	public void initListenerGetSubscriptionButton() {
		final SimpleClientActivity outerClass = this;
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				try {
					client.getSubscriptions(outerClass);
				} catch (MissingAttrException e) {
					logger.error("message : ", e);
				}
			}
		};
		getSubcriptionButton.setOnClickListener(listener);
	}
	
	public void initListenerGetThreadButton() {
		final SimpleClientActivity outerClass = this;
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				String actor = channelIDText.getText().toString();
				String convid = convidEditText.getText().toString();
				try{
					client.getThread(actor, convid, outerClass);
				} catch (Exception e) {
					logger.error("message: ",e);
				}
			}
		};
		getThreadButton.setOnClickListener(listener);
	}
	
	public void initListenerGetThreadsButton() {
		final SimpleClientActivity outerClass = this;
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				String actor = channelIDText.getText().toString();
				String status = convstateEditText.getText().toString();
				try{
					client.getThreads(actor, status, outerClass);
				} catch (Exception e) {
					logger.error("message: ", e);
				}
			}
		};
		getThreadsButton.setOnClickListener(listener);
	}
	
	public void initListenerPubConvStateButton() {
		final SimpleClientActivity outerClass = this;
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				String actor = channelIDText.getText().toString();
				String convid = convidEditText.getText().toString();
				String status = convstateEditText.getText().toString();
				HMessageOptions msgOptions = new HMessageOptions();
				
				RadioButton persistentRadioBtn = (RadioButton) findViewById(messageRadioGroup.getCheckedRadioButtonId());
				if(persistentRadioBtn.getText().toString().equalsIgnoreCase("Persistent")) {
					msgOptions.setPersistent(true);
				} else {
					msgOptions.setPersistent(false);
				}
				
				try{
					HMessage pubMsg = client.buildConvState(actor, convid, status, msgOptions);
					client.send(pubMsg, outerClass);
				} catch (Exception e) {
					logger.error("message: ", e);
				}
			}
		};
		pubConvState.setOnClickListener(listener);
	}
	
	public void initListenerSetFilterButton() {
		final SimpleClientActivity outerClass = this;
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				 HCondition filter = new HCondition();
				 HArrayOfValue values = new HArrayOfValue();
				 values.setName("publisher");
				 JSONArray jsonArray = new JSONArray();
				 jsonArray.put("u1@localhost");
				 values.setValues(jsonArray);
				 filter.setValueArray(OperandNames.IN, values);
				 try {
					client.setFilter(filter, outerClass);
				} catch (MissingAttrException e) {
					logger.error("message: ",e);
				}
			}
		};
		setFilterButton.setOnClickListener(listener);
	}
	
	public void initListenerGetRelevantMsgButton() {
		final SimpleClientActivity outerClass = this;
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				String actor = channelIDText.getText().toString();
			
				try{
					client.getRelevantMessages(actor, outerClass);
				} catch (Exception e) {
					logger.error("message: ", e);
				}
			}
		};
		getRelevantMsgButton.setOnClickListener(listener);
	}
	
	public void initListenerExitButton(){
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		};
		exitButton.setOnClickListener(listener);
	}
	
	@Override
	public void onStatus(final HStatus status) {
		runOnUiThread(new Runnable() {

			public void run() {
				connectionStatusLabel.setText(client.status().toString());
				outputTextArea.append("Status : " + status.getStatus() + " error : " + status.getErrorCode() + "  errorMsg : " + status.getErrorMsg() + "\n\n");
				Timer scrollTimer = new Timer();
				TimerTask scrollTask = new TimerTask() {

					@Override
					public void run() {
						runOnUiThread(new Runnable() {

							public void run() {
								outputScroller.smoothScrollTo(0, outputTextArea.getBottom());

							}
						});

					}
				};

				scrollTimer.schedule(scrollTask, 10);
			}
		});	
	}

	@Override
	public void onMessage(final HMessage message) {
		runOnUiThread(new Runnable() {
			public void run() {
				outputTextArea.append("HMessage : " + message + "\n\n");
				Timer scrollTimer = new Timer();
				TimerTask scrollTask = new TimerTask() {

					@Override
					public void run() {
						runOnUiThread(new Runnable() {

							public void run() {
								outputScroller.smoothScrollTo(0, outputTextArea.getBottom());

							}
						});

					}
				};

				scrollTimer.schedule(scrollTask, 10);
			}
		});	

	}
}	