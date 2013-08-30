package common;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import play.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedReader {

	public static SyndFeed fetch(String feedUrl) {
		XmlReader reader = null;
		SyndFeed feed = null;
		try {
			URL url = new URL(feedUrl);
			reader = new XmlReader(url);
			Logger.info("About to fetch from feed [%s] ", feedUrl);
			feed = new SyndFeedInput().build(reader);
		} catch (Exception e) {
			Logger.error("Error while fetching feed [%s] - %s", feedUrl,
					e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
				}
			}
			return feed;
		}
	}
}