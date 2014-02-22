package com.marketo.mktoshell.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
	public static String readInputStream(HttpURLConnection urlConnection)
			throws IOException {
		String response = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			for (String line; (line = reader.readLine()) != null;) {
				response += line;
			}

		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				return response;
			} catch (IOException logOrIgnore) {
				// ignore
			}
		}
		return response;
	}

	public static String readGetResponse(String urlStr) throws IOException {
		URL urlWithParams = new URL(urlStr);
		HttpURLConnection urlConnection = (HttpURLConnection) urlWithParams
				.openConnection();
		int status = urlConnection.getResponseCode();
		String response = null;
		if (status == HttpURLConnection.HTTP_OK) {
			response = HttpClient.readInputStream(urlConnection);
		}

		return response;
	}
	
}