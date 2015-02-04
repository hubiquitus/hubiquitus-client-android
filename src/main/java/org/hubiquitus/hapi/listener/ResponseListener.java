package org.hubiquitus.hapi.listener;

import org.hubiquitus.hapi.message.Message;

/**
 * Interface to get response of message sent with
 * {@link org.hubiquitus.hapi.Hubiquitus#send(String, Object, int, ResponseListener)} or
 * {@link org.hubiquitus.hapi.Hubiquitus#send(String, Object, int, ResponseListener)}
 */
public interface ResponseListener {
    /**
     * Called on response from Hubiquitus actors
     * @param err an optional {@link org.hubiquitus.hapi.utils.HubiquitusErrorCodes} or a {@link org.json.JSONObject} from actor
     * @param message the response message
     */
    void onResponse(Object err, Message message);
}
