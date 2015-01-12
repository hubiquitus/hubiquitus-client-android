package org.hubiquitus.hapi.message;

import org.hubiquitus.hapi.transport.callback.ReplyCallback;

/**
 * Models representing a request message
 * 
 * @author t.bourgeois
 *
 */
public class Request extends Message {
	
	/**
	 * The reply callback
	 */
	private ReplyCallback replyCallback;

	public ReplyCallback getReplyCallback() {
		return replyCallback;
	}

	public void setReplyCallback(ReplyCallback replyCallback) {
		this.replyCallback = replyCallback;
	}

}
