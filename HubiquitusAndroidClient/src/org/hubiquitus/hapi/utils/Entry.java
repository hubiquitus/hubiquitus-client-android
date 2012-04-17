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

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

public class Entry implements PacketExtension {

	public static final String NAMESPACE = "org.hubiquitus.hapi.entry";
	public static final String ELEMENT_NAME = "entry";
	private String name = null;
	
	public Entry(String name){
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String getElementName() {
		return Entry.ELEMENT_NAME;
	}

	@Override
	public String getNamespace() {
		return Entry.NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder localStringBuilder = new StringBuilder();
		
	    localStringBuilder.append("<").append("entry").append(" xmlns=\"").append("org.hubiquitus.hapi.entry").append("\">");
		
	    localStringBuilder.append(getName());
		localStringBuilder.append("</").append("entry").append('>');
		
	    return localStringBuilder.toString();
	}
	
	public static class Provider
		implements PacketExtensionProvider
		{
	    	public PacketExtension parseExtension(XmlPullParser paramXmlPullParser) throws Exception
	    	{
	    		paramXmlPullParser.next();
	    		String str = paramXmlPullParser.getText();
	
	    		while (paramXmlPullParser.getEventType() != 3) {
	    			paramXmlPullParser.next();
	    		}
	    		return new Entry(str);
	    	}
		}
}
