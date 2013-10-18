package jobs;

import java.util.List;

import common.Constants;
import common.MarketoUtility;

import models.Lead;
import models.Rule;
import models.SMSCampaign;
import play.Logger;
import play.jobs.Job;

public class SyncListAndRunFirstSMSRule extends Job {

	private SMSCampaign sc;

	public SyncListAndRunFirstSMSRule(SMSCampaign sc) {
		this.sc = sc;
	}

	public void doJob() {
		MarketoUtility mu = new MarketoUtility();
		Logger.info("campaign[%d] - Fetching leads from static list %s", sc.id,
				sc.leadListWithPhoneNumbers);
		String[] fields = new String[] { "Email", sc.phoneNumFieldApiName,
				Constants.UNSUB_FIELD_NAME, Constants.COUNTRY_FIELD_NAME };

		List<Lead> leadList = mu.fetchFromStaticListForSms(sc.soapUserId,
				sc.soapEncKey, sc.munchkinAccountId, sc.id, sc.programName,
				sc.leadListWithPhoneNumbers, fields, sc.phoneNumFieldApiName);
		Logger.info(
				"campaign[%d] - Finished fetching leads from static list %s",
				sc.id, sc.leadListWithPhoneNumbers);

		new RunFirstSMSRule(sc.id, leadList).now();

		return;
	}

	// private void runCampaign(MarketoUtility mu, Rule rule, List<Lead>
	// leadList) {
	// int numSent = mu.performOutRule(sc, rule, leadList);
	// SMSCampaign toSave = SMSCampaign.findById(sc.id);
	// toSave.numSent += numSent;
	// toSave.save();
	// }
}
