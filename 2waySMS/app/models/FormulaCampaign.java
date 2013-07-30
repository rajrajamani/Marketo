package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class FormulaCampaign extends Model {
	public FormulaCampaign() {
	}

	public static final int MAX_RULES = 25;

	public String campaignURL; // provided by marketer
	public String munchkinAccountId; // from token
	public String soapEncKey;
	public String soapUserId;
	@Column(length = 2000)
	public String formula;
	public String leadList;
	public String programName;
	public String statusMessage;
	public String status;

	public int numLeadsSynced;
}
