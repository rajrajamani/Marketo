package com.marketo.mktoshell;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.marketo.mktoshell.common.Constants;
import com.marketo.mktoshell.content.Content;

/**
 * A fragment representing a single Track detail screen. This fragment is either
 * contained in a {@link TrackListActivity} in two-pane mode (on tablets) or a
 * {@link TrackDetailActivity} on handsets.
 */
public class TrackDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Content.ContentItem mItem;

	private MapView mapView;

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
			break;
		case Constants.MAP_VIEW:
			rootView = inflater.inflate(R.layout.fragment_track_detail_map,
					container, false);
			break;
		case Constants.TXT_VIEW:
		default:
			rootView = inflater.inflate(R.layout.fragment_track_detail,
					container, false);
			break;
		}

		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			if (mItem.type == Constants.WEB_VIEW) {
				WebView wv = (WebView) rootView
						.findViewById(R.id.track_detail_html);
				if (wvClient == null) {
					wvClient = new WebViewClient();
				}
				wv.setWebViewClient(wvClient);
				wv.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
				wv.loadUrl(mItem.url);
			} else if (mItem.type == Constants.TXT_VIEW) {
				((TextView) rootView.findViewById(R.id.track_detail))
						.setText(mItem.content);
			} else if (mItem.type == Constants.MAP_VIEW) {
				// super.onCreateView(inflater, container, savedInstanceState);
				Context ctx = this.getActivity();
				MapsInitializer.initialize(ctx);

//				View v = inflater.inflate(R.layout.fragment_track_detail_map,
//						container, false);

				// Gets the MapView from the XML layout and creates it
				mapView = (MapView) rootView.findViewById(R.id.track_detail_map);
				mapView.onCreate(savedInstanceState);

				GoogleMap map = mapView.getMap();

				if (map != null) {
					map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					map.getUiSettings().setMyLocationButtonEnabled(false);
					//map.setMyLocationEnabled(true);
					map.moveCamera(CameraUpdateFactory
							.newLatLngZoom(new LatLng(-34.397, 150.644), 10));
					map.animateCamera(CameraUpdateFactory.zoomIn());
					
					Log.i("mktoShell", "Updated map camera pos");
				} else {
					Log.d("mktoShell", "failed to grab map object from view");
				}
			}

		}
		return rootView;
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
}
