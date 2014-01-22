import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;

import play.Play;
import play.test.UnitTest;

import com.marketo.rest.leadapi.client.Lead;
import com.marketo.rest.leadapi.client.LeadAPI;
import com.marketo.rest.leadapi.client.MarketoException;
import com.marketo.rest.oauth.client.AuthToken;
import com.marketo.rest.oauth.client.IdentityClient;

public class LeadAPITests extends UnitTest {

	private String clientId;
	private String clientSecret;
	private String idSrvr;
	private String grantUri;
	private String restSrvr;
	private String url;
	private String fields[];
	private AuthToken at;
	private String[] leadIds;
	private String[] cookies;
	private String[] twIds;
	private String[] fbIds;
	private String[] liIds;

	@Before
	public void setUp() throws ClientProtocolException, IOException {

		clientId = Play.configuration.getProperty("CLIENT_ID");
		clientSecret = Play.configuration.getProperty("CLIENT_SECRET");
		idSrvr = Play.configuration.getProperty("ID_SRVR");
		grantUri = Play.configuration.getProperty("GRANT_TOKEN_URI");
		restSrvr = Play.configuration.getProperty("REST_SRVR");
		leadIds = Play.configuration.getProperty("LEAD_IDS").split(",");
		cookies = Play.configuration.getProperty("COOKIES").split(",");
		twIds = Play.configuration.getProperty("TW_IDS").split(",");
		fbIds = Play.configuration.getProperty("FB_IDS").split(",");
		liIds = Play.configuration.getProperty("LI_IDS").split(",");

		url = idSrvr + grantUri;
		at = IdentityClient.getAuthToken(url, clientId, clientSecret);

		fields = new String[] { "firstName", "lastName", "email", "facebookId",
				"linkedinId", "twitterId" };

	}

	@Test
	public void getLeadbyIdTest() throws ClientProtocolException, IOException,
			MarketoException {
		// Get Lead by Id
		Lead ld1 = LeadAPI.getLeadById(restSrvr, at,
				Integer.valueOf(leadIds[0]), fields);
		assertNotNull(ld1);
		if (ld1 != null) {
			String lastName = ld1.getLeadAttrib("lastName");
			assertEquals(lastName, "Wugsy24");
		}
	}

	@Test
	public void getLeadbyCookieTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Lead by Cookie
		Lead ld2 = LeadAPI.getLeadByCookie(restSrvr, at,
				cookies[0], fields);
		assertNotNull(ld2);
		if (ld2 != null) {
			String firstName = ld2.getLeadAttrib("firstName");
			assertEquals(firstName, "abra");
		}
	}

	@Test
	public void getLeadbyFullCookieTest() throws ClientProtocolException,
			IOException, MarketoException {
		Lead ld3 = LeadAPI.getLeadByCookie(restSrvr, at, cookies[1], fields);
		assertNotNull(ld3);
		if (ld3 != null) {
			String email = ld3.getLeadAttrib("email");
			assertEquals(email, "abra@dabra.com");
		}
	}

	@Test
	public void getLeadsbyIdTest() throws ClientProtocolException, IOException,
			MarketoException {
		// Get Multiple Leads by Id
		int[] lIds = { Integer.valueOf(leadIds[1]), Integer.valueOf(leadIds[2]) };
		ArrayList<Lead> leads1 = LeadAPI.getMultipleLeadsById(restSrvr, at,
				lIds, fields);
		assertNotNull(leads1);
		int i = 1;
		for (Lead lead : leads1) {
			String id = lead.getLeadAttrib("id");
			assertEquals(id, leadIds[i]);
			i++;
		}
	}

	@Test
	public void getLeadsbyEmailTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by Email
		String[] emails = { "kmluce@gmail.com", "glen@marketo.com" };
		ArrayList<Lead> leads2;
		leads2 = LeadAPI.getMultipleLeadsByEmail(restSrvr, at, emails, fields);
		assertNotNull(leads2);
		ArrayList<String> results = new ArrayList<String>();
		for (Lead lead : leads2) {
			results.add(lead.getLeadAttrib("email"));
		}
		Collections.sort(results.subList(1, results.size()));
		this.assertArrayEquals(emails, results.toArray());
	}

	@Test
	public void getLeadsbyCookieTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by Cookie
		ArrayList<Lead> leads3;
		leads3 = LeadAPI.getMultipleLeadsByCookie(restSrvr, at, cookies, fields);
		assertNotNull(leads3);
		assertEquals(leads3.size(), 1);
	}

	@Test
	public void getLeadsbyFacebookIdTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by FacebookId
		ArrayList<Lead> leads;
		leads = LeadAPI.getMultipleLeadsByFacebookId(restSrvr, at, fbIds,
				fields);
		assertNotNull(leads);
		ArrayList<String> leadsRes  = new ArrayList<String>();;
		for (Lead lead : leads) {
			leadsRes.add(lead.getLeadAttrib("facebookId"));
		}
		this.assertArrayEquals(fbIds, leadsRes.toArray());
	}

	@Test
	public void getLeadsbyLinkedInIdTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by LinkedinId
		ArrayList<Lead> leads;
		leads = LeadAPI.getMultipleLeadsByLinkedinId(restSrvr, at, liIds,
				fields);
		assertNotNull(leads);
		assertEquals(leads.size(),2);
	}

	@Test
	public void getLeadsbyTwitterIdTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by TwitterId
		ArrayList<Lead> leads;
		leads = LeadAPI.getMultipleLeadsByTwitterId(restSrvr, at, twIds,
				fields);
		assertNotNull(leads);
		assertEquals(leads.size(),2);
	}

}
