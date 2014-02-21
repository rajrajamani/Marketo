package com.marketo.mktoshell.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.marketo.mktoshell.common.Constants;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Content {

	/**
	 * An array of content items.
	 */
	public static List<ContentItem> ITEMS = new ArrayList<ContentItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, ContentItem> ITEM_MAP = new HashMap<String, ContentItem>();

	static {
		// Add 3 sample items.
		addMenuItem(new ContentItem(Constants.TXT_VIEW, "1", "Welcome",
				"Some other content", "http://www.google.com", -1, -1));
		addMenuItem(new ContentItem(Constants.WEB_VIEW, "2", "Marketo Sweeps",
				"This is a test of item 1",
				"http://iancode.info/mkto/sweeps.html", -1, -1));
		addMenuItem(new ContentItem(Constants.WEB_VIEW, "3", "NYT Website",
				"alternatively, show this", "http://www.nyt.com", -1, -1));
		addMenuItem(new ContentItem(Constants.MAP_VIEW, "4", "Map View",
				"Our conf center", null, 37.32, -122.04));
		addMenuItem(new ContentItem(Constants.VDO_VIEW, "5", "Video View",
				"Summit 2013 highlights",
				"http://www.youtube.com/watch?v=1m8cYxjSwas", -1, -1));
	}

	public static void addMenuItem(ContentItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	public static void removeMenuItem(String id) {
		ContentItem ciVal = null;
		Iterator<ContentItem> it = ITEMS.iterator();
		while(it.hasNext()) {	
			ciVal = it.next();
			if (ciVal.id.equals(id)) {
				ITEM_MAP.remove(ciVal);
				it.remove();
			}
		}
	}

	public static void removeAllButWelcomeItem() {
		ContentItem ciVal = null;
		Iterator<ContentItem> it = ITEMS.iterator();
		while(it.hasNext()) {	
			ciVal = it.next();
			if (!ciVal.id.equals("1")) {
				ITEM_MAP.remove(ciVal.id);
				it.remove();
			}
		}
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class ContentItem {
		public int type;
		public String id;
		public String label;
		public String content;
		public String url;
		public double lattitude;
		public double longitude;

		public ContentItem(int type, String id, String label, String content,
				String url, double lattitude, double longitude) {
			this.type = type;
			this.id = id;
			this.label = label;
			this.content = content;
			this.url = url;
			this.lattitude = lattitude;
			this.longitude = longitude;
		}

		@Override
		public String toString() {
			return label;
		}
	}
}
