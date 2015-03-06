package org.hubiquitus.hapi.transport.utils;

import java.util.UUID;

public class TransportUtils {

    public static String getSessionId() {
        return UUID.randomUUID().toString();
    }

    public static String getServerId() {
        return new StringBuilder()
                .append(generateDigit())
                .append(generateDigit())
                .append(generateDigit())
                .toString();
    }

    private static long generateDigit() {
        return Math.round(Math.random() * 10);
    }

}
