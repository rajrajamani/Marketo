package com.marketo.leadexplorer;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.mamlambo.tutorial.tutlist.R;
import com.marketo.leadexplorer.data.LeadListDatabase;
import com.marketo.leadexplorer.data.TutListProvider;

public class TutListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String DEBUG_TAG = "TutListFragment";

    private OnTutSelectedListener tutSelectedListener;
    private static final int TUTORIAL_LIST_LOADER = 0x01;

    private SimpleCursorAdapter adapter;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String projection[] = { LeadListDatabase.COL_FB_URL };
        Uri viewedTut = Uri.withAppendedPath(TutListProvider.CONTENT_URI,
                String.valueOf(id));
        Cursor tutorialCursor = getActivity().getContentResolver().query(
                viewedTut, projection, null, null, null);
        if (tutorialCursor.moveToFirst()) {
            String tutorialUrl = tutorialCursor.getString(0);
            tutSelectedListener.onTutSelected(tutorialUrl);
        }
        tutorialCursor.close();
        l.setItemChecked(position, true);
    }

    private static final String[] UI_BINDING_FROM = {
            LeadListDatabase.COL_EMAIL, LeadListDatabase.COL_FN };
    private static final int[] UI_BINDING_TO = { R.id.title, R.id.date };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(TUTORIAL_LIST_LOADER, null, this);

        adapter = new SimpleCursorAdapter(
                getActivity().getApplicationContext(), R.layout.list_item,
                null, UI_BINDING_FROM, UI_BINDING_TO,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        adapter.setViewBinder(new TutorialViewBinder());
        setListAdapter(adapter);
        setHasOptionsMenu(true);
    }

    public interface OnTutSelectedListener {
        public void onTutSelected(String tutUrl);
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
            tutSelectedListener = (OnTutSelectedListener) activity;
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

        // refresh menu item
        MenuItem refresh = menu.findItem(R.id.refresh_option_item);

        // pref menu item
        Intent prefsIntent = new Intent(getActivity().getApplicationContext(),
                TutListPreferencesActivity.class);

        MenuItem preferences = menu.findItem(R.id.settings_option_item);
        preferences.setIntent(prefsIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.refresh_option_item:
            getActivity().startService(item.getIntent());
            break;
        case R.id.settings_option_item:
            getActivity().startActivity(item.getIntent());
            break;
        }
        return true;
    }

    // custom viewbinder
    private class TutorialViewBinder implements SimpleCursorAdapter.ViewBinder {

        public boolean setViewValue(View view, Cursor cursor, int index) {
            if (index == cursor.getColumnIndex(LeadListDatabase.COL_EMAIL)) {
                // get a locale based string for the date
                DateFormat formatter = android.text.format.DateFormat
                        .getDateFormat(getActivity().getApplicationContext());
                long date = cursor.getLong(index);
                Date dateObj = new Date(date * 1000);
                ((TextView) view).setText(formatter.format(dateObj));
                return true;
            } else {
                return false;
            }
        }
    }

    // LoaderManager.LoaderCallbacks<Cursor> methods

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { LeadListDatabase.ID, LeadListDatabase.COL_EMAIL,
                LeadListDatabase.COL_FN };

        Uri content = TutListProvider.CONTENT_URI;
        CursorLoader cursorLoader = new CursorLoader(getActivity(), content,
                projection, null, null, LeadListDatabase.COL_EMAIL + " desc");
        return cursorLoader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
