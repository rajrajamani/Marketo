import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import play.Play;
import play.test.FunctionalTest;

import com.marketo.rest.base.LeadAPIBase;
import com.marketo.rest.leadapi.client.Lead;
import com.marketo.rest.leadapi.client.LeadRequest;
import com.marketo.rest.leadapi.client.LeadResponse;
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
	private LeadAPIBase[] restClients;
	private String listId;

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
		listId = Play.configuration.getProperty("LISTID");

		restClients = new LeadAPIBase[1];
		restClients[0] = com.marketo.rest.network.LeadAPI.getInstance();
		// restClients[1] = com.marketo.rest.android.LeadAPI.getInstance();

		url = idSrvr + grantUri;
		at = IdentityClient.getInstance().getAuthToken(url, clientId,
				clientSecret);

		fields = new String[] { "firstName", "lastName", "email", "facebookId",
				"linkedinId", "twitterId" };

	}

	@Test
	public void t00getLeadByIdTest() throws ClientProtocolException,
			IOException {
		// Get Lead by Id
		Lead ld1;
		try {
			for (LeadAPIBase lbc : restClients) {
				ld1 = null;
				ld1 = lbc.getLeadById(restSrvr, at,
						Integer.valueOf(leadIds[0]), fields);
				assertNotNull(ld1);
				if (ld1 != null) {
					String lastName = ld1.getLeadAttrib("lastName");
					assertEquals(lastName, "Wugsy24");
				}
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
	public void t03getLeadsbyIdTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by Id
		int[] lIds = { Integer.valueOf(leadIds[1]), Integer.valueOf(leadIds[2]) };
		for (LeadAPIBase lbc : restClients) {
			LeadResponse lr = null;
			ArrayList<Lead> leads1 = null;
			lr = lbc.getMultipleLeadsById(restSrvr, at, lIds, fields);
			leads1 = lr.getLeads();
			assertNotNull(leads1);
			int i = 1;
			for (Lead lead : leads1) {
				String id = lead.getLeadAttrib("id");
				assertEquals(id, leadIds[i]);
				i++;
			}
		}
	}

	@Test
	public void t04getLeadsbyEmailTest() throws ClientProtocolException,
			IOException {
		try {
			// Get Multiple Leads by Email
			String[] emails = { "kmluce@gmail.com", "glen@marketo.com" };
			for (LeadAPIBase lbc : restClients) {
				LeadResponse lr = null;
				lr = lbc.getMultipleLeadsByEmail(restSrvr, at, emails, fields);
				assertNotNull(lr.getLeads());
				ArrayList<String> results = new ArrayList<String>();
				for (Lead lead : lr.getLeads()) {
					results.add(lead.getLeadAttrib("email"));
				}
				Collections.sort(results.subList(1, results.size()));
				this.assertArrayEquals(emails, results.toArray());
			}
		} catch (MarketoException e) {
			fail(e.getErrorsAsString());
		}
	}

	@Test
	public void t05getLeadsbyCookieTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by Cookie
		for (LeadAPIBase lbc : restClients) {
			LeadResponse lr = null;
			lr = lbc.getMultipleLeadsByCookie(restSrvr, at, cookies, fields);
			ArrayList<Lead> leads = lr.getLeads();
			assertNotNull(leads);
			assertEquals(leads.size(), 1);
		}
	}

	@Test
	public void t06getLeadsbyFacebookIdTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by FacebookId
		for (LeadAPIBase lbc : restClients) {
			LeadResponse lr = null;
			lr = lbc.getMultipleLeadsByFacebookId(restSrvr, at, fbIds, fields);
			ArrayList<Lead> leads = lr.getLeads();
			assertNotNull(leads);
			ArrayList<String> leadsRes = new ArrayList<String>();

			for (Lead lead : leads) {
				leadsRes.add(lead.getLeadAttrib("facebookId"));
			}
			this.assertArrayEquals(fbIds, leadsRes.toArray());
		}
	}

	@Test
	public void t07getLeadsbyLinkedInIdTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by LinkedinId
		for (LeadAPIBase lbc : restClients) {
			LeadResponse lr = null;
			lr = lbc.getMultipleLeadsByLinkedinId(restSrvr, at, liIds, fields);
			ArrayList<Lead> leads = lr.getLeads();
			assertNotNull(leads);
			assertEquals(leads.size(), 2);
		}
	}

	@Test
	public void t08getLeadsbyTwitterIdTest() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads by TwitterId
		for (LeadAPIBase lbc : restClients) {
			LeadResponse lr = null;
			lr = lbc.getMultipleLeadsByTwitterId(restSrvr, at, twIds, fields);
			ArrayList<Lead> leads = lr.getLeads();
			assertNotNull(leads);
			assertEquals(leads.size(), 2);
		}
	}

	@Test
	public void t09getLeadsFromList() throws ClientProtocolException,
			IOException, MarketoException {
		// Get Multiple Leads from List
		int lid = Integer.valueOf(listId);
		for (LeadAPIBase lbc : restClients) {

			LeadResponse lr = null;
			ArrayList<Lead> leads = null;
			int batchSize = 299;
			int retSz = 0;
			int total = 0;
			String next = null;
			do {
				lr = lbc.getMultipleLeadsFromStaticList(restSrvr, at, lid,
						batchSize, next, fields);
				if (lr != null) {
					leads = lr.getLeads();
					if (leads != null && !leads.isEmpty()) {
						next = lr.nextPageToken;
						retSz = leads.size();
						total += retSz;
					} else {
						retSz = 0;
						break;
					}
				}
				assertNotNull(leads);
				assertTrue(retSz <= batchSize);
				System.out.println("Retrieved : " + retSz
						+ " leads from list :" + listId);
				System.out.println("Retrieved total: " + total
						+ " leads from list :" + listId);
				leads = null;
				try {
					/*
					 * Without this, we will hit peak limit and fail when
					 * pulling from large lists
					 */
					Thread.sleep(225);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (retSz > 0);
			System.out.println("Retrieved total: " + total
					+ " leads from list :" + listId);
		}
	}

	@Test
	public void t10syncLeadsCreateOnly() throws ClientProtocolException,
			IOException, MarketoException {
		ArrayList<Lead> createLeads = createLeads(100);
		for (LeadAPIBase lbc : restClients) {

			ArrayList<Lead> leads = null;
			LeadResponse lr = null;
			LeadRequest lreq = new LeadRequest(LeadRequest.CREATE_ONLY,
					"email", createLeads);
			System.out.println(lreq.getJsonString());

			lr = lbc.syncMultipleLeads(restSrvr, at, lreq);
			leads = lr.getLeads();
			assertNotNull(leads);
			for (Lead ld : leads) {
				ld.printLeadAttributes();
			}
		}
	}

	private ArrayList<Lead> createLeads(int batchSize) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date date = new Date();
		String prefix = dateFormat.format(date);
		ArrayList<Lead> leads = new ArrayList<Lead>();

		for (int i = 0; i < batchSize; i++) {
			String em = prefix + i + "@mktoapitesting.com";
			int leadScore = i;
			HashMap<String, String> attrMap = new HashMap<String, String>();
			attrMap.put("email", em);
			attrMap.put("leadScore", String.valueOf(leadScore));
			Lead ld = new Lead(attrMap);
			leads.add(ld);
		}

		return leads;
	}

}
