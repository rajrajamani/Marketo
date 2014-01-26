package com.marketo.leadexplorer;

import com.mamlambo.tutorial.tutlist.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class TutListActivity extends FragmentActivity implements
        TutListFragment.OnTutSelectedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutlist_fragment);
    }

    public void onTutSelected(String tutUrl) {
        TutViewerFragment viewer = (TutViewerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.tutview_fragment);

        if (viewer == null || !viewer.isInLayout()) {
            Intent showContent = new Intent(getApplicationContext(),
                    TutViewerActivity.class);
            showContent.setData(Uri.parse(tutUrl));
            startActivity(showContent);
        } else {
            viewer.updateUrl(tutUrl);
        }
    }
}