/*
 * Copyright (c) Novedia Group 2012.
 *
 *     This file is part of Hubiquitus.
 *
 *     Hubiquitus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Hubiquitus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Hubiquitus.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.hubiquitus.hapi.client;

import java.util.List;
import java.util.Random;

import org.hubiquitus.hapi.hStructures.ConnectionError;
import org.hubiquitus.hapi.hStructures.ConnectionStatus;
import org.hubiquitus.hapi.hStructures.HAck;
import org.hubiquitus.hapi.hStructures.HAckValue;
import org.hubiquitus.hapi.hStructures.HAlert;
import org.hubiquitus.hapi.hStructures.HCommand;
import org.hubiquitus.hapi.hStructures.HConv;
import org.hubiquitus.hapi.hStructures.HJsonObj;
import org.hubiquitus.hapi.hStructures.HMeasure;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.hubiquitus.hapi.hStructures.HMessageOptions;
import org.hubiquitus.hapi.hStructures.HOptions;
import org.hubiquitus.hapi.hStructures.HResult;
import org.hubiquitus.hapi.hStructures.HStatus;
import org.hubiquitus.hapi.hStructures.ResultStatus;
import org.hubiquitus.hapi.structures.JabberID;
import org.hubiquitus.hapi.transport.HTransport;
import org.hubiquitus.hapi.transport.HTransportCallback;
import org.hubiquitus.hapi.transport.HTransportOptions;
import org.hubiquitus.hapi.transport.socketio.HTransportSocketio;
import org.hubiquitus.hapi.transport.xmpp.HTransportXMPP;
import org.hubiquitus.hapi.util.HJsonDictionnary;
import org.hubiquitus.hapi.util.HUtil;
import org.json.JSONObject;


/**
 * @version 0.3
 * Hubiquitus client, public api
 */

public class HClient {
	
	private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED; /* only connecting, connected, diconnecting, disconnected */
	@SuppressWarnings("unused")
	private HOptions options = null;
	private HTransportOptions transportOptions = null;
	private HDelegate callback = null;
	private HTransport transport;
	
	private TransportCallback transportCallback = new TransportCallback();
	
	public HClient() {
		transportOptions = new HTransportOptions();
	}

