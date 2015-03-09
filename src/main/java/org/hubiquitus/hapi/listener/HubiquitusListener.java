package org.hubiquitus.hapi.listener;

import org.hubiquitus.hapi.message.Request;
import org.hubiquitus.hapi.utils.HubiquitusErrorCodes;

public interface HubiquitusListener {

    void onConnect();

    /**
     * Called when hubiquitus is disconnected
     * Can be called at same time than {@link #onError(org.hubiquitus.hapi.utils.HubiquitusErrorCodes, Object, boolean)}
     */
    void onDisconnect();

    void onMessage(Request request);

    /**
     * Caution this is called at same time as {@link #onDisconnect()}
     *
     * @param cause        The disconnection cause {@link org.hubiquitus.hapi.utils.HubiquitusErrorCodes}
     * @param message      An optional message (can be a String or an Exception or {@code null}
     *                     in these case it will be automatically logged as an error
     * @param tryToRecover if true Hubiquitus is trying to automatically recover itself from the error
     *                     else you have to recover yourself
     */
    void onError(HubiquitusErrorCodes cause, Object message, boolean tryToRecover);
}
