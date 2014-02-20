package com.marketo.mktoshell.common;

import android.view.View;

import com.marketo.mktoshell.TrackDetailFragment;
import com.marketo.mktoshell.content.Content.ContentItem;

public class YoutubeCallBackInfo {

	public ContentItem mItem;
	public View rootView;
	public TrackDetailFragment track;
	
	public YoutubeCallBackInfo(TrackDetailFragment trk, View vw, ContentItem itm) {
		track = trk;
		rootView = vw;
		mItem = itm;
	}
}
