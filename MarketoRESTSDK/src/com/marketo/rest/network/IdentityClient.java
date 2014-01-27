package com.marketo.rest.network;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marketo.rest.base.IdentityClientBase;
import com.marketo.rest.oauth.client.AuthToken;
import com.marketo.rest.oauth.client.TokenScope;

public class IdentityClient extends IdentityClientBase {

	@Override
	public AuthToken getAuthToken(String url, String clientId,
			String clientSecret) throws ClientProtocolException, IOException {

		Response response = Request.Get(
				url + "&client_id=" + clientId + "&client_secret="
						+ clientSecret).execute();
		Gson gson = new GsonBuilder().create();
		AuthToken at = gson.fromJson(response.returnContent().asString(),
				AuthToken.class);
		return at;
	}

	public AuthToken getAuthTokenForUser(String url, String clientId,
			String clientSecret, String user, String pass)
			throws ClientProtocolException, IOException {

		String urlWithParams = url + "&client_id=" + clientId
				+ "&client_secret=" + clientSecret + "&username=" + user
				+ "&password=" + pass;
		Response response = Request.Get(urlWithParams).execute();
		Gson gson = new GsonBuilder().create();
		AuthToken at = gson.fromJson(response.returnContent().asString(),
				AuthToken.class);
		return at;
	}

	public TokenScope validateToken(String url, AuthToken at)
			throws ClientProtocolException, IOException {
		return validateToken(url, at.access_token);
	}

	public TokenScope validateToken(String url, String access_token)
			throws ClientProtocolException, IOException {
		Response response = Request.Get(url + access_token).execute();
		Gson gson = new GsonBuilder().create();
		TokenScope ts = gson.fromJson(response.returnContent().asString(),
				TokenScope.class);
		return ts;
	}

	protected IdentityClient() {
		
	}
	
	public static IdentityClientBase getInstance() {
		if (instance == null) {
			instance = new IdentityClient();
		} 
		return instance;
	}
}
