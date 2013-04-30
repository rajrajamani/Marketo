package common;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import models.Lead;
import models.Rule;
import models.SMSCampaign;
import play.Logger;
import play.libs.WS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marketo.mktows.client.MktServiceException;
import com.marketo.mktows.client.MktowsClientException;
import com.marketo.mktows.client.MktowsUtil;
import com.marketo.mktows.wsdl.ArrayOfAttribute;
import com.marketo.mktows.wsdl.Attrib;
import com.marketo.mktows.wsdl.LeadKey;
import com.marketo.mktows.wsdl.LeadKeyRef;
import com.marketo.mktows.wsdl.LeadRecord;
import com.marketo.mktows.wsdl.ResultSyncLead;
import com.twilio.sdk.TwilioRestException;

public class MarketoUtility {

	public static void main(String[] args) {
		MarketoUtility mu = new MarketoUtility();
		SMSCampaign sc = new SMSCampaign();
		sc.munchkinAccountId = Constants.MUNCHKINID;
		sc.soapUserId = Constants.SOAP_USER_ID;
		sc.soapEncKey = Constants.SOAP_ENC_KEY;
		sc.programName = Constants.PROG_NAME;
		sc.campaignToLogOutgoingRequests = Constants.OUTBOUND_CAMP;
		mu.requestCampaign(sc, sc.campaignToLogOutgoingRequests, 2,
				Constants.SMS_OUTBOUND, "abcdefg");

	}

	public SMSCampaign readSettings(String targetUrl) {
		play.libs.WS.HttpResponse res = WS.url(targetUrl).get();
		int status = res.getStatus();
		if (status != 200) {
			Logger.error("Unable to read settings from %s.  Got response %d",
					targetUrl, status);
			return null;
		}
		String retVal = res.getString();
		SMSCampaign sc = null;
		try {
			Gson gson = new GsonBuilder().create();
			sc = gson.fromJson(retVal, SMSCampaign.class);
			sc.campaignURL = targetUrl;
		} catch (Exception e) {
			Logger.error("Unable to parse %s into json", retVal);
			Logger.error("Exception is %s", e.getMessage());
			return sc;
		}
		List<Rule> ruleList = extractRules(sc);
		if (ruleList.size() == 0) {
			Logger.error("Unable to extract rules from json");
			return null;
		}
		return sc;
	}

	/*
	 * Example definition "mktSmsOut(this kicks off the
	 * campaign):::mktSmsIn(contains(stop,unsub)),mktSmsOut(we have unsubscribed
	 * you):::mktSmsIn(contains(optin, start)), mktSmsOut(you are now
	 * subscribed):::mktSmsIn(matches(votes for 1)),mktSmsOut(you voted for
	 * choice 1):::mktSmsIn(*),mktSmsOut(this is a default mesg)"
	 */
	private ArrayList<Rule> extractRules(SMSCampaign sc) {
		if (sc.smsCampaignDefinition == null
				|| "".equals(sc.smsCampaignDefinition)) {
			Logger.debug("campaign[%d] - Null or empty campaign definition",
					sc.id);
			return null;
		}
		String[] rulePair = sc.smsCampaignDefinition.split(":::");
		ArrayList<Rule> retVal = new ArrayList<Rule>();
		int cntr = 0;
		for (String pair : rulePair) {
			if (cntr == SMSCampaign.MAX_RULES) {
				Logger.debug(
						"campaign[%d] - Sorry, we will only accept the first %d rules",
						SMSCampaign.MAX_RULES);
				break;
			}
			int idxOut = pair.indexOf("mktSmsOut");
			int idxIn = pair.indexOf("mktSmsIn");
			String inRule = null;
			String outRule = null;
			if (idxIn != -1) {
				if (idxOut > idxIn) {
					int idxEndingParanthesis = pair.indexOf("),");
					if (idxEndingParanthesis == -1) {
						// something wrong if there is no closing paranthesis,
						// ignore this
						Logger.debug(
								"campaign[%d] - Mismatched paranthesis in rule pair %s",
								sc.id, pair);
						continue;
					}
					inRule = pair.substring(idxIn + 9, idxEndingParanthesis);
					Logger.debug("campaign[%d] - inRule[%d] %s", sc.id, cntr,
							inRule);
				} else {
					/*
					 * ignore this case where mktSmsOut is followed by mktSmsIn
					 * because it can be accomplished using a new rulePair
					 */
					Logger.error(
							"campaign[%d] will ignore %s - mktSmsIn must always preced mktSmsOut",
							sc.id, pair);
				}
			}
			if (idxOut != -1) {
				int idxEndingParanthesis = pair.lastIndexOf(")");
				if (idxEndingParanthesis == -1) {
					// something wrong if there is no closing paranthesis,
					// ignore this
					Logger.debug(
							"campaign[%d] - Mismatched paranthesis in rule pair %s",
							sc.id, pair);
					continue;
				}
				outRule = pair.substring(idxOut + 10, idxEndingParanthesis);
				Logger.debug("campaign[%d] - outRule[%d] %s", sc.id, cntr,
						outRule);

			}

			// if you got this far, we have an inRule and an outRule
			Rule newRule = new Rule(sc, inRule, outRule);
			sc.rules.add(newRule);
			retVal.add(newRule);
			cntr++;
		}
		Logger.info("campaign[%d] - parsed %d rules.  accepted %d", sc.id,
				cntr, retVal.size());
		return retVal;
	}

