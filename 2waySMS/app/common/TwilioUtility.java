package common;

import java.util.HashMap;
import java.util.Map;

import play.Logger;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.ApplicationFactory;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Sms;
import com.twilio.sdk.resource.list.AccountList;
import com.twilio.sdk.resource.list.ApplicationList;

public class TwilioUtility {
	// Find your Account Sid and Token at twilio.com/user/account
	public static final String ACCOUNT_SID = "AC09e767fc3f77c6570adc1b5608166a16";
	public static final String AUTH_TOKEN = "0139de0591840e8839a109412daa506f";
	public static final String TWILIO_PHONE_NUM = "+16505675735";

	public static void main(String[] args) throws TwilioRestException {
		/*
		 * sendSMS(ACCOUNT_SID, AUTH_TOKEN, TWILIO_PHONE_NUM, "+16506915076",
		 * "This is a new payload string");
		 * 
		 * createApplication(ACCOUNT_SID, AUTH_TOKEN, "NewApp",
		 * "http://localhost:9000/abcde/callbackurl", "GET");
		 */
		deleteAllApplications(ACCOUNT_SID, AUTH_TOKEN);
	}

	/**
	 * 
	 * @param sid
	 * @param aToken
	 * @param from
	 * @param to
	 * @param payload
	 * @throws TwilioRestException
	 */
	public static void sendSMS(String sid, String aToken, String from,
			String to, String payload) throws TwilioRestException {
		Logger.debug("Connecting to twilio %s:%s", sid, aToken);
		TwilioRestClient client = new TwilioRestClient(sid, aToken);
		String e164FormattedPhoneNumber = to;
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		try {
			PhoneNumber usPhone = phoneUtil.parse(to, "US");
			e164FormattedPhoneNumber = phoneUtil.format(usPhone,
					PhoneNumberFormat.E164);
		} catch (NumberParseException e) {
			Logger.error("NumberParseException was thrown: %s", e.toString());
		}

		// Build a filter for the SmsList
		Map<String, String> params = new HashMap<String, String>();
		params.put("Body", payload);
		params.put("To", to);
		params.put("From", from);

		Logger.debug("Trying to send message %s to %s.  ", payload,
				e164FormattedPhoneNumber);
		SmsFactory messageFactory = client.getAccount().getSmsFactory();
		Sms message = messageFactory.create(params);
		Logger.debug("Successfully sent message to %s.  SMS id : %s",
				e164FormattedPhoneNumber, message.getSid());
	}

	public static void getAccounts(String sid, String aToken) {
		TwilioRestClient client = new TwilioRestClient(sid, aToken);

		// Build a filter for the AccountList
		Map<String, String> params = new HashMap<String, String>();
		params.put("Status", "active");
		AccountList accounts = client.getAccounts(params);

		// Loop over accounts and print out a property
		for (Account account : accounts) {
			System.out.println(account.getFriendlyName());
		}
	}

	/**
	 * 
	 * @param sid
	 * @param aToken
	 * @param friendlyName
	 * @param smsUrl
	 * @param smsMethod
	 *            - GET or POST
	 * @throws TwilioRestException
	 */
	public static String createApplication(String sid, String aToken,
			String friendlyName, String smsUrl, String smsMethod)
			throws TwilioRestException {
		Logger.debug("Connecting to twilio %s:%s", sid, aToken);
		TwilioRestClient client = new TwilioRestClient(sid, aToken);

		// Build a filter for the ApplicationList
		Map<String, String> params = new HashMap<String, String>();
		params.put("FriendlyName", friendlyName);
		params.put("SmsUrl", smsUrl);
		params.put("SmsMethod", smsMethod);

		ApplicationFactory appFactory = client.getAccount()
				.getApplicationFactory();
		Logger.debug("Trying to create a new application with url:%s", smsUrl);
		com.twilio.sdk.resource.instance.Application app = appFactory
				.create(params);
		Logger.debug("Successfully created a new application with id:%s",
				app.getSid());
		// System.out.println(app.getSid());
		return app.getSid();
	}

	public static void deleteAllApplications(String sid, String aToken)
			throws TwilioRestException {
		TwilioRestClient client = new TwilioRestClient(sid, aToken);
		ApplicationList appList = client.getAccount().getApplications();
		for (com.twilio.sdk.resource.instance.Application app : appList) {
			app.delete();
		}
	}

}
