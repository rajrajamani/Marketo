package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jobs.ProcessInboundMessage;
import jobs.SyncListAndExecFormula;
import jobs.SyncListAndRunFirstCampaign;
import models.AddScores;
import models.FormulaCampaign;
import models.GoogleCampaign;
import models.PhoneQuery;
import models.SMSCampaign;
import models.User;

import org.apache.commons.io.FileUtils;

import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Http.Header;
import play.mvc.Http.Request;
import play.mvc.Router;
import play.mvc.With;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import com.marketo.mktows.wsdl.ResultSyncLead;
import com.twilio.sdk.resource.list.AccountList;
import common.Constants;
import common.MarketoUtility;
import common.RegionUtil;
import common.TwilioUtility;

@With(Secure.class)
public class Application extends Controller {

	private static final String String = null;

	public static void showHeaders() {
		Request req = request.get();
		Map<String, Header> hdrs = req.headers;

		String allValues = "";
		for (String name : hdrs.keySet()) {
			Header value = hdrs.get(name);
			allValues += name + ":" + value.value() + "/\n";
		}
		renderText(allValues);
	}
	
	public static void smsConfig(String url) {
		String user = Security.connected();
		if (url == null) {
			render(user);
		} else  {
			Logger.info("Looking up campaignURL %s", url);
			// Check to see if this has been configured previously
			List<SMSCampaign> msExisting = SMSCampaign.find(
					"campaignURL = ? and status = ?", url,
					Constants.CAMPAIGN_STATUS_ACTIVE).fetch();
			if (msExisting.size() == 1) {
				SMSCampaign ms = msExisting.get(0);
				Logger.info("campaign[%d] was configured previously", ms.id);
				/*
				 * renderText(
				 * "This campaign has been configured previously for account :"
				 * + ms.munchkinAccountId);
				 */
				savedSmsConfig();
			}

			MarketoUtility mu = new MarketoUtility();
			SMSCampaign sc = (SMSCampaign) mu.readSettings(url,
					Constants.CAMPAIGN_SMS);
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
			} else if (sc.phoneNumFieldApiName.equals("null")) {
				Logger.info(
						"campaign[%d] does not have a phoneNumFieldApiName.  Using Phone instead",
						sc.id);
				sc.phoneNumFieldApiName = Constants.DEFAULT_PHONE_FIELD_API_NAME;
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

			sc.status = Constants.CAMPAIGN_STATUS_ACTIVE;
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

				boolean setApp = TwilioUtility.setCallbackUrl(sc.smsGatewayID,
						sc.smsGatewayPassword, callBackurl,
						sc.smsGatewayPhoneNumber);
				if (setApp) {
					Logger.info("campaign[%d] - SMS application will now respond to inbound msgs");
				} else {
					Logger.info("campaign[%d] - SMS application will NOT respond to inbound msgs");
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

			savedSmsConfig();
		}
	}
	
	public static void googleConfig(String url) {
		String user = Security.connected();
		if (url == null) {
			render(user);
		}  else {
			NonGatedApp.processGoogleCampaign(url);
		}
	}

	public static void formulaConfig(String url) {
		String user = Security.connected();
		if (url == null) {
			render(user);
		}  else {
			processFormula(url);
		}
	}

	public static void blogConfig(String url) {
		String user = Security.connected();
		if (url == null) {
			render(user);
		}  else {
			blogThis(url);
		}
	}

	private static void blogThis(java.lang.String url) {
				
	}

	public static void index(String url) {
		String user = Security.connected();
		String welcome = "Welcome to the Marketo Claps Service";
		render(welcome);
	}

	private static void processFormula(String url) {
		MarketoUtility mu = new MarketoUtility();
		FormulaCampaign fc = (FormulaCampaign) mu.readSettings(url,
				Constants.CAMPAIGN_FORMULA);
		fc.save();
		Logger.info(
				"campaign[%d] - Kicking off background task to fetch lead list",
				fc.id);
		new SyncListAndExecFormula(fc).in(2);

		execFormulaStatus();
	}

	public static void showConversionFiles(String munchkinId) {
		String urlBase = Play.configuration.getProperty("mkto.serviceUrl");
		String dirBase = Play.configuration.getProperty("mkto.googBaseDir");
		String dirName = dirBase + munchkinId;
		List<String> allConversionFiles = new ArrayList<String>();
		File dirFile = new File(dirName);
		File[] listOfFiles = dirFile.listFiles();
		if (listOfFiles != null) {
			for (File f : listOfFiles) {
				String fqFileName = urlBase + "/public/google/" + munchkinId
						+ "/" + f.getName();
				Logger.debug("File name is : %s", fqFileName);
				allConversionFiles.add(fqFileName);
			}
		}
		String user = Security.connected();
		render(user, allConversionFiles);
	}

	public static void savedSmsConfig() {
		String user = Security.connected();
		List<SMSCampaign> allCampaigns = SMSCampaign.find(
				"munchkinAccountId = ? and status = ?", user,
				Constants.CAMPAIGN_STATUS_ACTIVE).fetch();
		render(user, allCampaigns);
	}

	public static void execFormulaStatus() {
		String user = Security.connected();
		List<FormulaCampaign> allCampaigns = FormulaCampaign.find(
				"munchkinAccountId = ?", user).fetch();
		render(user, allCampaigns);
	}

	public static void blogStatus() {
		String user = Security.connected();
		render(user);
	}

	public static void cancelCampaign(String id) {
		SMSCampaign sc = SMSCampaign.findById(Long.valueOf(id));
		if (sc != null) {
			Logger.info("About to cancel campaign [%s]", id);
		}
		// TwilioUtility.deleteApplication(sc.smsGatewayID,
		// sc.smsGatewayPassword,
		// sc.smsGatewayApplicationId);
		sc.status = Constants.CAMPAIGN_STATUS_CANCELED;
		sc.save();
		Logger.info("Canceled campaign [%s]", id);
		renderHtml("Canceled campaign successfully");
	}

}