	public List<Lead> getLeadsFromStaticList(SMSCampaign sc) {
		Logger.info("campaign[%d] - trying to fetch leads from list %s", sc.id,
				sc.leadListWithPhoneNumbers);
		StreamPostionHolder posHolder = new StreamPostionHolder();
		List<LeadRecord> leadRecords = null;
		List<Lead> leadList = new ArrayList<Lead>();
		List<String> leadAttrs = new ArrayList<String>();
		leadAttrs.add("Email");
		leadAttrs.add("Phone");
		leadAttrs.add(Constants.UNSUB_FIELD_NAME);
		leadAttrs.add(Constants.COUNTRY_FIELD_NAME);

		try {
			MktowsClient client = makeSoapConnection(sc.id, sc.soapUserId,
					sc.soapEncKey, sc.munchkinAccountId);
			do {
				Logger.debug("campaign[%d] - get multiple leads from list :%s",
						sc.id, sc.leadListWithPhoneNumbers);
				String listName = sc.programName + "."
						+ sc.leadListWithPhoneNumbers;
				leadRecords = client.getMultipleLeads(Constants.BATCH_SIZE,
						listName, posHolder, leadAttrs);
				for (LeadRecord item : leadRecords) {
					Lead newLead = new Lead();
					newLead.munchkinId = sc.munchkinAccountId;
					newLead.leadId = item.getId();
					newLead.email = item.getEmail();
					Logger.debug("processing lead with id : %d", newLead.leadId);

					Map<String, Object> attrMap = null;
					ArrayOfAttribute aoAttribute = item.getLeadAttributeList();
					if (aoAttribute != null) {
						attrMap = MktowsUtil.getLeadAttributeMap(aoAttribute);
						if (attrMap != null && !attrMap.isEmpty()) {
							Set<String> keySet = attrMap.keySet();
							if (keySet.contains("Phone")) {
								newLead.phoneNumber = attrMap.get("Phone")
										.toString();
								Logger.debug(
										"lead with id : %d has phone number %s",
										newLead.leadId, newLead.phoneNumber);
							}
							if (keySet.contains(Constants.COUNTRY_FIELD_NAME)) {
								newLead.country = attrMap.get(
										Constants.COUNTRY_FIELD_NAME)
										.toString();
								Logger.debug(
										"lead with id : %d has country %s",
										newLead.leadId, newLead.country);
							} else {
								newLead.country = "USA";
								Logger.debug(
										"lead with id : %d does not have country.  Using USA",
										newLead.leadId);
							}
							if (keySet.contains(Constants.UNSUB_FIELD_NAME)) {
								String unsubValue = attrMap.get(
										Constants.UNSUB_FIELD_NAME).toString();
								if (unsubValue.equals("1")) {
									newLead.unsubscribed = true;
								} else {
									// should never come here
									newLead.unsubscribed = false;
								}
							} else {
								newLead.unsubscribed = false;
							}
							Logger.debug(
									"lead with id : %d has sms unsubscribed set to %s",
									newLead.leadId,
									String.valueOf(newLead.unsubscribed));
						}
					}
					newLead.save();
					leadList.add(newLead);
				}
				Logger.debug("campaign[%d] - retrieved and parsed %d leads",
						sc.id, leadRecords.size());
			} while (leadRecords.size() != 0);

			Logger.debug("campaign[%d] - returning %d leads", sc.id,
					leadList.size());
			return leadList;
		} catch (MktowsClientException e) {
			Logger.error(
					"campaign[%d] - Exception occurred while fetching leads from list: %s",
					sc.id, e.getMessage());
			return leadList;
		} catch (MktServiceException e) {
			Logger.error("campaign[%d] - Exception occurred: %s", sc.id,
					e.getLongMessage());
			return leadList;
		}
	}

