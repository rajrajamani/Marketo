package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class SMSCampaign extends Model {
	public SMSCampaign(SMSCampaign ms) {
		campaignURL = ms.campaignURL;
		smsGatewayApplicationId = ms.smsGatewayApplicationId;
		munchkinAccountId = ms.munchkinAccountId;
		soapUserId = ms.soapUserId;
		soapEncKey = ms.soapEncKey;
		programName = ms.programName;
		campaignToLogIncomingRequests = ms.campaignToLogIncomingRequests;
		campaignToLogOutgoingRequests = ms.campaignToLogOutgoingRequests;
		leadListWithPhoneNumbers = ms.leadListWithPhoneNumbers;
		smsGatewayID = ms.smsGatewayID;
		smsGatewayPassword = ms.smsGatewayPassword;
		smsGatewayPhoneNumber = ms.smsGatewayPhoneNumber;
		smsFooter = ms.smsFooter;
		smsCampaignDefinition = ms.smsCampaignDefinition;
		phoneNumFieldApiName = ms.phoneNumFieldApiName;
		status = ms.status;
		numSent = ms.numSent;
		numRecvd = ms.numRecvd;
		
		for (Rule rule: ms.rules) {
			rules.add(new Rule(rule));
		}
	}

	public SMSCampaign() {
	}

	public static final int MAX_RULES = 25;

	public String campaignURL; // provided by marketer
	public String smsGatewayApplicationId; // fetched from gateway
	public String munchkinAccountId; // from token
	public String soapUserId; // from token
	public String soapEncKey; // from token
	public String programName; // from token
	public String campaignToLogIncomingRequests; // from token
	public String campaignToLogOutgoingRequests; // from token
	public String leadListWithPhoneNumbers; // from token
	public String smsGatewayID; // from token
	public String smsGatewayPassword; // from token
	public String smsGatewayPhoneNumber; // from token
	public String smsFooter; // from token
	@Column(length=1000) 
	public String smsCampaignDefinition; // from token
	public String phoneNumFieldApiName; // from token
	public String status;
	public int numSent;
	public int numRecvd;

	@OneToMany (cascade=CascadeType.ALL, mappedBy="sc", fetch=FetchType.EAGER)
	public List<Rule> rules = new ArrayList<Rule>();
}