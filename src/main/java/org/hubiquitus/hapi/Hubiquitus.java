package org.hubiquitus.hapi;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.hubiquitus.hapi.listener.HubiquitusListener;
import org.hubiquitus.hapi.listener.ResponseListener;
import org.hubiquitus.hapi.message.Request;
import org.hubiquitus.hapi.transport.Transport;
import org.hubiquitus.hapi.transport.WebSocketTransport;
import org.hubiquitus.hapi.transport.XhrTransport;
import org.hubiquitus.hapi.transport.callback.NewAuthenticationData;
import org.hubiquitus.hapi.transport.listener.TransportListener;
import org.hubiquitus.hapi.transport.service.ServiceManager;
import org.hubiquitus.hapi.transport.service.ServiceResponse;
import org.hubiquitus.hapi.utils.ConnectivityUtils;
import org.hubiquitus.hapi.utils.HubiquitusErrorCodes;
import org.hubiquitus.hapi.utils.HubiquitusInfoUtils;
import org.hubiquitus.hapi.utils.InternalErrorCodes;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by m.Ruetsch on 03/02/15.
 */
public class Hubiquitus implements TransportListener {
    private enum State {
        INIT_TRANSPORT, CONNECTING, CONNECTED, DISCONNECTED, ERROR
    }

    private static final String URN = "urn";
    private static final String TICKET = "ticket";
    private static final String INFO = "/info";

    private static final int RECONNECT_DELAY = 5000;
    private static final int DEFAULT_SEND_TIME_OUT = 20000;

    private static final int MAX_RECONNECT_TRY_COUNT = 2;

    private String mEndpoint;
    private JSONObject mAuthData;

    private Transport mTransport;

    private Handler mHandler;
    private Context mContext;

    private boolean mShouldAutoReconnect = false;
    private boolean mDebugLog = false;

    private Boolean mWebSocketSupported;

    private int mWSConnectionTryCount = 0;
    private int mXHRConnectionTryCount = 0;
    private Runnable mReconnectionRunnable;

    private NewAuthenticationData mAuthenticator;
    private HubiquitusListener mHubiquitusListener;

    private State mState = State.DISCONNECTED;

    // ============ Constructors ============

    public Hubiquitus(Context context, HubiquitusListener hubiquitusListener) {
        this(context, hubiquitusListener, null, false);
    }

    public Hubiquitus(Context context, HubiquitusListener hubiquitusListener, NewAuthenticationData authenticator) {
        this(context, hubiquitusListener, authenticator, false);
    }

