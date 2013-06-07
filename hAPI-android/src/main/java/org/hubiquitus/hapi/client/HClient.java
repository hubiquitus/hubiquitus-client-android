/*
 * Copyright (c) Novedia Group 2012.
 *
 *    This file is part of Hubiquitus
 *
 *    Permission is hereby granted, free of charge, to any person obtaining a copy
 *    of this software and associated documentation files (the "Software"), to deal
 *    in the Software without restriction, including without limitation the rights
 *    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *    of the Software, and to permit persons to whom the Software is furnished to do so,
 *    subject to the following conditions:
 *
 *    The above copyright notice and this permission notice shall be included in all copies
 *    or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *    INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *    PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 *    FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *    You should have received a copy of the MIT License along with Hubiquitus.
 *    If not, see <http://opensource.org/licenses/mit-license.php>.
 */

package org.hubiquitus.hapi.client;

import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.hubiquitus.hapi.exceptions.MissingAttrException;
import org.hubiquitus.hapi.hStructures.ConnectionError;
import org.hubiquitus.hapi.hStructures.ConnectionStatus;
import org.hubiquitus.hapi.hStructures.HCommand;
import org.hubiquitus.hapi.hStructures.HCondition;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.hubiquitus.hapi.hStructures.HMessageOptions;
import org.hubiquitus.hapi.hStructures.HOptions;
import org.hubiquitus.hapi.hStructures.HResult;
import org.hubiquitus.hapi.hStructures.HStatus;
import org.hubiquitus.hapi.hStructures.ResultStatus;
import org.hubiquitus.hapi.transport.HTransportDelegate;
import org.hubiquitus.hapi.transport.HTransportManager;
import org.hubiquitus.hapi.transport.HTransportOptions;
import org.hubiquitus.hapi.transport.socketio.HTransportSocketio;
import org.hubiquitus.hapi.util.ErrorMsg;
import org.hubiquitus.hapi.util.HUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 0.6 Hubiquitus client, public API
 */

public class HClient {
	final Logger logger = LoggerFactory.getLogger(HClient.class);

	/**
	 * only connecting , connected , disconnecting , disconnected
	 */
	private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

	@SuppressWarnings("unused")
	private HOptions options = null;
	private HTransportOptions transportOptions = null;
	private HCondition filter;
	//private HTransport transport;
	private HTransportManager transportManager = new HTransportManager();

	private HStatusDelegate statusDelegate = null;
	private HMessageDelegate messageDelegate = null;
	// private HCommandDelegate commandDelegate = null;

	private Hashtable<String, HMessageDelegate> messagesDelegates = new Hashtable<String, HMessageDelegate>();
	private Hashtable<String, Timer> timeoutHashtable = new Hashtable<String, Timer>();

	private TransportDelegate transportDelegate = new TransportDelegate();

    public String getFullUrn() {
		return this.transportOptions.getFullUrn();
	}

    public String getBareUrn() {
		return this.transportOptions.getFullUrn();
	}

	public String getResource() {
		return this.transportOptions.getResource();
	}


    public HClient() {
		transportOptions = new HTransportOptions();
		try {
			filter = new HCondition("{}");
		} catch (JSONException e) {
			logger.error("Can not init filter : ", e);
		}
	}


