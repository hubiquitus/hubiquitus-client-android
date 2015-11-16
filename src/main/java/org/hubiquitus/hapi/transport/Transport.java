package org.hubiquitus.hapi.transport;

import android.os.Handler;
import android.util.Log;

import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.message.Message;
import org.hubiquitus.hapi.message.MessageType;
import org.hubiquitus.hapi.message.Request;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.hubiquitus.hapi.transport.utils.MessageBuilder;
import org.hubiquitus.hapi.utils.HubiquitusErrorCodes;
import org.hubiquitus.hapi.utils.InternalErrorCodes;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by m.Ruetsch on 03/02/15.
 */
public abstract class Transport {
    public static final String HEARTBEAT_FREQUENCY = "heartbeatFreq";
    public static final String HB = "hb";

    protected HashMap<String, ResponseListener> mResponseQueue;

    protected boolean mIsAuthenticated = false;
    protected JSONObject mAuthData;
    protected boolean mDebugLog = false;
    protected TransportListener mTransportListener;
    private long mLastMessageTime;
    private int mHeartbeatFreq = 15000;
    private Handler mHandler;
    private Runnable mCheckConnectionRunnable;

    public Transport(TransportListener transportListener) {
        if (mDebugLog) {
            Log.d(getClass().getCanonicalName(), "New " + this + " transport instanced");
        }

        mTransportListener = transportListener;
        mResponseQueue = new HashMap<>();
        mCheckConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                checkConnection();
            }
        };
    }

    /**
     * Set handler used for checking connection
     * You shouldn't do long blocking tasks on this Handler
     *
     * @param handler the handler to use
     */
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * Enable full debug output in log
     *
     * @param enable the full output (disabled by default)
     */
    public void enableFullDebugLog(boolean enable) {
        mDebugLog = enable;
    }

    /**
     * Connect to endpoint
     *
     * @param endpoint endpoint
     * @param authData authentication data
     */
    public void connect(String endpoint, JSONObject authData) {
        if (mDebugLog) {
            Log.d(getClass().getCanonicalName(), this + " connect to " + endpoint + " with " + authData);
        }

        mAuthData = authData;
    }


    private void checkConnection() {
        if (mDebugLog) {
            Log.d(getClass().getCanonicalName(), this + " Check connection");
        }


        long hbInterval = System.currentTimeMillis() - mLastMessageTime;

        //In this special case the server closed the webSocket as we connect with invalid credential
        if (mLastMessageTime == 0 && this instanceof WebSocketTransport && ((WebSocketTransport) this).isWebSocketClosed()) {
            if (mTransportListener != null) {
                mTransportListener.onError(InternalErrorCodes.AUTHENTICATION_FAILED, null);
            }
            mLastMessageTime = 0;

            //If we don't get a HeartBeat since 3 time the HB frequency we assume connection lost
        } else if (hbInterval > 3 * mHeartbeatFreq) {
            Log.e(getClass().getCanonicalName(), this + " LOST Connection interval =" + (hbInterval) + " => disconnect");

            if (mTransportListener != null) {
                mTransportListener.onError(InternalErrorCodes.TRANSPORT_TIMEOUT, null);
            }
            disconnect();
            mLastMessageTime = 0;

        } else {
            // Remove any scheduled runnable to avoid several threads to do the same thing
            mHandler.removeCallbacks(mCheckConnectionRunnable);
            mHandler.postDelayed(mCheckConnectionRunnable, mHeartbeatFreq);
        }
    }

    /**
     * Handler for json messages
     *
     * @param stringMessage a json message
     */
    protected void handleMessage(String stringMessage) {
        if (mDebugLog) {
            Log.d(getClass().getCanonicalName(), this + " Got new message = " + stringMessage);
        }

        // Whatever the message is, we are still connected to the server
        mLastMessageTime = System.currentTimeMillis();

        if (HB.equals(stringMessage)) {
            // TODO Comment this for Hubiquitus < 0.9
            // Respond to the hb message
            sendHeartBeat();
        } else {

            JSONObject jsonMessage;
            try {
                jsonMessage = new JSONObject(stringMessage);
            } catch (JSONException e) {
                Log.w(getClass().getCanonicalName(), "Unable to parse json message = " + stringMessage);
                return;
            }


            String messageId = jsonMessage.optString(MessageBuilder.ID);
            String from = jsonMessage.optString(MessageBuilder.FROM);
            Object content = jsonMessage.opt(MessageBuilder.CONTENT);
            JSONObject err = null;

            if (jsonMessage.has(MessageBuilder.ERR) && !jsonMessage.isNull(MessageBuilder.ERR)) {
                err = jsonMessage.optJSONObject(MessageBuilder.ERR);
            }

            if (jsonMessage.has(MessageBuilder.TYPE)) {

                String messageType = jsonMessage.optString(MessageBuilder.TYPE);

                switch (MessageType.valueOf(messageType.toUpperCase(Locale.US))) {
                    case LOGIN:
                        if (mDebugLog) Log.d(getClass().getCanonicalName(), this + " Got LOGIN");

                        mIsAuthenticated = true;
                        if (mTransportListener != null) {
                            mTransportListener.onConnect();
                        }
                        break;

                    case NEGOTIATE:
                        if (mDebugLog) {
                            Log.d(getClass().getCanonicalName(), this + " Got NEGOTIATE");
                        }

                        int newHBFrequency = jsonMessage.optInt(HEARTBEAT_FREQUENCY);
                        if (newHBFrequency > 0) {
                            mHeartbeatFreq = newHBFrequency;
                        }

                        // Remove any scheduled runnable to avoid several threads to do the same thing
                        mHandler.removeCallbacks(mCheckConnectionRunnable);
                        mHandler.postDelayed(mCheckConnectionRunnable, mHeartbeatFreq);
                        break;

                    case REQ:
                        if (mDebugLog) Log.d(getClass().getCanonicalName(), this + " Got REQ");

                        Request request = MessageBuilder.buildRequest(this, mTransportListener, from, content, messageId);
                        if (mTransportListener != null) {
                            mTransportListener.onMessage(request);
                        }
                        break;

                    case RES:
                        final ResponseListener responseListener = mResponseQueue.get(messageId);
                        if (mDebugLog) {
                            Log.d(getClass().getCanonicalName(), this + " Got RES responseListener=" + (responseListener != null));
                        }

                        if (responseListener != null) {
                            final Message message = new Message(from, content);
                            final JSONObject error = err;

                            // Runs in a new thread the request callback
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    responseListener.onResponse(error, message);
                                }
                            }).start();
                            mResponseQueue.remove(messageId);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Send a hubiquitus message
     *
     * @param to               the recipient of the message
     * @param content          the content of the message
     * @param timeout          the timeout of the message
     * @param responseListener the response listener
     * @return a json object describing the message
     */
    public JSONObject send(String to, Object content, int timeout, ResponseListener responseListener) {


        JSONObject jsonMessage = null;
        try {
            jsonMessage = MessageBuilder.buildMessage(to, content, responseListener != null);
            final String messageId = jsonMessage.getString(MessageBuilder.ID);

            if (responseListener != null) {
                //Add an responseListener in the response queue
                mResponseQueue.put(jsonMessage.getString(MessageBuilder.ID), responseListener);
            }

            //Post an timeOutListener
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final ResponseListener responseListener = mResponseQueue.get(messageId);
                    if (responseListener != null) {
                        //Run this in an other thread
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                responseListener.onResponse(HubiquitusErrorCodes.TIME_OUT, null);
                            }
                        });
                        mResponseQueue.remove(messageId);
                    }
                }
            }, timeout);

        } catch (JSONException e) {
            Log.w(getClass().getCanonicalName(), "unable to construct the message to send " + e);
        }

        if (mDebugLog) {
            Log.d(getClass().getCanonicalName(), this + " send new message = " + jsonMessage);
        }
        return jsonMessage;
    }

    public abstract boolean isReady();

    /**
     * Send a hubiquitus message
     *
     * @param jsonObject the json object describing the message
     */
    public abstract void send(JSONObject jsonObject);

    /**
     * Send the hb message in response of the hb received from the gateway
     */
    protected abstract void sendHeartBeat();

    /**
     * Disconnect from endpoint
     * {@link org.hubiquitus.hapi.transport.listener.TransportListener(Object)} will be called
     */
    protected abstract void disconnect();


    /**
     * Silent disconnect from endpoint
     * {@link org.hubiquitus.hapi.transport.listener.TransportListener(Object)} will not be called
     */
    public void silentDisconnect() {
        mHandler.removeCallbacksAndMessages(null);
        mLastMessageTime = 0;
    }

}
