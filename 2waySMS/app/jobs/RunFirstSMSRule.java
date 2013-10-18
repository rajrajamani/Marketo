package jobs;

import java.util.List;

import common.Constants;
import common.MarketoUtility;

import models.Lead;
import models.Rule;
import models.SMSCampaign;
import play.Logger;
import play.jobs.Job;

public class RunFirstSMSRule extends Job {

	public RunFirstSMSRule(Long scId, List<Lead> leadList) {
		MarketoUtility mu = new MarketoUtility();

		SMSCampaign sc = SMSCampaign.findById(scId);
		Rule firstRule = sc.rules.get(0);
		if (firstRule.inRule == null && firstRule.outRule != null) {
			Logger.info(
					"campaign[%d] - Running outbound campaign for campaign %s",
					sc.id, firstRule.outRule);
			// outgoing campaign
			int numSent = mu.performOutRule(sc, firstRule, leadList);
			sc.numSent += numSent;
			sc.save();

			Logger.info("campaign[%d] - Finished running outbound campaign %s",
					sc.id, firstRule.outRule);
		} else if (firstRule.inRule != null) {
			Logger.info(
					"campaign[%d] - First rule acts only on inbound messages",
					sc.id);
		}

	}
}
