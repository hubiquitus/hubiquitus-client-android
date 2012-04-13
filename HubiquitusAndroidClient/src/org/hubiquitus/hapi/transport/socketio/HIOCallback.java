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

package org.hubiquitus.hapi.transport.socketio;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

import org.hubiquitus.hapi.codes.Context;
import org.hubiquitus.hapi.codes.Status;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HIOCallback implements IOCallback {
	
	private HTransportSocketIO callback;
	
	/**
	 * the constructor class
	 */
	public HIOCallback(HTransportSocketIO callback){
		this.callback = callback;
	}
	
	@Override
	public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
		Log.i(getClass().getCanonicalName(), "A JSON message arrived");
		Log.i(getClass().getCanonicalName(), arg0.toString());
	}
	
	@Override
	public void onMessage(String arg0, IOAcknowledge arg1) {
		Log.i(getClass().getCanonicalName(), "A String message arrived");
		Log.i(getClass().getCanonicalName(), arg0);
	}
	
	@Override
	public void onError(SocketIOException arg0) {
		Log.i(getClass().getCanonicalName(), "An error ocurred : ");
		Log.i(getClass().getCanonicalName(), arg0.getMessage());
		callback.hCallback(Context.ERROR, Status.DISCONNECTED, null);
	}
	
	@Override
	public void onDisconnect() {
		Log.i(getClass().getCanonicalName(), "Connection dismissed");
		callback.hCallback(Context.LINK, Status.DISCONNECTED, null);
	}
	
	@Override
	public void onConnect() {
		Log.i(getClass().getCanonicalName(), "Connection established");
		
		// Only the connection with socket io is on
		callback.hCallback(null, Status.CONNECTED, null);
	}
	
	// pour les évènements, arg0 = nom de l'evenement et arg1 = JSON
	@Override
	public void on(String arg0, IOAcknowledge arg1, Object... arg2) {
		Log.i(getClass().getCanonicalName(), "on something...");
		
		// get the incoming message
//		Log.i(getClass().getCanonicalName(), "Titre : " + arg0);
		if(arg1 != null)Log.i(getClass().getCanonicalName(), "Contenu (IOAcknowledge) : " + arg1);
		if(arg2 != null){
			JSONObject response = new JSONObject();
			if(arg2.length == 1){
				response = (JSONObject) arg2[0];
			}
			else response = null;
			
			JSONObject [] test = new JSONObject[arg2.length];
			for(int i=0; i<arg2.length; i++){
				test[i] = (JSONObject) arg2[i];
				Log.i(getClass().getCanonicalName(), "Contenu (Object) : " + test[i]);
			}
			
			if(arg0.equals("attrs") && response != null){
				try {
					callback.setAttributes(new Attributes(response.getString("userid"), response.getInt("sid"), response.getInt("rid")));
				} catch (JSONException e) {
					Log.i(getClass().getCanonicalName(),"JSON exception");
					Log.i(getClass().getCanonicalName(), e.getMessage());
				}
//				Log.i(getClass().getCanonicalName(), "Attributes : " + callback.getAttributes().toString());
			}
			
			Context context;
			if(arg0.equals(Context.ERROR.getValue()) || arg0.equals("result_error")) context = Context.ERROR;
			else if(arg0.equals(Context.LINK.getValue())) context = Context.LINK;
			else if(arg0.equals("hMessage")) context = Context.MESSAGE;
			else if(arg0.equals(Context.RESULT.getValue())) context = Context.RESULT;
			else context = null;
			callback.hCallback(context, Status.CONNECTED, response);
		}
		
	}

}
