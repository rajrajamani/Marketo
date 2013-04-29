package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.marketo.mktows.wsdl.MktowsPort;
import com.marketo.mktows.wsdl.ResultSyncLead;
import com.twilio.sdk.resource.list.AccountList;

import jobs.ProcessInboundMessage;
import jobs.SyncListAndRunFirstCampaign;
import models.SMSCampaign;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Router;

import common.Constants;
import common.MarketoUtility;
import common.TwilioUtility;

public class Application extends Controller {

	public static void index(String url) {

		if (url == null) {
			render();
		} else {
			Logger.info("Looking up campaignURL %s", url);
			// Check to see if this has been configured previously
			List<SMSCampaign> msExisting = SMSCampaign.find(
					"campaignURL = ? and status = ?", url,
					Constants.SMSCAMPAIGN_STATUS_ACTIVE).fetch();
			if (msExisting.size() == 1) {
				SMSCampaign ms = msExisting.get(0);
				Logger.info("campaign[%d] was configured previously", ms.id);
				/*
				 * renderText(
				 * "This campaign has been configured previously for account :"
				 * + ms.munchkinAccountId);
				 */
				savedConfig(ms.id);
			}

			MarketoUtility mu = new MarketoUtility();
			SMSCampaign sc = mu.readSettings(url);
			if (sc == null) {
				Logger.info("Unable to read settings from %s", url);
				renderText("Unable to read settings from %s", url);
			} else if (sc.munchkinAccountId.equals("null")) {
				Logger.info("campaign[%d] does not have a valid munchkin id",
						sc.id);
				renderText("Please provide the correct munchkin account id");
			} else if (sc.smsGatewayID.equals("null")
					|| sc.smsGatewayPassword.equals("null")) {
				Logger.info(
						"campaign[%d] does not have the sms Gateway id or password",
						sc.id);
				renderText("Please configure a valid twilio Account ID and Secret in your program my tokens");
			} else if (sc.soapUserId.equals("null")
					|| sc.soapEncKey.equals("null")) {
				Logger.info(
						"campaign[%d] does not have the Marketo soap credentials",
						sc.id);
				renderText("Please provide your Marketo soap credentials");
			} else if (sc.rules.size() == 0) {
				Logger.info("campaign[%d] does not have any campaign rules",
						sc.id);
				renderText("Please provide rules for the SMS campaign");
			} else if (sc.leadListWithPhoneNumbers.equals("null")) {
				Logger.info("campaign[%d] does not lead list set", sc.id);
				renderText("Please provide a static list from with leads whose phone numbers are known");
			}

			ResultSyncLead dummyLead = mu.createNewLead(sc, "+1smstesting");
			if (dummyLead == null) {
				/* Insufficient - must make a call */
				Logger.info("Cannot connect with soap credentials [%s:%s]",
						sc.soapUserId, sc.soapEncKey);
				renderText("The SOAP credentials you provided are invalid");
			} else {
				mu.deleteLead(sc, dummyLead);
			}

			AccountList accounts = TwilioUtility.getAccounts(sc.smsGatewayID,
					sc.smsGatewayPassword);
			if (accounts == null || accounts.getTotal() == 0) {
				Logger.info("No accounts set up with SMS gateway [%s:%s]",
						sc.smsGatewayID, sc.smsGatewayPassword);
				renderText("Please setup the SMS gateway account and retry");
			}

			sc.status = Constants.SMSCAMPAIGN_STATUS_ACTIVE;
			sc.save();
			Logger.debug("campaign[%d] has been saved", sc.id);

			Map<String, Object> map = new HashMap();
			// Generate URL to listen for inbound SMS
			map.put("campaignId", sc.id);
			String urlBase = Play.configuration.getProperty("mkto.serviceUrl");
			String callBackurl = urlBase
					+ Router.reverse("Application.smsCallback", map).url;
			Logger.debug("campaign[%d] - callback URL is %s", sc.id,
					callBackurl);

			// create Twilio application and save appId in database
			try {
//				Logger.info(
//						"campaign[%d] - creating new SMS gateway application",
//						sc.id, callBackurl);
//				String appId = TwilioUtility.createApplication(sc.smsGatewayID,
//						sc.smsGatewayPassword, "SMSSubscriber", callBackurl,
//						"GET");
//				sc.smsGatewayApplicationId = appId;
//				Logger.info(
//						"campaign[%d] - New SMS gateway application [%s] created successfully",
//						sc.id, appId);
				boolean setApp = TwilioUtility.setCallbackUrl(sc.smsGatewayID,
						sc.smsGatewayPassword, callBackurl,
						sc.smsGatewayPhoneNumber);
				if (setApp) {
					Logger.info(
							"campaign[%d] - SMS application will now respond to inbound msgs");
				} else {
					Logger.info(
							"campaign[%d] - SMS application will NOT respond to inbound msgs");
				}
				sc.save();
			} catch (Exception e) {
				// Log this properly
				Logger.fatal(
						"campaign[%d] - Could not create a new SMS gateway application [%s]",
						sc.id, e.getMessage());
				renderHtml("Exception while creating Twilio Application for SMS callback");
			}

			// kick off background thread to do read the list and run outgoing
			// campaign
			Logger.info(
					"campaign[%d] - Kicking off background task to fetch lead list",
					sc.id);
			new SyncListAndRunFirstCampaign(sc).in(2);

			savedConfig(sc.id);
		}
	}

	public static void savedConfig(Long scId) {
		SMSCampaign sc = SMSCampaign.findById(scId);
		List<SMSCampaign> allCampaigns = new ArrayList<SMSCampaign>();
		if (sc != null) {
			allCampaigns = SMSCampaign.find(
					"munchkinAccountId = ? and status = ?",
					sc.munchkinAccountId, Constants.SMSCAMPAIGN_STATUS_ACTIVE)
					.fetch();
		}
		render(allCampaigns);
	}

	public static void smsCallback(String campaignId, String SmsSid,
			String AccountSid, String From, String To, String Body) {
		// look up application in database - if not present, ignore message
		SMSCampaign sc = SMSCampaign.findById(Long.valueOf(campaignId));
		if (sc == null) {
			Logger.fatal("campaign[%s] does not exist", campaignId);
			renderText("Sorry, we do not know anything about this campaign");
		}
		sc.numRecvd++;
		sc.save();
		new ProcessInboundMessage(sc, AccountSid, From, Body).in(2);
		renderHtml("I just received an SMS" + campaignId);
	}

	public static void cancelCampaign(String id) {
		SMSCampaign sc = SMSCampaign.findById(Long.valueOf(id));
		if (sc != null) {
			Logger.info("About to cancel campaign [%s]", id);
		}
		TwilioUtility.deleteApplication(sc.smsGatewayID, sc.smsGatewayPassword,
				sc.smsGatewayApplicationId);
		sc.status = Constants.SMSCAMPAIGN_STATUS_CANCELED;
		sc.save();
		Logger.info("Canceled campaign [%s]", id);
		renderHtml("Canceled campaign successfully");
	}

}