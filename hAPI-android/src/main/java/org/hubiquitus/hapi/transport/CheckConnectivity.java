package org.hubiquitus.hapi.transport;

import org.hubiquitus.hapi.util.MyApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public abstract class CheckConnectivity{
	protected boolean hasConnectivity = false;
	
	final Logger logger = LoggerFactory.getLogger(CheckConnectivity.class);
	
	public CheckConnectivity(){
		registerReceivers();
	}
	
	protected abstract void reconnect();
	

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
               
            if(noConnectivity){
            	hasConnectivity = false;
            }else{
            	hasConnectivity = true;
            }
        }
    };
    
    /*
     * method to be invoked to register the receiver
     */
    private void registerReceivers() {    
        MyApplication.getAppContext().registerReceiver(mConnReceiver, 
            new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
}
