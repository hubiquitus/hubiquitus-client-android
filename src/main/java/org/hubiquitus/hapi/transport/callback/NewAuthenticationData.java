package org.hubiquitus.hapi.transport.callback;

import org.json.JSONObject;

/**
 * Interface for getting new AuthData
 * Created by m.Ruetsch on 09/02/15.
 */
public interface NewAuthenticationData {

    /**
     * It's safe to do network here
     *
     * @return you should return a {@link org.json.JSONObject} with auth data,
     * or null if you can't give new authData at this time.
     */
    public JSONObject getNewAuthData();
}
