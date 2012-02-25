package com.codeskraps.sbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
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
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

public class SBrowserActivity extends Activity implements OnClickListener {
	private static final String TAG = SBrowserActivity.class.getSimpleName();

	private SBrowserData sBrowserData = null;
	private boolean activityPaused;
	private boolean webLoading;

	// private long enqueue;
	// private DownloadManager dm;

	private ProgressBar prgBar = null;
	private WebView webView = null;
	private HorizontalScrollView hscrlView = null;
	private ScrollView scrlView = null;
	private ImageView btnWww = null;
	private ImageView btnHome = null;
	private ImageView btnLeft = null;
	private ImageView btnRight = null;
	private ImageView btnRefresh = null;
	private ImageView btnSearch = null;
	private ImageView btnQuit = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sBrowserData = ((SBrowserApplication) getApplication())
				.getsBrowserData();

		activityPaused = false;
		webLoading = false;

		if (sBrowserData.isChkFullscreen()) {

			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview);

		prgBar = (ProgressBar) findViewById(R.id.prgBar);
		btnWww = (ImageView) findViewById(R.id.btnWww);
		btnHome = (ImageView) findViewById(R.id.btnHome);
		btnLeft = (ImageView) findViewById(R.id.btnLeft);
		btnRight = (ImageView) findViewById(R.id.btnRight);
		btnRefresh = (ImageView) findViewById(R.id.btnRefresh);
		btnSearch = (ImageView) findViewById(R.id.btnSearch);
		btnQuit = (ImageView) findViewById(R.id.btnBack);

		btnWww.setOnClickListener(this);
		btnHome.setOnClickListener(this);
		btnLeft.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		btnRefresh.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnQuit.setOnClickListener(this);

