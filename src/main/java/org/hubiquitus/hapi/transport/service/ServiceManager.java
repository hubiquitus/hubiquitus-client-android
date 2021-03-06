package org.hubiquitus.hapi.transport.service;

import android.util.Log;

import org.hubiquitus.hapi.transport.Transport;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;

public class ServiceManager {

    public enum Method {
        POST, GET
    }

    private static final String TAG = ServiceManager.class.getCanonicalName();

    /**
     * Request a web service
     *
     * @param serverUrl the url of the server to request
     * @param service   the service to request
     * @param method    the {@link org.hubiquitus.hapi.transport.service.ServiceManager.Method} of the request
     * @param request   the {@link org.json.JSONObject} with the request for the web service
     * @return the response of the service in a {@link org.hubiquitus.hapi.transport.service.ServiceResponse}
     * @throws IOException
     */
    public static ServiceResponse requestService(String serverUrl, String service, Method method,
                                                 JSONObject request) throws IOException {

        String url = serverUrl + service;
        HttpURLConnection connection;

        try {
            connection = WebServiceConnexionManager.getUrlConnection(url);

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "error while connecting to service", e);
            return null;
        }

        if (Method.POST == method) {
            connection.setRequestMethod("POST");
            if (request != null) {
                connection.setDoOutput(true);
                StringBuilder sb = new StringBuilder();
                if (request.has(Transport.HB) && Transport.HB.equals(request.optString(Transport.HB))) {
                    sb.append(Transport.HB);
                } else {
                    sb.append("[")
                            .append(JSONObject.quote(request.toString()))
                            .append("]");
                }

                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(sb.toString());
                out.close();
            }
        }

        ServiceResponse serviceResponse = null;
        try {
            serviceResponse = readResponse(connection);
        } catch (IOException | IllegalStateException e) {
            Log.w(ServiceManager.class.getCanonicalName(), e);
        }
        return serviceResponse;
    }


    private static ServiceResponse readResponse(HttpURLConnection connection) throws IOException {

        ServiceResponse serviceResponse = new ServiceResponse();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        in.close();

        serviceResponse.setText(sb.toString());
        serviceResponse.setStatus(connection.getResponseCode());

        return serviceResponse;
    }

}
