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
 * @cond internal
 * @version 0.3
 * Class HXMPPPubsubEntry to receive pubsub entry from xmpp
 * Smack packet extension to be able to receive pubsub
 */

public class HXMPPPubsubEntry implements PacketExtension{

	public static final String NAMESPACE = "http://jabber.org/protocol/pubsub";
	public static final String ELEMENT_NAME = "entry";
	private String entry = null;
	
	public HXMPPPubsubEntry() {
	}
	
	public HXMPPPubsubEntry(String content) {
		setContent(content);
	}
	
	/* Getters & Setters */
	
	public String getContent(){
		return this.entry;
	}
	
	public void setContent(String content) {
		this.entry = content;
	}
	
	@Override
	public String getElementName() {
		return HXMPPPubsubEntry.ELEMENT_NAME;
	}

	@Override
	public String getNamespace() {
		return HXMPPPubsubEntry.NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder localStringBuilder = new StringBuilder();

		localStringBuilder.append("<entry xmlns=\"").append(getNamespace()).append("\">");
		localStringBuilder.append(getContent());
		localStringBuilder.append("</entry>");

	    return localStringBuilder.toString();
	}
	
	public static class Provider implements PacketExtensionProvider {
    	public PacketExtension parseExtension(XmlPullParser paramXmlPullParser) throws Exception
    	{
    		paramXmlPullParser.next();
    		String content = paramXmlPullParser.getText();
    		return new HXMPPPubsubEntry(content);
    	}
	}
}

/**
 * @endcond
 */