	/**
	 * Connect to server
	 * @param publisher - user jid (ie : my_user@domain/resource)
	 * @param password
	 * @param callback - client callback to get api notifications
	 * @param options
	 */
	public void connect(String publisher, String password, HDelegate callback, HOptions options) {
		boolean shouldConnect = false;
		boolean connInProgress = false;
		boolean disconInProgress = false;

		//synchronize connection status updates to make sure, we have one connect at a time
		synchronized (this) {
			if (this.connectionStatus == ConnectionStatus.DISCONNECTED) {
				shouldConnect = true;
				
				//update connection status
				connectionStatus = ConnectionStatus.CONNECTING;
			} else if(this.connectionStatus == ConnectionStatus.CONNECTING) {
				connInProgress = true;
			} else if(this.connectionStatus == ConnectionStatus.DISCONNECTING) {
				disconInProgress = true;
			}
		}
		
		if (shouldConnect) { //if not connected, then connect
			
			this.callback = callback;
			
			//notify connection
			this.updateStatus(ConnectionStatus.CONNECTING, ConnectionError.NO_ERROR, null);
			
			//fill HTransportOptions
			try {
				this.fillHTransportOptions(publisher, password, options);
			} catch (Exception e) { 
				//stop connecting if filling error
				this.updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.JID_MALFORMAT, e.getMessage());
				return;
			}
			
			//choose transport layer
			if(options.getTransport().equals("socketio")) {
				/*if (this.transport != null) { //check if other transport mode connect
					this.transport.disconnect();
				}*/
				if (this.transport == null || (this.transport.getClass() != HTransportSocketio.class)) {
					this.transport = new HTransportSocketio();
				}
				this.transport.connect(transportCallback, this.transportOptions);
			} else {
				/*if (this.transport != null) { //check if other transport mode connect
					this.transport.disconnect();
				}*/
				this.transport = new HTransportXMPP();
				this.transport.connect(transportCallback, this.transportOptions);
			}
		} else {
			if (connInProgress) {
				updateStatus(ConnectionStatus.CONNECTING, ConnectionError.CONN_PROGRESS, null);
			} else if (disconInProgress) {
				//updateStatus(ConnectionStatus.DISCONNECTING, ConnectionError.ALREADY_CONNECTED, null);
			} else {
				updateStatus(ConnectionStatus.CONNECTED, ConnectionError.ALREADY_CONNECTED, null);
			}	
		}
	}

	public void disconnect() {
		boolean shouldDisconnect = false;
		boolean connectInProgress = false;
		synchronized (this) {
			if (this.connectionStatus == ConnectionStatus.CONNECTED) {
				shouldDisconnect = true;
				//update connection status
				connectionStatus = ConnectionStatus.DISCONNECTING;
			} else if(this.connectionStatus == ConnectionStatus.CONNECTING) {
				connectInProgress = true;
			}
		}
		
		if(shouldDisconnect) {
			updateStatus(ConnectionStatus.DISCONNECTING, ConnectionError.NO_ERROR, null);
			transport.disconnect();
		} else if (connectInProgress) {
			updateStatus(ConnectionStatus.CONNECTING, ConnectionError.CONN_PROGRESS, "Can't disconnect while a connection is in progress");
		} else {
			updateStatus(ConnectionStatus.DISCONNECTED, ConnectionError.NOT_CONNECTED, null);
			//remove callback
			this.callback = null;
		}
		
		
	}

	/**
	 * Used to perform a command on an hubiquitus component : a hserver or a hubot.
	 * @param cmd - name of the command
	 * @return reqid
	 */
	public String command(HCommand cmd) {
		String reqid = null;
		if(this.connectionStatus == ConnectionStatus.CONNECTED) {
			if(cmd == null) {
				cmd = new HCommand();
			}
			reqid = cmd.getReqid();
			if(reqid == null) {
				Random rand = new Random();
				reqid = "javaCmd:" + rand.nextInt();
				cmd.setReqid(reqid);
			}
			if(cmd.getSender() == null) {
				cmd.setSender(transportOptions.getJid().getFullJID());
			}
			if(cmd.getTransient() == null) {
				cmd.setTransient(true);
			}
			
			if(cmd.getEntity() != null) {
				transport.sendObject(cmd.toJSON());
			} else {
				final HCommand command = cmd;
				(new Thread(new Runnable() {
					public void run() {
						HJsonDictionnary obj = new HJsonDictionnary(); 
						obj.put("errorMsg", "Entity not found");
						HResult hresult = new HResult(command.getReqid(),command.getCmd(),obj);
						hresult.setStatus(ResultStatus.MISSING_ATTR);
						callback.hDelegate("hresult", hresult);
					}
				})).start();
			}
		} else if(callback != null){
			(new Thread(new Runnable() {
				public void run() {
					HStatus hstatus = new HStatus(connectionStatus, ConnectionError.NOT_CONNECTED, "Can not send hCommand. Not connected");
					callback.hDelegate("hstatus", hstatus);
				}
			})).start();
		}
		return reqid;
	}
	
	/**
	 * Demands the server a subscription to the channel id.
	 * The hAPI performs a hCommand of type hsubscribe.
	 * The server will check if not already subscribed and if authorized and subscribe him.
	 * @param chid - channel id
	 * @return request id
	 */
	public String subscribe(String chid) {
		HJsonDictionnary params = new HJsonDictionnary();
		params.put("chid", chid);
		HCommand cmd = new HCommand(transportOptions.getHserverService(), "hsubscribe", params);
		return this.command(cmd);
	}
	
	/**
	 * Demands the server an unsubscription to the channel id.
	 * The hAPI checks the current publisher’s subscriptions and if he is subscribed performs a hCommand of type hunsubscribe.
	 * @param chid - channel id
	 * @return request id
	 */
	public String unsubscribe(String chid) {
		HJsonDictionnary params = new HJsonDictionnary();
		params.put("chid", chid);
		HCommand cmd = new HCommand(transportOptions.getHserverService(), "hunsubscribe", params);
		return this.command(cmd);
	}
	
	/**
	 * Perform a publish operation of the provided hMessage to a channel.
	 * @param message
	 * @return reqid
	 */
	public String publish(HMessage message) {
		//fill mandatory fields
		String msgid = message.getMsgid();
		if(msgid == null) {
			Random rand = new Random();
			msgid = "javaCmd:" + rand.nextInt();
			message.setMsgid(msgid);
		}
		
		String convid = message.getConvid();
		if(convid == null) {
			message.setConvid(msgid);
		}
				
		message.setConvid(convid);
		HCommand cmd = new HCommand(transportOptions.getHserverService(), "hpublish", message);
		return this.command(cmd);				
	}
	/**
	 * Demands the hserver a list of the last messages saved for a dedicated channel.
	 * The publisher must be in the channel’s participants list.
	 * 
	 * Nominal response : an hCallback with an hResult will be performed when the result is available. 
	 * If the hResult had status 0, the user should expect to receive n calls to hCallback of type
	 * hMessage where n is equal to the number of messages retrieved with nbLastMsg as an upper limit.
	 * @warning HResult result type will be a JSonArray if successful
	 * @param chid - channel id
	 * @param nbLastMsg
	 * @return request id
	 */
	public String getLastMessages(String chid, int nbLastMsg) {
		HJsonDictionnary params = new HJsonDictionnary();
		params.put("chid", chid);
		if(nbLastMsg > 0) {
			params.put("nbLastMsg", nbLastMsg);
		}
		HCommand cmd = new HCommand(transportOptions.getHserverService(), "hgetlastmessages", params);
		return this.command(cmd);
	}
	
	/**
	 * @see getLastMessages(String chid, int nbLastMsg) 
	 * @param chid - channel id
	 * @return request id 
	 */
	public String getLastMessages(String chid) {
		return this.getLastMessages(chid,-1);
	}
	
	/**
	 * Demands the server a list of the publisher’s subscriptions.	
	 * 
	 * Nominal response : a hCallback with a hResult will be performed when the result is available with an array of channel id.
	 * @return request id
	 */
	public String getSubscriptions() {
		HCommand cmd = new HCommand(transportOptions.getHserverService(), "hgetsubscriptions", null);
		return this.command(cmd);
	}
	
	/* Builder */
	
	/**
	 * Helper to create hmessage
	 * @see HMessage
	 * @param chid - channel id
	 * @param type
	 * @param payload
	 * @param options
	 * @return hMessage
	 */
	public HMessage buildMessage(String chid, String type, HJsonObj payload, HMessageOptions options) {
		HMessage hmessage = new HMessage();
		if(this.connectionStatus == ConnectionStatus.CONNECTED) {
			hmessage.setChid(chid);
			hmessage.setConvid(options.getConvid());
			hmessage.setType(type);
			if(options != null) {
				hmessage.setPriority(options.getPriority());
				hmessage.setRelevance(options.getRelevance());
				hmessage.setTransient(options.getTransient());
				hmessage.setLocation(options.getLocation());
				hmessage.setAuthor(options.getAuthor());
				hmessage.setHeaders(options.getHeaders());
			}
			if(transportOptions != null && transportOptions.getJid() != null) {
				hmessage.setPublisher(transportOptions.getJid().getBareJID());
			} else {
				hmessage.setPublisher(null);
			}		
			hmessage.setPayload(payload);
		} else {
			if(callback != null) {
				(new Thread(new Runnable() {
					public void run() {
						HStatus hstatus = new HStatus(connectionStatus, ConnectionError.NOT_CONNECTED, "Can not build a message. Not connected");
						callback.hDelegate("hstatus", hstatus);
					}
				})).start();
			}
		}
		return hmessage;
	}
	
	/**
	 * Helper to create hconv
	 * @param chid - channel id
	 * @param topic
	 * @param participants
	 * @param options
	 * @return hmessage
	 */
	public HMessage buildConv(String chid, String topic, List<String> participants, HMessageOptions options) {
		HMessage hmessage = new HMessage();
		if(this.connectionStatus == ConnectionStatus.CONNECTED) {
			HConv hconv = new HConv();
			hconv.setTopic(topic);
			hconv.setParticipants(participants);
			hmessage = buildMessage(chid, "hconv", hconv, options);
		} else {
			if(callback != null) {
				(new Thread(new Runnable() {
					public void run() {
						HStatus hstatus = new HStatus(connectionStatus, ConnectionError.NOT_CONNECTED, "Can not build a message. Not connected");
						callback.hDelegate("hstatus", hstatus);
					}
				})).start();
			}
		}
		return hmessage;
	}
	
	/**
	 * Helper to create hack
	 * @param chid - channel id
	 * @param ackid
	 * @param ack
	 * @param options
	 * @return hmessage
	 */
	public HMessage buildAck(String chid, String ackid,HAckValue ack, HMessageOptions options) {
		HMessage hmessage = new HMessage();
		if(this.connectionStatus == ConnectionStatus.CONNECTED) {
			HAck hack = new HAck();
			hack.setAckid(ackid);
			hack.setAck(ack);
			hmessage = buildMessage(chid, "hack", hack, options);
		} else {
			if(callback != null) {
				(new Thread(new Runnable() {
					public void run() {
						HStatus hstatus = new HStatus(connectionStatus, ConnectionError.NOT_CONNECTED, "Can not build a message. Not connected");
						callback.hDelegate("hstatus", hstatus);
					}
				})).start();
			}
		}
		return hmessage;
	}
	
	/**
	 * Helper to create halert
	 * @param chid - channel id
	 * @param alert
	 * @param options
	 * @return hmessage
	 */
	public HMessage buildAlert(String chid, String alert, HMessageOptions options) {
		HMessage hmessage = new HMessage();
		if(this.connectionStatus == ConnectionStatus.CONNECTED) {
			HAlert halert = new HAlert();
			halert.setAlert(alert);
			hmessage = buildMessage(chid, "halert", halert, options);
		} else {
			if(callback != null) {
				(new Thread(new Runnable() {
					public void run() {
						HStatus hstatus = new HStatus(connectionStatus, ConnectionError.NOT_CONNECTED, "Can not build a message. Not connected");
						callback.hDelegate("hstatus", hstatus);
					}
				})).start();
			}
		}
		return hmessage;
	}
	
	/**
	 * Helper to create hmeasure
	 * @param chid - channel id
	 * @param value
	 * @param unit
	 * @param options
	 * @return hmessage
	 */
	public HMessage buildMeasure(String chid, String value, String unit, HMessageOptions options) {
		HMessage hmessage = new HMessage();
		if(this.connectionStatus == ConnectionStatus.CONNECTED) {
			HMeasure hmeasure = new HMeasure();
			hmeasure.setValue(value);
			hmeasure.setUnit(unit);
			hmessage = buildMessage(chid, "hmeasure", hmeasure, options);
		} else {
			if(callback != null) {
				(new Thread(new Runnable() {
					public void run() {
						HStatus hstatus = new HStatus(connectionStatus, ConnectionError.NOT_CONNECTED, "Can not build a message. Not connected");
						callback.hDelegate("hstatus", hstatus);
					}
				})).start();
			}
		}
		return hmessage;
	}
	/* HTransportCallback functions */

	/**
	 * @internal
	 * fill htransport, randomly pick an endpoint from availables endpoints.
	 * By default it uses options server host to fill serverhost field and as fallback jid domain
	 * @param publisher - publisher as jid format (my_user@serverhost.com/my_resource)
	 * @param password
	 * @param options 
	 * @throws Exception - in case jid is malformatted, it throws an exception
	 */
	private void fillHTransportOptions(String publisher, String password, HOptions options) throws Exception {
		JabberID jid = new JabberID(publisher);
		
		this.transportOptions.setJid(jid);
		this.transportOptions.setPassword(password);
		this.transportOptions.setHserver(options.getHserver());
		
		//by default we user server host rather than publish host if defined
		if (options.getServerHost() != null ) {
			this.transportOptions.setServerHost(options.getServerHost());
		} else { 
			this.transportOptions.setServerHost(jid.getDomain());
		}
		
		this.transportOptions.setServerPort(options.getServerPort());
		//for endpoints, pick one randomly and fill htransport options
		if (options.getEndpoints().size() > 0) {
			int endpointIndex = HUtil.pickIndex(options.getEndpoints()); 
			String endpoint = options.getEndpoints().get(endpointIndex);
			
			this.transportOptions.setEndpointHost(HUtil.getHost(endpoint));
			this.transportOptions.setEndpointPort(HUtil.getPort(endpoint));
			this.transportOptions.setEndpointPath(HUtil.getPath(endpoint));
		} else {
			this.transportOptions.setEndpointHost(null);
			this.transportOptions.setEndpointPort(0);
			this.transportOptions.setEndpointPath(null);
		}
	}
	
	/**
	 * @internal
	 * change current status and notify delegate through callback
	 * @param status - connection status
	 * @param error - error code
	 * @param errorMsg - a low level description of the error
	 */
	private void updateStatus(ConnectionStatus status, ConnectionError error, String errorMsg) {
		if (callback != null) {
			connectionStatus = status;
			//create structure 
			HStatus hstatus = new HStatus();
			hstatus.setStatus(status);
			hstatus.setErrorCode(error);
			hstatus.setErrorMsg(errorMsg);
			
			try {
				callback.hDelegate("hstatus", hstatus);
			} catch(Exception e) {
			}
			
			if(status == ConnectionStatus.DISCONNECTED) {
				callback = null;
			}
		} else {
			System.out.println("Error : " + this.getClass().getName() + " requires a callback");
		}
	}
	

	/**
	 * @internal
	 * Class used to get callbacks from transport layer.
	 */
	private class TransportCallback implements HTransportCallback {

		/**
		 * @internal
		 * see HTransportCallback for more informations
		 */
		public void connectionCallback(ConnectionStatus status,
				ConnectionError error, String errorMsg) {
			updateStatus(status, error, errorMsg);
		}

		/**
		 * @internal
		 * see HTransportCallback for more information
		 */
		@Override
		public void dataCallback(String type, JSONObject jsonData) {
			try {
				if(type.equalsIgnoreCase("hresult")) {
					callback.hDelegate(type, new HResult(jsonData));
				} else if (type.equalsIgnoreCase("hmessage")) {
					callback.hDelegate(type, new HMessage(jsonData));
				} else {
					callback.hDelegate(type, new HJsonDictionnary(jsonData));
				}
			} catch (Exception e) {
				System.out.println("erreur datacallBack");
			}
		}
	}

	
}
