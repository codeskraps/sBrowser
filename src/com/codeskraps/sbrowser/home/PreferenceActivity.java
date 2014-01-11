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

package com.codeskraps.sbrowser.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeskraps.sbrowser.R;
import com.codeskraps.sbrowser.misc.SBrowserData;

public class PreferenceActivity extends android.preference.PreferenceActivity implements
		OnSharedPreferenceChangeListener, OnClickListener, OnPreferenceClickListener {
	private static final String TAG = PreferenceActivity.class.getSimpleName();
	private static final String CHKFULLSCREEN = "ckbfullscreen";
	private static final String CHKJAVASCRIPT = "ckbjavascript";
	private static final String LSTFLASH = "lstflash";
	private static final String ETXTHOME = "etxtHome";
	private static final String USERAGENT = "lstUserAgent";
	private static final String USER = "prefUser";

	private SBrowserData sBrowserData = null;
	private SharedPreferences prefs = null;

	private TextView txtIcon = null;
	private ImageView imgIcon = null;

	private String[] lstFlashArray;
	private String[] lstuserAgentArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "Prefs onCreate");

		sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
		lstFlashArray = getResources().getStringArray(R.array.prefs_flash_human_value);
		lstuserAgentArray = getResources().getStringArray(R.array.prefs_user_agent_human_value);

		if (sBrowserData.isChkFullscreen()) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setTitle(R.string.preference_activity);
		setContentView(R.layout.preference);
		addPreferencesFromResource(R.xml.preferences);

		txtIcon = (TextView) findViewById(R.id.txtIcon);
		imgIcon = (ImageView) findViewById(R.id.imgIcon);

		txtIcon.setOnClickListener(this);
		imgIcon.setOnClickListener(this);

		EditTextPreference etxtPrefHome = (EditTextPreference) getPreferenceScreen()
				.findPreference(ETXTHOME);
		ListPreference lstFlash = (ListPreference) getPreferenceScreen().findPreference(LSTFLASH);
		ListPreference lstUserAgent = (ListPreference) getPreferenceScreen().findPreference(
				USERAGENT);
		getPreferenceScreen().findPreference(USER).setOnPreferenceClickListener(this);

		etxtPrefHome.setSummary(sBrowserData.getetxtHome());
		lstFlash.setSummary(lstFlashArray[sBrowserData.getLstflash()]);
		lstUserAgent.setSummary(lstuserAgentArray[sBrowserData.getUserAgent()]);

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

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(TAG, "Prefs Changed");

		if (key.equals(CHKFULLSCREEN)) {
			Log.d(TAG, "Prefs fullscreen Changed");

			boolean chkFullscreen = prefs.getBoolean(CHKFULLSCREEN, false);
			sBrowserData.setChkFullscreen(chkFullscreen);
			sBrowserData.setInvalidate(true);

			Log.d(TAG, key + ": " + sBrowserData.isChkFullscreen());

			PreferenceActivity.this.startActivity(new Intent(PreferenceActivity.this,
					PreferenceActivity.class));
			PreferenceActivity.this.finish();

		} else if (key.equals(CHKJAVASCRIPT)) {

			boolean chkJavascript = prefs.getBoolean(CHKJAVASCRIPT, true);
			sBrowserData.setChkJavascript(chkJavascript);
			sBrowserData.setInvalidate(true);

		} else if (key.equals(LSTFLASH)) {

			String intLstFlash = prefs.getString(LSTFLASH, "0");
			sBrowserData.setLstflash(Integer.parseInt(intLstFlash));

			ListPreference lstFlash = (ListPreference) getPreferenceScreen().findPreference(
					LSTFLASH);
			lstFlash.setSummary(lstFlashArray[sBrowserData.getLstflash()]);

			sBrowserData.setInvalidate(true);

		} else if (key.equals(ETXTHOME)) {

			String etxtHome = prefs.getString(ETXTHOME,
					getResources().getString(R.string.pref_home_summary));
			if (etxtHome.startsWith("http")) sBrowserData.setetxtHome(etxtHome);
			else sBrowserData.setetxtHome("http://" + etxtHome);

			EditTextPreference etxtPrefHome = (EditTextPreference) getPreferenceScreen()
					.findPreference(ETXTHOME);
			etxtPrefHome.setSummary(sBrowserData.getetxtHome());

		} else if (key.equals(USERAGENT)) {

			String sUserAgent = prefs.getString(USERAGENT, "0");
			sBrowserData.setUserAgent(Integer.parseInt(sUserAgent));

			ListPreference lstUserAgent = (ListPreference) getPreferenceScreen().findPreference(
					USERAGENT);
			lstUserAgent.setSummary(lstuserAgentArray[sBrowserData.getUserAgent()]);

			Log.d(TAG, "prefs user agent: " + sBrowserData.getUserAgent());
		}
	}

	public void onClick(View arg0) {
		this.finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(USER)) {
			startActivity(new Intent(this, UserActivity.class));
			return true;
		}
		return false;
	}
}
