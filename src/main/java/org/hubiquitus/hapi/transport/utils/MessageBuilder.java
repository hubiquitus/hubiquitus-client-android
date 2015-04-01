package org.hubiquitus.hapi.transport.utils;

import android.util.Log;

import org.hubiquitus.hapi.message.MessageType;
import org.hubiquitus.hapi.message.Request;
import org.hubiquitus.hapi.transport.Transport;
import org.hubiquitus.hapi.transport.callback.ReplyCallback;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.hubiquitus.hapi.utils.InternalErrorCodes;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by m.Ruetsch on 03/02/15.
 */
public class MessageBuilder {
    public static final String TYPE = "type";
    public static final String AUTH_DATA = "authData";
    public static final String TO = "to";
    public static final String FROM = "from";
    public static final String PAYLOAD = "payload";
    public static final String CB = "cb";
    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String CONTENT = "content";
    public static final String CODE = "code";
    public static final String ERR = "err";

    /**
     * Build an authentication message
     *
     * @param authData authentication data
     * @return the built message
     * @throws org.json.JSONException
     */
    public static JSONObject buildAuthMessage(JSONObject authData)
            throws JSONException {
        JSONObject authDataMessage = new JSONObject();
        authDataMessage.put(TYPE, MessageType.LOGIN.format());
        authDataMessage.put(AUTH_DATA, authData);
        return authDataMessage;
    }

    /**
     * Build a negociateMessage
     *
     * @return the built negotiate message
     * @throws JSONException
     */
    public static JSONObject buildNegotiateMessage() throws JSONException {
        JSONObject negotiateMessage = new JSONObject();
        negotiateMessage.put(TYPE, MessageType.NEGOTIATE.format());
        return negotiateMessage;
    }

    /**
     * Build an error message
     *
     * @param error the error message
     * @return the built error message
     * @throws JSONException
     */
    public static JSONObject buildErrorMessage(String error) throws JSONException {
        JSONObject errorMessage = new JSONObject();
        JSONObject jsonCode = new JSONObject();

        jsonCode.put(CODE, error);
        errorMessage.put(ERR, jsonCode);
        return errorMessage;
    }

    /**
     * Build a hubiquitus message to send
     *
     * @param to      the recipient of the message
     * @param content the content of the message
     * @param cb      true if callback needed
     * @return the built message
     * @throws JSONException
     */
    public static JSONObject buildMessage(String to, Object content, boolean cb) throws JSONException {
        JSONObject message = new JSONObject();
        message.put(TO, to);
        message.put(ID, UUID.randomUUID().toString());
        message.put(DATE, new Date().getTime());
        message.put(TYPE, MessageType.REQ.format());
        message.put(CONTENT, content);
        if (cb) {
            message.put(CB, cb);
        }
        return message;
    }

    /**
     * Builds a hubiquitus response object
     *
     * @param from      recipient of the message
     * @param messageId the message id of the message
     * @param err       the error
     * @param content   the content of the message
     * @return the built message
     * @throws JSONException
     */
    public static JSONObject buildResponse(String from, String messageId, JSONObject err, JSONObject content) throws JSONException {
        JSONObject response = new JSONObject();
        response.put(TYPE, MessageType.RES.format());
        response.put(ID, messageId);
        response.put(TO, from);
        response.put(CONTENT, content);
        response.put(ERR, err);
        return response;
    }

    /**
     * Build a hubiquitus request response object
     *
     * @param from      recipient of the message
     * @param content   the content of the message
     * @param messageId the message id of the message
     * @return the built request
     */
    public static Request buildRequest(final Transport transport, final TransportListener listener,
                                       final String from, Object content,
                                       final String messageId) {

        Request request = new Request();
        request.setContent(content);
        request.setFrom(from);
        request.setReplyCallback(new ReplyCallback() {
            @Override
            public void reply(JSONObject err, JSONObject content) {
                if (transport == null) {
                    if (listener != null) {
                        listener.onError(InternalErrorCodes.NO_TRANSPORT, null);
                    }
                    return;
                }

                try {
                    JSONObject response = buildResponse(from, messageId, err, content);
                    transport.send(response);
                } catch (JSONException e) {
                    Log.w(getClass().getCanonicalName(), e);
                }
            }
        });
        return request;
    }
}