	/**
	 * Establishes a connection to hNode to allow the reception and sending of messages and commands.
	 * @param login : login, mandatory.
	 * @param password : Mandatory.
	 * @param options : Complementary values used for the connection to the server. Not mandatory.
	 * @param context : Not mandatory.
	 */
    @SuppressWarnings("unused")
	public void connect(String login, String password, HOptions options, JSONObject context) {
		boolean shouldConnect = false;
		boolean connInProgress = false;
		boolean disconInProgress = false;
		this.options = new HOptions(options);

		// synchronize connection status updates to make sure, we have one
		// connect at a time
		synchronized (this) {
			if (this.connectionStatus == ConnectionStatus.DISCONNECTED) {
				shouldConnect = true;

				// update connection status
				connectionStatus = ConnectionStatus.CONNECTING;
			} else if (this.connectionStatus == ConnectionStatus.CONNECTING) {
				connInProgress = true;
			} else if (this.connectionStatus == ConnectionStatus.DISCONNECTING) {
				disconInProgress = true;
			}
		}

		if (shouldConnect) { // if not connected, then connect

			// notify connection
			this.notifyStatus(ConnectionStatus.CONNECTING, ConnectionError.NO_ERROR, null);

			// fill HTransportOptions
			this.fillHTransportOptions(login, password, options, context);

			// choose transport layer
			if (options.getTransport().equals("socketio")) {
				this.transportManager.setTransport(new HTransportSocketio());
			} else {
			// for the future transports.
			}
			//set the callback and transport options in transport manager.
			this.transportManager.setCallback(transportDelegate);
			this.transportManager.setOptions(transportOptions);
			this.transportManager.connect();
		} else {
			if (connInProgress) {
				notifyStatus(ConnectionStatus.CONNECTING, ConnectionError.CONN_PROGRESS, null);
			} else if (disconInProgress) {
				// updateStatus(ConnectionStatus.DISCONNECTING,
				// ConnectionError.ALREADY_CONNECTED, null);
			} else {
				notifyStatus(ConnectionStatus.CONNECTED, ConnectionError.ALREADY_CONNECTED, null);
			}
		}
	}
    
    
	/**
	 * Establishes a connection to hNode to allow the reception and sending of messages and commands.
	 * @param publisher : user jid (ie : my_user@domain/resource). Mandatory.
	 * @param password : Mandatory.
	 * @param options : Complementary values used for the connection to the server. Not mandatory.
	 */
    public void connect(String login, String password, HOptions options){
    	this.connect(login, password, options, null);
    }
    

	/**
	 * Disconnect the user from the current working session.
	 */
    @SuppressWarnings("unused")
	public void disconnect() {
		boolean shouldDisconnect = false;
		boolean connectInProgress = false;
		synchronized (this) {
			if (this.connectionStatus == ConnectionStatus.CONNECTED) {
				shouldDisconnect = true;
				// update connection status
				connectionStatus = ConnectionStatus.DISCONNECTING;
			} else if (this.connectionStatus == ConnectionStatus.CONNECTING) {
				connectInProgress = true;
			}
		}
		if (shouldDisconnect) {
			notifyStatus(ConnectionStatus.DISCONNECTING, ConnectionError.NO_ERROR, null);
			transportManager.disconnect();
		} else if (connectInProgress) {
			notifyStatus(ConnectionStatus.CONNECTING, ConnectionError.CONN_PROGRESS, ErrorMsg.disconnWhileConnecting);
		} else {
			notifyStatus(ConnectionStatus.DISCONNECTED, ConnectionError.NOT_CONNECTED, null);
			transportManager.disconnect();
		}

	}

	/**
	 * Status delegate receive all connection status events.
	 * @param statusDelegate the delegate to call when the status changes
	 */
    @SuppressWarnings("unused")
	public void onStatus(HStatusDelegate statusDelegate) {
		this.statusDelegate = statusDelegate;
	}

	/**
	 * @param messageDelegate Message delegate receive all incoming HMessage
	 */
    @SuppressWarnings("unused")
	public void onMessage(HMessageDelegate messageDelegate) {
		this.messageDelegate = messageDelegate;
	}

	/**
	 * Get current connection status
     * @return the current connection status
	 */
	public ConnectionStatus status() {
		return this.connectionStatus;
	}