	public ResultSyncLead createNewLead(SMSCampaign sc, String from) {
		ResultSyncLead result = null;
		try {
			HashMap<String, String> attrs = new HashMap<String, String>();
			attrs.put("Phone", from);
			LeadRecord leadRec = MktowsUtil.newLeadRecord(null, null, null,
					null, attrs);

			MktowsClient client = makeSoapConnection(sc.id, sc.soapUserId,
					sc.soapEncKey, sc.munchkinAccountId);
			Logger.debug(
					"campaign[%d] - calling sync lead on lead with phone number %s",
					sc.id, from);

			result = client.syncLead(leadRec, null, true);
		} catch (MktowsClientException e) {
			Logger.error("campaign[%d] - Exception occurred: %s", sc.id,
					e.getMessage());
			return null;
		} catch (MktServiceException e) {
			Logger.error("campaign[%d] - Exception occurred: %s", sc.id,
					e.getLongMessage());
			return null;
		}
		return result;
	}

	public void deleteLead(SMSCampaign sc, ResultSyncLead dummyLead) {

	}

	public MktowsClient makeSoapConnection(Long scid, String soapUserId,
			String soapEncKey, String munchkinAccountId) {
		Logger.debug(
				"campaign[%d] - making soap connection user:%s encKey:%s munchId:%s",
				scid, soapUserId, soapEncKey, munchkinAccountId);
		return new MktowsClient(soapUserId, soapEncKey, munchkinAccountId
				+ ".mktoapi.com");
	}

	public void requestCampaign(SMSCampaign sc, String campaignName,
			Integer leadId, Integer smsDirection, String body) {
		// Request that lead(s) be added to the campaign
		List<Attrib> tokenList = new ArrayList<Attrib>();
		Attrib token = null;
		token = MktowsUtil.objectFactory.createAttrib();
		if (smsDirection == Constants.SMS_INBOUND) {
			token.setName("my.inboundSMSText");
		} else {
			token.setName("my.outboundSMSText");
		}
		token.setValue(body);
		tokenList.add(token);

		LeadKey leadKey = MktowsUtil.objectFactory.createLeadKey();
		leadKey.setKeyType(LeadKeyRef.IDNUM);
		leadKey.setKeyValue(String.valueOf(leadId));
		List<LeadKey> leadList = new ArrayList<LeadKey>();
		leadList.add(leadKey);

		boolean success = false;
		try {
			MktowsClient client = makeSoapConnection(sc.id, sc.soapUserId,
					sc.soapEncKey, sc.munchkinAccountId);
			Logger.debug(
					"campaign[%d] - calling requestCampaign prog:%s campaign:%s #leads:%d token:%s",
					sc.id, sc.programName, campaignName, leadList.size(),
					token.getName());
			success = client.requestCampaign(sc.programName, campaignName,
					leadList, tokenList);
		} catch (MktowsClientException e) {
			Logger.error("campaign[%d] - Exception occurred: %s", sc.id,
					e.getMessage());
			return;
		} catch (MktServiceException e) {
			Logger.error("campaign[%d] - Exception occurred: %s", sc.id,
					e.getLongMessage());
			return;
		}
		if (success) {
			Logger.info("campaign[%d] - Lead %d added to campaign %s", sc.id,
					leadId, campaignName);
		} else {
			Logger.error("campaign[%d] - Failed to add lead %d to campaign %s",
					sc.id, leadId, campaignName);
		}
	}

