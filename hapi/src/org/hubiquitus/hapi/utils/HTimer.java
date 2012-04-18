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

package org.hubiquitus.hapi.utils;

import org.hubiquitus.hapi.HClient;
import org.hubiquitus.hapi.codes.Context;
import org.hubiquitus.hapi.codes.Error;
import org.hubiquitus.hapi.codes.Status;
import org.hubiquitus.hapi.hmessage.Data;
import org.hubiquitus.hapi.options.HOptions;

import android.util.Log;

public class HTimer extends Thread {

	/**
	 * the instance
	 */
	private static HTimer hTimer;
	
	/**
	 * delay before dropping connection
	 */
	private long timeOut;

	/**
	 * the delay has elapsed
	 */
	private static boolean isTimedOut = false;
	
	/**
	 * the client
	 */
	private static HClient hClient;
	
	/**
	 * default constructor
	 * @param options
	 */
	private HTimer(HOptions options){
		super();
		this.timeOut = options.getTimeOut();
	}
	
	/**
	 * the method that returns the instance of the object
	 */
	public static HTimer getHTimer(HOptions options, HClient client){
		isTimedOut = false;
		if(hTimer == null){
			hClient = client;
			hTimer = new HTimer(options);
			return hTimer;
		}
		else {
			return hTimer;
		}
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(timeOut);
			isTimedOut = true;
			Log.e(getClass().getCanonicalName(),"TIME OUT");
			hClient.hCallbackConnection(Context.LINK, new Data(Status.DISCONNECTED, Error.CONNECTION_TIMEOUT, null, null, null, null));
		} catch (InterruptedException e) {
			Log.i(getClass().getCanonicalName(),"Thread exception");
		}
	}
	
	/*****   Getters et Setters   *****/

	/**
	 * @return the isTimedOut
	 */
	public static boolean isTimedOut() {
		return isTimedOut;
	}

	/**
	 * @param isTimedOut the isTimedOut to set
	 */
	public static void setTimedOut(boolean isTimedOut) {
		HTimer.isTimedOut = isTimedOut;
	}



}
