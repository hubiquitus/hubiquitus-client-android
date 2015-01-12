package org.hubiquitus.hapi.transport.utils;

import java.util.UUID;

public class TransportUtils {

	
	public static String getSessionId() {
		return UUID.randomUUID().toString();
	}
	
	public static String getServerId() {
		StringBuilder sb = new StringBuilder();
		sb.append(generateDigit()).append(generateDigit()).append(generateDigit());
		return sb.toString();
	}
	
	private static long generateDigit() {
		return Math.round(Math.random() * 10);
	}
	
}
