package com.marketo.mktoshell.common;

import java.io.File;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

public class DownloadFile extends Activity {

	boolean started = false;
	String url = "http://go.kokopop.com/rs/mktodemo17/images/resume.pdf";
	String FILE_NAME = "resume.pdf";
	File directory = new File(Environment.getExternalStorageDirectory(),
			"ContentDirectory");
	File examplefile = new File(Environment.getExternalStorageDirectory()
			+ "/ContentDirectory", FILE_NAME);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_downloadfile);
		registerReceiver(onComplete, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		check();
	}

	BroadcastReceiver onComplete = new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent intent) {
			if (!started) {
				started = true;
				// perform action upon completion
			}
		}
	};

	public void check() {
		if (!directory.exists())
			directory.mkdir();
		if (!getConnectivityStatus()) {
			if (!started) {
				started = true;
				// perform action if no connection
			}
		}
		if (examplefile.exists()) {
			boolean deleted = examplefile.delete();
			if (deleted && !started) {
				if (getConnectivityStatus())
					downloadFile(FILE_NAME);
			}
		}
	}

	public boolean getConnectivityStatus() {
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null)
			return info.isConnected();
		else
			return false;
	}

	public void downloadFile(String FILE_NAME) {
		String url = "http://go.kokopop.com/rs/mktodemo17/images/resume.pdf";
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(url));
		request.setDescription("Example file to be displayed.");
		request.setTitle(FILE_NAME);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			request.allowScanningByMediaScanner();
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
		}
		request.setDestinationInExternalPublicDir("ContentDirectory", FILE_NAME);

		DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);
	}

}
