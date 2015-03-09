package org.hubiquitus.hapi.utils;

import android.util.Log;

import org.hubiquitus.hapi.transport.service.ServiceResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by m.Ruetsch on 03/02/15.
 */
public class HubiquitusInfoUtils {
    private static final String WEBSOCKET = "websocket";

    /**
     * Parse the supported websocket request
     *
     * @param response the response of the service
     * @return WebSocket supported, or {@code null} if we can't request the hubiquitus info
     */
    public static Boolean parseWebSocketSupported(ServiceResponse response) {
        if (response == null) {
            return null;
        }
        String text = response.getText();
        if (text != null) {
            try {
                JSONObject jsonObject = new JSONObject(text);
                if (jsonObject.has(WEBSOCKET)) {
                    return jsonObject.getBoolean(WEBSOCKET);
                }
            } catch (JSONException e) {
                Log.w(HubiquitusInfoUtils.class.getCanonicalName(), e);
            }

        }
        return null;
    }
}
