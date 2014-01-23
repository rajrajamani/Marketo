import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;

import play.Play;
import play.test.FunctionalTest;
import play.test.UnitTest;

import com.marketo.rest.oauth.client.AuthToken;
import com.marketo.rest.oauth.client.IdentityClient;
import com.marketo.rest.oauth.client.TokenScope;

public class IdentityTests extends FunctionalTest {

	private String idSrvr;
	private String grantUri;
	private String clientId;
	private String clientSecret;
	private String validateUri;
	private String munchkinId;

	@Before
	public void setUp() {
		idSrvr = Play.configuration.getProperty("ID_SRVR");
		grantUri = Play.configuration.getProperty("GRANT_TOKEN_URI");
		clientId = Play.configuration.getProperty("CLIENT_ID");
		clientSecret = Play.configuration.getProperty("CLIENT_SECRET");
		validateUri = Play.configuration.getProperty("VALIDATE_TOKEN_URI");
		munchkinId = Play.configuration.getProperty("MUNCHKIN_ID");
	}

	@Test
	public void getAuthTokenTest() {
		String url = idSrvr + grantUri;
		AuthToken at;
		try {
			at = IdentityClient.getAuthToken(url, clientId, clientSecret);
			assertNotNull(at.access_token);
		} catch (ClientProtocolException e) {
			fail("Client Exception");
		} catch (IOException e) {
			fail("IO Exception");
		}
	}

	@Test
	public void validateToken() {
		String url = idSrvr + grantUri;
		String url2 = idSrvr + validateUri;

		try {
			AuthToken at = IdentityClient.getAuthToken(url, clientId,
					clientSecret);
			TokenScope ts = IdentityClient.validateToken(url2, at);
			assertTrue(ts.isValid);
			assertEquals(ts.userId, "apiuser@marketo.com");
			assertEquals(ts.info.munchkinId, munchkinId);
			assertTrue(ts.info.apiOnlyUser);
		} catch (ClientProtocolException e) {
			fail("Client Exception");
		} catch (IOException e) {
			fail("IO Exception");
		}
	}

}
