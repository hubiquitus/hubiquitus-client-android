package org.hubiquitus.hapi.util;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

	 private static Context context;

	    public void onCreate(){
	        super.onCreate();
	        MyApplication.context = getApplicationContext();
	    }
	    
		/**
		 * Help to get application context.
		 * @return
		 */
	    public static Context getAppContext() {
	        return MyApplication.context;
	    }

}
