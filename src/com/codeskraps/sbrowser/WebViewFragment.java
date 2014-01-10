package com.codeskraps.sbrowser;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebViewFragment extends Fragment {
	private static final String TAG = WebViewFragment.class.getSimpleName();

	private ProgressBar prgBar = null;
	private WebView webView = null;
	private String defaultUserAgent = null;

	private boolean webLoading;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.webview, container, false);
	}

	@SuppressLint("NewApi")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		webView = (WebView) view.findViewById(R.id.webview);
		prgBar = (ProgressBar) view.findViewById(R.id.prgBar);

		((SBrowserActivity) getActivity()).setWebView(webView);
		webLoading = false;
		registerForContextMenu(webView);

		defaultUserAgent = new String(webView.getSettings().getUserAgentString());

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

		webView.setInitialScale(100);
		webView.setNetworkAvailable(true);
		webView.setWebViewClient(new WebViewActivityClient());
		webView.setWebChromeClient(new WebChromeActivityClient());
		webView.setDownloadListener(new DownloadActivityListener());

		SBrowserData sBrowserData = ((SBrowserApplication) getActivity().getApplication())
				.getsBrowserData();
		webView.loadUrl(sBrowserData.getetxtHome());
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onResume() {
		super.onResume();

		SBrowserData sBrowserData = ((SBrowserApplication) getActivity().getApplication())
				.getsBrowserData();

		webView.getSettings().setJavaScriptEnabled(sBrowserData.isChkJavascript());

		// @formatter:off
		switch (sBrowserData.getLstflash()) {
		case 0: webView.getSettings().setPluginState(PluginState.ON); break;
		case 1: webView.getSettings().setPluginState(PluginState.ON_DEMAND); break;
		case 2: webView.getSettings().setPluginState(PluginState.OFF); break;
		}
		// @formatter:on

		String userAgent = new String(webView.getSettings().getUserAgentString());
		String[] lstUserAgentArray = getResources().getStringArray(
				R.array.prefs_user_agent_human_value);

		switch (sBrowserData.getUserAgent()) {
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
		}

		webView.getSettings().setUserAgentString(userAgent);
		Log.d(TAG, "User Agent: " + webView.getSettings().getUserAgentString());

		if (sBrowserData.isSelected()) {
			webView.loadUrl(sBrowserData.getSaveState());
			sBrowserData.setSelected(false);
		}
	}

	private class WebViewActivityClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, url);
			Log.d(TAG, "MimeType: " + MimeTypeMap.getFileExtensionFromUrl(url));

			if ((new String("mp4").equalsIgnoreCase(MimeTypeMap.getFileExtensionFromUrl(url)))
					|| (new String("3gp")
							.equalsIgnoreCase(MimeTypeMap.getFileExtensionFromUrl(url)))) {

				Intent intent = new Intent();
				intent.setClass(getActivity(), VideoPlayer.class);
				intent.setData(Uri.parse(url));
				startActivity(intent);

			} else if ((new String("ppt")
					.equalsIgnoreCase(MimeTypeMap.getFileExtensionFromUrl(url)))
					|| (new String("doc")
							.equalsIgnoreCase(MimeTypeMap.getFileExtensionFromUrl(url)))
					|| (new String("pdf")
							.equalsIgnoreCase(MimeTypeMap.getFileExtensionFromUrl(url)))
					|| (new String("apk")
							.equalsIgnoreCase(MimeTypeMap.getFileExtensionFromUrl(url)))) {

				DownloadManager dm = (DownloadManager) getActivity().getSystemService(
						Context.DOWNLOAD_SERVICE);
				Request request = new Request(Uri.parse(url));
				dm.enqueue(request);
				Intent i = new Intent();
				i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
				startActivity(i);

			} else if (url.startsWith("market://")) {
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				goToMarket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(goToMarket);

			} else {
				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();

			Log.d(TAG, "onReceivedSslError");
		}

		public void onReceivedError(WebView view, int errorCode, String description,
				String failingUrl) {
			Log.d(TAG, "Error: " + description + ", " + failingUrl);
			// Toast.makeText(SBrowserActivity.this,
			// "sBrowser - Something when wrong!!!", Toast.LENGTH_SHORT).show();
		}
	}

	private class WebChromeActivityClient extends WebChromeClient {
		public void onProgressChanged(WebView view, int progress) {

			if (!webLoading) {
				// btnRefresh.setImageResource(R.drawable.webview_stop);
				webLoading = true;
				prgBar.setVisibility(View.VISIBLE);
			}
			prgBar.setProgress(progress);

			if (progress == 100) {
				prgBar.setVisibility(View.GONE);
				// btnRefresh.setImageResource(R.drawable.webview_refresh);
				// setBackForwardButtons();
				webLoading = false;
			}
		}
	}

	private class DownloadActivityListener implements DownloadListener {
		public void onDownloadStart(final String url, String userAgent, String contentDisposition,
				String mimetype, long contentLength) {
			Log.d(TAG, "onDownloadStart");
		}
	}
}
