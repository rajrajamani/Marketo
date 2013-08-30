package jobs;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import models.BlogCampaign;
import models.FeedFetchQueue;
import models.User;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;

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
		Logger.debug("Following blog are in fetch queue");
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
						break;
					}
				} else {
					Logger.debug(
							"qItem[%d] - has no timestamp for individual posts",
							qItem.id);
					break;
				}
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
		if ("".equals(contents)) {
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
				emailSent = sendEmail(user, bc, contents);
			}
		}

		if (emailSent) {
			qItem.status = Constants.CAMPAIGN_STATUS_COMPLETED;
			qItem.save();

			if (latestPost != null) {
				bc.dateOfLastEmailedBlogPost = latestPost.getTime();
			}
			bc.dateOfNextScheduledEmail = -1L;
			bc.save();
		}

	}

	private boolean sendEmail(User user, BlogCampaign bc, String contents) {
		boolean retVal = false;
		MarketoUtility mu = new MarketoUtility();
		Date dt = new Date();
		Logger.debug("About to schedule email");
		retVal = mu.scheduleCampaign(user, dt, bc, "my.content", contents);
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
