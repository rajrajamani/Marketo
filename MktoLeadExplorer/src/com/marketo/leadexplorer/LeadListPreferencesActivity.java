package com.marketo.leadexplorer;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.marketo.leadexplorer.R;
import com.marketo.leadexplorer.data.LeadListSharedPrefs;

public class LeadListPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(
                LeadListSharedPrefs.PREFS_NAME);
        addPreferencesFromResource(R.xml.prefs);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Context context = getApplicationContext();
        if (LeadListSharedPrefs.getBackgroundUpdateFlag(getApplicationContext())) {
//            setRecurringAlarm(context);
        } else {
//            cancelRecurringAlarm(context);
        }
    }

}
