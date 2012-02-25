package com.codeskraps.sbrowser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends Activity {
//implements OnBufferingUpdateListener, OnCompletionListener,
//OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {
	private static final String TAG = VideoPlayer.class.getSimpleName();
	
	private VideoView videoView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "VideoPlayer onCreate");
		
		setContentView(R.layout.videoview);
		
		Log.d(TAG, "uri: " + getIntent().getData());
		videoView=(VideoView)findViewById(R.id.surface_view);
		videoView.setVideoURI(getIntent().getData());
		videoView.setMediaController(new MediaController(this));
		videoView.requestFocus();
		videoView.start();

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
					
			this.finish();
			//overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