	/**
	 * The hAPI sends the hMessage to the hserver which transfer it to the specified actor.
	 * The hserver will perform one of the following actions :
	 *  (1). If the actor is a channel (ie : #channelName@domain) the hserver will perform a publish operation of the provided hMessage to the channel and send an hMessage with hResult payload containing the published message and cmd name set with hsend to acknowledge publishing only if a timeout value has been provided.
	 *  (2). If the actor is either ‘session’ and payload type is ‘hCommand’ the server will handle it. In other cases, it will send an hMessage with a hResult error NOT_AUTHORIZED. Only if the timeout is provided.
	 *  (3). If the actor is a jid, hserver will relay the message to the relevant actor.
	 * @param message : The message to send. Mandatory.
	 * @param messageDelegate : If provided, called by the hAPI when the first message refering to current message arrive . Not mandatory.
	 */
	public void send(final HMessage message, final HMessageDelegate messageDelegate) {
		if (this.connectionStatus != ConnectionStatus.CONNECTED) {
			notifyResultError(message.getMsgid(), ResultStatus.NOT_CONNECTED, ErrorMsg.notConn, messageDelegate);
			return;
		}
		if (message == null) {
			notifyResultError(null, ResultStatus.MISSING_ATTR, ErrorMsg.nullMessage, messageDelegate);
			return;
		}
		if (message.getActor() == null) {
			notifyResultError(message.getMsgid(), ResultStatus.MISSING_ATTR, ErrorMsg.missingActor, messageDelegate);
			return;
		}
		message.setSent(new Date());
		message.setPublisher(transportOptions.getFullUrn());
		if (message.getTimeout() > 0) {
			// hAPI will do correlation. If no answer within the
			// timeout, a timeout error will be sent.
			if (messageDelegate != null) {
				message.setMsgid(UUID.randomUUID().toString());
				messagesDelegates.put(message.getMsgid(), messageDelegate);

                Timer timeoutTimer = new Timer();
				timeoutTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						notifyResultError(message.getMsgid(), ResultStatus.EXEC_TIMEOUT, ErrorMsg.timeout, null);
						messagesDelegates.remove(message.getMsgid());
					}
				}, message.getTimeout());
				timeoutHashtable.put(message.getMsgid(), timeoutTimer);
			}else{
				//when there is no callback, timeout has no sense. delete timeout.
				message.setTimeout(0);
			}
		}
		try {
			transportManager.sendObject(message);
		} catch (Exception e) {
			logger.error("message: ", e);
		}
		
	}

	/**
	 * Demands the server a subscription to the channel id. The hAPI performs a hMessage with a hCommand of type hsubscribe. 
	 * The server will check if not already subscribed and if authorized and subscribe him. 
	 * Nominal response : a hMessage with an hResult payload with status 0.
	 * @param actor : The channel id to subscribe to. (ie : #test@domain”). Mandatory.
	 * @param messageDelegate : A delegate notified when the result is sent by server. Mandatory.
     * @throws MissingAttrException raised if a mandatory attribute is not well provided
	 */
    @SuppressWarnings("unused")
	public void subscribe(String actor, HMessageDelegate messageDelegate) throws MissingAttrException {
		if(messageDelegate == null){
			throw new MissingAttrException("messageDelegate");
		}
		HMessage cmdMessage = buildCommand(actor, "hSubscribe",null, null, null);
		cmdMessage.setTimeout(options.getTimeout());
		send(cmdMessage, messageDelegate);
	}

	/**
	 * Demands the server an unsubscription to the channel id. 
	 * The hAPI checks the current publisher’s subscriptions and if he is subscribed performs a hCommand of type hunsubscribe.
	 * Nominal response : an hMessage with an hResult where the status 0.
	 * @param actor : The channel to unsubscribe from. Mandatory.
	 * @param messageDelegate : A delegate notified when the result is sent by server. Mandatory.
	 * @throws MissingAttrException  raised if a mandatory attribute is not well provided
	 */
    @SuppressWarnings("unused")
	public void unsubscribe(String actor, HMessageDelegate messageDelegate) throws MissingAttrException {
		if(messageDelegate == null){
			throw new MissingAttrException("messageDelegate");
		}
		JSONObject params = new JSONObject();
		try {
			params.put("channel", actor);
		} catch (JSONException e) {
			logger.info("Message : ",e);
		}
		HMessage cmdMessage = buildCommand("session", "hUnsubscribe", params, null, null);
		HCommand cmd = cmdMessage.getPayloadAsHCommand();
		send(cmdMessage, messageDelegate);
	}

	/**
	 * Demands the server a list of the publisher’s subscriptions.
	 * Nominal response : a hMessage with a hResult payload contains an array of channel id which are all active.
	 * @param messageDelegate : A delegate notified when the result is sent by server. Mandatory.
	 * @throws MissingAttrException raised if a mandatory attribute is not well provided
	 */
    @SuppressWarnings("unused")
	public void getSubscriptions(HMessageDelegate messageDelegate) throws MissingAttrException {
		if(messageDelegate == null){
			throw new MissingAttrException("messageDelegate");
		}
		HMessage cmdMessage = buildCommand("session", "hGetSubscriptions", null, null, null);
		cmdMessage.setTimeout(options.getTimeout());
		this.send(cmdMessage, messageDelegate);
	}
	
	/**
	 * Set a filter to be applied to upcoming messages at the session level 
	 * @param filter the filter to apply on the current session managed on the hnode side for this actor
     * @param messageDelegate a delegate notified when the command result is issued. Mandatory.
	 * @throws MissingAttrException raised if a mandatory attribute is not well provided
	 */
    @SuppressWarnings("unused")
	public void setFilter(HCondition filter, HMessageDelegate messageDelegate) throws MissingAttrException{
		if(messageDelegate == null){
			throw new MissingAttrException("messageDelegate");
		}
		HMessage cmdMessage = buildCommand("session", "hSetFilter", filter, null, null);
		this.filter = filter;
		cmdMessage.setTimeout(options.getTimeout());
		this.send(cmdMessage, messageDelegate);
	}

	/* Builder */

	/**
	 * Helper to create a hMessage. Payload type could be instance of JSONObject(HAlert, HAck, HCommand ...), JSONObject, JSONArray, String, Boolean, Number
	 * @param actor : The Actor for the hMessage. Mandatory.
	 * @param type : The type of the hMessage. Not mandatory.
	 * @param payload : The payload for the hMessage. Not mandatory.
	 * @param options : The options if any to use for the creation of the hMessage. Not mandatory.
	 * @throws MissingAttrException raised if a mandatory attribute is not well provided
     * @return a hMessage which can be used with the send method
	 */
	public HMessage buildMessage(String actor, String type, Object payload, HMessageOptions options) throws MissingAttrException {

		// check for required attributes
		if (actor == null || actor.length() <= 0) {
			throw new MissingAttrException("actor");
		}

		// build the message
		HMessage hmessage = new HMessage();

		hmessage.setActor(actor);
		hmessage.setType(type);
		if (options != null) {
			hmessage.setRef(options.getRef());
			hmessage.setPriority(options.getPriority());
			//override relevance if relevanceOffset is set.
			if (options.getRelevanceOffset() != null) {
				hmessage.setRelevance((new Date()).getTime() + options.getRelevanceOffset());
			}else{
				hmessage.setRelevance(options.getRelevance());
			}
			hmessage.setPersistent(options.getPersistent());
			hmessage.setLocation(options.getLocation());
			hmessage.setAuthor(options.getAuthor());
			hmessage.setHeaders(options.getHeaders());
			hmessage.setPublished(options.getPublished());
			hmessage.setTimeout(options.getTimeout());
		}
		if (transportOptions != null && transportOptions.getFullUrn() != null) {
			hmessage.setPublisher(transportOptions.getFullUrn());
		} else {
			hmessage.setPublisher(null);
		}
		hmessage.setPayload(payload);
		return hmessage;
	}

	/**
	 * Helper to create a hMessage with a hCommand payload.
	 * @param actor : The actor for the hMessage. Mandatory.
	 * @param cmd : The name of the command. Mandatory.
	 * @param params : Parameters of the command. Not mandatory.
	 * @param filter : The filter on the session.
	 * @param options : The options to use if any for the creation of the hMessage. Not mandatory.
	 * @return A hMessage with a hCommand payload.
	 * @throws MissingAttrException raised if a mandatory attribute is not well provided
	 */
	public HMessage buildCommand(String actor, String cmd, JSONObject params,HCondition filter, HMessageOptions options) throws MissingAttrException {
		// check for required attributes
		if (actor == null || actor.length() <= 0) {
			throw new MissingAttrException("actor");
		}

		// check for required attributes
		if (cmd == null || cmd.length() <= 0) {
			throw new MissingAttrException("cmd");
		}

		HCommand hcommand = new HCommand(cmd, params, filter);
		return buildMessage(actor, "hCommand", hcommand, options);
	}


    /**
     * Helper to create a hMessage with a hResult payload.
     * @param actor : The actor for the hMessage. Mandatory.
     * @param ref : The id of the message received, for correlation purpose. Mandatory.
     * @param status : Result status code. Mandatory.
     * @param result : The String result of a command.
     * @param options : The options to use if any for the creation of the hMessage. Not mandatory.
     * @return A hMessage with a hResult payload.
     * @throws MissingAttrException raised if a mandatory attribute is not well provided
     */
    public HMessage buildResult(String actor, String ref, ResultStatus status, String result, HMessageOptions options) throws MissingAttrException {
        return internalBuildResult(actor, ref, status, result, options);
    }

    /**
     * Helper to create a hMessage with a hResult payload.
     * @param actor : The actor for the hMessage. Mandatory.
     * @param ref : The id of the message received, for correlation purpose. Mandatory.
     * @param status : Result status code. Mandatory.
     * @param result : The JSONObject result of a command.
     * @param options : The options to use if any for the creation of the hMessage. Not mandatory.
     * @return A hMessage with a hResult payload.
     * @throws MissingAttrException raised if a mandatory attribute is not well provided
     */
    public HMessage buildResult(String actor, String ref, ResultStatus status, JSONObject result, HMessageOptions options) throws MissingAttrException {
        return internalBuildResult(actor, ref, status, result, options);
    }

    /**
     * Helper to create a hMessage with a hResult payload.
     * @param actor : The actor for the hMessage. Mandatory.
     * @param ref : The id of the message received, for correlation purpose. Mandatory.
     * @param status : Result status code. Mandatory.
     * @param result : The JSONArray result of a command.
     * @param options : The options to use if any for the creation of the hMessage. Not mandatory.
     * @return A hMessage with a hResult payload.
     * @throws MissingAttrException raised if a mandatory attribute is not well provided
     */
    public HMessage buildResult(String actor, String ref, ResultStatus status, JSONArray result, HMessageOptions options) throws MissingAttrException {
        return internalBuildResult(actor, ref, status, result, options);
    }

    /**
     * Helper to create a hMessage with a hResult payload.
     * @param actor : The actor for the hMessage. Mandatory.
     * @param ref : The id of the message received, for correlation purpose. Mandatory.
     * @param status : Result status code. Mandatory.
     * @param result : The double result of a command.
     * @param options : The options to use if any for the creation of the hMessage. Not mandatory.
     * @return A hMessage with a hResult payload.
     * @throws MissingAttrException raised if a mandatory attribute is not well provided
     */
    public HMessage buildResult(String actor, String ref, ResultStatus status, double result, HMessageOptions options) throws MissingAttrException {
        return internalBuildResult(actor, ref, status, result, options);
    }

    /**
     * Helper to create a hMessage with a hResult payload.
     * @param actor : The actor for the hMessage. Mandatory.
     * @param ref : The id of the message received, for correlation purpose. Mandatory.
     * @param status : Result status code. Mandatory.
     * @param result : The boolean result of a command.
     * @param options : The options to use if any for the creation of the hMessage. Not mandatory.
     * @return A hMessage with a hResult payload.
     * @throws MissingAttrException raised if a mandatory attribute is not well provided
     */
     public HMessage buildResult(String actor, String ref, ResultStatus status, boolean result, HMessageOptions options) throws MissingAttrException {
        return internalBuildResult(actor, ref, status, result, options);
    }

        /**
       * Helper to create a hMessage with a hResult payload.
       * @param actor : The actor for the hMessage. Mandatory.
       * @param ref : The id of the message received, for correlation purpose. Mandatory.
       * @param status : Result status code. Mandatory.
       * @param result : The result of a command. Possible types: JSONObject, JSONArray, String, Boolean, Number. Not mandatory.
       * @param options : The options to use if any for the creation of the hMessage. Not mandatory.
       * @return A hMessage with a hResult payload.
       * @throws MissingAttrException raised if a mandatory attribute is not well provided
       */
	private HMessage internalBuildResult(String actor, String ref, ResultStatus status, Object result, HMessageOptions options) throws MissingAttrException {
		// check for required attributes
		if (actor == null || actor.length() <= 0) {
			throw new MissingAttrException("actor");
		}
		// check for required attributes
		if (ref == null || ref.length() <= 0) {
			throw new MissingAttrException("ref");
		}

		// check for required attributes
		if (status == null) {
			throw new MissingAttrException("status");
		}

		HResult hresult = new HResult();
		hresult.setResult(result);
		hresult.setStatus(status);
		if(options == null){
			options = new HMessageOptions();
		}
		options.setRef(ref);
		return buildMessage(actor, "hResult", hresult, options);
	}

	/* HTransportCallback functions */

	/**
	 * fill htransport, randomly pick an endpoint from availables endpoints. By default it uses options server host to fill serverhost field and as fallback jid domain
	 * @param login : login
	 * @param password the password to open the a session with the hnode
	 * @param options options to open a session
	 */
	private void fillHTransportOptions(String login, String password, HOptions options, JSONObject context) {


		this.transportOptions.setLogin(login);
		this.transportOptions.setPassword(password);
		this.transportOptions.setContext(context);
		this.transportOptions.setAuthCB(options.getAuthCB());

		// by default we user server host rather than publish host if defined

		// for endpoints, pick one randomly and fill htransport options
		if (options.getEndpoints().length() > 0) {
			int endpointIndex = HUtil.pickIndex(options.getEndpoints());
			try {
				String endpoint = options.getEndpoints().getString(endpointIndex);
				this.transportOptions.setEndpoint(endpoint);
			} catch (JSONException e) {
				logger.error("message: ", e);
			}
			
		} else {
			this.transportOptions.setEndpoint(null);
		}
	}

	/**
	 * change current status and notify delegate through callback
	 * @param status : connection status
	 * @param error : error code
	 * @param errorMsg : a low level description of the error
	 */
	private void notifyStatus(ConnectionStatus status, ConnectionError error, String errorMsg) {
		try {
			connectionStatus = status;
			if (this.statusDelegate != null) {
				// create structure
				final HStatus hstatus = new HStatus();
				hstatus.setStatus(status);
				hstatus.setErrorCode(error);
				hstatus.setErrorMsg(errorMsg);

				// return status asynchronously
				(new Thread(new Runnable() {
					public void run() {
						try {
							statusDelegate.onStatus(hstatus);
						} catch (Exception e) {
							logger.error("message: ", e);
						}
					}
				})).start();
			}
		} catch (Exception e) {
			logger.error("message", e);
		}
	}

    private class MyRunnable implements Runnable {
        public HMessageDelegate delegate2Use;
        public HMessage message;
        public void run() {
            try {
                delegate2Use.onMessage(message);
            } catch (Exception e) {
                logger.error("message: ", e);
            }
        }
    }

        /**
         * Notify message delegate of an incoming hmessage. If the callback is not set, it will call onMessage.
         * If the callback is set in the service functions, it will call the callback function instead of onMessage
         * @param message the received message
         */
        private void notifyMessage(final HMessage message, HMessageDelegate messageDelegate) {
            MyRunnable arun = new MyRunnable();
            String apiRef = HUtil.getApiRef(message.getRef());
            // 1 we search the delegate with the ref if any
            if (!this.messagesDelegates.isEmpty() && message.getRef() != null && this.messagesDelegates.containsKey(HUtil.getApiRef(message.getRef()))) {
                if (this.timeoutHashtable.containsKey(apiRef)) {
                    Timer timeout = timeoutHashtable.get(apiRef);
                    timeoutHashtable.remove(apiRef);
                    if (timeout != null) {
                        timeout.cancel();
                    }
                }
                arun.delegate2Use = this.messagesDelegates.get(HUtil.getApiRef(message.getRef()));
                messagesDelegates.remove(apiRef);
            }
            // 2 - if the ref can not provide a delegate, we try the parameter sent
            else if (messageDelegate != null) {
            	arun.delegate2Use = messageDelegate;
            } else {
                // in other cases we try the default delegate message
                arun.delegate2Use = this.messageDelegate;
            }
            try {
                if (arun.delegate2Use != null) {
                    // return message asynchronously
                    arun.message = message;
                    new Thread(arun).start();
                }
            } catch (Exception e) {
                logger.error("message: ", e);
            }

        }

	/**
	 * Helper function to return a hmessage with hresult error
     * @param ref used to update the hMessage sent as a result to the client
     * @param resultstatus the status of the error
     * @param errorMsg the error messsage
	 * @param messageDelegate the delegate used to notify the error
	 */
	private void notifyResultError(String ref, ResultStatus resultstatus, String errorMsg, HMessageDelegate messageDelegate) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("errorMsg", errorMsg);
		} catch (JSONException e) {
			logger.error("message: ", e);
		}
		HResult hresult = new HResult();
		hresult.setResult(obj);
		try {
			hresult.setStatus(resultstatus);
		} catch (MissingAttrException e) {
			logger.error("message: ", e);
		}
		HMessage message = new HMessage();
		message.setRef(ref);
		message.setType("hResult");
		message.setPayload(hresult);

		this.notifyMessage(message, messageDelegate);
	}

	/**
	 * Class used to get callbacks from transport layer.
	 */
	private class TransportDelegate implements HTransportDelegate {

		/**
		 * see HTransportDelegate for more informations
		 */
		public void onStatus(ConnectionStatus status, ConnectionError error, String errorMsg) {
			notifyStatus(status, error, errorMsg);
		}

		/**
		 * see HTransportDelegate for more information
		 */
		public void onData(String type, JSONObject jsonData) {
			try {
				if (type.equalsIgnoreCase("hmessage")) {
					HMessage message = new HMessage(jsonData);
					notifyMessage(message, null);
				}

			} catch (Exception e) {
				logger.error("message: ", e);
			}
		}
	}

}
