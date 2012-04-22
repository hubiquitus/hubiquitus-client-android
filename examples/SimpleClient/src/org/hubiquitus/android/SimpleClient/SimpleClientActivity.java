package org.hubiquitus.android.SimpleClient;

import java.util.ArrayList;

import org.hubiquitus.hapi.client.HCallback;
import org.hubiquitus.hapi.client.HClient;
import org.hubiquitus.hapi.client.HOptions;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SimpleClientActivity extends Activity  implements HCallback{
    /** Called when the activity is first created. */
	
	private String login;
	private String password;
	private String gateways;
	private String serverHost;
	private String serverPort;
	private String transport;
	
	private Button connectionButton;
	private Button deconnectionButton;
	private Button clearButton;
	
	private EditText loginEditText;
	private EditText passwordEditText;
	private EditText gatewaysEditText;
	private EditText serverportEditText;
	private EditText serverhostEditText;
	
	private TextView outputTextArea;
	private RadioGroup transportRadioGroup;
	
	private HClient client;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        initComponent();
        initListenerBoutonConnection();
        initListenerBoutonDeconnection();
        initListenerBoutonClear();
        
        client = new HClient();
    }
    
    public void initComponent() {
    	
    	connectionButton = (Button) findViewById(R.id.ConnectionButton);
    	deconnectionButton = (Button) findViewById(R.id.DeconnectionButton);
    	clearButton = (Button) findViewById(R.id.ClearButton);
    	
    	loginEditText = (EditText) findViewById(R.id.loginText);
    	passwordEditText = (EditText) findViewById(R.id.passwordText);
    	gatewaysEditText = (EditText) findViewById(R.id.gatewaysText); 
    	serverportEditText = (EditText) findViewById(R.id.serverportText);
    	serverhostEditText = (EditText) findViewById(R.id.serverhostText);
    	
    	transportRadioGroup = (RadioGroup) findViewById(R.id.transportGroupbutton);
    	outputTextArea = (TextView) findViewById(R.id.outputView);
    	
    	loginEditText.setText("nadim@localhost");
    	passwordEditText.setText("12031989");
    	serverhostEditText.setText("192.168.0.14");
    	
    }
   
    
    public void initListenerBoutonConnection() {
    	final SimpleClientActivity parentClass = this;
    	OnClickListener listener = new OnClickListener()
        {
   	       	public void onClick(View v) {
	   	       	login = loginEditText.getText().toString();
	   	        password = passwordEditText.getText().toString();
	   	        gateways = gatewaysEditText.getText().toString();
	   	        serverHost = serverhostEditText.getText().toString();
	   	        serverPort = serverportEditText.getText().toString();
	   	        
	   	        String[] endpointsArray = gateways.split(";");
	   	        ArrayList<String> endpoints = new ArrayList<String>();
	   	        for (int i = 0; i < endpointsArray.length; i++) {
	   	        	endpoints.add(endpointsArray[i]);
	   	        }
	   	        
				RadioButton temp = (RadioButton) findViewById(transportRadioGroup.getCheckedRadioButtonId());
				transport = temp.getText().toString();
				
	   	        //outputTextArea.append("login : " + login + " , password : " + password 
	   	        //					+ " , gateways : " + gateways + " , serverHost : " + serverHost 
	   	        //					+ " , serverPort : " + serverPort + " , transport : " + transport);
				
				HOptions options = new HOptions();
				options.setServerHost(serverHost);
				options.setServerPort(serverPort);
				options.setTransport(transport);
				options.setEndpoints(endpoints);
				
				client.connect(login, password, parentClass, options);
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

	public void hCallback(final String type, final Object data) {
		Log.i("DEBUG", "callback for type " + type + " with data " + data.toString());
		runOnUiThread(new Runnable() {
			
			public void run() {
				outputTextArea.append("Type : " + type + "  data : " + data.toString() + "\n\n");	
			}
		});	
	}
	
	
	
}	