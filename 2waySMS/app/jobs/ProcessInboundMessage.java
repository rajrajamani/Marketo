package jobs;

import java.util.ArrayList;
import java.util.List;

import models.Lead;
import models.Rule;
import models.SMSCampaign;
import play.Logger;
import play.jobs.Job;

import com.marketo.mktows.wsdl.ResultSyncLead;
import common.Constants;
import common.MarketoUtility;

public class ProcessInboundMessage extends Job {

	private SMSCampaign sc;
	private String from;
	private String body;
	private String accountSid;

	/**
	 * 
	 * @param sc
	 * @param accountsSid
	 * @param from
	 * @param body
	 */
	public ProcessInboundMessage(SMSCampaign sc, String accountsSid,
			String from, String body) {
		this.sc = sc;
		this.from = from;
		this.body = body;
		this.accountSid = accountsSid;
	}

	public void doJob() {
		Logger.info(
				"campaign[%d] - Processing inbound message from phoneNumber %s",
				sc.id, from);
		Logger.debug("campaign[%d] - Looking up lead with phone number %s",
				sc.id, from);
		// look up lead in list, if not present, create new lead and add to list
		List<Lead> leadList = Lead.find("munchkinId = ? and phoneNumber = ? ",
				sc.munchkinAccountId, from).fetch();
		MarketoUtility mu = new MarketoUtility();
		Lead ld = null;
		if (leadList == null || leadList.size() == 0) {
			Logger.debug(
					"campaign[%d] - Did not find lead with phone number %s",
					sc.id, from);
			ResultSyncLead leadCreated = mu.createNewLead(sc, from);
			if (leadCreated != null) {
				Lead newLead = new Lead();
				newLead.munchkinId = sc.munchkinAccountId;
				newLead.phoneNumber = from;
				newLead.leadId = leadCreated.getLeadId();
				newLead.unsubscribed = false;
				newLead.save();
				ld = newLead;
				Logger.debug(
						"campaign[%d] - created new lead with lead id: %d",
						sc.id, newLead.leadId);
			}
		} else {
			ld = leadList.get(0);
		}

		// Log an activity
		if (ld.leadId != -1) {
			Logger.info(
					"campaign[%d] - Requesting campaign %s with lead id %d",
					sc.id, sc.campaignToLogIncomingRequests, ld.leadId);
			mu.requestCampaign(sc, sc.campaignToLogIncomingRequests, ld.leadId,
					Constants.SMS_INBOUND, body);
		}
		parseBodyAndRespond(sc, ld, accountSid, from, body);
	}

	private static void parseBodyAndRespond(SMSCampaign sc, Lead lead,
			String accountSid, String from, String payload) {
		Logger.info("campaign[%d] - Parsing message %s from phoneNumber %s",
				sc.id, payload, from);

		List<Rule> rules = sc.rules;
		MarketoUtility mu = new MarketoUtility();
		if (payload == null)
			return;

		/* special cases */
		if (payload.equalsIgnoreCase("stop")
				|| payload.equalsIgnoreCase("unsubscribe")) {
			Logger.info("campaign[%d] - lead %s unsubscribed with message %s",
					sc.id, from, payload);
			mu.setLeadUnsubscribed(sc, lead.leadId, "true");
		}
		if (payload.equalsIgnoreCase("optin") || payload.equalsIgnoreCase("start")) {
			Logger.info("campaign[%d] - lead %s subscribed with message %s",
					sc.id, from, payload);
			mu.setLeadUnsubscribed(sc, lead.leadId, "false");
		}

		List<Lead> ldList = new ArrayList<Lead>();
		ldList.add(lead);
		boolean processed = false;

		int cnt = 0;
		for (Rule rule : rules) {
			Logger.debug("campaign[%d] - Evaluating rule # : %d", sc.id, cnt++);
			if (rule.inRule == null) {
				continue;
			}

			if (rule.inRule.startsWith("contains")) {
				int idx1 = rule.inRule.indexOf("(");
				int idx2 = rule.inRule.indexOf(")");
				String keylist = rule.inRule.substring(idx1 + 1, idx2);
				String[] keywords = keylist.split(",");
				for (String word : keywords) {
					if (payload.contains(word)) {
						Logger.debug("campaign[%d] - %s Matched rule # %d:%s",
								sc.id, payload, cnt++, rule.inRule);
						// perform outrule
						mu.performOutRule(sc, rule, ldList);
						processed = true;
						break;
					}
				}
			}

			if (rule.inRule.startsWith("matches")) {
				int idx1 = rule.inRule.indexOf("(");
				int idx2 = rule.inRule.indexOf(")");
				String matchThis = rule.inRule.substring(idx1 + 1, idx2);
				if (payload.equals(matchThis)) {
					Logger.debug("campaign[%d] - %s Matched rule # %d:%s",
							sc.id, payload, cnt++, rule.inRule);
					// perform outrule
					mu.performOutRule(sc, rule, ldList);
					processed = true;
					break;
				}
			}

			if (rule.inRule.equals("*")) {
				Logger.debug("campaign[%d] - %s Matched rule # %d:%s",
						sc.id, payload, cnt++, rule.inRule);
				// perform outrule
				mu.performOutRule(sc, rule, ldList);
				processed = true;
				break;
			}
		}
		if (processed == false) {
			Logger.info("campaign[%d] - %s did not match any rule", sc.id, payload);
		}
	}
}
