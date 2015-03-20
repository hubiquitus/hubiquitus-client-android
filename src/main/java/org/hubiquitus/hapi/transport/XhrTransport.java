package org.hubiquitus.hapi.transport;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.hubiquitus.hapi.transport.service.ServiceManager;
import org.hubiquitus.hapi.transport.service.ServiceResponse;
import org.hubiquitus.hapi.transport.utils.MessageBuilder;
import org.hubiquitus.hapi.transport.utils.TransportUtils;
import org.hubiquitus.hapi.utils.InternalErrorCodes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by m.Ruetsch on 04/02/15.
 */
public class XhrTransport extends Transport {
    private static final String SOCKJS_START_MESSAGE = "a";
    private static final String HB_ARRAY = "[\"hb\"]";
    private static final String XHR = "/xhr";
    private static final String XHR_SEND = "/xhr_send";

    private String mFullUrl;

    private boolean mIsConnected = false;
    private boolean mIsClosed = false;

    private PollThread mPollThread;
    private Handler mNetworkHandler;


    public XhrTransport(TransportListener transportListener) {
        super(transportListener);
    }

    @Override
    public boolean isReady() {
        return mIsConnected && !mIsClosed && mPollThread != null && mNetworkHandler != null && mIsAuthenticated;
    }

    @Override
    public void connect(final String endpoint, final JSONObject authData) {
        super.connect(endpoint, authData);


        String serverId = TransportUtils.getServerId();
        String sessionId = TransportUtils.getSessionId();
        mFullUrl = endpoint + "/" + serverId + "/" + sessionId;

        if (mDebugLog) {
            Log.d(getClass().getCanonicalName(), this + " connect to " + endpoint + " fullUrl=" + mFullUrl);
        }

        //Start a new handler thread to send messages and connect
        HandlerThread thread = new HandlerThread("NetworkHandlerThread");
        thread.start();
        mNetworkHandler = new Handler(thread.getLooper());

        mNetworkHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //Connect via XHR
                    ServiceResponse responseConnect = ServiceManager.requestService(mFullUrl, XHR, ServiceManager.Method.POST, null);

                    if (responseConnect != null && responseConnect.getStatus() == HttpURLConnection.HTTP_OK) {
                        ServiceResponse responseAuth = ServiceManager.requestService(
                                mFullUrl, XHR_SEND, ServiceManager.Method.POST, MessageBuilder.buildAuthMessage(authData));

                        if (responseAuth != null && responseAuth.getStatus() == HttpURLConnection.HTTP_NO_CONTENT) {
                            mIsConnected = true;
                            //Start XHR polling thread when connected and authenticated
                            mPollThread = new PollThread();
                            mPollThread.start();

                        } else {
                            if (mTransportListener != null) {
                                mTransportListener.onError(InternalErrorCodes.AUTHENTICATION_FAILED,
                                        "Http status=" + (responseAuth == null ? "no response" : responseAuth.getStatus()));
                            }
                        }
                    } else {
                        if (mTransportListener != null) {
                            mTransportListener.onError(InternalErrorCodes.CONNECTION_FAILED,
                                    "Can't connect to host " + (responseConnect == null ? "no response" : responseConnect.getStatus()));
                        }
                    }
                } catch (IOException | JSONException e) {
                    if (mTransportListener != null) {
                        mTransportListener.onError(InternalErrorCodes.CONNECTION_FAILED, e);
                    }
                }
            }
        });
    }

    @Override
    public void send(JSONObject jsonObject) {
        internalSend(jsonObject);
    }

    @Override
    public JSONObject send(String to, Object content, int timeout, ResponseListener responseListener) {
        JSONObject jsonMessage = super.send(to, content, timeout, responseListener);

        internalSend(jsonMessage);
        return jsonMessage;
    }

    private void internalSend(final JSONObject jsonObject) {
        if (!isReady()) {
            //It's normally not possible to be here, hubiquitus should check this case before calling this method
            Log.e(getClass().getCanonicalName(), "try to send with not ready transport");
            if (mTransportListener != null) {
                mTransportListener.onError(InternalErrorCodes.TRANSPORT_NOT_READY, null);
            }
            return;
        }

        mNetworkHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ServiceManager.requestService(mFullUrl, XHR_SEND, ServiceManager.Method.POST, jsonObject);
                } catch (IOException e) {
                    Log.w(getClass().getCanonicalName(), e);
                }
            }
        });
    }

    @Override
    protected void sendHeartBeat() {
        try {
            send(new JSONObject().put(Transport.HB, Transport.HB));
        } catch (JSONException e) {
            Log.e(getClass().getCanonicalName(), "Unable to create json HeartBeat message");
        }
    }

    @Override
    public void disconnect() {
        if (mTransportListener != null) {
            mTransportListener.onDisconnect();
        }
        silentDisconnect();
    }

    @Override
    public void silentDisconnect() {
        super.silentDisconnect();
        //The mTransportListener is set to null here to avoid have multiple transport
        //calling the same TransportListener of the Hubiquitus instance
        mTransportListener = null;
        mIsAuthenticated = false;
        mIsConnected = false;
        mIsClosed = true;
        if (mNetworkHandler != null) {
            mNetworkHandler.removeCallbacksAndMessages(null); //Removes all pending task
            mNetworkHandler.getLooper().quit();
        }
    }


    /**
     * Extracts a json object list from a String
     *
     * @param text the text to parse
     * @return the parsed json object
     */
    private List<JSONObject> extractJSON(String text) {


        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(text);
        } catch (JSONException e) {
            Log.w(getClass().getCanonicalName(), "Unable to parse json data : " + text);
            return null;
        }

        if (jsonArray.length() > 0) {
            List<JSONObject> jsonObjects = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    if (HB.equals(jsonArray.optString(i))) {
                        handleMessage(HB);
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                        jsonObjects.add(jsonObject);
                    }
                } catch (JSONException e) {
                    Log.w(getClass().getCanonicalName(), "Unable to parse json data : " + jsonArray.optString(i));
                }
            }
            return jsonObjects;
        }
        return null;
    }

    /**
     * @param e the raised exception
     */
    private void pollErrorHandler(Exception e) {
        //If an exception is throw in the PollThread we assume the transport is closed
        if (mTransportListener != null) {
            mTransportListener.onError(InternalErrorCodes.UNEXPECTED_TRANSPORT_CLOSE, e);
        }
        disconnect();
    }

    /**
     * Thread used for xhr-polling
     *
     * @author t.bourgeois
     */
    private class PollThread extends Thread {

        @Override
        public void run() {

            while (mIsConnected && !mIsClosed) {

                try {
                    ServiceResponse response = ServiceManager.requestService(mFullUrl, XHR, ServiceManager.Method.POST, null);

                    if (response != null) {
                        String text = response.getText();
                        if (text.startsWith(SOCKJS_START_MESSAGE)) {
                            Log.d(getClass().getCanonicalName(), this + " SOCKJS MESSAGE => " + text);

                            String stripText = text.replaceFirst(SOCKJS_START_MESSAGE, "");

                            if (HB_ARRAY.equals(stripText)) {
                                XhrTransport.this.handleMessage(HB);

                            } else {
                                List<JSONObject> jsonObjects = extractJSON(stripText);

                                if (jsonObjects != null && jsonObjects.size() > 0) {
                                    for (JSONObject jsonObject : jsonObjects) {
                                        XhrTransport.this.handleMessage(jsonObject.toString());
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    pollErrorHandler(e);
                }

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    pollErrorHandler(e);
                }

            }

        }

    }
}
