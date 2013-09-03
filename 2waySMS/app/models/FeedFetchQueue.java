package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class FeedFetchQueue extends Model {
	public FeedFetchQueue() {
	}

	@ManyToOne
	public BlogCampaign bc;

	public String status;
	
	public int numItems;

	@Column(length = 2000)
	public String subject;

	@Column(length = 2000)
	public String content;

	public Long processedAt;
}
