package com.codeskraps.sbrowser;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

public class SBrowserApplication extends Application {
	private static final String TAG = SBrowserApplication.class.getSimpleName();
	private static final String CHKFULLSCREEN = "ckbfullscreen";
	private static final String CHKJAVASCRIPT = "ckbjavascript";
	private static final String LSTFLASH = "lstflash";
	private static final String ETXTHOME = "etxtHome";
	
	private SBrowserData sBrowserData = null;
	private DataBaseData dataBaseData = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(TAG, "onCreate started");
		
		setsBrowserData(new SBrowserData(this));
		setDataBaseData(new DataBaseData(this));
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		boolean chkFullscreen = prefs.getBoolean(CHKFULLSCREEN, false);
		boolean chkJavascript = prefs.getBoolean(CHKJAVASCRIPT, true);
		String lstFlash = prefs.getString(LSTFLASH, "0");
		String etxtHome = prefs.getString(ETXTHOME, getResources().getString(R.string.pref_home_summary));
		
		sBrowserData.setChkFullscreen(chkFullscreen);
		sBrowserData.setChkJavascript(chkJavascript);
		sBrowserData.setLstflash(Integer.parseInt(lstFlash));
		sBrowserData.setetxtHome(etxtHome);
	}
	
	public Intent getMenuIntent(MenuItem item, Context context) {
		Log.d(TAG, "SBrowserApplication - menu intent2");
		switch (item.getItemId()) {
//		
		case R.id.itemBookmarks: 	return new Intent(context, BookmarksActivity.class);
		case R.id.itemDownloads:	
			Intent i = new Intent();
			i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
			return i;
		case R.id.itemPreference:	return new Intent(context, PreferenceActivity.class);
		}
		Log.d(TAG, "SBrowserApplication - menu intent2");
		return null;
	}

	public SBrowserData getsBrowserData() {
		return sBrowserData;
	}

	public void setsBrowserData(SBrowserData sBrowserData) {
		this.sBrowserData = sBrowserData;
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		dataBaseData.close();
	}

	public DataBaseData getDataBaseData() {
		return dataBaseData;
	}

	public void setDataBaseData(DataBaseData dataBaseData) {
		this.dataBaseData = dataBaseData;
	}
}
