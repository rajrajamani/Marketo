import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IdentityServer {

	public static AuthToken getAuthToken(String clientId, String clientSecret)
			throws ClientProtocolException, IOException {
		Response response = Request.Get(
				Constants.GRANT_TOKEN_URI + "&client_id=" + clientId
						+ "&client_secret=" + clientSecret).execute();
		Gson gson = new GsonBuilder().create();
		AuthToken at = gson.fromJson(response.returnContent().asString(),
				AuthToken.class);
		return at;
	}

	public static TokenScope validateToken(AuthToken at)
			throws ClientProtocolException, IOException {
		return validateToken(at.access_token);
	}

	public static TokenScope validateToken(String access_token)
			throws ClientProtocolException, IOException {
		Response response = Request.Get(
				Constants.VALIDATE_TOKEN_URI + access_token).execute();
		Gson gson = new GsonBuilder().create();
		TokenScope ts = gson.fromJson(response.returnContent().asString(),
				TokenScope.class);
		return ts;
	}

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		AuthToken at = getAuthToken(Constants.CLIENT_ID,
				Constants.CLIENT_SECRET);
		System.out.println(at.access_token);
		TokenScope ts = validateToken(at);
		System.out.println(ts.timeToLive);
	}
}
