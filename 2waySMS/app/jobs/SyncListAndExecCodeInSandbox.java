package jobs;

import java.util.List;

import models.FormulaCampaign;
import play.Logger;
import play.jobs.Job;

import com.marketo.mktows.wsdl.LeadRecord;
import common.MarketoUtility;

public class SyncListAndExecCodeInSandbox extends Job {

	private FormulaCampaign fc;

	public SyncListAndExecCodeInSandbox(FormulaCampaign fc) {
		this.fc = fc;
	}

	public void doJob() {
		MarketoUtility mu = new MarketoUtility();
		Logger.info("campaign[%d] - Fetching leads from static list %s", fc.id,
				fc.leadList);
		List<LeadRecord> leadList = mu.fetchFromStaticList(fc.soapUserId,
				fc.soapEncKey, fc.munchkinAccountId, fc.id, fc.programName,
				fc.leadList);
		Logger.info("campaign[%d] - Finished fetching leads from static list."
				+ "  %d leads retrieved", fc.id, leadList.size());

		List<LeadRecord> processedList = mu.executeFunctionInSandBox(fc.soapUserId,
				fc.soapEncKey, fc.munchkinAccountId, fc.id, fc.formula,
				leadList);

		return;
	}

}
