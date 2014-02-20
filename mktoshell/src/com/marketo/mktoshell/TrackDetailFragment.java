package com.marketo.mktoshell;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marketo.mktoshell.common.Constants;
import com.marketo.mktoshell.common.GetYoutubeData;
import com.marketo.mktoshell.common.YoutubeCallBackInfo;
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
			YoutubeCallBackInfo ytCInfo = new YoutubeCallBackInfo(this, rootView, mItem);
			try {
				String url = new GetYoutubeData().execute(ytCInfo).get();
				setVdoView(rootView, url);
			} catch (InterruptedException e) {
				Log.e("mktoshell", "Interrupted call while finding youtube url");
				e.printStackTrace();
			} catch (ExecutionException e) {
				Log.e("mktoshell", "Execution error while playing youtube video");
				e.printStackTrace();
			}
			break;
		case Constants.CONT_VIEW:
			rootView = inflater.inflate(R.layout.fragment_track_detail_cont,
					container, false);
			setContentView(rootView);
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

	public void setVdoView(View rootView, String url) {
		VideoView vw = (VideoView) rootView
				.findViewById(R.id.track_detail_vdo);
		
		vw.setVideoURI(Uri.parse(url));
        MediaController mc = new MediaController(this.getActivity());
        vw.setMediaController(mc);
        vw.requestFocus();
        vw.start();          
        mc.show();
        
//		Uri uri = Uri.parse(url);
//		vw.setVideoPath(url);
//		vw.start();
	}
	

	private void setMapView(View rootView, Bundle savedInstanceState) {
		Context ctx = this.getActivity();
		MapsInitializer.initialize(ctx);

		// Gets the MapView from the XML layout and creates it
		mapView = (MapView) rootView
				.findViewById(R.id.track_detail_map);
		mapView.onCreate(savedInstanceState);

		GoogleMap map = mapView.getMap();

		if (map != null) {
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			// map.setMyLocationEnabled(true);

			LatLng pos = new LatLng(mItem.lattitude, mItem.longitude);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(
					pos, 15));
			map.addMarker(new MarkerOptions()
					.position(pos).title(mItem.content));

			Log.i("mktoShell", "Updated map camera pos");
		} else {
			Log.d("mktoShell", "failed to grab map object from view");
		}		
	}

	private void setWebView(View rootView) {
		WebView wv = (WebView) rootView
				.findViewById(R.id.track_detail_html);
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
}
