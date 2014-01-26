package com.marketo.leadexplorer;

import com.mamlambo.tutorial.tutlist.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class TutViewerActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutview_fragment);

        Intent launchingIntent = getIntent();
        String content = launchingIntent.getData().toString();

        TutViewerFragment viewer = (TutViewerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.tutview_fragment);

        viewer.updateUrl(content);
    }

}
