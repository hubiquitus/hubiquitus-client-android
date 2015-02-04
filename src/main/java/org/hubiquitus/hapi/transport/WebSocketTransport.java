package org.hubiquitus.hapi.transport;

import android.text.TextUtils;
import android.util.Log;

import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.message.MessageType;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.hubiquitus.hapi.transport.utils.MessageBuilder;
import org.hubiquitus.hapi.utils.InternalErrorCodes;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by m.Ruetsch on 03/02/15.
 */
public class WebSocketTransport extends Transport {

    private static final String WSS = "wss";
    private static final String HTTPS = "https";

    private static final int CLOSE_PING_TIMEOUT_CODE = 2000; //WS standard codes are between 1000 & 1016
    private static final int PING_TIMEOUT = 3000;


    private WebSocketClient mWebSocketClient;
    private Timer mPingTimeoutTimer;


    public WebSocketTransport(TransportListener transportListener) {
        super(transportListener);
    }

    public boolean isWebSocketClosed() {
        return mWebSocketClient == null || mWebSocketClient.isClosed();
    }

    @Override
    public boolean isReady() {
        return mWebSocketClient != null && mWebSocketClient.isOpen() && mIsAuthenticated;
    }

    // ========== Transport Implementation ============

    private void initSocket(String endpoint) {
        if (mDebugLog) {
            Log.d(getClass().getCanonicalName(), this + " init websocket, endpoint =" + endpoint);
        }

        URI endpointURI;

        try {
            endpointURI = new URI(endpoint);

            this.mWebSocketClient = new WebSocketClient(endpointURI) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    if (mDebugLog) {
                        Log.d(getClass().getCanonicalName(), this + " init websocket onOpen");
                    }

                    try {
                        send(MessageBuilder.buildNegotiateMessage().toString());

                        mPingTimeoutTimer = new Timer();
                        mPingTimeoutTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Log.e(getClass().getCanonicalName(), this + " ping timeout ==> close socket");
                                close(CLOSE_PING_TIMEOUT_CODE);
                            }
                        }, PING_TIMEOUT);
                    } catch (JSONException e) {
                        if (mTransportListener != null) {
                            mTransportListener.onError(InternalErrorCodes.CONNECTION_FAILED,null);
                        }
                        Log.w(getClass().getCanonicalName(), e);
                    }
                }

                @Override
                public void onMessage(String message) {
                    try {
                        if (!HB.equals(message)) {
                            JSONObject object = new JSONObject(message);
                            String messageType = object.optString(MessageBuilder.TYPE);
                            if (!TextUtils.isEmpty(messageType) && MessageType.NEGOTIATE.equals(MessageType.valueOf(messageType.toUpperCase(Locale.US)))) {

                                //Cancel the ping timeOut task
                                if (mDebugLog) {
                                    Log.d(getClass().getCanonicalName(), this + " websocket cancelPingTimeOutTimer");
                                }

                                if (mPingTimeoutTimer != null) {
                                    mPingTimeoutTimer.cancel();
                                }

                                onWebSocketReady();
                            }
                        }

                        handleMessage(message);
                    } catch (JSONException e) {
                        Log.w(getClass().getCanonicalName(), e);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(getClass().getCanonicalName(), this + " Transport error ==> close socket");
                    //Doesn't notify the transport listener, it will be at socket close with the BuggyClose code
                    close(CloseFrame.BUGGYCLOSE);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    if (mDebugLog) {
                        Log.d(getClass().getCanonicalName(), this + " websocket onClose code=" + code + " reason=" + reason + " remote=" + remote);
                    }

                    mIsAuthenticated = false;
                    if (mTransportListener != null) {

                        //If the socket close is normal (disconnect was called) we only notify with onDisconnect
                        if (code == CloseFrame.NORMAL) {
                            mTransportListener.onDisconnect();
                        } else {
                            mTransportListener.onError(InternalErrorCodes.UNEXPECTED_TRANSPORT_CLOSE, reason);
                        }
                    }
                }
            };

            if (endpoint.startsWith(WSS) || endpoint.startsWith(HTTPS)) {

                TrustManager tm = new X509TrustManager() {

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
                };

                SSLContext sslContext;
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{tm}, null);

                SSLSocketFactory factory = sslContext.getSocketFactory();
                mWebSocketClient.setSocket(factory.createSocket());
            }

        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException | IOException e) {
            if (mTransportListener != null) {
                mTransportListener.onError(InternalErrorCodes.INIT_TRANSPORT, e);
            }
        }
    }

    private void onWebSocketReady() {
        if (mDebugLog) {
            Log.d(getClass().getCanonicalName(), this + " Connect with " + mWebSocketClient + " " + mAuthData);
        }

        //When the socket is ready (after successful handshaking) the client send authentication data
        if (mWebSocketClient != null && !mWebSocketClient.isClosed()) {
            try {
                mWebSocketClient.send(MessageBuilder.buildAuthMessage(mAuthData).toString());
            } catch (JSONException e) {
                if (mTransportListener != null) {
                    mTransportListener.onError(InternalErrorCodes.AUTHENTICATION_FAILED, e);
                }
            }
        } else {
            if (mTransportListener != null) {
                mTransportListener.onError(InternalErrorCodes.EMPTY_TRANSPORT, null);
            }
        }
    }

    @Override
    public void connect(String endpoint, JSONObject authData) {
        super.connect(endpoint, authData);

        if (mWebSocketClient == null && mTransportListener != null) {
            initSocket(endpoint + "/websocket");
            mWebSocketClient.connect();
        } else {
            Log.e(getClass().getCanonicalName(), this + " shouldn't reuse transport");
        }
    }

    @Override
    public JSONObject send(String to, Object content, int timeout, ResponseListener responseListener) {
        JSONObject jsonMessage = super.send(to, content, timeout, responseListener);

        send(jsonMessage);
        return jsonMessage;
    }

    @Override
    public void send(JSONObject jsonObject) {
        if (mWebSocketClient == null) {
            //Should never be here, hubiquitus performs check before calling the transport send.
            if (mDebugLog) {
                Log.d(getClass().getCanonicalName(), this + " websocket client is null in send");
            }
            if (mTransportListener != null) {
                mTransportListener.onError(InternalErrorCodes.EMPTY_TRANSPORT, null);
            }
            return;
        }
        try {
            mWebSocketClient.send(jsonObject.toString());
        } catch (Exception e) {
            if (mTransportListener != null) {
                mTransportListener.onError(InternalErrorCodes.TRANSPORT_NOT_READY, e);
            }
        }
    }

    @Override
    protected void sendHeartBeat() {
        if (mWebSocketClient == null) {
            if (mTransportListener != null) {
                mTransportListener.onError(InternalErrorCodes.EMPTY_TRANSPORT, null);
            }
            return;
        }
        mWebSocketClient.send(Transport.HB);
    }

    @Override
    protected void disconnect() {
        if (mDebugLog) Log.d(getClass().getCanonicalName(), this + " websocket disconnect");

        if (mTransportListener != null) {
            mTransportListener.onDisconnect();
        }
        silentDisconnect();
    }

    @Override
    public void silentDisconnect() {
        super.silentDisconnect();
        if (mDebugLog) Log.d(getClass().getCanonicalName(), this + " websocket silent disconnect");

        mIsAuthenticated = false;
        //The transport listener is set to null here to avoid have multiple transport
        //calling the same TransportListener of the Hubiquitus instance
        mTransportListener = null;
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
            mWebSocketClient = null;
        }
    }
}
