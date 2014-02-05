package com.marketo.leadexplorer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LeadListActivity extends FragmentActivity implements
		LeadListFragment.OnLeadSelectedListener {

	private String mAccessToken;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leadlist_fragment);
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			// Hardcode access token for testing
			mAccessToken = "2bef9d76-f11b-4e2f-91b0-03ade3de11dd:int";
		}
		if (bundle.getString("accessToken") != null) {
			mAccessToken = bundle.getString("accessToken");
			Log.e("Marketo", "LeadList recieved access token: " + mAccessToken);
		} else {
			Log.e("Marketo", "No access tokens available");
		}
	}

	public void onLeadSelected(String leadUrl) {
		LeadViewerFragment viewer = (LeadViewerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.leadview_fragment);

		if (viewer == null || !viewer.isInLayout()) {
			Intent showContent = new Intent(getApplicationContext(),
					LeadViewerActivity.class);
			showContent.setData(Uri.parse(leadUrl));
			showContent.putExtra("accessToken", mAccessToken);
			startActivity(showContent);
		} else {
			viewer.updateUrl(leadUrl);
		}
	}
}