package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class BlogCampaign extends Model {
	public BlogCampaign() {
	}

	@Column(length = 2000)
	public String url;
	public String blogUrl; // provided by marketer - the blog feed
	public int maxPosts;
	public String munchkinAccountId; // from token
	public String soapEncKey;
	public String soapUserId;

	public String leadList;
	public String programName;
	public String campaignName;

	public String emailOnDays;
	public String emailAtTime;
	public String emailTZ;
	
	public String status;
	public String dateOfLastEmailedBlogPost;
	public int numSent;
	public int numRecvd;


}
