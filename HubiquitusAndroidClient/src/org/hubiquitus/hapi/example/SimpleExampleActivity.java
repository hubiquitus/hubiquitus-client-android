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

package org.hubiquitus.hapi.example;

import org.hubiquitus.hapi.HClient;
import org.hubiquitus.hapi.R;
import org.hubiquitus.hapi.callback.HTransportCallback;
import org.hubiquitus.hapi.codes.Context;
import org.hubiquitus.hapi.hmessage.Data;
import org.hubiquitus.hapi.options.HOptions;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SimpleExampleActivity extends Activity implements HTransportCallback {

	private HOptions hOptions;
	
	private HClient hClient;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
//        //XMPP
//        hOptions = new HOptions();
//        hOptions.setDomain("domain.com");
//        hOptions.setPorts(new int[]{5222});
//        hOptions.setTransport("bosh");
        
        //Socket IO
        hOptions = new HOptions();
        hOptions.setDomain("domain.com");
        // the port for hubiquitus-node
        hOptions.setPorts(new int[]{8080});
        // the port for the server (if different from hubiquitus-node port)
        hOptions.setServerPorts(new int[]{5222});
        hOptions.setTransport("socketio");
        
        hClient = new HClient("username", "password", this, hOptions);
      }

  	@Override
  	public void hCallbackConnection(Context context, Data data) {
  		Log.i(getClass().getCanonicalName(), "Context : " + context);
  		Log.i(getClass().getCanonicalName(), "Data : " + data.toString());
  		
  		
  	}
}
