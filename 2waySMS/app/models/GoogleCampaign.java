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
		munchkinId = ms.munchkinId;
		timezone = ms.timezone;
		numEntries = ms.numEntries;
		
	}

	public GoogleCampaign() {
	}

	public String campaignURL; // provided by marketer
	public String munchkinId; // from token
	public String timezone;
	public int numEntries;
}