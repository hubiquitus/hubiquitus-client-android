package org.hubiquitus.hapi.message;

import org.hubiquitus.hapi.transport.callback.ReplyCallback;

/**
 * Models representing a request message
 *
 * @author t.bourgeois
 */
public class Request extends Message {

    private ReplyCallback mReplyCallback;

    /**
     * Get class to respond to the request
     *
     * @return a {@link org.hubiquitus.hapi.transport.callback.ReplyCallback} implementation for the request.
     */
    public ReplyCallback getReplyCallback() {
        return mReplyCallback;
    }


    public void setReplyCallback(ReplyCallback replyCallback) {
        this.mReplyCallback = replyCallback;
    }

}
