package com.marketo.mktoshell.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marketo.mktoshell.common.AppDefinition;
import com.marketo.mktoshell.common.AppDefinitionFileStorage;
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

	private static Context mCtx;

	static {
		// Add 3 sample items.
		// addMenuItem(new ContentItem(String.valueOf(Constants.TXT_VIEW), "1",
		// "Welcome", "This is a Proof of Concept app", null, null, null));
		// addMenuItem(new ContentItem(String.valueOf(Constants.WEB_VIEW), "2",
		// "Marketo Sweeps", null, "http://iancode.info/mkto/sweeps.html",
		// null, null));
		// addMenuItem(new ContentItem(String.valueOf(Constants.WEB_VIEW), "3",
		// "NYT Website", null, "http://www.nyt.com", null, null));
		// addMenuItem(new ContentItem(String.valueOf(Constants.MAP_VIEW), "4",
		// "Map View", null, null, "37.32", "-122.04"));
		// addMenuItem(new ContentItem(String.valueOf(Constants.VDO_VIEW), "5",
		// "Summit 2013 highlights", null,
		// "http://www.youtube.com/watch?v=1m8cYxjSwas", null, null));
	}

	public static void addMenuItem(ContentItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	public static void removeMenuItem(String id) {
		ContentItem ciVal = null;
		Iterator<ContentItem> it = ITEMS.iterator();
		while (it.hasNext()) {
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
		while (it.hasNext()) {
			ciVal = it.next();
			if (!ciVal.id.equals("1")) {
				ITEM_MAP.remove(ciVal.id);
				it.remove();
			}
		}
	}

	public static String getJson() {
		String rep = null;
		Gson gson = new GsonBuilder().create();

		AppDefinition appDef = new AppDefinition();
		appDef.items = ITEMS;
		appDef.success = "true";

		rep = gson.toJson(appDef);
		return rep;
	}

	public static AppDefinition getAppDef() {
		AppDefinition appDef = new AppDefinition();
		appDef.items = ITEMS;
		appDef.success = "true";
		return appDef;
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class ContentItem {
		public String type;
		public String id;
		public String label;
		public String content;
		public String url;
		public String lattitude;
		public String longitude;

		public ContentItem(String type, String id, String label,
				String content, String url, String lattitude, String longitude) {
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

	public static void instantiate(String storedRep) {
		Gson gson = new GsonBuilder().create();
		AppDefinition appDef = gson.fromJson(storedRep, AppDefinition.class);
		initialize(appDef);
	}

	private static void initialize(AppDefinition apps) {
		// clear everything first
		ITEMS.removeAll(ITEMS);
//		Iterator<ContentItem> it = ITEMS.iterator();
//		while (it.hasNext()) {
//			it.remove();
//		}

		Iterator<ContentItem> storedIt = apps.items.iterator();
		while (storedIt.hasNext()) {
			ContentItem item = storedIt.next();
			addMenuItem(item);
		}
		
	}

	public static void initializeOnLoad(Context ctx) {
		mCtx = ctx;
		try {
			AppDefinition apps = new AppDefinitionFileStorage(mCtx).execute(
					Constants.ACTION_TYPE_READ).get();
			initialize(apps);
		} catch (InterruptedException e) {
			Log.e("mktoshell", "Unable to load app definition");
		} catch (ExecutionException e) {
			Log.e("mktoshell", "Unable to load app definition");
		}
	}

}
