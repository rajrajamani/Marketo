package common;

import java.net.URL;
import java.util.Iterator;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * @author Hanumant Shikhare
 */
public class FeedReader {

	public static void main(String[] args) throws Exception {

		URL url = new URL("http://feeds.feedburner.com/modernb2bmarketing");XmlReader reader = null;

		try {

			reader = new XmlReader(url);
			SyndFeed feed = new SyndFeedInput().build(reader);
			System.out.println("Feed Title: " + feed.getAuthor());

			for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
				SyndEntry entry = (SyndEntry) i.next();
				System.out.println(entry.getPublishedDate() + ":"
						+ entry.getTitle());
			}
		} finally {
			if (reader != null)
				reader.close();
		}
	}
}