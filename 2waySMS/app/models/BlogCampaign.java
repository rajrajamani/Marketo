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
public class BlogCampaign extends Model {
	public BlogCampaign() {
	}

	@Column(length = 2000)
	public String url;
	public String blogUrl; // provided by marketer - the blog feed
	public int maxPosts;
	public Long userId;
	public String munchkinId;
	public String subject;
	public String content;
	
	public String leadList;
	public String programName;
	public String campaignName;

	public String emailOnDays;
	public String emailAtTime;
	public String emailTZ;
	
	public String status;
	public Long dateOfLastEmailedBlogPost;
	public Long dateOfNextScheduledEmail;
	
	public int numSent;
	public int numRecvd;

	@OneToMany (cascade=CascadeType.ALL, mappedBy="bc", fetch=FetchType.EAGER)
	public List<FeedFetchQueue> queue = new ArrayList<FeedFetchQueue>();

}
