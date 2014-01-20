import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class LeadAPI {

	public static Lead getLeadById(AuthToken at, int id)
			throws ClientProtocolException, IOException {
		String url = Constants.REST_SRVR + "/v1/lead/" + id
				+ ".json?access_token=" + at.access_token;
		Response response = Request.Get(url).execute();
		Gson gson = new GsonBuilder().create();
		String json = response.returnContent().asString();
		LeadResponse attrMap = gson.fromJson(json, LeadResponse.class);
		if (attrMap.result.size() == 0) {
			return null;
		} else {
			Map<String, String> ldAsMap = attrMap.result.iterator().next();
			Lead ld = new Lead(ldAsMap);
			return ld;
		}
	}

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		AuthToken at = IdentityServer.getAuthToken(Constants.CLIENT_ID,
				Constants.CLIENT_SECRET);
		Lead ld = getLeadById(at, 60);
		ld.printLeadAttributes();
		String lastName = ld.getLeadAttrib("lastName");
		System.out.println(lastName);
	}
}
