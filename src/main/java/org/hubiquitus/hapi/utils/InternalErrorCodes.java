package org.hubiquitus.hapi.utils;

/**
 * Created by m.Ruetsch on 03/02/15.
 */
public enum InternalErrorCodes {
    NO_CONNECTION,              //onError
    NO_TRANSPORT,               //Reconnect

    EMPTY_TRANSPORT,            //Should be reusable

    GET_HUBI_INFOS,             //only used to notify this error should do nothing

    INIT_TRANSPORT,             //connection point error should check endpoint

    CONNECTION_FAILED,          //Unable to connect to server
    TRANSPORT_TIMEOUT,          //No heartbeat since 2*HeartBeatFrequency

    AUTHENTICATION_TIMEOUT,     //Socket should be reusable
    AUTHENTICATION_FAILED,      //Bad credentials

    TRANSPORT_NOT_READY,        //Transport not ready to send
    UNEXPECTED_TRANSPORT_CLOSE, //Transport closed unexpectedly
    UNABLE_TO_RECOVER_CONNECTION

}
