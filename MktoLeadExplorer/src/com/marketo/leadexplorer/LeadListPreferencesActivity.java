package com.marketo.leadexplorer;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.mamlambo.tutorial.tutlist.R;
import com.marketo.leadexplorer.data.TutListSharedPrefs;

public class TutListPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(
                TutListSharedPrefs.PREFS_NAME);
        addPreferencesFromResource(R.xml.prefs);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Context context = getApplicationContext();
        if (TutListSharedPrefs.getBackgroundUpdateFlag(getApplicationContext())) {
//            setRecurringAlarm(context);
        } else {
//            cancelRecurringAlarm(context);
        }
    }

}
