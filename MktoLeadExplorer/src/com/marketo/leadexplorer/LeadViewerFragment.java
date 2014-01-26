package com.marketo.leadexplorer;

import com.mamlambo.tutorial.tutlist.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class TutViewerFragment extends Fragment {
    private WebView viewer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        viewer = (WebView) inflater
                .inflate(R.layout.tut_view, container, false);
        return viewer;
    }

    public void updateUrl(String newUrl) {
        if (viewer != null) {
            viewer.loadUrl(newUrl);
        }
    }
}
