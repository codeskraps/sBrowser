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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

public class PreferenceActivity extends android.preference.PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String TAG = PreferenceActivity.class.getSimpleName();
	private static final String CHKFULLSCREEN = "ckbfullscreen";
	private static final String CHKJAVASCRIPT = "ckbjavascript";
	private static final String LSTFLASH = "lstflash";
	private static final String ETXTHOME = "etxtHome";
		
	private SBrowserData sBrowserData = null;
	private SharedPreferences prefs = null;
	
	private String[] lstFlashArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "Prefs onCreate");
		
		sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
		lstFlashArray = getResources().getStringArray(R.array.prefs_flash_human_value);
        
		if (sBrowserData.isChkFullscreen()) {
        	
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		setTitle(R.string.preference_activity);
		addPreferencesFromResource(R.xml.preferences);
		
		EditTextPreference etxtPrefHome = (EditTextPreference) getPreferenceScreen().findPreference(ETXTHOME);
		ListPreference lstFlash = (ListPreference) getPreferenceScreen().findPreference(LSTFLASH);
		
		etxtPrefHome.setSummary(sBrowserData.getetxtHome());
		lstFlash.setSummary(lstFlashArray[sBrowserData.getLstflash()]);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		prefs.unregisterOnSharedPreferenceChangeListener(this);

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(TAG, "Prefs Changed");
		
		if (key.equals(CHKFULLSCREEN)) {
			Log.d(TAG, "Prefs fullscreen Changed");
			
			boolean chkFullscreen = prefs.getBoolean(CHKFULLSCREEN, false);
			sBrowserData.setChkFullscreen(chkFullscreen);
			sBrowserData.setInvalidate(true);
			
			Log.d(TAG, key + ": " + sBrowserData.isChkFullscreen());
			
			
			PreferenceActivity.this.startActivity(new Intent(PreferenceActivity.this, PreferenceActivity.class));
			PreferenceActivity.this.finish();
			
		}else if (key.equals(CHKJAVASCRIPT)) {
			
			boolean chkJavascript = prefs.getBoolean(CHKJAVASCRIPT, true);
			sBrowserData.setChkJavascript(chkJavascript);
			sBrowserData.setInvalidate(true);
			
		}else if (key.equals(LSTFLASH)) {
			
			String intLstFlash = prefs.getString(LSTFLASH, "0");
			sBrowserData.setLstflash(Integer.parseInt(intLstFlash));
			
			ListPreference lstFlash = (ListPreference) getPreferenceScreen().findPreference(LSTFLASH);
			lstFlash.setSummary(lstFlashArray[sBrowserData.getLstflash()]);
			
			sBrowserData.setInvalidate(true);
		
		}else if (key.equals(ETXTHOME)) {
			
			String etxtHome = prefs.getString(ETXTHOME, getResources().getString(R.string.pref_home_summary));
			sBrowserData.setetxtHome(etxtHome);
			
			EditTextPreference etxtPrefHome = (EditTextPreference) getPreferenceScreen().findPreference(ETXTHOME);
			etxtPrefHome.setSummary(sBrowserData.getetxtHome());
		}
	}
}
