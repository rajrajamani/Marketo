package com.marketo.mktoshell;

import java.util.concurrent.ExecutionException;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.marketo.mktoshell.common.Constants;
import com.marketo.mktoshell.common.GetYoutubeData;
import com.marketo.mktoshell.common.YoutubeCallBackInfo;
import com.marketo.mktoshell.content.Content;
import com.marketo.mktoshell.content.Content.ContentItem;

/**
 * A fragment representing a single Track detail screen. This fragment is either
 * contained in a {@link TrackListActivity} in two-pane mode (on tablets) or a
 * {@link TrackDetailActivity} on handsets.
 */
public class TrackDetailFragment extends Fragment implements
		OnInitializedListener {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	public static final String YOUTUBE_KEY = "AIzaSyB52k-xbNxGVynHq4nwXKZwM8oAA0pXHTg";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Content.ContentItem mItem;

	private MapView mapView;

	private YouTubePlayer YPlayer = null;

	private String videoId = null;

	private static WebViewClient wvClient;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TrackDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = Content.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = null;
		switch (mItem.type) {
		case Constants.WEB_VIEW:
			rootView = inflater.inflate(R.layout.fragment_track_detail_html,
					container, false);
			setWebView(rootView);
			break;
		case Constants.MAP_VIEW:
			rootView = inflater.inflate(R.layout.fragment_track_detail_map,
					container, false);
			setMapView(rootView, savedInstanceState);
			break;
		case Constants.VDO_VIEW:
			rootView = inflater.inflate(R.layout.fragment_track_detail_vdo,
					container, false);
			YoutubeCallBackInfo ytCallback = new YoutubeCallBackInfo(this,
					rootView, mItem);
			try {
				videoId = new GetYoutubeData().execute(ytCallback).get();
				if (videoId != null) {
					setVdoView(rootView, videoId);
				} else {
					Log.e("Unable to find id of video with URL: %s", mItem.url);
				}
			} catch (InterruptedException e) {
				Log.e("mktoshell", "Error getting Youtube info");
			} catch (ExecutionException e) {
				Log.e("mktoshell", "Error getting Youtube info");
			}
			break;
		case Constants.CONT_VIEW:
			rootView = inflater.inflate(R.layout.fragment_track_detail_cont,
					container, false);

			break;
		case Constants.TXT_VIEW:
		default:
			rootView = inflater.inflate(R.layout.fragment_track_detail,
					container, false);
			((TextView) rootView.findViewById(R.id.track_detail))
					.setText(mItem.content);
			
			break;
		}

		return rootView;
	}

	private void setContentView(View rootView) {
		// TODO Auto-generated method stub

	}

	private void setVdoView(View rootView, String id) {
		YouTubePlayerView vw = (YouTubePlayerView) rootView
				.findViewById(R.id.fragment_track_detail_vdo);
		if (YPlayer == null) {
			vw.initialize(YOUTUBE_KEY, this);
		} else {
			YPlayer.cueVideo(id);
		}
	}

	private void setMapView(View rootView, Bundle savedInstanceState) {
		Context ctx = this.getActivity();
		MapsInitializer.initialize(ctx);

		// Gets the MapView from the XML layout and creates it
		mapView = (MapView) rootView.findViewById(R.id.track_detail_map);
		mapView.onCreate(savedInstanceState);

		GoogleMap map = mapView.getMap();

		if (map != null) {
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			// map.setMyLocationEnabled(true);

			LatLng pos = new LatLng(mItem.lattitude, mItem.longitude);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
			map.addMarker(new MarkerOptions().position(pos)
					.title(mItem.content));

			Log.i("mktoShell", "Updated map camera pos");
		} else {
			Log.d("mktoShell", "failed to grab map object from view");
		}
	}

	private void setWebView(View rootView) {
		WebView wv = (WebView) rootView.findViewById(R.id.track_detail_html);
		if (wvClient == null) {
			wvClient = new WebViewClient();
		}
		wv.setWebViewClient(wvClient);
		wv.getSettings().setDomStorageEnabled(true);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		wv.loadUrl(mItem.url);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mapView != null) {
			mapView.onPause();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mapView != null) {
			mapView.onResume();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mapView != null) {
			mapView.onDestroy();
		}
	}

	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider,
			YouTubeInitializationResult errorReason) {
		if (errorReason.isUserRecoverableError()) {
			Log.e("mktoshell", "recoverable youtube error");
		} else {
			Log.e("mktoshell",
					"There was an error initializing the YouTubePlayer");
		}
	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			YPlayer = player;
			/*
			 * Now that this variable YPlayer is global you can access it
			 * throughout the activity, and perform all the player actions like
			 * play, pause and seeking to a position by code.
			 */
			YPlayer.loadVideo(videoId);
		}
		Log.i("mktoshell", "Starting video now");

	}
}
