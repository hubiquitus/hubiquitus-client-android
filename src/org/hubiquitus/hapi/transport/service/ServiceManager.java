package org.hubiquitus.hapi.transport.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.util.Log;

public class ServiceManager {
	
	public static ServiceResponse initSerConnectionService(String serverUrl) throws ClientProtocolException, IOException {
		
		final HttpClient httpClient = ConnexionService.getConnexionService()
				.getHttpClient();
		
		HttpGet httpGet = new HttpGet(serverUrl);
		httpGet.setHeader("Connection", "Keep-Alive");
		HttpResponse httpResponse = httpClient.execute(httpGet);
		ServiceResponse serviceResponse = readResponse(httpResponse);
		return serviceResponse;
	}
	

	public static ServiceResponse requestService(String serverUrl, String service,
			JSONObject request) throws ClientProtocolException, IOException {

		final HttpClient httpClient = ConnexionService.getConnexionService()
				.getHttpClient();

		StringBuilder sb = new StringBuilder();
		sb.append(serverUrl).append(service);
		HttpPost httpost = new HttpPost(sb.toString());
		if (request != null) {

			StringEntity entity = new StringEntity("["
					+ JSONObject.quote(request.toString()) + "]");
			httpost.setEntity(entity);
			httpost.setHeader("Content-Type", "text/plain");
		}

		ServiceResponse serviceResponse = null;
		HttpResponse httpResponse = httpClient.execute(httpost);
		try {
			serviceResponse = readResponse(httpResponse);
		} catch (IOException e) {
			Log.e("ServiceManager", e.getMessage());
		}
		return serviceResponse;
	}

	private static ServiceResponse readResponse(HttpResponse response)
			throws IOException {

		ServiceResponse serviceResponse = new ServiceResponse();

		if (response.getEntity() != null) {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			StringBuilder stringBuilder = new StringBuilder();
			String line = "";
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
