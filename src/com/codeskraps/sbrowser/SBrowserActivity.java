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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class SBrowserActivity extends FragmentActivity implements OnClickListener {
	private static final String TAG = "sBrowser";

	private SBrowserData sBrowserData = null;
	private DataBaseData dataBaseData = null;

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

		if (sBrowserData.isChkFullscreen()) {

			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sbrowser);

		FrameLayout fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
		WebViewFragment wF = new WebViewFragment();
		if (fragmentContainer != null) {
			if (savedInstanceState != null) { return; }
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, wF)
					.commit();
		}

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

		String newURL = new String();
		Uri data = this.getIntent().getData();
		if (data != null) {
			newURL = data.toString();
			Log.d(TAG, "text: " + newURL);
		}
	}

	public void setWebView(WebView webView) {
		this.webView = webView;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (webView.canGoBack()) webView.goBack();
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

	@SuppressWarnings("deprecation")
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
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

			try {
				dataBaseData.deleteTable(DataBaseData.DB_TABLE_TABS);
			} catch (Exception e) {
				Log.e(TAG, "deleteTable: " + e.getMessage());
			}
			this.finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);

		} else if (item.getItemId() == R.id.itemFeedback) {

			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

			String aEmailList[] = { "codeskraps@gmail.com" };

			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "sBrowser - Feedback");
			emailIntent.setType("plain/text");

			startActivity(Intent.createChooser(emailIntent, "Send your feedback in:"));

		} else if (item.getItemId() == R.id.itemBuyMeAPint) {

			try {
				Intent marketIntent = new Intent(Intent.ACTION_VIEW);
				marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
						| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				startActivity(marketIntent.setData(Uri.parse("market://developer?id=Codeskraps")));
			} catch (Exception e) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/developer?id=Codeskraps"));
				browserIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
						| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				startActivity(browserIntent);
				Log.e(TAG, e.getMessage());
			}

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
				Log.e(TAG, "Picture:" + e.getMessage());
				BookmarkItem bookmarkItem = new BookmarkItem("Set title", "Set url");
				bookmarkItem.setImage(null);
				sBrowserData.setBookmarkItem(bookmarkItem);
			}

			SBrowserApplication sBrwoserApp = (SBrowserApplication) getApplication();
			SBrowserActivity.this.startActivity(sBrwoserApp.getMenuIntent(item,
					SBrowserActivity.this));
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
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

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(result.getExtra());
			} else {
				android.content.ClipboardManager newClipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("label", result.getExtra());
				newClipboard.setPrimaryClip(clip);
			}

			break;

		case R.id.itemShareLink:

			try {
				sharingIntent.setType("text/html");
				sharingIntent.putExtra(Intent.EXTRA_TEXT, result.getExtra());
				startActivity(Intent.createChooser(sharingIntent, "Share using..."));
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, "Erro Sharing link: " + e);
			}

			break;

		case R.id.itemShareImage:

			try {
				sharingIntent.setType("image/*");
				sharingIntent.putExtra(Intent.EXTRA_STREAM, result.getExtra());
				startActivity(Intent.createChooser(sharingIntent, "Share image using..."));
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, "Erro Sharing Image: " + e);
			}

			break;
		}

		return super.onContextItemSelected(item);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnWww:
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(getResources().getString(R.string.alertHttpTitle));
			alert.setMessage(getResources().getString(R.string.alertHttpSummary));
			final EditText input = new EditText(this);
			String url = webView == null ? null : webView.getUrl();
			if (url != null) input.setText(url);
			alert.setView(input);
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = input.getText().toString().trim();
					if (value.startsWith("http")) webView.loadUrl(value);
					else webView.loadUrl("http://" + value);
				}
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
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
			// if (!webLoading) {
			// webView.reload();
			// } else {
			// webView.stopLoading();
			// }
			break;

		case R.id.btnSearch:
			doSearch();
			break;
		}
	}

	private void setBackForwardButtons() {
		if (webView.canGoForward()) btnRight.setImageResource(R.drawable.webview_right);
		else btnRight.setImageResource(R.drawable.webview_right_bw);
	}

	private void doSearch() {
		final AlertDialog.Builder alertSearch = new AlertDialog.Builder(this);
		alertSearch.setTitle(getResources().getString(R.string.alertSearchTitle));
		alertSearch.setMessage(getResources().getString(R.string.alertSearchSummary));
		final EditText inputSearch = new EditText(this);
		alertSearch.setView(inputSearch);
		alertSearch.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = inputSearch.getText().toString().trim();
				webView.loadUrl("https://encrypted.google.com/search?q=" + value);
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

		if (webView != null) {
			webView.clearCache(true);
			webView.clearHistory();
		}
	}

}