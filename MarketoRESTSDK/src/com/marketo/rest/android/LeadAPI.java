package com.marketo.rest.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marketo.rest.leadapi.client.Lead;
import com.marketo.rest.leadapi.client.LeadResponse;
import com.marketo.rest.leadapi.client.MarketoException;
import com.marketo.rest.oauth.client.AuthToken;
import com.marketo.rest.utils.HttpClient;

public class LeadAPI {

	public static Lead getLeadById(String urlBase, AuthToken at, int id,
			String[] fields) throws IOException, MarketoException {
		String url = urlBase + "/v1/lead/" + id + ".json?access_token="
				+ at.access_token;
		if (fields != null) {
			url += "&fields=";
			for (String fld : fields) {
				url += fld + ",";
			}
		}
		String response = HttpClient.readGetResponse(url);
		if (response == null) {
			return null;
		}
		Gson gson = new GsonBuilder().create();
		String json = response;
		LeadResponse attrMap = gson.fromJson(json, LeadResponse.class);
		if (attrMap.success != true) {
			throw new MarketoException(attrMap.requestId, attrMap.errors);
		}
		if (attrMap.result.size() == 0) {
			return null;
		} else {
			// return the first one
			Map<String, String> ldAsMap = attrMap.result.iterator().next();
			Lead ld = new Lead(ldAsMap);
			return ld;
		}
	}

	public static ArrayList<Lead> getMultipleLeadsById(String urlBase,
			AuthToken at, int[] ids, String[] fields) throws IOException,
			MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=id&filterValues=";
		String idStr = "";
		for (int i : ids) {
			idStr += i + ",";
		}
		url += idStr;
		return getMultipleLeads(url, fields);
	}

	public static ArrayList<Lead> getMultipleLeadsByEmail(String urlBase,
			AuthToken at, String[] emails, String[] fields) throws IOException,
			MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=email&filterValues=";
		String emailStr = "";
		for (String email : emails) {
			emailStr += email + ",";
		}
		url += emailStr;
		return getMultipleLeads(url, fields);
	}

	public static ArrayList<Lead> getMultipleLeadsByCookie(String urlBase,
			AuthToken at, String[] cookies, String[] fields)
			throws IOException, MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=cookie&filterValues=";
		String ckStr = "";
		for (String cookie : cookies) {
			ckStr += cookie + ",";
		}
		url += ckStr;
		return getMultipleLeads(url, fields);
	}

	public static ArrayList<Lead> getMultipleLeadsByFacebookId(String urlBase,
			AuthToken at, String[] fbIds, String[] fields) throws IOException,
			MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=facebookId&filterValues=";
		String fbStr = "";
		for (String fbId : fbIds) {
			fbStr += fbId + ",";
		}
		url += fbStr;
		return getMultipleLeads(url, fields);
	}

	public static ArrayList<Lead> getMultipleLeadsByLinkedinId(String urlBase,
			AuthToken at, String[] liIds, String[] fields) throws IOException,
			MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=linkedinId&filterValues=";
		String liStr = "";
		for (String liId : liIds) {
			liStr += liId + ",";
		}
		url += liStr;
		return getMultipleLeads(url, fields);
	}

	public static ArrayList<Lead> getMultipleLeadsByTwitterId(String urlBase,
			AuthToken at, String[] twIds, String[] fields) throws IOException,
			MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=twitterId&filterValues=";
		String twStr = "";
		for (String twId : twIds) {
			twStr += twId + ",";
		}
		url += twStr;
		return getMultipleLeads(url, fields);
	}

	private static ArrayList<Lead> getMultipleLeads(String url, String[] fields)
			throws IOException, MarketoException {
		if (fields != null) {
			url += "&fields=";
			for (String fld : fields) {
				url += fld + ",";
			}
		}
		String response = HttpClient.readGetResponse(url);
		if (response == null) {
			return null;
		}
		Gson gson = new GsonBuilder().create();
		String json = response;
		LeadResponse attrMap = gson.fromJson(json, LeadResponse.class);
		if (attrMap.success != true) {
			throw new MarketoException(attrMap.requestId, attrMap.errors);
		}
		if (attrMap.result.size() == 0) {
			return null;
		} else {
			Map<String, String> ldAsMap = null;
			ArrayList<Lead> leadArr = new ArrayList<Lead>();
			Iterator<Map<String, String>> it = attrMap.result.iterator();
			while (it.hasNext()) {
				ldAsMap = it.next();
				Lead ld = new Lead(ldAsMap);
				leadArr.add(ld);
			}
			return leadArr;
		}
	}
}
