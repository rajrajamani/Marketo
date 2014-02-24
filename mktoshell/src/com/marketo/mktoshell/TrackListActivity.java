package com.marketo.mktoshell;

import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.marketo.mktoshell.common.AppDefinition;
import com.marketo.mktoshell.common.AppDefinitionFileStorage;
import com.marketo.mktoshell.common.Constants;
import com.marketo.mktoshell.common.GetAppDefinition;
import com.marketo.mktoshell.content.Content;
import com.marketo.mktoshell.content.Content.ContentItem;

/**
 * An activity representing a list of Tracks. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link TrackDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link TrackListFragment} and the item details (if present) is a
 * {@link TrackDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link TrackListFragment.Callbacks} interface to listen for item selections.
 */
public class TrackListActivity extends YouTubeBaseActivity implements
		TrackListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	/**
	 * Event Handling for Individual menu item selected Identify single menu
	 * item by it's id
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_reset:
			// Single menu item is selected do something
			// Ex: launching new activity/screen or show alert message
			Toast.makeText(TrackListActivity.this, "Reset is Selected",
					Toast.LENGTH_SHORT).show();
			resetApp();
			return true;

		case R.id.menu_refresh:
			// Single menu item is selected do something
			// Ex: launching new activity/screen or show alert message
			Toast.makeText(TrackListActivity.this, "Refresh is Selected",
					Toast.LENGTH_SHORT).show();
			refreshApp(Constants.HARDCODED_APP_DEFN_URL);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void refreshApp(String url) {
		try {
			AppDefinition apps = new GetAppDefinition().execute(url).get();
			int i = 0;
			for (ContentItem item : apps.items) {
				if (i++ == 0) {
					Content.removeItemsWithSameUUID(item.uuid);
				}
				Content.addMenuItem(item);
			}
			ListView lv = ((TrackListFragment) getFragmentManager()
					.findFragmentById(R.id.track_list)).getListView();
			AppDefinition storedApps = new AppDefinitionFileStorage(this)
					.execute(Constants.ACTION_TYPE_STORE).get();
			lv.requestLayout();
		} catch (InterruptedException e) {
			Log.e("mktoshell", "Unable to retrieve app definition");
		} catch (ExecutionException e) {
			Log.e("mktoshell", "Unable to retrieve app definition");
		}

	}

	private void resetApp() {
		Content.removeAllButWelcomeItem();
		ListView lv = ((TrackListFragment) getFragmentManager()
				.findFragmentById(R.id.track_list)).getListView();
		try {
			AppDefinition apps = new AppDefinitionFileStorage(this)
					.execute(Constants.ACTION_TYPE_STORE).get();
		} catch (InterruptedException e) {
			Log.e("mktoshell", "Unable to retrieve app definition");
		} catch (ExecutionException e) {
			Log.e("mktoshell", "Unable to retrieve app definition");
		}
		lv.requestLayout();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_list);

		if (findViewById(R.id.track_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((TrackListFragment) getFragmentManager().findFragmentById(
					R.id.track_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
		// Get the intent that started this activity
		Intent intent = getIntent();
		if (intent != null) {
			Uri url = intent.getData();
			if (url != null) {
				String path = url.getPath();
				Log.i("mktoshell: Received intent from external app %s", path);
				refreshApp("http:/" + path);
			}
		}
	}

	/**
	 * Callback method from {@link TrackListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(TrackDetailFragment.ARG_ITEM_ID, id);
			TrackDetailFragment fragment = new TrackDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.track_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, TrackDetailActivity.class);
			detailIntent.putExtra(TrackDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
