package com.marketo.rest.network;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marketo.rest.base.IdentityClientBase;
import com.marketo.rest.base.LeadAPIBase;
import com.marketo.rest.leadapi.client.Lead;
import com.marketo.rest.leadapi.client.LeadRequest;
import com.marketo.rest.leadapi.client.LeadResponse;
import com.marketo.rest.leadapi.client.MarketoException;
import com.marketo.rest.oauth.client.AuthToken;

public class LeadAPI extends LeadAPIBase {

	public Lead getLeadById(String urlBase, AuthToken at, int id,
			String[] fields) throws ClientProtocolException, IOException,
			MarketoException {
		String url = urlBase + "/v1/lead/" + id + ".json?access_token="
				+ at.access_token;
		if (fields != null) {
			url += "&fields=";
			for (String fld : fields) {
				url += fld + ",";
			}
		}
		Response response = Request.Get(url).execute();
		Gson gson = new GsonBuilder().create();
		String json = response.returnContent().asString();
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

	public LeadResponse getMultipleLeads(String url, String[] fields)
			throws ClientProtocolException, IOException, MarketoException {
		if (fields != null) {
			url += "&fields=";
			for (String fld : fields) {
				url += fld + ",";
			}
		}
		Response response = Request.Get(url).execute();
		Gson gson = new GsonBuilder().create();
		String json = response.returnContent().asString();
		LeadResponse attrMap = gson.fromJson(json, LeadResponse.class);
		if (attrMap.success != true) {
			throw new MarketoException(attrMap.requestId, attrMap.errors);
		}
		if (attrMap.result.size() == 0) {
			return null;
		} else {
			return attrMap;
		}
	}

	public LeadResponse getMultipleLeadsById(String urlBase,
			AuthToken at, int[] ids, String[] fields)
			throws ClientProtocolException, IOException, MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=id&filterValues=";
		String idStr = "";
		for (int i : ids) {
			idStr += i + ",";
		}
		url += idStr;
		return getMultipleLeads(url, fields);
	}

	public LeadResponse getMultipleLeadsByEmail(String urlBase,
			AuthToken at, String[] emails, String[] fields)
			throws ClientProtocolException, IOException, MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=email&filterValues=";
		String emailStr = "";
		for (String email : emails) {
			emailStr += email + ",";
		}
		url += emailStr;
		return getMultipleLeads(url, fields);
	}

	public LeadResponse getMultipleLeadsByCookie(String urlBase,
			AuthToken at, String[] cookies, String[] fields)
			throws ClientProtocolException, IOException, MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=cookie&filterValues=";
		String ckStr = "";
		for (String cookie : cookies) {
			ckStr += cookie + ",";
		}
		url += ckStr;
		return getMultipleLeads(url, fields);
	}

	public LeadResponse getMultipleLeadsByFacebookId(String urlBase,
			AuthToken at, String[] fbIds, String[] fields)
			throws ClientProtocolException, IOException, MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=facebookId&filterValues=";
		String fbStr = "";
		for (String fbId : fbIds) {
			fbStr += fbId + ",";
		}
		url += fbStr;
		return getMultipleLeads(url, fields);
	}

	public LeadResponse getMultipleLeadsByLinkedinId(String urlBase,
			AuthToken at, String[] liIds, String[] fields)
			throws ClientProtocolException, IOException, MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=linkedinId&filterValues=";
		String liStr = "";
		for (String liId : liIds) {
			liStr += liId + ",";
		}
		url += liStr;
		return getMultipleLeads(url, fields);
	}

	public LeadResponse getMultipleLeadsByTwitterId(String urlBase,
			AuthToken at, String[] twIds, String[] fields)
			throws ClientProtocolException, IOException, MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token
				+ "&filterType=twitterId&filterValues=";
		String twStr = "";
		for (String twId : twIds) {
			twStr += twId + ",";
		}
		url += twStr;
		return getMultipleLeads(url, fields);
	}

	public LeadResponse getMultipleLeadsFromStaticList(String urlBase,
			AuthToken at, int listId, int batchSize, String nPageToken,
			String[] fields) throws ClientProtocolException, IOException,
			MarketoException {
		String url = urlBase + "/v1/list/" + listId
				+ "/leads.json?access_token=" + at.access_token;
		if (nPageToken != null) {
			url += "&nextPageToken=" + nPageToken;
		}
		if (batchSize > 0) {
			url += "&batchSize=" + batchSize;
		}

		// System.out.println("Making request :" + url);
		return getMultipleLeads(url, fields);
	}

	public LeadResponse syncMultipleLeads(String urlBase, AuthToken at,
			LeadRequest lreq) throws ClientProtocolException, IOException,
			MarketoException {
		String url = urlBase + "/v1/leads.json?access_token=" + at.access_token;
		String lStr = lreq.getJsonString();
		Response response = Request.Post(url).bodyString(lStr, ContentType.APPLICATION_JSON).execute();
		Gson gson = new GsonBuilder().create();
		String json = response.returnContent().asString();
		LeadResponse attrMap = gson.fromJson(json, LeadResponse.class);
		if (attrMap.success != true) {
			throw new MarketoException(attrMap.requestId, attrMap.errors);
		}
		if (attrMap.result.size() == 0) {
			return null;
		} else {
			return attrMap;
		}
	}
	
	protected LeadAPI() {
		
	}
	
	public static LeadAPIBase getInstance() {
		if (instance == null) {
			instance = new LeadAPI();
		} 
		return instance;
	}

}
