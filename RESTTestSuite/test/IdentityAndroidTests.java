import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import play.Play;
import play.test.FunctionalTest;
import play.test.UnitTest;

import com.marketo.rest.oauth.client.AuthToken;
import com.marketo.rest.oauth.client.IdentityClient;
import com.marketo.rest.oauth.client.TokenScope;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IdentityAndroidTests extends FunctionalTest {

	private String idSrvr;
	private String grantUri;
	private String clientId;
	private String clientSecret;
	private String validateUri;
	private String munchkinId;
	private String user;
	private String pass;
	private String userUri;
	private String uclientId;
	private String uclientSecret;

	@Before
	public void setUp() {
		idSrvr = Play.configuration.getProperty("ID_SRVR");
		grantUri = Play.configuration.getProperty("GRANT_TOKEN_URI");
		userUri = Play.configuration.getProperty("USER_TOKEN_URI");
		clientId = Play.configuration.getProperty("CLIENT_ID");
		clientSecret = Play.configuration.getProperty("CLIENT_SECRET");
		uclientId = Play.configuration.getProperty("UCLIENT_ID");
		uclientSecret = Play.configuration.getProperty("UCLIENT_SECRET");
		validateUri = Play.configuration.getProperty("VALIDATE_TOKEN_URI");
		munchkinId = Play.configuration.getProperty("MUNCHKIN_ID");
		user = Play.configuration.getProperty("USER");
		pass = Play.configuration.getProperty("PASS");
	}

	@Test
	public void t00getAuthTokenTest() {
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
	public void t03getAuthTokenForUserTest() {
		String url = idSrvr + userUri;
		AuthToken at;
		try {
			at = IdentityClient.getAuthTokenForUser(url, uclientId,
					uclientSecret, user, pass);
			assertNotNull(at.access_token);
		} catch (ClientProtocolException e) {
			fail("Client Exception");
		} catch (IOException e) {
			fail("IO Exception");
		}
	}
	
	@Test
	public void t04getAuthTokenForUserInvalidTest() {
		String url = idSrvr + userUri;
		AuthToken at;
		try {
			at = IdentityClient.getAuthTokenForUser(url, uclientId,
					uclientSecret, user, pass + "123");
		} catch (ClientProtocolException e) {
			assertTrue(true);
		} catch (IOException e) {
			fail("IO Exception");
		}
	}

	@Test
	public void t02invalidToken() {
		String url = idSrvr + grantUri;
		String url2 = idSrvr + validateUri;

		try {
			AuthToken at = IdentityClient.getAuthToken(url, clientId,
					clientSecret);
			at.access_token = "0x447";
			TokenScope ts = IdentityClient.validateToken(url2, at);
			assertFalse(ts.isValid);
		} catch (ClientProtocolException e) {
			fail("Client Exception");
		} catch (IOException e) {
			fail("IO Exception");
		}
	}

	@Test
	public void t01validateToken() {
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
