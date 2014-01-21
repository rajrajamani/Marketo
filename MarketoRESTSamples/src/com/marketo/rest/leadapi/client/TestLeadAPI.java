package com.marketo.rest.leadapi.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;

import com.marketo.rest.oauth.client.AuthToken;
import com.marketo.rest.oauth.client.IdentityClient;

public class TestLeadAPI {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		try {
			Properties properties = new Properties();
			properties.load(TestLeadAPI.class
					.getResourceAsStream("/default.properties"));
			String clientId = properties.getProperty("CLIENT_ID");
			String clientSecret = properties.getProperty("CLIENT_SECRET");
			String idSrvr = properties.getProperty("ID_SRVR");
			String grantUri = properties.getProperty("GRANT_TOKEN_URI");
			String restSrvr = properties.getProperty("REST_SRVR");

			String url = idSrvr + grantUri;
			AuthToken at = IdentityClient.getAuthToken(url, clientId,
					clientSecret);

			String fields[] = new String[] { "firstName", "lastName", "email",
					"facebookId", "linkedinId", "twitterId" };

			// Get Lead by Id
			System.out.println("TEST - get Lead by Id");
			Lead ld1 = LeadAPI.getLeadById(restSrvr, at, 60, fields);
			if (ld1 != null) {
				ld1.printLeadAttributes();
				String lastName = ld1.getLeadAttrib("lastName");
				System.out.println(lastName);
			}
			System.out.println("COMPLETED - get Lead by Id");

			// Get Lead by Cookie
			System.out.println("TEST - get Lead by Cookie");
			Lead ld2 = LeadAPI.getLeadByCookie(restSrvr, at,
					"token:_mch-marketo.com-1390324071622-56079", fields);
			if (ld2 != null) {
				ld2.printLeadAttributes();
			}
			System.out.println("COMPLETED - get Lead by Cookie");

			System.out.println("TEST - get Lead by Cookie (Full)");
			String fullCookie = "id:287-GTJ-838%26token:_mch-marketo.com-1390324071622-56079";
			// fullCookie = URLEncoder.encode(fullCookie, "ISO-8859-1");
			Lead ld3 = LeadAPI
					.getLeadByCookie(restSrvr, at, fullCookie, fields);
			if (ld3 != null) {
				ld3.printLeadAttributes();
			}
			System.out.println("TEST - get Lead by Cookie (Full)");

			// Get Multiple Leads by Id
			System.out.println("TEST - get Multiple Leads by Id");
			int[] leadIds = { 17, 24 };
			ArrayList<Lead> leads1 = LeadAPI.getMultipleLeadsById(restSrvr, at,
					leadIds, fields);
			for (Lead lead : leads1) {
				lead.printLeadAttributes();
			}
			System.out.println("COMPLETED - get Multiple Leads by Id");

			// Get Multiple Leads by Email
			System.out.println("TEST - get Multiple Leads by Email");
			String[] emails = { "kmluce@gmail.com", "glen@marketo.com" };
			ArrayList<Lead> leads2;
			leads2 = LeadAPI.getMultipleLeadsByEmail(restSrvr, at, emails,
					fields);
			for (Lead lead : leads2) {
				lead.printLeadAttributes();
			}
			System.out.println("COMPLETED - get Multiple Leads by Id");

			// Get Multiple Leads by Cookie
			System.out.println("TEST - get Multiple Leads by Cookie");
			String[] cks = new String[] {
					"token:_mch-marketo.com-1390324071622-56079",
					"id:287-GTJ-838%26token:_mch-marketo.com-1390324071622-56079" };
			// fullCookie = URLEncoder.encode(fullCookie, "ISO-8859-1");
			ArrayList<Lead> leads3;
			leads3 = LeadAPI
					.getMultipleLeadsByCookie(restSrvr, at, cks, fields);
			for (Lead lead : leads3) {
				lead.printLeadAttributes();
			}
			System.out.println("Returned " + leads3.size() + " leads");
			System.out.println("TEST - get Multiple Leads by Cookie");

			// Get Multiple Leads by FacebookId
			System.out.println("TEST - get Multiple Leads by FacebookId");
			String[] fbIds = new String[] { "345" };
			// fullCookie = URLEncoder.encode(fullCookie, "ISO-8859-1");
			ArrayList<Lead> leads4;
			leads4 = LeadAPI.getMultipleLeadsByFacebookId(restSrvr, at, fbIds,
					fields);
			for (Lead lead : leads4) {
				lead.printLeadAttributes();
			}
			System.out.println("TEST - get Multiple Leads by FacebookId");

			// Get Multiple Leads by FacebookId
			System.out.println("TEST - get Multiple Leads by LinkedinId");
			String[] liIds = new String[] { "678", "91011" };
			// fullCookie = URLEncoder.encode(fullCookie, "ISO-8859-1");
			ArrayList<Lead> leads5;
			leads5 = LeadAPI.getMultipleLeadsByLinkedinId(restSrvr, at, liIds,
					fields);
			for (Lead lead : leads5) {
				lead.printLeadAttributes();
			}
			System.out.println("TEST - get Multiple Leads by LinkedinId");

			// Get Multiple Leads by FacebookId
			System.out.println("TEST - get Multiple Leads by TwitterId");
			String[] twIds = new String[] { "123" };
			// fullCookie = URLEncoder.encode(fullCookie, "ISO-8859-1");
			ArrayList<Lead> leads6;
			leads6 = LeadAPI.getMultipleLeadsByTwitterId(restSrvr, at, twIds,
					fields);
			for (Lead lead : leads6) {
				lead.printLeadAttributes();
			}
			System.out.println("TEST - get Multiple Leads by TwitterId");

		} catch (MarketoException e) {
			System.out.println("REST Error.  Id:" + e.getRequestId());
			e.printStackTrace();
		}

	}
}
