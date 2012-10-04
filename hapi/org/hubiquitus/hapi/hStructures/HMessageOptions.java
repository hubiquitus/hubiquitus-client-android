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

package org.hubiquitus.hapi.hStructures;

import org.joda.time.DateTime;
import org.json.JSONObject;

/**
 * @version 0.5
 * hAPI MessageOption. For more info, see Hubiquitus reference
 */

public class HMessageOptions {
	
	private String ref = null;
	private String convid = null;
	private HMessagePriority priority = null;
	private DateTime relevance = null;
	private Integer relevanceOffset = null;
	private Boolean persistent = null;
	private HLocation location = null;
	private String author = null;
	private JSONObject headers = null;
	private DateTime published = null;
	private Integer timeout = 0;
	
	/**
	 * @return The msgid of the message refered to
	 */
	public String getRef() {
		return ref;
	}
	
	public void setRef(String ref){
		this.ref = ref;
	}
	
	/**
	 * @return conversation id. NULL if undefined 
	 */
	public String getConvid() {
		return convid;
	}
	public void setConvid(String convid) {
		this.convid = convid;
	}
	
	/**
	 * If UNDEFINED, priority lower to 0. 
	 * @return priority.
	 */
	public HMessagePriority getPriority() {
		return priority;
	}
	public void setPriority(HMessagePriority priority) {
		this.priority = priority;
	}
	
	/**
	 * Date-time until which the message is considered as relevant.
	 * @return relevance. NULL if undefined
	 */
	public DateTime getRelevance() {
		return relevance;
	}
	public void setRelevance(DateTime relevance) {
		this.relevance = relevance;
	}
	
	
	public Integer getRelevanceOffset() {
		return relevanceOffset;
	}
	/**
	 * You can use this option to indicate a duration in ms. 
	 * If you use this parameter, it will override the relevance one by updating the date-time for the relevance of the hMessage.
	 * @param relevanceOffset
	 */
	public void setRelevanceOffset(Integer relevanceOffset) {
		this.relevanceOffset = relevanceOffset;
	}
	
	/**
	 * Persistent if false.
	 * @return persist message or not. NULL if undefined
	 */
	public Boolean getPersistent() {
		return persistent;
	}
	public void setPersistent(Boolean persistent) {
		this.persistent = persistent;
	}
	
	/**
	 * The geographical location to which the message refer.
	 * @return location. NULL if undefined
	 */
	public HLocation getLocation() {
		return location;
	}
	public void setLocation(HLocation location) {
		this.location = location;
	}
	
	/**
	 * @return author of this message. NULL if undefined 
	 */
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
	/**
	 * The list of headers attached to this message.
	 * @return Headers. NULL if undefined
	 */
	public JSONObject getHeaders() {
		return headers;
	}
	public void setHeaders(JSONObject headers) {
		this.headers = headers;
	}
	
	/**
	 * Date-time when the message is publish
	 * @return relevance. NULL if undefined
	 */
	public DateTime getPublished() {
		return published;
	}
	public void setPublished(DateTime published) {
		this.published = published;
	}
	
	/**
	 * Time (in ms) to wait for a response before hAPI sends a timeout
	 *@return timeout. 0 if undefined.
	 */
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	
}
