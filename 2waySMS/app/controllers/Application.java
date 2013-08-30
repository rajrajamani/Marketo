package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jobs.SyncListAndExecFormula;
import jobs.SyncListAndRunFirstCampaign;
import models.BlogCampaign;
import models.FormulaCampaign;
import models.GoogleCampaign;
import models.SMSCampaign;
import models.User;
import play.Logger;
import play.Play;
import play.data.validation.Match;
import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;
import play.data.validation.URL;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.Http.Header;
import play.mvc.Http.Request;
import play.mvc.Router;
import play.mvc.With;

import com.marketo.mktows.wsdl.ResultSyncLead;
import com.twilio.sdk.resource.list.AccountList;
import common.Constants;
import common.MarketoUtility;
import common.TwilioUtility;

@With(Secure.class)
public class Application extends Controller {

	private static final String String = null;

	public static void changePassword(String munchkinId, String cpw,
			String pw1, String pw2, String suid, String skey) {
		String currUser = Security.connected();
		String placeholder = "";
		if (currUser == null || "".equals(currUser)) {
			placeholder = "Munchkin Id";
		} else {
			placeholder = currUser;
		}
		if (munchkinId == null && cpw == null && pw1 == null && pw2 == null) {
			render(placeholder);
		}

		if (munchkinId != null) {
			munchkinId = munchkinId.trim();
		}
		Logger.debug(
				"Change Password - mId:%s; Curr:%s, New:%s; SOAP uid:%s, SOAP key:%s",
				munchkinId, cpw, pw1, suid, skey);
		User user = User.find("byMunchkinId", munchkinId).first();
		if (user != null) {
			if (cpw != null && !cpw.equals("")
					&& user.password.equals(Crypto.passwordHash(cpw))) {

				if (!"".equals(pw1)) {
					// Update password now
					String msg = "Saved New Password ";
					user.password = Crypto.passwordHash(pw1);
					if (suid != null && !suid.equals("")) {
						user.suid = suid.trim();
						msg += "and SOAP credentials";
					}
					if (skey != null && !skey.equals("")) {
						user.skey = skey.trim();
					}
					user.save();
					Application.index(msg);
				} else if (!("").equals(suid)) {
					user.suid = suid.trim();
					if (skey != null && !skey.equals("")) {
						user.skey = skey.trim();
					}
					user.save();
					Application.index("Saved SOAP credentials");
				}
			} else {
				// Todo - ask for existing password and reset
				Application.index("wrong password");
			}
		} else {
			Application.index("No such user - please logout and try again");
		}

		// should never reach here
		render(placeholder);
	}

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
		} else {
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
					+ Router.reverse("NonGatedApp.smsCallback", map).url;
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
		} else {
			processGoogleCampaign(url);
		}
	}

	protected static void processGoogleCampaign(String url) {
		GoogleCampaign gc = NonGatedApp.getGoogleCampaignFromUrl(url);

		/*
		 * For Testing gc.munchkinAccountId = "1234"; gc.save();
		 * 
		 * addGCLID("1234", "idnum", "add", "hsd84jk", "marketo target", "null",
		 * "2013-06-21 12:40:03");
		 */

		Application.showConversionFiles();
	}

	public static void formulaConfig(String url) {
		String user = Security.connected();
		if (url == null) {
			render(user);
		} else {
			processFormula(url);
		}
	}

	public static void blogConfig(
			int init,
			@URL @Required(message = "Must provide URL") String url,
			@Required(message = "Please pick the days on which you want to run this campaign") @Match(".*\\b(Mon|Tue|Wed|Thu|Fri|Sat|Sun)\\b") String days,
			@Required(message = "Please pick the time when you wish to run the campaign") @Min(0) @Max(2359) String time,
			@Required(message = "Which timezone are you running the campaign in?") String tz) {
		if (init != 1 && validation.hasErrors()) {
			String errMsg = "";
			for (play.data.validation.Error error : validation.errors()) {
				errMsg += error.message() + "<br/>";
			}
			renderHtml(errMsg);
		}

		String user = Security.connected();
		if (url == null) {
			render(user);
		} else {
			blogThis(url, days, time, tz);
		}
	}

	private static void blogThis(String url, String days, String time, String tz) {
		BlogCampaign bc = NonGatedApp.getBlogCampaignFromUrl(url);
		bc.url = url;
		bc.emailOnDays = days;
		bc.emailAtTime = time;
		bc.emailTZ = tz;
		bc.status = Constants.CAMPAIGN_STATUS_ACTIVE;
		bc.save();
		Application.blogStatus();

	}

	public static void index(String msg) {
		String user = Security.connected();
		if (msg==null || ("").equals(msg)) {
			msg = "Welcome to the Marketo ClApps Service";
		}
		render(msg);
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

	public static void showConversionFiles() {
		String urlBase = Play.configuration.getProperty("mkto.serviceUrl");
		String dirBase = Play.configuration.getProperty("mkto.googBaseDir");
		String user = Security.connected();
		String dirName = dirBase + user;
		List<String> allConversionFiles = new ArrayList<String>();
		File dirFile = new File(dirName);
		File[] listOfFiles = dirFile.listFiles();
		if (listOfFiles != null) {
			for (File f : listOfFiles) {
				String fqFileName = urlBase + "/public/google/" + user + "/"
						+ f.getName();
				Logger.debug("File name is : %s", fqFileName);
				allConversionFiles.add(fqFileName);
			}
		}
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