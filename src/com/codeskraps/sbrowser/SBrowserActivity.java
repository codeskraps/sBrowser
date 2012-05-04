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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SBrowserActivity extends Activity implements OnClickListener {
	private static final String TAG = "sBrowser";

	private SBrowserData sBrowserData = null;
	private DataBaseData dataBaseData = null;
	
	private boolean activityPaused;
	private boolean webLoading;
	private String defaultUserAgent;

	private ProgressBar prgBar = null;
	private WebView webView = null;
	private ImageView btnWww = null;
	private ImageView btnHome = null;
	private ImageView btnRight = null;
	private ImageView btnRefresh = null;
	private ImageView btnSearch = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
		dataBaseData = ((SBrowserApplication) getApplication()).getDataBaseData();

		activityPaused = false;
		webLoading = false;

		if (sBrowserData.isChkFullscreen()) {

			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview);

		webView = (WebView) findViewById(R.id.webview);
		prgBar = (ProgressBar) findViewById(R.id.prgBar);
		btnWww = (ImageView) findViewById(R.id.btnWww);
		btnHome = (ImageView) findViewById(R.id.btnHome);
		btnRight = (ImageView) findViewById(R.id.btnRight);
		btnRefresh = (ImageView) findViewById(R.id.btnRefresh);
		btnSearch = (ImageView) findViewById(R.id.btnSearch);
		
		btnWww.setOnClickListener(this);
		btnHome.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		btnRefresh.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		
		registerForContextMenu(webView);
		
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.setWebViewClient(new WebViewActivityClient());
		webView.setWebChromeClient(new WebChromeActivityClient());
		webView.setDownloadListener(new DownloadActivityListener());

		String newURL = new String();
		Uri data = this.getIntent().getData();
	    if(data != null) {
	    	newURL = data.toString();
		    Log.d(TAG, "text: " + newURL);    
	    }
	    
	    if (newURL.startsWith("http")) {
			webView.loadUrl(newURL);
		} else if (sBrowserData.getSaveState().equalsIgnoreCase(new String()))
			webView.loadUrl(sBrowserData.getetxtHome());
		else {
			webView.loadUrl(sBrowserData.getSaveState());
		}

	    WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());
	    defaultUserAgent = webView.getSettings().getUserAgentString();
	    
	    BookmarkItem b = new BookmarkItem(webView.getTitle(), webView.getUrl());
	    dataBaseData.insert(DataBaseData.DB_TABLE_TABS, b);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		webView.restoreState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		webView.saveState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		try {

			if (sBrowserData.isChkJavascript())
				webView.getSettings().setJavaScriptEnabled(true);
			else
				webView.getSettings().setJavaScriptEnabled(false);
			
			webView.getSettings().setPluginsEnabled(true);
			
			switch (sBrowserData.getLstflash()) {
			case 0: webView.getSettings().setPluginState(PluginState.ON); break;
			case 1: webView.getSettings().setPluginState(PluginState.ON_DEMAND); break;
			case 2: webView.getSettings().setPluginState(PluginState.OFF); break;
			}

		} catch (Exception e) {
			Log.e(TAG, "Error: " + e.getMessage());
			Toast.makeText(this, getResources().getString(R.string.errorMessage), Toast.LENGTH_LONG).show();
		}
		
		String userAgent = new String(webView.getSettings().getUserAgentString());
	    String[] lstUserAgentArray = getResources().getStringArray(R.array.prefs_user_agent_human_value);
	    
		switch(sBrowserData.getUserAgent()){
		case 0:
			userAgent = defaultUserAgent;
			break;
		case 1: 
			userAgent = userAgent.replaceAll("Android", lstUserAgentArray[1]);
			userAgent = userAgent.replaceAll("Chrome", lstUserAgentArray[1]);
			userAgent = userAgent.replaceAll("Ipad", lstUserAgentArray[1]);
			userAgent = userAgent.replaceAll("Mobile", "Desktop");
			break;
		case 2: 
			userAgent = userAgent.replaceAll("Android", lstUserAgentArray[2]);
			userAgent = userAgent.replaceAll("Firefox", lstUserAgentArray[2]);
			userAgent = userAgent.replaceAll("Ipad", lstUserAgentArray[2]);
			userAgent = userAgent.replaceAll("Mobile", "Desktop");
			break;
		case 3: 
			userAgent = userAgent.replaceAll("Android", lstUserAgentArray[3]);
			userAgent = userAgent.replaceAll("Chrome", lstUserAgentArray[3]);
			userAgent = userAgent.replaceAll("Firefox", lstUserAgentArray[3]); 
			userAgent = userAgent.replaceAll("Mobile", "Desktop");
			break;
		default: break;
		}
		
		webView.getSettings().setUserAgentString(userAgent);

		Log.d(TAG, "User Agent: " + webView.getSettings().getUserAgentString());

		if (sBrowserData.isInvalidate() && activityPaused) {

			sBrowserData.setSaveState(webView.getUrl());
			SBrowserActivity.this.startActivity(new Intent(
					SBrowserActivity.this, SBrowserActivity.class));
			SBrowserActivity.this.finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);

		} else if (sBrowserData.isSelected()) {
			webView.loadUrl(sBrowserData.getSaveState());
			sBrowserData.setSelected(false);
			sBrowserData.setSaveState(new String());
		}
		
		activityPaused = false;
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.d(TAG, "onPause");

		activityPaused = true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (webView.canGoBack())
				webView.goBack();
			else {
				
				finish();
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
			return true;

		} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			doSearch();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		WebView.HitTestResult result = ((WebView) v).getHitTestResult();

		if (result.getType() == HitTestResult.IMAGE_TYPE
				|| result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
			Log.d(TAG, "onCreateContextMenu - SRC_IMAGE_ANCHOR_TYPE");
			menu.setHeaderTitle(result.getExtra());

			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.contextmenuimage, menu);

		} else if (result.getType() == HitTestResult.ANCHOR_TYPE
				|| result.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
			Log.d(TAG, "onCreateContextMenu - SRC_ANCHOR_TYPE");
			menu.setHeaderTitle(result.getExtra());

			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.contextmenulink, menu);

		} else if (result.getType() == HitTestResult.UNKNOWN_TYPE) {
			Log.d(TAG, "onCreateContextMenu - Unknown_type");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.itemQuit) {

			this.finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);

		} else if (item.getItemId() == R.id.itemFeedback) {
			
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);  
					  
			String aEmailList[] = { "codeskraps@gmail.com" };  
			  
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);    
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "sBrowser - Feedback");  
			emailIntent.setType("plain/text");  
			
			startActivity(Intent.createChooser(emailIntent, "Send your feedback in:"));
			
		} else {
			try {
				Picture picture = webView.capturePicture();
				PictureDrawable pictureDrawable = new PictureDrawable(picture);
				Bitmap bitmap = Bitmap.createBitmap(300, 300, Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				canvas.drawPicture(pictureDrawable.getPicture());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, (OutputStream) bos);
				bitmap.isRecycled();
				
				BookmarkItem bookmarkItem = new BookmarkItem(webView.getTitle(), webView.getUrl());
				bookmarkItem.setImage(bos.toByteArray());
				sBrowserData.setBookmarkItem(bookmarkItem);
				bos.close();
				
			} catch (Exception e) {
				Log.d(TAG, "Error - " + e);
				BookmarkItem bookmarkItem = new BookmarkItem("Set title", "Set url");
				bookmarkItem.setImage(null);
				sBrowserData.setBookmarkItem(bookmarkItem);
			}
			
			SBrowserApplication sBrwoserApp = (SBrowserApplication) getApplication();
			SBrowserActivity.this.startActivity(sBrwoserApp.getMenuIntent(item, SBrowserActivity.this));
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		WebView.HitTestResult result = webView.getHitTestResult();
		Log.d(TAG, "result: " + result.getExtra());
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);

		switch (item.getItemId()) {
		case R.id.itemSaveImage:
		case R.id.itemSaveLink:

			try {
				DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
				Request request = new Request(Uri.parse(result.getExtra()));
				dm.enqueue(request);
			} catch (Exception e) {
				Toast.makeText(this, "sBrowser - Error saving...", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Erro Downloading: " + e);
			}
			break;

		case R.id.itemCopyLink:

			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			clipboard.setText(result.getExtra());

			break;

		case R.id.itemShareLink:

			try {
				sharingIntent.setType("text/html");
				sharingIntent.putExtra(Intent.EXTRA_TEXT, result.getExtra());
				startActivity(Intent.createChooser(sharingIntent,
						"Share using..."));
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, "Erro Sharing link: " + e);
			}

			break;

		case R.id.itemShareImage:

			try {
				sharingIntent.setType("image/*");
				sharingIntent.putExtra(Intent.EXTRA_STREAM, result.getExtra());
				startActivity(Intent.createChooser(sharingIntent,
						"Share image using..."));
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, "Erro Sharing Image: " + e);
			}

			break;
		}

		return super.onContextItemSelected(item);
	}
	
	private class WebViewActivityClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, url);
			Log.d(TAG, "MimeType: " + MimeTypeMap.getFileExtensionFromUrl(url));

			if ((new String("mp4").equalsIgnoreCase(MimeTypeMap
					.getFileExtensionFromUrl(url)))
					|| (new String("3gp").equalsIgnoreCase(MimeTypeMap
							.getFileExtensionFromUrl(url)))) {

				Intent intent = new Intent();
				intent.setClass(SBrowserActivity.this, VideoPlayer.class);
				intent.setData(Uri.parse(url));
				startActivity(intent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);

			} else if ((new String("ppt").equalsIgnoreCase(MimeTypeMap
					.getFileExtensionFromUrl(url)))
					|| (new String("doc").equalsIgnoreCase(MimeTypeMap
							.getFileExtensionFromUrl(url)))
					|| (new String("pdf").equalsIgnoreCase(MimeTypeMap
							.getFileExtensionFromUrl(url)))
					|| (new String("apk").equalsIgnoreCase(MimeTypeMap
							.getFileExtensionFromUrl(url)))) {

				DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
				Request request = new Request(Uri.parse(url));
				dm.enqueue(request);
				Intent i = new Intent();
				i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
				startActivity(i);

			} else if (url.startsWith("market://")){
				
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				goToMarket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(goToMarket);
				
			} else {

				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
			
			Log.d(TAG, "onReceivedSslError");
		}

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.d(TAG, "Error: " + description + ", " + failingUrl);
			//Toast.makeText(SBrowserActivity.this, "sBrowser - Something when wrong!!!", Toast.LENGTH_SHORT).show();
		}
	}

	private class WebChromeActivityClient extends WebChromeClient {
		public void onProgressChanged(WebView view, int progress) {

			if (!webLoading) {
				btnRefresh.setImageResource(R.drawable.webview_stop);
				webLoading = true;
				prgBar.setVisibility(View.VISIBLE);
			}
			prgBar.setProgress(progress);

			if (progress == 100) {
				prgBar.setVisibility(View.GONE);
				btnRefresh.setImageResource(R.drawable.webview_refresh);
				setBackForwardButtons();
				webLoading = false;
			}
		}
	}

	private class DownloadActivityListener implements DownloadListener {
		public void onDownloadStart(final String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Log.d(TAG, "onDownloadStart");
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnWww:
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(getResources().getString(R.string.alertHttpTitle));
			alert.setMessage(getResources()
					.getString(R.string.alertHttpSummary));
			final EditText input = new EditText(this);
			input.setText(webView.getUrl());
			alert.setView(input);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString().trim();
							if (value.startsWith("http"))
								webView.loadUrl(value);
							else webView.loadUrl("http://" + value);
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;

		case R.id.btnHome:
			webView.loadUrl(sBrowserData.getetxtHome());
			break;

		case R.id.btnRight:
			webView.goForward();
			break;

		case R.id.btnRefresh:
			if (!webLoading) {
				webView.reload();
			} else {
				webView.stopLoading();
			}
			break;

		case R.id.btnSearch:
			doSearch();
			break;
		}
	}

	private void setBackForwardButtons() {

		if (webView.canGoForward())
			btnRight.setImageResource(R.drawable.webview_right);
		else
			btnRight.setImageResource(R.drawable.webview_right_bw);
	}

	private void doSearch() {
		final AlertDialog.Builder alertSearch = new AlertDialog.Builder(this);
		alertSearch.setTitle(getResources().getString(R.string.alertSearchTitle));
		alertSearch.setMessage(getResources().getString(R.string.alertSearchSummary));
		final EditText inputSearch = new EditText(this);
		alertSearch.setView(inputSearch);
		alertSearch.setPositiveButton("Ok",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = inputSearch.getText().toString().trim();
						webView.loadUrl("http://www.google.com/search?q="
								+ value);
					}
				});

		alertSearch.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alertSearch.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "OnDestroy");

		webView.clearCache(true);
		webView.clearHistory();
	}

}