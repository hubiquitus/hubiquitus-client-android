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

package org.hubiquitus.hapi.hStructures;

import java.util.Date;

import org.hubiquitus.hapi.exceptions.MissingAttrException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 0.6 
 * hAPI Command. For more info, see Hubiquitus reference
 */

public class HMessage extends JSONObject {
	
	final Logger logger = LoggerFactory.getLogger(HMessage.class);

	public HMessage() {
		super();
	}

	public HMessage(JSONObject jsonObj) throws JSONException {
		super(jsonObj.toString());
	}

	/* Getters & Setters */

	/**
	 * Mandatory. Filled by the hApi.
	 * @return message id. NULL if undefined
	 */
	public String getMsgid() {
		String msgid;
		try {
			msgid = this.getString("msgid");
		} catch (Exception e) {
			msgid = null;
		}
		return msgid;
	}

	public void setMsgid(String msgid){
		try {
			if (msgid == null || msgid.length()<=0) {
				this.remove("msgid");
			} else {
				this.put("msgid", msgid);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * Mandatory The unique ID of the channel through which the message is published.
	 * The JID through which the message is published. The JID can be that of a channel (beginning with #) or a user.
	 * A special actor called ‘session’ indicates that the HServer should handle the hMessage.
	 * @return actor. NULL if undefined
	 */
	public String getActor() {
		String actor;
		try {
			actor = this.getString("actor");
		} catch (Exception e) {
			actor = null;
		}
		return actor;
	}

	public void setActor(String actor) throws MissingAttrException {
		try {
			if (actor == null || actor.length()<=0) {
				throw new MissingAttrException("actor");
			} else {
				this.put("actor", actor);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * Mandatory. Filled by the hApi if empty.
	 * @return conversation id. NULL if undefined
	 */
	public String getConvid() {
		String convid;
		try {
			convid = this.getString("convid");
		} catch (Exception e) {
			convid = null;
		}
		return convid;
	}

	public void setConvid(String convid) {
		try {
			if (convid == null || convid.length()<=0) {
				this.remove("convid");
			} else {
				this.put("convid", convid);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return reference to another hMessage msgid. NULL if undefined.
	 */
	public String getRef() {
		String ref;
		try {
			ref = this.getString("ref");
		} catch (Exception e) {
			ref = null;
		}
		return ref;
	}

	public void setRef(String ref) {
		try {
			if (ref == null) {
				this.remove("ref");
			} else {
				this.put("ref", ref);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}

	}

	/**
	 * @return type of the message payload. NULL if undefined
	 */
	public String getType() {
		String type;
		try {
			type = this.getString("type");
		} catch (Exception e) {
			type = null;
		}
		return type;
	}

	public void setType(String type) {
		try {
			if (type == null) {
				this.remove("type");
			} else {
				this.put("type", type);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return Priority.If UNDEFINED, priority lower to 0.
	 */
	public HMessagePriority getPriority() {
		HMessagePriority priority;
		try {
			int priorityInt = this.getInt("priority");
			if (priorityInt < 0 || priorityInt > 5) {
				priority = null;
			} else {
				priority = HMessagePriority.constant(priorityInt);
			}
		} catch (Exception e1) {
			priority = null;
		}
		return priority;
	}

	public void setPriority(HMessagePriority priority) {
		try {
			if (priority == null) {
				this.remove("priority");
			} else {
				this.put("priority", priority.value());
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * Date until which the message is considered as relevant.
	 * @return relevance. NULL if undefined
	 */
	public Date getRelevanceAsDate() {
		Date relevance;
		try {
			relevance = new Date(this.getLong("relevance"));
		} catch (Exception e) {
			relevance = null;
		}
		return relevance;
	}
	
	public long getRelevance() {
		long relevance;
		try {
			relevance = this.getLong("relevance");
		} catch (Exception e) {
			relevance = 0;
		}
		return relevance;
	}

	public void setRelevance(Date relevance) {
		try {
			if (relevance == null) {
				this.remove("relevance");
			} else {
				this.put("relevance", relevance.getTime());
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public void setRelevance(long relevance) {
		try {
			if (relevance == 0) {
				this.remove("relevance");
			} else {
				this.put("relevance", relevance);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return persist message or not. NULL if undefined
	 */
	public Boolean getPersistent() {
		Boolean persistent;
		try {
			persistent = this.getBoolean("persistent");
		} catch (JSONException e) {
			persistent = null;
		}
		return persistent;
	}

	public void setPersistent(Boolean persistent) {
		try {
			if (persistent == null) {
				this.remove("persistent");
			} else {
				this.put("persistent", persistent);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * The geographical location to which the message refer.
	 * @return location. NULL if undefined
	 */
	public HLocation getLocation() {
		HLocation location;
		try {
			if(this.getJSONObject("location").length() > 0){
				location = new HLocation(this.getJSONObject("location"));
			}else{
				location = new HLocation();
			}
		} catch (Exception e) {
			location = null;
		}
		return location;
	}

	public void setLocation(HLocation location) {
		try {
			if (location == null) {
				this.remove("location");
			} else {
				this.put("location", location);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return author of this message. NULL if undefined
	 */
	public String getAuthor() {
		String author;
		try {
			author = this.getString("author");
		} catch (Exception e) {
			author = null;
		}
		return author;
	}

	public void setAuthor(String author) {
		try {
			if (author == null) {
				this.remove("author");
			} else {
				this.put("author", author);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return publisher of this message. NULL if undefined
	 */
	public String getPublisher() {
		String publisher;
		try {
			publisher = this.getString("publisher");
		} catch (Exception e) {
			publisher = null;
		}
		return publisher;
	}

	public void setPublisher(String publisher){
		try {
			if (publisher == null || publisher.length()<=0) {
				this.remove("publisher");
			} else {
				this.put("publisher", publisher);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return published. NULL if undefined
	 */
	public Date getPublishedAsDate() {
		Date published;
		try {
			published = new Date(this.getLong("published"));
		} catch (JSONException e) {
			published = null;
		}
		return published;
	}
	
	public long getPublished() {
		long published;
		try {
			published = this.getLong("published");
		} catch (JSONException e) {
			published = 0;
		}
		return published;
	}

	public void setPublished(Date published) {
		try {
			if (published == null) {
				this.remove("published");
			} else {
				this.put("published", published.getTime());
			}
		} catch (JSONException e) {
			logger.warn("message: " , e);
		}
	}
	
	public void setPublished(long published) {
		try {
			if (published == 0) {
				this.remove("published");
			} else {
				this.put("published", published);
			}
		} catch (JSONException e) {
			logger.warn("message: " , e);
		}
	}

	/**
	 * The list of headers attached to this message.
	 * @return Headers. NULL if undefined
	 */
	public JSONObject getHeaders() {
		// HJsonDictionnary headers = new HJsonDictionnary();
		JSONObject headers = null;
		try {
			headers = this.getJSONObject("headers");
		} catch (JSONException e) {
			headers = null;
		}
		return headers;
	}

	public void setHeaders(JSONObject headers) {
		try {
			if (headers == null) {
				this.remove("headers");
			} else {
				this.put("headers", headers);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * When we don't know the type of payload. It will return an object. 
	 * @return payload reference. NULL if undefined
	 */
	public Object getPayload() {
		Object payload;
		try {
			payload = this.get("payload");
		} catch (JSONException e) {
			payload = null;
		}
		return payload;
	}

	/**
	 * if payload type is JSONObject
	 * @return payload reference. NULL if undefined
	 */

	public JSONObject getPayloadAsJSONObject() {
		JSONObject payload;
		try {
			payload = this.getJSONObject("payload");
		} catch (JSONException e) {
			payload = null;
		}
		return payload;
	}

	/**
	 * if payload type is JSONArray
	 * @return payload reference. NULL if undefined
	 */
	public JSONArray getPayloadAsJSONArray() {
		JSONArray payload;
		try {
			payload = this.getJSONArray("payload");
		} catch (JSONException e) {
			payload = null;
		}
		return payload;
	}

	/**
	 * if payload type is String
	 * @return payload reference. NULL if undefined
	 */
	public String getPayloadAsString() {
		String payload;
		try {
			payload = this.getString("payload");
		} catch (JSONException e) {
			payload = null;
		}
		return payload;
	}

	/**
	 * if payload type is Boolean
	 * @return payload reference. Null if undefined
	 */
	public Boolean getPayloadAsBoolean() {
		Boolean payload;
		try {
			payload = this.getBoolean("payload");
		} catch (JSONException e) {
			payload = null;
		}
		return payload;
	}

	/**
	 * if payload type is Integer
	 * @return payload reference. Null if undefined.
	 */
	public Integer getPayloadAsInt() {
		Integer payload;
		try {
			payload = this.getInt("payload");
		} catch (JSONException e) {
			payload = null;
		}
		return payload;
	}

	/**
	 * if payload type is Double
	 * @return payload reference, Null if undefined.
	 */
	public Double getPayloadAsDouble() {
		Double payload;
		try {
			payload = this.getDouble("payload");
		} catch (JSONException e) {
			payload = null;
		}
		return payload;
	}

	/**
	 * if payload is HResult, if not return null.
	 * @return HResult copy. Null if undefined.
	 */
	public HResult getPayloadAsHResult() {
		try {
			if (this.getType().toLowerCase().equalsIgnoreCase("hresult")) {
				HResult hresult = new HResult(this.getJSONObject("payload"));
				return hresult;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * if payload is HCommand, if not return null.
	 * @return HCommand copy. Null if undefined.
	 */
	public HCommand getPayloadAsHCommand() {
		try {
			if (this.getType().toLowerCase().equalsIgnoreCase("hcommand")) {
				HCommand hcommand = new HCommand(this.getJSONObject("payload"));
				return hcommand;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Payload type could be instance of JSONObject(HAlert, HAck ...), JSONArray, String, Boolean, Number
	 * @param payload
	 */
	public void setPayload(Object payload) {
		try {
			if (payload == null) {
				this.remove("payload");
			} else {
				this.put("payload", payload);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public void setPayload(JSONObject payload) {
		try {
			if (payload == null) {
				this.remove("payload");
			} else {
				this.put("payload", payload);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public void setPayload(JSONArray payload) {
		try {
			if (payload == null) {
				this.remove("payload");
			} else {
				this.put("payload", payload);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public void setPayload(String payload) {
		try {
			if (payload == null) {
				this.remove("payload");
			} else {
				this.put("payload", payload);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public void setPayload(Boolean payload) {
		try {
			if (payload == null) {
				this.remove("payload");
			} else {
				this.put("payload", payload);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	public void setPayload(Integer payload) {
		try {
			if (payload == null) {
				this.remove("payload");
			} else {
				this.put("payload", payload);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public void setPayload(Double payload) {
		try {
			if (payload == null) {
				this.remove("payload");
			} else {
				this.put("payload", payload);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public void setPayload(HResult payload) {
		try {
			if (payload == null) {
				this.remove("payload");
			} else {
				this.put("payload", payload);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public void setPayload(HCommand payload) {
		try {
			if (payload == null) {
				this.remove("payload");
			} else {
				this.put("payload", payload);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	/**
	 * @return timeout. 0 if undefined.
	 */
	public long getTimeout() {
		Integer timeout;
		try {
			timeout = this.getInt("timeout");
		} catch (Exception e) {
			timeout = 0;
		}
		return timeout;
	}

	public void setTimeout(long timeout) {
		try {
			if (timeout == 0) {
				this.remove("timeout");
			} else {
				this.put("timeout", timeout);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}

	/**
	 * @return sent. Null if undefined.
	 */
	public Date getSentAsDate() {
		Date sent;
		try {
			sent = new Date(this.getLong("sent"));
		} catch (Exception e) {
			sent = null;
		}
		return sent;
	}
	
	public long getSent() {
		long sent;
		try {
			sent = this.getLong("sent");
		} catch (Exception e) {
			sent = 0;
		}
		return sent;
	}

	public void setSent(Date sent){
		try {
			if (sent == null) {
				this.remove("sent");
			} else {
				this.put("sent", sent.getTime());
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
	
	public void setSent(long sent){
		try {
			if (sent == 0) {
				this.remove("sent");
			} else {
				this.put("sent", sent);
			}
		} catch (JSONException e) {
			logger.warn("message: ", e);
		}
	}
}