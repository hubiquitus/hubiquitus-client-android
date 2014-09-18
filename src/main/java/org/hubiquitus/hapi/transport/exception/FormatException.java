package org.hubiquitus.hapi.transport.exception;

import org.json.JSONException;

public class FormatException extends JSONException {

    /**
     *
     */
    private static final long serialVersionUID = 3331512289546887548L;

    public FormatException(String s) {
        super(s);
    }

}
