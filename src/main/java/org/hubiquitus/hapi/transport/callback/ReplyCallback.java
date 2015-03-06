package org.hubiquitus.hapi.transport.callback;

import org.json.JSONObject;

public interface ReplyCallback {
    /**
     * Reply to the request
     *
     * @param err     an {@link org.json.JSONObject} representing an error
     * @param content an {@link org.json.JSONObject} with the responseContent
     */
    void reply(JSONObject err, JSONObject content);
}
