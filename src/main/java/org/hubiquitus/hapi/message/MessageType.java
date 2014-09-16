package org.hubiquitus.hapi.message;

import java.util.Locale;

public enum MessageType {
	LOGIN, REQ, RES, PING, NEGOTIATE;
	
	public String format() {
		return this.name().toLowerCase(Locale.US);
	}
}
