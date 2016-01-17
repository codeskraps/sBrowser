/**
 * sBrowser
 * Copyright (C) Carles Sentis 2011 <codeskraps@gmail.com>
 * <p/>
 * sBrowser is free software: you can
 * redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later
 * version.
 * <p/>
 * sBrowser is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU
 * General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.codeskraps.sbrowser.home;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.codeskraps.sbrowser.R;
import com.codeskraps.sbrowser.misc.L;

public class VideoPlayer extends Activity {
    private static final String TAG = VideoPlayer.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        L.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        if (getIntent().getIntExtra("type", 0) == 0) {
            setContentView(R.layout.videoview);

            VideoView videoView = (VideoView) findViewById(R.id.surface_view);
            videoView.setVideoURI(getIntent().getData());
            videoView.setMediaController(new MediaController(this));
            videoView.requestFocus();
            videoView.start();

        } else {
            WebView webView = new WebView(this);
            setContentView(webView);

            WebSettings ws = webView.getSettings();
            ws.setLoadsImagesAutomatically(true);
            ws.setUseWideViewPort(true);
            ws.setAllowFileAccess(true);
            ws.setLoadWithOverviewMode(true);
            ws.setDomStorageEnabled(true);
            ws.setBuiltInZoomControls(true);
            ws.setSupportZoom(true);
            // ws.setDefaultZoom(WebSettings.ZoomDensity.FAR);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                ws.setDisplayZoomControls(true);

            webView.loadUrl(getIntent().getStringExtra("url"));
        }
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