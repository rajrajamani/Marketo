import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LeadAPI {

	public static Lead getLeadById(AuthToken at, int id)
			throws ClientProtocolException, IOException, MarketoException {
		String url = Constants.REST_SRVR + "/v1/lead/" + id
				+ ".json?access_token=" + at.access_token;
		Response response = Request.Get(url).execute();
		Gson gson = new GsonBuilder().create();
		String json = response.returnContent().asString();
		LeadResponse attrMap = gson.fromJson(json, LeadResponse.class);
		if (attrMap.success != true) {
			throw new MarketoException(attrMap.requestId);
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

	public static Lead getLeadByCookie(AuthToken at, String cookie)
			throws ClientProtocolException, IOException, MarketoException {
		String url = Constants.REST_SRVR + "/v1/lead/cookie.json?access_token="
				+ at.access_token + "&value=" + cookie;
		Response response = Request.Get(url).execute();
		Gson gson = new GsonBuilder().create();
		String json = response.returnContent().asString();
		LeadResponse attrMap = gson.fromJson(json, LeadResponse.class);
		if (attrMap.success != true) {
			throw new MarketoException(attrMap.requestId);
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

	public static ArrayList<Lead> getMultipleLeadsById(AuthToken at, int[] ids)
			throws ClientProtocolException, IOException, MarketoException {
		String url = Constants.REST_SRVR + "/v1/leads.json?access_token="
				+ at.access_token + "&filterType=id&filterValues=";
		String idStr = "";
		for (int i : ids) {
			idStr += i + ",";
		}
		url += idStr;
		return getMultipleLeads(url);
	}

	public static ArrayList<Lead> getMultipleLeadsByEmail(AuthToken at,
			String[] emails) throws ClientProtocolException, IOException,
			MarketoException {
		String url = Constants.REST_SRVR + "/v1/leads.json?access_token="
				+ at.access_token + "&filterType=email&filterValues=";
		String emailStr = "";
		for (String email : emails) {
			emailStr += email + ",";
		}
		url += emailStr;
		return getMultipleLeads(url);
	}

	private static ArrayList<Lead> getMultipleLeads(String url)
			throws ClientProtocolException, IOException, MarketoException {
		Response response = Request.Get(url).execute();
		Gson gson = new GsonBuilder().create();
		String json = response.returnContent().asString();
		LeadResponse attrMap = gson.fromJson(json, LeadResponse.class);
		if (attrMap.success != true) {
			throw new MarketoException(attrMap.requestId);
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