    /**
     * @param autoReconnect allow Hubiquitus to try to auto reconnect
     */
    public Hubiquitus(Context context, HubiquitusListener hubiquitusListener, NewAuthenticationData authenticator, boolean autoReconnect) {
        this.mHubiquitusListener = hubiquitusListener;
        this.mShouldAutoReconnect = autoReconnect;
        this.mAuthenticator = authenticator;
        this.mContext = context;

        HandlerThread thread = new HandlerThread("HubiquitusHandlerThread");
        thread.start();
        this.mHandler = new Handler(thread.getLooper());

        mReconnectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (State.CONNECTED != mState && State.CONNECTING != mState && State.INIT_TRANSPORT != mState) {
                    connect();
                }
            }
        };
    }

    // ============ Transport Handle Methods ============

    /**
     * Connect to hubiquitus endpoint
     *
     * @param endpoint endpoint
     * @param authData authentication data
     */
    synchronized public void connect(String endpoint, JSONObject authData) {
        if (mDebugLog) Log.d("DEBUG", "Hubiquitus external");
        if (isConnected() || isConnecting()) {
            Log.w(getClass().getCanonicalName(), "Hubiquitus already connecting");
            return;
        }

        mEndpoint = endpoint;
        mAuthData = authData;
        connect();
    }

    /**
     * Internal connect method,used for auto reconnect
     */
    synchronized private void connect() {
        if (mDebugLog) {
            Log.d(getClass().getCanonicalName(), "Hubiquitus connect internal state=" + mState + " transport=" + mTransport +
                    "\r\n endpoit=" + mEndpoint + " authData=" + mAuthData + "\r\n XHRtryCount=" + String.valueOf(mXHRConnectionTryCount) +
                    " WSTryCount=" + String.valueOf(mXHRConnectionTryCount));
        }

        if (ConnectivityUtils.isConnected(mContext)) {

            //The transport is initiated, so we can connect
            if (mTransport != null && State.INIT_TRANSPORT == mState) {
                mState = State.CONNECTING;

                if (mAuthData == null && mAuthenticator != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject authData;
                            if (mAuthenticator != null) {
                                authData = mAuthenticator.getNewAuthData();
                            } else {
                                return;
                            }

                            synchronized (Hubiquitus.this) {
                                if (authData != null) {
                                    if (mTransport != null) {
                                        mTransport.connect(mEndpoint, authData);
                                    } else if (mAuthData == null) {
                                        //Store authData for next time
                                        mAuthData = authData;
                                    }
                                } else {
                                    onError(InternalErrorCodes.AUTHENTICATION_FAILED, null);
                                }
                            }
                        }
                    }).start();
                } else {
                    mTransport.connect(mEndpoint, mAuthData);
                    mAuthData = null; //Credential are usable only one time //TODO  add usage count ?
                }

            } else if (canCreateNewTransport()) {
                initTransport();

            } else {
                onError(InternalErrorCodes.INIT_TRANSPORT,null);
                Log.e(getClass().getCanonicalName(), "IllegalState in connect" + mState);
            }
        } else {
            onError(InternalErrorCodes.NO_CONNECTION, null);
        }
    }

    /**
     * Initializes transport
     */
    synchronized private void initTransport() {
        mState = State.INIT_TRANSPORT;

        //Check if WebSocket is supported only if we have no response or an error last time
        if (mWebSocketSupported == null) {
            checkWebSocketSupport();
        } else {
            createTransport();
        }
    }

    synchronized private void checkWebSocketSupport() {
        //We must start a new Thread as we do network operation and it can take a while.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Get the hubiquitus info to know if WebSocket is supported
                    ServiceResponse responseWSSupported = ServiceManager
                            .requestService(mEndpoint, INFO, ServiceManager.Method.GET, null);

                    //The result is stored in a Boolean wrapper to have a three state var;
                    Boolean WsSupported = HubiquitusInfoUtils.parseWebSocketSupported(responseWSSupported);

                    synchronized (Hubiquitus.this) {
                        mWebSocketSupported = WsSupported;
                    }
                } catch (IOException e) {
                    // Call onError for log purpose
                    onError(InternalErrorCodes.GET_HUBI_INFOS, e);
                }
                createTransport();
            }
        }).start();
    }

    synchronized private void createTransport() {
        if (!isTransportInit() || isTransportReady()) {
            if (mDebugLog) {
                Log.w(getClass().getCanonicalName(), "Try to instantiate a new transport while the actual is ready !");
            }
            //We assume here that hubiquitus is already connecting
            return;

        } else if (mTransport != null) {
            mTransport.silentDisconnect();
        }

        if (mWebSocketSupported && mWSConnectionTryCount++ < MAX_RECONNECT_TRY_COUNT) {
            mTransport = new WebSocketTransport(Hubiquitus.this);

        } else if (mXHRConnectionTryCount++ < MAX_RECONNECT_TRY_COUNT) {
            mTransport = new XhrTransport(Hubiquitus.this);

        } else {
            //if we can't recover connection we notify it
            onError(InternalErrorCodes.UNABLE_TO_RECOVER_CONNECTION, null);
            return;
        }

        mTransport.enableFullDebugLog(mDebugLog);
        mTransport.setHandler(Hubiquitus.this.mHandler);
        if (mDebugLog) {
            Log.d(getClass().getCanonicalName(), "new transport =" + mTransport);
        }

        connect();
    }

    synchronized public void disconnect() {
        mState = State.DISCONNECTED;
        if (mDebugLog) Log.d(getClass().getCanonicalName(), "asked to disconnect from outside");

        if (mTransport != null) {
            mTransport.silentDisconnect();
        }
    }

    // ============ Hubiquitus machine state ============

    synchronized public boolean isConnected() {
        return State.CONNECTED == mState;
    }

    synchronized public boolean isConnecting() {
        return State.CONNECTING == mState || State.INIT_TRANSPORT == mState;
    }

    synchronized public boolean isDisconnected() {
        return State.DISCONNECTED == mState || State.ERROR == mState;
    }

    //Private
    synchronized private void reInitTransportTryCount() {
        mWSConnectionTryCount = 0;
        mXHRConnectionTryCount = 0;
    }

    synchronized private boolean isTransportInit() {
        return State.INIT_TRANSPORT == mState;
    }

    synchronized private boolean canCreateNewTransport() {
        return State.CONNECTING != mState
                && State.INIT_TRANSPORT != mState
                && (mTransport == null
                || !mTransport.isReady());
    }

    synchronized private boolean isTransportReady() {
        return mTransport != null && mTransport.isReady();
    }

    // ============ Methods ============

    public static JSONObject generateAuthDataWithCredential(String urn, String password) throws JSONException {
        JSONObject authData = new JSONObject();
        authData.put(URN, urn);
        authData.put(TICKET, password);
        return authData;
    }


    public void enableFullDebugLog(boolean enable) {
        mDebugLog = enable;
    }


    /**
     * Send a hubiquitus message
     *
     * @param to               recipient of the message
     * @param content          content of the message
     * @param responseListener response listener
     */
    public void send(String to, Object content, ResponseListener responseListener) {
        send(to, content, DEFAULT_SEND_TIME_OUT, responseListener);
    }

    /**
     * Send a hubiquitus message
     *
     * @param to      recipient of the message
     * @param content content of the message
     */
    public void send(String to, Object content) {
        send(to, content, DEFAULT_SEND_TIME_OUT, null);
    }

    /**
     * Send a hubiquitus message
     *
     * @param to               recipient of the message
     * @param content          content of the message
     * @param timeout          timeout of the message
     * @param responseListener response listener
     */
    synchronized public void send(String to, Object content, int timeout, ResponseListener responseListener) {
        if (mDebugLog) {
            Log.d(getClass().getCanonicalName(), "Hubiquitus send via " + mTransport + " : " + content + " => " + to);
        }

        if (State.CONNECTED != mState || !isTransportReady()) {
            if (!ConnectivityUtils.isConnected(mContext)) {
                responseListener.onResponse(HubiquitusErrorCodes.NO_NETWORK, null);
            } else if (mTransport == null) {
                responseListener.onResponse(HubiquitusErrorCodes.UNABLE_TO_SEND, null);
                onError(InternalErrorCodes.NO_TRANSPORT, null);
            } else {
                responseListener.onResponse(HubiquitusErrorCodes.UNABLE_TO_SEND, null);
                onError(InternalErrorCodes.TRANSPORT_NOT_READY, null);
            }
            return;
        }

        mTransport.send(to, content, timeout, responseListener);
    }

    // ============ Transport Listener ============


    @Override
    synchronized public void onConnect() {
        mState = State.CONNECTED;
        reInitTransportTryCount();
        mHubiquitusListener.onConnect();
    }

    @Override
    synchronized public void onDisconnect() {
        if (mState != State.ERROR) {
            mState = State.DISCONNECTED;
        }
        mHubiquitusListener.onDisconnect();
    }

    @Override
    public void onMessage(final Request request) {
        //This runs in a new thread to avoid blocking network thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHubiquitusListener.onMessage(request);
            }
        }).start();
    }

    @Override
    synchronized public void onError(InternalErrorCodes cause, Object message) {
        if (message instanceof Exception || message instanceof String) {
            Log.w(getClass().getCanonicalName(), "on Error code=" + cause + " state=" + mState + "\r\n" + message);
        } else {
            Log.w(getClass().getCanonicalName(), "on Error code=" + cause + " state=" + mState);
        }

        if (!ConnectivityUtils.isConnected(mContext)) {
            reInitTransportTryCount();
            mHubiquitusListener.onError(HubiquitusErrorCodes.NO_NETWORK, message, false);
            mState = State.ERROR;
            return;
        }

        switch (cause) {
            case AUTHENTICATION_FAILED:
                reInitTransportTryCount();
                mHubiquitusListener.onError(HubiquitusErrorCodes.INVALID_CREDENTIAL, message, false);
                break;

            case NO_CONNECTION:
                reInitTransportTryCount();
                mHubiquitusListener.onError(HubiquitusErrorCodes.NO_NETWORK, message, false);
                break;

            case INIT_TRANSPORT:
            case UNABLE_TO_RECOVER_CONNECTION:
            case CONNECTION_FAILED:
                //We can't handle theses errors
                reInitTransportTryCount();
                mHubiquitusListener.onError(HubiquitusErrorCodes.UNABLE_TO_CONNECT_TO_ENDPOINT, message, false);
                break;


            case TRANSPORT_TIMEOUT:
                if (State.ERROR == mState) { //Ignore this error if we get it while hubiquitus is  already in error
                    return;
                }

            case UNEXPECTED_TRANSPORT_CLOSE:
            case NO_TRANSPORT:
                //Create a new transport instance
                mHubiquitusListener.onError(HubiquitusErrorCodes.UNEXPECTED_DISCONNECT, message, mShouldAutoReconnect);

                //Try to reconnect only if permitted
                if (mShouldAutoReconnect) {
                    mHandler.postDelayed(mReconnectionRunnable, (mWSConnectionTryCount + mXHRConnectionTryCount + 1) * RECONNECT_DELAY);
                }
                break;

            case EMPTY_TRANSPORT:
                //Should never be called
                //The transport is empty, we can here simply call connect
                connect();
                break;

            case TRANSPORT_NOT_READY:
            case GET_HUBI_INFOS:
            default:
                //Do nothing is for logging purpose
                break;
        }

        mState = State.ERROR;
    }

}
