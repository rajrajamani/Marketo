package com.marketo.rest.oauth.client;
import java.io.IOException;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IdentityClient {

	public static AuthToken getAuthToken(String url, String clientId,
			String clientSecret) throws ClientProtocolException, IOException {

		Response response = Request.Get(
				url + "&client_id=" + clientId + "&client_secret="
						+ clientSecret).execute();
		Gson gson = new GsonBuilder().create();
		AuthToken at = gson.fromJson(response.returnContent().asString(),
				AuthToken.class);
		return at;
	}

	public static TokenScope validateToken(String url, AuthToken at)
			throws ClientProtocolException, IOException {
		return validateToken(url, at.access_token);
	}

	public static TokenScope validateToken(String url, String access_token)
			throws ClientProtocolException, IOException {
		Response response = Request.Get(url + access_token).execute();
		Gson gson = new GsonBuilder().create();
		TokenScope ts = gson.fromJson(response.returnContent().asString(),
				TokenScope.class);
		return ts;
	}

}
