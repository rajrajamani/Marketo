package com.marketo.leadexplorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.marketo.rest.android.LeadAPI;
import com.marketo.rest.leadapi.client.Lead;
import com.marketo.rest.leadapi.client.MarketoException;
import com.marketo.rest.oauth.client.AuthToken;

public class LeadSearchActivity extends Activity {

	private EditText mSearchView;
	private String mSearchKeyword;
	private SearchTask mAuthTask;
	public Object mAuthToken;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private String mAccessToken;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu., menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if (bundle.getString("accessToken") != null) {
			mAccessToken = bundle.getString("accessToken");
			Log.d("Marketo", "Search received Access Token :" + mAccessToken);
		} else {
			Log.e("Marketo", "No access tokens available");
		}

		setContentView(R.layout.search);

		mSearchView = (EditText) findViewById(R.id.search_email);
		mSearchView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.search_email) {
							attemptSearch();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.search_button).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						attemptSearch();
					}
				});
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptSearch() {
		// Reset errors.
		mSearchView.setError(null);

		// Store values at the time of the login attempt.
		mSearchKeyword = mSearchView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mSearchKeyword)) {
			mSearchView.setError(getString(R.string.error_field_required));
			focusView = mSearchView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new SearchTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class SearchTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Perform search
				String[] keys = mSearchKeyword.split(",");
				ArrayList<Lead> leads2;
				String[] fields = new String[] { "id", "firstName", "lastName",
						"email", "facebookId", "linkedinId", "twitterId",
						"leadScore" };
				AuthToken at = new AuthToken();
				at.access_token = mAccessToken;
				/*
				 * leads2 = LeadAPI.getMultipleLeadsByEmail(
				 * getString(R.string.apiUrl), at, keys, fields); //
				 * assertNotNull(leads2); ArrayList<String> results = new
				 * ArrayList<String>(); for (Lead lead : leads2) {
				 * results.add(lead.getLeadAttrib("email")); }
				 * Collections.sort(results.subList(1, results.size())); //
				 * this.assertArrayEquals(emails, results.toArray());
				 */
				return false;
			} catch (Exception e) {
				Log.e("Marketo", "Search Failed");
				return false;
			}

//			Log.i("Marketo", "Search Successful");
//			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				Intent leadMaster = new Intent(getApplicationContext(),
						LeadListActivity.class);

				// Sending data to another Activity
				leadMaster.putExtra("accessToken", mAccessToken);

				Log.d("Marketo", "Switching to Lead Master after search");
				startActivity(leadMaster);
				finish();
			} else {
				mSearchView
						.setError(getString(R.string.error_no_leads_found));
				mSearchView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

}
