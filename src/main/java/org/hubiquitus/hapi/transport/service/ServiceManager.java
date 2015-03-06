package org.hubiquitus.hapi.transport.service;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.hubiquitus.hapi.transport.Transport;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServiceManager {

    public enum Method {
        POST, GET
    }

    public static ServiceResponse initConnectionService(String serverUrl) throws IOException {

        HttpClient httpClient = WebServiceConnexionManager.getConnexionService().getHttpClient();

        HttpGet httpGet = new HttpGet(serverUrl);
        httpGet.setHeader("Connection", "Keep-Alive");
        HttpResponse httpResponse = httpClient.execute(httpGet);
        return readResponse(httpResponse);
    }


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

        HttpClient httpClient = WebServiceConnexionManager.getConnexionService().getHttpClient();

        StringBuilder sb = new StringBuilder();
        sb.append(serverUrl).append(service);

        HttpRequestBase httpRequest = null;
        switch (method) {
            case GET:
                httpRequest = new HttpGet(sb.toString());
                break;
            case POST:
                httpRequest = new HttpPost(sb.toString());
                if (request != null) {
                    StringEntity entity;
                    if (request.has(Transport.HB) && Transport.HB.equals(request.optString(Transport.HB))) {
                        //Object send from XHR Transport for sending heartbeat : new JSONObject().put(Transport.HB,Transport.HB)
                        entity = new StringEntity(Transport.HB);
                    } else {
                        entity = new StringEntity("[" + JSONObject.quote(request.toString()) + "]");
                    }
                    ((HttpPost) httpRequest).setEntity(entity);
                    httpRequest.setHeader("Content-Type", "text/plain");
                }
                break;
            default:
                break;
        }

        ServiceResponse serviceResponse = null;
        try {
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            serviceResponse = readResponse(httpResponse);
        } catch (IOException | IllegalStateException e) {
            Log.w(ServiceManager.class.getCanonicalName(), e);
        }
        return serviceResponse;
    }


    private static ServiceResponse readResponse(HttpResponse response) throws IOException {

        ServiceResponse serviceResponse = new ServiceResponse();

        if (response.getEntity() != null) {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();

            serviceResponse.setText(stringBuilder.toString());
        }

        serviceResponse.setStatus(response.getStatusLine().getStatusCode());
        return serviceResponse;
    }

}