	public boolean setLeadUnsubscribed(SMSCampaign sc, Integer leadId,
			String value) {
		Logger.debug("campaign[%d] - Fetching lead with id:%d", sc.id, leadId);
		List<Lead> leadList = Lead.find("munchkinId = ? and leadId = ? ",
				sc.munchkinAccountId, leadId).fetch();
		if (leadList != null) {
			for (Lead ld : leadList) {
				HashMap<String, String> attrs = new HashMap<String, String>();
				attrs.put(Constants.UNSUB_FIELD_NAME, value);
				LeadRecord leadRec = MktowsUtil.newLeadRecord(leadId, null,
						null, null, attrs);

				MktowsClient client = makeSoapConnection(sc.id, sc.soapUserId,
						sc.soapEncKey, sc.munchkinAccountId);
				try {
					Logger.info(
							"campaign[%d] - Setting sms unsubscribed:%s for lead:",
							sc.id, value, leadId);
					client.syncLead(leadRec, null, true);
				} catch (MktowsClientException e) {
					Logger.error("campaign[%d] - Exception occurred: %s",
							sc.id, e.getMessage());
				} catch (MktServiceException e) {
					Logger.error("campaign[%d] - Exception occurred: %s",
							sc.id, e.getLongMessage());
				}
				// set the flag locally as well
				ld.unsubscribed = Boolean.valueOf(value);
				ld.save();
				return ld.unsubscribed;
			}
		}
		// do not send if lead not present
		Logger.debug("Unable to find lead with leadId : %d in local database",
				leadId);
		return true;
	}

