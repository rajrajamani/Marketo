package jobs;

import java.util.List;

import models.FormulaCampaign;
import play.Logger;
import play.jobs.Job;

import com.marketo.mktows.wsdl.LeadRecord;

import common.Constants;
import common.ExecStatus;
import common.MarketoUtility;

public class SyncListAndExecFormula extends Job {

	private FormulaCampaign fc;

	public SyncListAndExecFormula(FormulaCampaign fc) {
		this.fc = fc;
	}

	public void doJob() {
		MarketoUtility mu = new MarketoUtility();
		Logger.info("campaign[%d] - Fetching leads from static list %s", fc.id,
				fc.leadList);
		ExecStatus initStat = new ExecStatus("Executing Formula", 0);
		initStat.errMsg = "Executing Formula";
		String status = Constants.CAMPAIGN_STATUS_ACTIVE; 
		updateStatus(fc.id, initStat, status);

		// List<LeadRecord> leadList = mu.fetchFromStaticList(fc.soapUserId,
		// fc.soapEncKey, fc.munchkinAccountId, fc.id, fc.programName,
		// fc.leadList);
		// Logger.info("campaign[%d] - Finished fetching leads from static list."
		// + "  %d leads retrieved", fc.id, leadList.size());
		//

		ExecStatus eStatus = mu.executeFormula(fc);

		status = Constants.CAMPAIGN_STATUS_COMPLETED;
		updateStatus(fc.id, eStatus, status);

		return;
	}

	private void updateStatus(Long id, ExecStatus errStatus, String status) {
		FormulaCampaign fcCampaign = FormulaCampaign.findById(fc.id);
		fcCampaign.statusMessage = errStatus.errMsg;
		fcCampaign.numLeadsSynced = errStatus.numLeadsSynced;
		fcCampaign.status = status;
		fcCampaign.save();
	}

}
