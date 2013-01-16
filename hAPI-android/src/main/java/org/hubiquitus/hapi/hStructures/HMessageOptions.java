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

import org.json.JSONObject;

/**
 * @version 0.5
 * hAPI MessageOption. For more info, see Hubiquitus reference
 */

public class HMessageOptions {
	
	private String ref = null;
	private String convid = null;
	private HMessagePriority priority = null;
	private long relevance = 0;
	private Integer relevanceOffset = null;
	private Boolean persistent = null;
	private HLocation location = null;
	private String author = null;
	private JSONObject headers = null;
	private long published = 0;
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
	public long getRelevance() {
		return relevance;
	}
	public void setRelevance(long relevance) {
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
	public long getPublished() {
		return published;
	}
	public void setPublished(long published) {
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
