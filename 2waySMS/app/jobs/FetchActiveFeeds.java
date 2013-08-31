package jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import models.BlogCampaign;
import models.FeedFetchQueue;
import models.User;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;

import com.marketo.mktows.client.MktowsUtil;
import com.marketo.mktows.wsdl.Attrib;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import common.Constants;
import common.FeedReader;
import common.MarketoUtility;
import common.TimeUtil;

@Every("90s")
public class FetchActiveFeeds extends Job {

	public void doJob() {
		Long currTime = TimeUtil.getCurrTime();
		List<FeedFetchQueue> qItems = FeedFetchQueue.find("status =  ? ",
				Constants.CAMPAIGN_STATUS_ACTIVE).fetch();
		Logger.debug("Following blogs are in fetch queue");
		for (FeedFetchQueue qItem : qItems) {
			BlogCampaign bc = qItem.bc;
			if (bc.dateOfNextScheduledEmail < currTime) {
				Logger.debug("qItem [%d] url[%s] is ready for processing",
						qItem.id, bc.blogUrl);
				fetchAndEmail(qItem, bc, currTime);
			} else {
				Logger.debug("qItem [%d] url[%s] is active later today",
						qItem.id, bc.blogUrl);
			}
		}
		Logger.debug("No more blogs in fetch queue");
	}

	private void fetchAndEmail(FeedFetchQueue qItem, BlogCampaign bc,
			Long currTime) {
		SyndFeed feed = FeedReader.fetch(bc.blogUrl);
		int counter = 0;
		String contents = "";
		String subject = "";
		java.util.Date latestPost = null;

		for (Iterator feedIter = feed.getEntries().iterator(); feedIter
				.hasNext() && counter < bc.maxPosts; counter++) {
			SyndEntry entry = (SyndEntry) feedIter.next();
			if (counter == 0) {
				latestPost = entry.getPublishedDate();
				if (latestPost != null) {
					if (latestPost.getTime() <= bc.dateOfLastEmailedBlogPost) {
						Logger.debug(
								"qItem[%d] - No new blog posts since last email",
								qItem.id);
						qItem.status = Constants.CAMPAIGN_STATUS_COMPLETED;
						qItem.save();
						break;
					}

					subject = entry.getTitle();
				} else {
					Logger.debug(
							"qItem[%d] - has no timestamp for individual posts",
							qItem.id);
					qItem.status = Constants.CAMPAIGN_STATUS_COMPLETED;
					qItem.save();
					break;
				}
			} else if (counter == 1) {
				subject += " + more";
			}

			String uri = entry.getUri();
			contents += "<h2>" + entry.getTitle() + "</h2>";
			contents += "<h4>" + entry.getPublishedDate() + "</h4>";
			contents += "<p>" + entry.getDescription().getValue() + "</p>";
			contents += "<a href=" + uri + ">" + uri + "</a>";
			contents += "<br/>";
			Logger.debug("Entry [%d] : pub date [%s] ", counter,
					entry.getPublishedDate());
		}

		boolean emailSent = false;
		if ("".equals(contents) || contents == null) {
			Logger.debug("No items match the feed");
		} else {
			Logger.debug("Contents of qitem[%d] - %s", qItem.id, contents);
			User user = User.findById(bc.userId);
			if (user.munchkinId == null || "".equals(user.munchkinId)
					|| user.skey == null || "".equals(user.skey)
					|| user.suid == null || "".equals(user.suid)) {
				Logger.debug(
						"Campaign[%d] - Cannot proceed without SOAP creds",
						bc.id);

			} else {
				emailSent = sendEmail(user, bc, subject, contents);
			}
		}

		if (emailSent) {
			qItem.status = Constants.CAMPAIGN_STATUS_COMPLETED;
			qItem.save();

			bc.dateOfNextScheduledEmail = -1L;
			bc.save();

			if (latestPost != null) {
				bc.dateOfLastEmailedBlogPost = latestPost.getTime();

				List<BlogCampaign> allOtherCampaigns = BlogCampaign.find(
						"blogUrl = ? and status = ?", bc.blogUrl,
						Constants.CAMPAIGN_STATUS_ACTIVE).fetch();
				for (BlogCampaign obc : allOtherCampaigns) {
					obc.dateOfLastEmailedBlogPost = bc.dateOfLastEmailedBlogPost;
					Logger.debug(
							"Campaign[%d] - setting lastBlogPostTS to [%s]",
							obc.id, obc.dateOfLastEmailedBlogPost);
					obc.save();
				}
			}
		}

	}

	private boolean sendEmail(User user, BlogCampaign bc, String subject,
			String content) {
		boolean retVal = false;
		MarketoUtility mu = new MarketoUtility();
		Date dt = new Date();
		Logger.debug("About to schedule email");

		List<Attrib> tokenList = new ArrayList<Attrib>();

		Attrib token1 = MktowsUtil.objectFactory.createAttrib();
		token1.setName("my.subject");
		token1.setValue(subject);
		tokenList.add(token1);

		Attrib token2 = MktowsUtil.objectFactory.createAttrib();
		token2.setName("my.content");
		token2.setValue(content);
		tokenList.add(token2);

		retVal = mu.scheduleCampaign(user, dt, bc, tokenList);
		return retVal;
	}

	public static void main(String[] args) {
		SyndFeed feed = FeedReader
				.fetch("http://feeds.feedburner.com/modernb2bmarketing");

		int counter = 0;
		String contents = "";
		for (Iterator feedIter = feed.getEntries().iterator(); feedIter
				.hasNext() && counter < 3; counter++) {
			SyndEntry entry = (SyndEntry) feedIter.next();
			String uri = entry.getUri();
			contents += "<h2>" + entry.getTitle() + "</h2>";
			contents += "<h4>" + entry.getPublishedDate() + "</h4>";
			contents += "<p>" + entry.getDescription().getValue() + "</p>";
			contents += "<a href=" + uri + ">" + uri + "</a>";
			contents += "<br/>";
			// System.out.println("pub date :" + entry.getPublishedDate());
			// System.out.println("title :" + entry.getTitle());
			// System.out.println("desc :" + entry.getDescription().getValue());
			// System.out.println("uri :" + entry.getUri());
		}
		System.out.println(contents);

	}
}
