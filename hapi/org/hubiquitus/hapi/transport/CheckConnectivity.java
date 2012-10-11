package org.hubiquitus.hapi.transport;

import org.hubiquitus.hapi.util.MyApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public abstract class CheckConnectivity{
	protected boolean hasConnectivity = false;
	
	final Logger logger = LoggerFactory.getLogger(CheckConnectivity.class);
	
	public CheckConnectivity(){
		logger.info(">>>>>>> CheckConnectivity");
		registerReceivers();
	}
	
	protected abstract void reconnect();
	

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	logger.info(">>>>>>> BroadcastReceiver onReceive");
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
            
            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
                
            if(noConnectivity){
            	hasConnectivity = false;
            }else{
            	hasConnectivity = true;
            }
            logger.info("<<<<<<<< BroadcastReceiver onReceive with hasConnectivity = " + hasConnectivity);
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
