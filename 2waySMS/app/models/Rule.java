package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Rule extends Model {

	
	public String inRule;
	public String outRule;
	
	@ManyToOne
	public SMSCampaign sc;

	public Rule(SMSCampaign camp, String in, String out) {
		sc = camp;
		inRule = in;
		outRule = out;
	}

	public Rule(Rule rule) {
		sc = null;
		inRule = rule.inRule;
		outRule = rule.outRule;
	}
}
