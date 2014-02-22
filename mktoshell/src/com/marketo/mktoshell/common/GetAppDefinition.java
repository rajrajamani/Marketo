package com.marketo.mktoshell.common;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marketo.mktoshell.TrackDetailFragment;

public class GetAppDefinition extends AsyncTask<String, Integer, AppDefinition> {

	@Override
	protected AppDefinition doInBackground(String... callback) {
		String url = callback[0];
		Gson gson = new GsonBuilder().create();
		String resp;
		try {
			resp = HttpClient.readGetResponse(url);
		} catch (IOException e) {
			Log.e("mktoshell", "Unable to fetch app definition");
			return null;
		}
		AppDefinition appDef = gson.fromJson(resp, AppDefinition.class);
		return appDef;
	}

}
