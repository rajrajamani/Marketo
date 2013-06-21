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
public class GoogleCampaign extends Model {
	public GoogleCampaign(GoogleCampaign ms) {
		campaignURL = ms.campaignURL;
		munchkinAccountId = ms.munchkinAccountId;
		timezone = ms.timezone;
		
	}

	public GoogleCampaign() {
	}

	public static final int MAX_RULES = 25;

	public String campaignURL; // provided by marketer
	public String munchkinAccountId; // from token
	public String timezone;
}