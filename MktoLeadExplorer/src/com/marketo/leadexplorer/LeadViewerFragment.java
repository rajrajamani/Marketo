package com.marketo.leadexplorer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.marketo.leadexplorer.R;

public class LeadViewerFragment extends Fragment {
    private WebView viewer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        viewer = (WebView) inflater
                .inflate(R.layout.lead_view, container, false);
        return viewer;
    }

    public void updateUrl(String newUrl) {
        if (viewer != null) {
            viewer.loadUrl(newUrl);
        }
    }
}
