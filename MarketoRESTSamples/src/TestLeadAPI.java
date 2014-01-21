import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

public class TestLeadAPI {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		try {
			AuthToken at = IdentityServer.getAuthToken(Constants.CLIENT_ID,
					Constants.CLIENT_SECRET);

			// Get Lead by Id
			Lead ld1 = LeadAPI.getLeadById(at, 60);
			if (ld1 != null) {
				ld1.printLeadAttributes();
				String lastName = ld1.getLeadAttrib("lastName");
				System.out.println(lastName);
			}
			
			// Get Lead by Cookie
			Lead ld2 = LeadAPI.getLeadByCookie(at,
					"token:_mch-marketo.com-1390324071622-56079");
			if (ld2 != null) {
				ld2.printLeadAttributes();
			}

			String fullCookie = "id:287-GTJ-838&token:_mch-marketo.com-1390325610448-75476";
			fullCookie = URLEncoder.encode(fullCookie, "ISO-8859-1");
			Lead ld3 = LeadAPI.getLeadByCookie(at, fullCookie);
			if (ld3 != null) {
				ld3.printLeadAttributes();
			}

			// Get Multiple Leads by Id
			int[] leadIds = { 17, 24 };
			ArrayList<Lead> leads1 = LeadAPI.getMultipleLeadsById(at, leadIds);
			for (Lead lead : leads1) {
				lead.printLeadAttributes();
			}

			// Get Multiple Leads by Email
			String[] emails = { "kmluce@gmail.com", "glen@marketo.com" };
			ArrayList<Lead> leads2;
			leads2 = LeadAPI.getMultipleLeadsByEmail(at, emails);
			for (Lead lead : leads2) {
				lead.printLeadAttributes();
			}

		} catch (MarketoException e) {
			System.out.println("REST Error.  Id:" + e.getRequestId());
			e.printStackTrace();
		}

	}

}
