package org.hubiquitus.hapi.utils;

/**
 * Created by m.Ruetsch on 06/02/15.
 */
public enum HubiquitusErrorCodes {
    /**
     * The provided credentials are invalid,
     * you should get new ones and call
     * <br />{@link org.hubiquitus.hapi.Hubiquitus#connect(String, org.json.JSONObject)}
     */
    INVALID_CREDENTIAL,

    NO_NETWORK,

    /**
     * This error comes in these cases
     * <ul>
     * <li>Bad endpoint address</li>
     * <li>Gateway not responding</li>
     * <li>Server unreachable</li>
     * </ul>
     */
    UNABLE_TO_CONNECT_TO_ENDPOINT,

    /**
     * This error comes if transport crashed, or we lost connection
     * <br/>Hubiquitus can recover from these errors if it's set to do so;
     */
    UNEXPECTED_DISCONNECT,

    TIME_OUT, /**
     * This error is send if you try to call <br/>{@link org.hubiquitus.hapi.Hubiquitus#send(String, Object)},
     * <br />{@link org.hubiquitus.hapi.Hubiquitus#send(String, Object, int, org.hubiquitus.hapi.listener.ResponseListener)}
     * <br />or {@link org.hubiquitus.hapi.Hubiquitus#send(String, Object, org.hubiquitus.hapi.listener.ResponseListener)}
     * <br />and transport is not ready
     */
    UNABLE_TO_SEND
}