	public boolean getLeadSubscriptionSetting(SMSCampaign sc, Integer leadId) {
		Logger.debug("campaign[%d] - Fetching lead with id:%d", sc.id, leadId);
		List<Lead> leadList = Lead.find("munchkinId = ? and leadId = ? ",
				sc.munchkinAccountId, leadId).fetch();
		if (leadList != null) {
			for (Lead ld : leadList) {
				Logger.debug(
						"campaign[%d] - Returning unsubscribed:%s for id:%d",
						sc.id, String.valueOf(ld.unsubscribed), leadId);
				return ld.unsubscribed;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param sc
	 * @param leadList
	 * @param rule
	 * @param from
	 */
	public int performOutRule(SMSCampaign sc, Rule rule, List<Lead> leadList) {
		Boolean subscribeUser = false;
		Boolean unsubscribeUser = false;
		Boolean operationalCampaign = false;
		Boolean multiByteString = false;
		int numSent = 0;
		if (rule.outRule != null) {
			String payload = null;
			String[] keywords = rule.outRule.split("::");
			for (String word : keywords) {
				if (word.equals("mktUnsubscribe")) {
					Logger.debug("campaign[%d] - will unsubscribe user", sc.id);
					unsubscribeUser = true;
				}
				if (word.equals("mktSubscribe")) {
					Logger.debug("campaign[%d] - will subscribe user", sc.id);
					subscribeUser = true;
				}
				
				if (word.startsWith("operational(")) {
					int idxEndingParanthesis = word.lastIndexOf(")");
					if (idxEndingParanthesis == -1) {
						Logger.error(
								"campaign[%d] - no ending paranthesis for rule %s",
								sc.id, word);
					}
					operationalCampaign = true;
					Logger.debug("campaign[%d] - %s is an operational rule",
							sc.id, rule.outRule);
					payload = word.substring(12, idxEndingParanthesis);
					Logger.debug("campaign[%d] - payload is %s", sc.id, payload);
				} else {
					payload = rule.outRule;
					Logger.debug("campaign[%d] - payload is %s", sc.id, payload);
				}
				multiByteString = isPayloadMultiByte(payload);
				Logger.debug(
						"campaign[%d] - payload is using multi-byte string = [%s]",
						sc.id, String.valueOf(multiByteString));
				if (sc.smsFooter != null && !sc.smsFooter.equals("null")) {
					payload = payload.concat(sc.smsFooter);
					int maxlen = (multiByteString ? Constants.SMS_MAX_LEN / 2
							: Constants.SMS_MAX_LEN);
					if (payload.length() > maxlen) {
						payload = payload.substring(0, maxlen); // max SMS
																// length
					}
					Logger.debug(
							"campaign[%d] - payload with footer is %s.  Length [%d] is less than max [%d]",
							sc.id, payload, payload.length(), maxlen);
				}
			}
			try {
				for (Lead ld : leadList) {
					if (unsubscribeUser) {
						Logger.debug(
								"campaign[%d] - Unsubscribing user with phone %s",
								sc.id, ld.phoneNumber);
						setLeadUnsubscribed(sc, ld.leadId, "true");
						ld.unsubscribed = true;
					}
					if (subscribeUser) {
						Logger.debug(
								"campaign[%d] - Subscribing user with phone %s",
								sc.id, ld.phoneNumber);
						setLeadUnsubscribed(sc, ld.leadId, "false");
						ld.unsubscribed = false;
					}
					if (operationalCampaign || ld.unsubscribed == false) {
						Logger.debug(
								"campaign[%d] - Sending message to %s : payload %s",
								sc.id, ld.phoneNumber, payload);
						TwilioUtility.sendSMS(sc.smsGatewayID,
								sc.smsGatewayPassword,
								sc.smsGatewayPhoneNumber, ld.phoneNumber,
								ld.country, payload);
						MarketoUtility mu = new MarketoUtility();
						Logger.debug(
								"campaign[%d] - Requesting campaign %s for lead with id %d",
								sc.id, sc.campaignToLogOutgoingRequests,
								ld.leadId);
						mu.requestCampaign(sc,
								sc.campaignToLogOutgoingRequests, ld.leadId,
								Constants.SMS_OUTBOUND, payload);
						Logger.debug(
								"campaign[%d] - Request campaign %s succeeded",
								sc.id, sc.campaignToLogOutgoingRequests);
						numSent++;
					} else {
						Logger.info(
								"campaign[%d] - Not sending message to %s because lead has unsubscribed",
								sc.id, ld.phoneNumber);
					}
				}
			} catch (TwilioRestException e) {
				Logger.error("campaign[%d] - Error talking to Twilio %s",
						sc.id, e.getMessage());
			}

		}
		return numSent;
	}

	private Boolean isPayloadMultiByte(String str) {
		char[] c_array;
		String c_string;
		byte[] c_byte_array;
		Boolean result = false;

		c_array = str.toCharArray();
		result = false;
		for (char c : c_array) {
			c_string = Character.toString(c);
			try {
				c_byte_array = c_string.getBytes("UTF-8");
				if (c_byte_array.length > 1) {
					Logger.debug(
							"Detected a multibyte character in payload [%s]",
							str);
					result = true;
					break;
				}
			} catch (UnsupportedEncodingException e) {
				Logger.error(
						"Unable to detect multibyte character due to exception [%s]",
						e.getMessage());
			}
		}
		return result;
	}

}
