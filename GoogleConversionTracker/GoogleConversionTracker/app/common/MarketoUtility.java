package common;

import models.GoogleCampaign;

import org.apache.commons.lang.StringEscapeUtils;

import play.Logger;
import play.db.jpa.Model;
import play.libs.WS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class MarketoUtility {

	public static void main(String[] args) {
		MarketoUtility mu = new MarketoUtility();
	}

	public Model readSettings(String targetUrl, int campaignType) {
		play.libs.WS.HttpResponse res = WS.url(targetUrl).get();
		int status = res.getStatus();
		if (status != 200) {
			Logger.error("Unable to read settings from %s.  Got response %d",
					targetUrl, status);
			return null;
		}
		JsonElement retVal = res.getJson();
		GoogleCampaign gc = null;
		try {
			Gson gson = new GsonBuilder().create();
			switch (campaignType) {

			case Constants.CAMPAIGN_GOOG:
				gc = gson.fromJson(retVal, GoogleCampaign.class);
				gc.munchkinId = StringEscapeUtils.unescapeHtml(gc.munchkinId);
				Logger.debug("Read values from settings file : munchkinId[%s]",
						 gc.munchkinId);
				gc.campaignURL = targetUrl;
				return gc;
			}

		} catch (Exception e) {
			Logger.error("Unable to parse %s into json", retVal);
			Logger.error("Exception is %s", e.getMessage());
			return null;
		}
		return null;
	}


}
