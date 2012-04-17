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

import org.jivesoftware.smack.packet.Message;

import android.util.Log;

public class Parser {

	// get the message from an item
	public static String parseItem(String xml){
		// modele item : <item id='53650CEBCB491'>
		//<entry xmlns=\"org.hubiquitus.hapi.entry\">Bonjour<\/entry>
		//<\/item>
		
		String [] split = xml.split(">");
		String msg = null;
		for(int i=0; i<split.length; i++){
			//Log.i("Parser", split[i]);
			if(split[i].endsWith("org.hubiquitus.hapi.entry\"")){
				//Log.i("Parser chose : ", split[i+1]);
				String [] messages = split[i+1].split("<");
				msg = messages[0];
			}
		}
		return msg;
	}

}
