package org.hubiquitus.hapi.transport.model;

import org.hubiquitus.hapi.listener.ResponseListener;
import org.json.JSONObject;

public class Message {

    private JSONObject jsonContent;

    private ResponseListener responseListener;

    public JSONObject getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(JSONObject jsonContent) {
        this.jsonContent = jsonContent;
    }

    @Override
    public String toString() {
        return "Message [jsonContent=" + jsonContent + ", responseListener="
                + responseListener + "]";
    }

    public ResponseListener getResponseListener() {
        return responseListener;
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

}
