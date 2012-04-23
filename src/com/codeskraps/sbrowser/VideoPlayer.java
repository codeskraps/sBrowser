/**
 * sBrowser
 * Copyright (C) Carles Sentis 2011 <codeskraps@gmail.com>
 *
 * sBrowser is free software: you can
 * redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later
 * version.
 *  
 * sBrowser is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *  
 * You should have received a copy of the GNU
 * General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.codeskraps.sbrowser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends Activity {
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
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}