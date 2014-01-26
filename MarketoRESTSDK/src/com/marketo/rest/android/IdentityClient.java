package com.marketo.rest.android;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marketo.rest.oauth.client.AuthToken;
import com.marketo.rest.oauth.client.TokenScope;
import com.marketo.rest.utils.HttpClient;

public class IdentityClient {

	public static AuthToken getAuthToken(String url, String clientId,
			String clientSecret) throws IOException {

		String urlStr = url + "&client_id=" + clientId + "&client_secret="
				+ clientSecret;
		String response = HttpClient.readGetResponse(urlStr);

		if (response == null) {
			return null;
		}

		Gson gson = new GsonBuilder().create();
		AuthToken at = gson.fromJson(response, AuthToken.class);
		return at;
	}

	public static AuthToken getAuthTokenForUser(String url, String clientId,
			String clientSecret, String user, String pass) throws IOException {

		String urlStr = url + "&client_id=" + clientId + "&client_secret="
				+ clientSecret + "&username=" + user + "&password=" + pass;
		String response = HttpClient.readGetResponse(urlStr);
		if (response == null) {
			return null;
		}

		Gson gson = new GsonBuilder().create();
		AuthToken at = gson.fromJson(response, AuthToken.class);
		return at;
	}

	public static TokenScope validateToken(String url, AuthToken at)
			throws IOException {
		return validateToken(url, at.access_token);
	}

	public static TokenScope validateToken(String url, String access_token)
			throws IOException {
		String urlStr = url + access_token;
		String response = HttpClient.readGetResponse(urlStr);

		if (response == null) {
			return null;
		}

		Gson gson = new GsonBuilder().create();
		TokenScope ts = gson.fromJson(response, TokenScope.class);
		return ts;
	}

}
