package com.marketo.leadexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.marketo.leadexplorer.R;

public class LeadViewerActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leadview_fragment);

        Intent launchingIntent = getIntent();
        String content = launchingIntent.getData().toString();

        LeadViewerFragment viewer = (LeadViewerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.leadview_fragment);

        viewer.updateUrl(content);
    }

}