		try {

			webView = (WebView) findViewById(R.id.webview);
			registerForContextMenu(webView);
			if (sBrowserData.isChkJavascript())
				webView.getSettings().setJavaScriptEnabled(true);
			else
				webView.getSettings().setJavaScriptEnabled(false);
			webView.getSettings().setPluginsEnabled(true);
			switch (sBrowserData.getLstflash()) {
			case 0:
				webView.getSettings().setPluginState(PluginState.ON);
				break;
			case 1:
				webView.getSettings().setPluginState(PluginState.ON_DEMAND);
				break;
			case 2:
				webView.getSettings().setPluginState(PluginState.OFF);
				break;
			}
			webView.getSettings().setBuiltInZoomControls(true);
			webView.getSettings().setUseWideViewPort(true);
			webView.setWebViewClient(new WebViewActivityClient());
			webView.setWebChromeClient(new WebChromeActivityClient());
			webView.setDownloadListener(new DownloadActivityListener());
			// webView.setOnLongClickListener(this);

		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "Browser: " + e.getMessage());
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		// BroadcastReceiver receiver = new BroadcastReceiver() {
		// @Override
		// public void onReceive(Context context, Intent intent) {
		// String action = intent.getAction();
		// if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
		// long downloadId = intent.getLongExtra(
		// DownloadManager.EXTRA_DOWNLOAD_ID, 0);
		// Query query = new Query();
		// query.setFilterById(enqueue);
		// Cursor c = dm.query(query);
		// if (c.moveToFirst()) {
		// int columnIndex = c
		// .getColumnIndex(DownloadManager.COLUMN_STATUS);
		// if (DownloadManager.STATUS_SUCCESSFUL == c
		// .getInt(columnIndex)) {
		// Log.d(TAG, "onReceive");
		// // ImageView view = (ImageView)
		// // findViewById(R.id.imageView1);
		// // String uriString = c
		// // .getString(c
		// // .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
		// // view.setImageURI(Uri.parse(uriString));
		// }
		// }
		// }
		// }
		// };
		//
		// registerReceiver(receiver, new IntentFilter(
		// DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		
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

		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();
		if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			scrlView = (ScrollView) findViewById(R.id.scrlView);
			scrlView.setVerticalScrollBarEnabled(false);
		} else {
			hscrlView = (HorizontalScrollView) findViewById(R.id.hscrlView);
			hscrlView.setHorizontalScrollBarEnabled(false);
		}

		Log.d(TAG, "User Agent: " + webView.getSettings().getUserAgentString());
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

		if (sBrowserData.isInvalidate() && activityPaused) {

			sBrowserData.setSaveState(webView.getUrl());
			SBrowserActivity.this.startActivity(new Intent(
					SBrowserActivity.this, SBrowserActivity.class));
			SBrowserActivity.this.finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);

		} else if (sBrowserData.isSelected()) {
			webView.loadUrl(sBrowserData.getSaveState());
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
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (webView.canGoBack())
				webView.goBack();
			else {
				final AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle(getResources()
						.getString(R.string.alertQuitTitle));
				alert.setMessage(getResources().getString(
						R.string.alertQuitSummary));
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								finish();
								overridePendingTransition(R.anim.fadein,
										R.anim.fadeout);
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
			// Menu options for an image.
			// set the header title to the image url
			menu.setHeaderTitle(result.getExtra());
			// menu.add(0, ID_SAVEIMAGE, 0,
			// "Save Image").setOnMenuItemClickListener(handler);
			// menu.add(0, ID_VIEWIMAGE, 0,
			// "View Image").setOnMenuItemClickListener(handler);

			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.contextmenuimage, menu);

		} else if (result.getType() == HitTestResult.ANCHOR_TYPE
				|| result.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
			Log.d(TAG, "onCreateContextMenu - SRC_ANCHOR_TYPE");
			// Menu options for a hyperlink.
			// set the header title to the link url
			menu.setHeaderTitle(result.getExtra());
			// menu.add(0, ID_SAVELINK, 0,
			// "Save Link").setOnMenuItemClickListener(handler);
			// menu.add(0, ID_SHARELINK, 0,
			// "Share Link").setOnMenuItemClickListener(handler);

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

		} else {
			Picture p = webView.capturePicture();
			//OutputStream os = null;
			//os = this.openFileOutput("testPicture", ontext.MODE_WORLD_READABLE); 
			//webView.capturePicture().writeToStream(os);
			//os.flush();
            //os.close(); 
			Bitmap b = Bitmap.createBitmap(p.getWidth(), p.getHeight(), Bitmap.Config.ARGB_8888);
            			
			BookmarkItem bookmarkItem = new BookmarkItem(webView.getTitle(), webView.getUrl());
			bookmarkItem.setImage(b);
			sBrowserData.setBookmarkItem(bookmarkItem);
			
			SBrowserApplication sBrwoserApp = (SBrowserApplication) getApplication();
			SBrowserActivity.this.startActivity(sBrwoserApp.getMenuIntent(item,
					SBrowserActivity.this));
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		WebView.HitTestResult result = webView.getHitTestResult();
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);

		switch (item.getItemId()) {
		case R.id.itemSaveImage:
		case R.id.itemSaveLink:

			try {
				DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
				Request request = new Request(Uri.parse(result.getExtra()));
				dm.enqueue(request);
			} catch (Exception e) {
				e.printStackTrace();
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
				// enqueue = dm.enqueue(request);
				Intent i = new Intent();
				i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
				startActivity(i);

			} else {

				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
		}

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Toast.makeText(SBrowserActivity.this, "Oh no! " + description,
					Toast.LENGTH_SHORT).show();
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

	@Override
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
							webView.loadUrl(value);
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

		case R.id.btnLeft:
			Log.d(TAG, "webLweft");
			webView.goBack();
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

		case R.id.btnBack:
			this.finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			break;
		}
	}

	private void setBackForwardButtons() {

		if (webView.canGoBack())
			btnLeft.setImageResource(R.drawable.webview_left);
		else
			btnLeft.setImageResource(R.drawable.webview_left_bw);

		if (webView.canGoForward())
			btnRight.setImageResource(R.drawable.webview_right);
		else
			btnRight.setImageResource(R.drawable.webview_right_bw);
	}

	private void doSearch() {
		final AlertDialog.Builder alertSearch = new AlertDialog.Builder(this);
		alertSearch.setTitle(getResources()
				.getString(R.string.alertSearchTitle));
		alertSearch.setMessage(getResources().getString(
				R.string.alertSearchSummary));
		final EditText inputSearch = new EditText(this);
		alertSearch.setView(inputSearch);
		alertSearch.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = inputSearch.getText().toString().trim();
						webView.loadUrl("http://www.google.com/search?q="
								+ value);
					}
				});

		alertSearch.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
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