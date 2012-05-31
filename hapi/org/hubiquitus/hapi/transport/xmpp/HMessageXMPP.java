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

package org.hubiquitus.hapi.transport.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 *
 * @author j.desousag
 * @version 0.3
 * Class HMessageXMPP for send/get the hCommand
 */
public class HMessageXMPP implements PacketExtension{

	public static final String NAMESPACE = "";
	public static final String ELEMENT_NAME = "hbody";
	private String type = null;
	private String body = null;
	
	public HMessageXMPP() {
	}
		
	public HMessageXMPP(String type, String body) {
		this.type = type;
		this.body = body;
	}
	
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getContent(){
		return this.body;
	}
	
	public void setContent(String content) {
		this.body = content;
	}
	
	@Override
	public String getElementName() {
		return HMessageXMPP.ELEMENT_NAME;
	}

	@Override
	public String getNamespace() {
		return HMessageXMPP.NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder localStringBuilder = new StringBuilder();
		
		if( type != null) {
		    localStringBuilder.append("<hbody type=\"").append(getType()).append("\">");
		    localStringBuilder.append(getContent());
			localStringBuilder.append("</hbody>");
		} else {
			System.out.println("Should define a type for Message's body");
		}
		
	    return localStringBuilder.toString();
	}
	
	public static class Provider implements PacketExtensionProvider
		{
	    	public PacketExtension parseExtension(XmlPullParser paramXmlPullParser) throws Exception
	    	{
	    		String type = paramXmlPullParser.getAttributeValue("", "type");
	    		paramXmlPullParser.next();
	    		String content = paramXmlPullParser.getText();
	    		while (paramXmlPullParser.getEventType() != XmlPullParser.END_TAG) {
	    			paramXmlPullParser.next();
	    		}
	    		return new HMessageXMPP(type, content);
	    	}
		}

}
