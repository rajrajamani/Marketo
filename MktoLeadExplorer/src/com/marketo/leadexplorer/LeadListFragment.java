package com.marketo.leadexplorer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.marketo.leadexplorer.data.LeadListDatabase;
import com.marketo.leadexplorer.data.LeadListProvider;

public class LeadListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String DEBUG_TAG = "Marketo";

	private OnLeadSelectedListener leadSelectedListener;
	private static final int LEAD_LIST_LOADER = 0x01;

	private SimpleCursorAdapter adapter;

	public int mEmail;

	public int mFirstName;

	public int mLastName;

	private String mAccessToken;

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String projection[] = { LeadListDatabase.COL_FB_URL,
				LeadListDatabase.COL_LI_URL, LeadListDatabase.COL_EMAIL };
		Uri viewedTut = Uri.withAppendedPath(LeadListProvider.CONTENT_URI,
				String.valueOf(id));
		Cursor leadCursor = getActivity().getContentResolver().query(viewedTut,
				projection, null, null, null);
		if (leadCursor.moveToFirst()) {
			String leadFbUrl = leadCursor.getString(0);
			String leadLiUrl = leadCursor.getString(1);
			String leadEmail = leadCursor.getString(2);
			if (leadFbUrl != null && leadFbUrl.isEmpty() && leadLiUrl != null
					&& leadLiUrl.isEmpty()) {
				// no-op
			} else {
				if (!leadLiUrl.isEmpty()) {
					Log.d(DEBUG_TAG, "Url :" + leadLiUrl);
					leadSelectedListener.onLeadSelected(leadLiUrl);
				} else {
					Log.d(DEBUG_TAG, "Url :" + leadFbUrl);
					leadSelectedListener.onLeadSelected(leadFbUrl);
				}
			}
		}
		leadCursor.close();
		l.setItemChecked(position, true);
	}

	private static final String[] UI_BINDING_FROM = { LeadListDatabase.COL_FN,
			LeadListDatabase.COL_LN, LeadListDatabase.COL_EMAIL, };
	private static final int[] UI_BINDING_TO = { R.id.lead_fn, R.id.lead_ln,
			R.id.lead_email };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle.getString("accessToken")!= null) {
        	mAccessToken = bundle.getString("accessToken");
			Log.d("Marketo", "Fragment received Access Token :" + mAccessToken);
        } else {
        	Log.e("Marketo", "No access tokens available");
        }

		getLoaderManager().initLoader(LEAD_LIST_LOADER, null, this);
		adapter = new SimpleCursorAdapter(
				getActivity().getApplicationContext(), R.layout.list_item,
				null, UI_BINDING_FROM, UI_BINDING_TO,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new LeadViewBinder());

		setListAdapter(adapter);
		setHasOptionsMenu(true);
	}

	public interface OnLeadSelectedListener {
		public void onLeadSelected(String url);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			leadSelectedListener = (OnLeadSelectedListener) activity;
		} catch (ClassCastException e) {
			Log.e(DEBUG_TAG, "Bad class", e);
			throw new ClassCastException(activity.toString()
					+ " must implement OnTutSelectedListener");
		}
	}

	// options menu
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.options_menu, menu);
		// search menu item
		Intent searchIntent = new Intent(getActivity().getApplicationContext(),
				LeadSearchActivity.class);

		MenuItem search = menu.findItem(R.id.search_menu_item);
		search.setIntent(searchIntent);

		// pref menu item
		Intent prefsIntent = new Intent(getActivity().getApplicationContext(),
				LeadListPreferencesActivity.class);

		MenuItem preferences = menu.findItem(R.id.settings_menu_item);
		preferences.setIntent(prefsIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search_menu_item:
			Intent searchIntent = item.getIntent();
            //Sending data to another Activity
			searchIntent.putExtra("accessToken", mAccessToken);
			getActivity().startActivity(searchIntent);
			break;

		case R.id.settings_menu_item:
			getActivity().startActivity(item.getIntent());
			break;
		}
		return true;
	}

	// custom viewbinder
	private class LeadViewBinder implements SimpleCursorAdapter.ViewBinder {

		public boolean setViewValue(View view, Cursor cursor, int index) {
			if (index == cursor.getColumnIndex(LeadListDatabase.COL_LI_URL)) {
				String url = cursor.getString(index);
				if (!url.isEmpty()) {

					Uri uri = Uri.parse(url);
					Log.d("DEBUG_TAG", "Setting image uri to " + url);
					return false;
				} else {
					return true;
				}

				// get a locale based string for the date
				// DateFormat formatter = android.text.format.DateFormat
				// .getDateFormat(getActivity().getApplicationContext());
				// long date = cursor.getLong(index);
				// Date dateObj = new Date(date * 1000);
				// ((TextView) view).setText(formatter.format(dateObj));
			} else {
				return false;
			}
		}
	}

	// LoaderManager.LoaderCallbacks<Cursor> methods

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { LeadListDatabase.ID, LeadListDatabase.COL_FN,
				LeadListDatabase.COL_LN, LeadListDatabase.COL_EMAIL,
				LeadListDatabase.COL_FB_URL, LeadListDatabase.COL_LI_URL,
				LeadListDatabase.COL_SCORE, LeadListDatabase.COL_MID };

		Uri content = LeadListProvider.CONTENT_URI;
		CursorLoader cursorLoader = new CursorLoader(getActivity(), content,
				projection, null, null, LeadListDatabase.ID + " desc");
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	/*
	 * private class LeadViewAdapter extends CursorAdapter {
	 * 
	 * // We have two list item view types
	 * 
	 * private static final int VIEW_TYPE_GROUP_START = 0; private static final
	 * int VIEW_TYPE_GROUP_CONT = 1; private static final int VIEW_TYPE_COUNT =
	 * 2;
	 * 
	 * LeadViewAdapter(Context context, Cursor cursor) { super(context, cursor);
	 * 
	 * // Get the layout inflater
	 * 
	 * mInflater = LayoutInflater.from(context);
	 * 
	 * // Get and cache column indices
	 * 
	 * mEmail = cursor.getColumnIndex(LeadListDatabase.COL_EMAIL); mFirstName =
	 * cursor.getColumnIndex(LeadListDatabase.COL_FN); mLastName =
	 * cursor.getColumnIndex(LeadListDatabase.COL_LN); }
	 * 
	 * @Override public int getViewTypeCount() { return VIEW_TYPE_COUNT; }
	 * 
	 * @Override public int getItemViewType(int position) { // There is always a
	 * group header for the first data item
	 * 
	 * if (position == 0) { return VIEW_TYPE_GROUP_START; }
	 * 
	 * // For other items, decide based on current data
	 * 
	 * Cursor cursor = getCursor(); cursor.moveToPosition(position); boolean
	 * newGroup = isNewGroup(cursor, position);
	 * 
	 * // Check item grouping
	 * 
	 * if (newGroup) { return VIEW_TYPE_GROUP_START; } else { return
	 * VIEW_TYPE_GROUP_CONT; } }
	 * 
	 * @Override public View newView(Context context, Cursor cursor, ViewGroup
	 * parent) {
	 * 
	 * int position = cursor.getPosition(); int nViewType;
	 * 
	 * if (position == 0) { // Group header for position 0
	 * 
	 * nViewType = VIEW_TYPE_GROUP_START; } else { // For other positions,
	 * decide based on data
	 * 
	 * boolean newGroup = isNewGroup(cursor, position);
	 * 
	 * if (newGroup) { nViewType = VIEW_TYPE_GROUP_START; } else { nViewType =
	 * VIEW_TYPE_GROUP_CONT; } }
	 * 
	 * View v;
	 * 
	 * if (nViewType == VIEW_TYPE_GROUP_START) { // Inflate a layout to start a
	 * new group
	 * 
	 * v = mInflater.inflate(R.layout.search, parent, false);
	 * 
	 * // Ignore clicks on the list header
	 * 
	 * // View vHeader = v.findViewById(R.id.search_fieldset); //
	 * vHeader.setOnClickListener(new OnClickListener() { // public void
	 * onClick(DialogInterface dialog, int which) { // // TODO Auto-generated
	 * method stub // } // }); } else { // Inflate a layout for "regular" items
	 * 
	 * v = mInflater.inflate(R.layout.list_item, parent, false); } return v; }
	 * 
	 * @Override public void bindView(View view, Context context, Cursor cursor)
	 * { TextView tv;
	 * 
	 * tv = (TextView) view.findViewById(R.id.lead_email);
	 * tv.setText(cursor.getString(mColSubject));
	 * 
	 * tv = (TextView) view.findViewById(R.id.lead_fn);
	 * tv.setText(cursor.getString(mColFrom));
	 * 
	 * tv = (TextView) view.findViewById(R.id.lead_ln);
	 * tv.setText(cursor.getString(mColFrom));
	 * 
	 * // If there is a group header, set its value to just the date
	 * 
	 * tv = (TextView) view.findViewById(R.id.search_fieldset); if (tv != null)
	 * { // tv.setText(gDateFormatGroupItem.format(d)); } }
	 * 
	 * 
	 * 
	 * private boolean isNewGroup(Cursor cursor, int position) { // Get date
	 * values for current and previous data items
	 * 
	 * long nWhenThis = cursor.getLong(mColWhen);
	 * 
	 * cursor.moveToPosition(position - 1); long nWhenPrev =
	 * cursor.getLong(mColWhen);
	 * 
	 * // Restore cursor position
	 * 
	 * cursor.moveToPosition(position);
	 * 
	 * // Compare date values, ignore time values
	 * 
	 * Calendar calThis = Calendar.getInstance();
	 * calThis.setTimeInMillis(nWhenThis);
	 * 
	 * Calendar calPrev = Calendar.getInstance();
	 * calPrev.setTimeInMillis(nWhenPrev);
	 * 
	 * int nDayThis = calThis.get(Calendar.DAY_OF_YEAR); int nDayPrev =
	 * calPrev.get(Calendar.DAY_OF_YEAR);
	 * 
	 * if (nDayThis != nDayPrev || calThis.get(Calendar.YEAR) !=
	 * calPrev.get(Calendar.YEAR)) { return true; }
	 * 
	 * return false; }
	 * 
	 * LayoutInflater mInflater;
	 * 
	 * private int mColSubject; private int mColFrom; private int mColWhen;
	 * 
	 * }
	 */
	// End of MessageAdapter
}
