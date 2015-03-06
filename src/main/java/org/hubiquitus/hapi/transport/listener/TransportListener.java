package org.hubiquitus.hapi.transport.listener;

import org.hubiquitus.hapi.message.Request;
import org.hubiquitus.hapi.utils.InternalErrorCodes;

/**
 * Hubiquitus TransportListener
 */
public interface TransportListener {

    void onConnect();

    void onDisconnect();

    void onMessage(Request request);

    /**
     * @param cause   the cause of the error
     * @param message the message can be an {@link java.lang.Exception},
     *                an {@link java.lang.String} or {@code null}
     */
    void onError(InternalErrorCodes cause, Object message);

}
