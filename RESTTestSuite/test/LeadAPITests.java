import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import play.Play;
import play.test.FunctionalTest;

import com.marketo.rest.leadapi.client.Lead;
import com.marketo.rest.leadapi.client.MarketoException;
import com.marketo.rest.network.IdentityClient;
import com.marketo.rest.network.LeadAPI;
import com.marketo.rest.oauth.client.AuthToken;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LeadAPITests extends FunctionalTest {

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
	private LeadAPI networkClient;
	private com.marketo.rest.android.LeadAPI androidClient;

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

		networkClient = LibFactory.getInstance().getNetworkLeadAPI();
		androidClient = LibFactory.getInstance().getAndroidLeadAPI();

		url = idSrvr + grantUri;
		at = IdentityClient.getAuthToken(url, clientId, clientSecret);

		fields = new String[] { "firstName", "lastName", "email", "facebookId",
				"linkedinId", "twitterId" };

	}

	@Test
	public void t00getLeadByIdTest() throws ClientProtocolException,
			IOException {
		// Get Lead by Id
		Lead ld1;
		try {
			ld1 = networkClient.getLeadById(restSrvr, at,
					Integer.valueOf(leadIds[0]), fields);
			assertNotNull(ld1);
			if (ld1 != null) {
				String lastName = ld1.getLeadAttrib("lastName");
				assertEquals(lastName, "Wugsy24");
			}
			ld1 = null;
			ld1 = androidClient.getLeadById(restSrvr, at,
					Integer.valueOf(leadIds[0]), fields);
			assertNotNull(ld1);
			if (ld1 != null) {
				String lastName = ld1.getLeadAttrib("lastName");
				assertEquals(lastName, "Wugsy24");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MarketoException e) {
			e.printStackTrace();
			this.fail(e.getErrorsAsString());
		}
	}

	@Test
	public void t01getLeadByCookieTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Lead by Cookie
		Lead ld2;
		ld2 = networkClient.getLeadByCookie(restSrvr, at, cookies[0], fields);
		assertNotNull(ld2);
		if (ld2 != null) {
			String firstName = ld2.getLeadAttrib("firstName");
			assertEquals(firstName, "abra");
		}
		ld2 = null;
		ld2 = androidClient.getLeadByCookie(restSrvr, at, cookies[0], fields);
		assertNotNull(ld2);
		if (ld2 != null) {
			String firstName = ld2.getLeadAttrib("firstName");
			assertEquals(firstName, "abra");
		}
	}

	@Test
	public void t02getLeadByFullCookieTest() throws ClientProtocolException,
			IOException, MarketoException {
		Lead ld3;
		ld3 = networkClient.getLeadByCookie(restSrvr, at, cookies[1], fields);
		assertNotNull(ld3);
		if (ld3 != null) {
			String email = ld3.getLeadAttrib("email");
			assertEquals(email, "abra@dabra.com");
		}
		ld3 = androidClient.getLeadByCookie(restSrvr, at, cookies[1], fields);
		assertNotNull(ld3);
		if (ld3 != null) {
			String email = ld3.getLeadAttrib("email");
			assertEquals(email, "abra@dabra.com");
		}
	}

	@Test
	public void t03getLeadsbyIdTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by Id
		int[] lIds = { Integer.valueOf(leadIds[1]), Integer.valueOf(leadIds[2]) };
		ArrayList<Lead> leads1;
		leads1 = networkClient.getMultipleLeadsById(restSrvr, at, lIds, fields);
		assertNotNull(leads1);
		int i = 1;
		for (Lead lead : leads1) {
			String id = lead.getLeadAttrib("id");
			assertEquals(id, leadIds[i]);
			i++;
		}
		leads1 = null;
		leads1 = androidClient.getMultipleLeadsById(restSrvr, at, lIds, fields);
		assertNotNull(leads1);
		i = 1;
		for (Lead lead : leads1) {
			String id = lead.getLeadAttrib("id");
			assertEquals(id, leadIds[i]);
			i++;
		}
	}

	@Test
	public void t04getLeadsbyEmailTest() throws ClientProtocolException,
			IOException {
		try {
			// Get Multiple Leads by Email
			String[] emails = { "kmluce@gmail.com", "glen@marketo.com" };
			ArrayList<Lead> leads2;
			leads2 = networkClient.getMultipleLeadsByEmail(restSrvr, at,
					emails, fields);
			assertNotNull(leads2);
			ArrayList<String> results = new ArrayList<String>();
			for (Lead lead : leads2) {
				results.add(lead.getLeadAttrib("email"));
			}
			Collections.sort(results.subList(1, results.size()));
			this.assertArrayEquals(emails, results.toArray());
			leads2 = null;
			leads2 = androidClient.getMultipleLeadsByEmail(restSrvr, at,
					emails, fields);
			assertNotNull(leads2);
			results = new ArrayList<String>();
			for (Lead lead : leads2) {
				results.add(lead.getLeadAttrib("email"));
			}
			Collections.sort(results.subList(1, results.size()));
			this.assertArrayEquals(emails, results.toArray());
		} catch (MarketoException e) {
			fail(e.getErrorsAsString());
		}
	}

	@Test
	public void t05getLeadsbyCookieTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by Cookie
		ArrayList<Lead> leads3;
		leads3 = networkClient.getMultipleLeadsByCookie(restSrvr, at, cookies,
				fields);
		assertNotNull(leads3);
		assertEquals(leads3.size(), 1);
		leads3 = null;
		leads3 = androidClient.getMultipleLeadsByCookie(restSrvr, at, cookies,
				fields);
		assertNotNull(leads3);
		assertEquals(leads3.size(), 1);
	}

	@Test
	public void t06getLeadsbyFacebookIdTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by FacebookId
		ArrayList<Lead> leads;
		leads = networkClient.getMultipleLeadsByFacebookId(restSrvr, at, fbIds,
				fields);
		assertNotNull(leads);
		ArrayList<String> leadsRes = new ArrayList<String>();
		;
		for (Lead lead : leads) {
			leadsRes.add(lead.getLeadAttrib("facebookId"));
		}
		this.assertArrayEquals(fbIds, leadsRes.toArray());
		leads = null;
		leads = androidClient.getMultipleLeadsByFacebookId(restSrvr, at, fbIds,
				fields);
		assertNotNull(leads);
		leadsRes = new ArrayList<String>();
		;
		for (Lead lead : leads) {
			leadsRes.add(lead.getLeadAttrib("facebookId"));
		}
		this.assertArrayEquals(fbIds, leadsRes.toArray());
	}

	@Test
	public void t07getLeadsbyLinkedInIdTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by LinkedinId
		ArrayList<Lead> leads;
		leads = networkClient.getMultipleLeadsByLinkedinId(restSrvr, at, liIds,
				fields);
		assertNotNull(leads);
		assertEquals(leads.size(), 2);
		leads = null;
		leads = androidClient.getMultipleLeadsByLinkedinId(restSrvr, at, liIds,
				fields);
		assertNotNull(leads);
		assertEquals(leads.size(), 2);
	}

	@Test
	public void t08getLeadsbyTwitterIdTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by TwitterId
		ArrayList<Lead> leads;
		leads = networkClient
				.getMultipleLeadsByTwitterId(restSrvr, at, twIds, fields);
		assertNotNull(leads);
		assertEquals(leads.size(), 2);
		leads = null;
		leads = androidClient
				.getMultipleLeadsByTwitterId(restSrvr, at, twIds, fields);
		assertNotNull(leads);
		assertEquals(leads.size(), 2);
	}

}
