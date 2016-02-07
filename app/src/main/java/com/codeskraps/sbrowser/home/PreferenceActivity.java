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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.codeskraps.sbrowser.BuildConfig;
import com.codeskraps.sbrowser.R;
import com.codeskraps.sbrowser.misc.L;
import com.codeskraps.sbrowser.misc.SBrowserData;

import java.util.Locale;

public class PreferenceActivity extends android.preference.PreferenceActivity implements
        OnSharedPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = PreferenceActivity.class.getSimpleName();
    private static final String CHKFULLSCREEN = "ckbfullscreen";
    private static final String CHKJAVASCRIPT = "ckbjavascript";
    private static final String LSTFLASH = "lstflash";
    private static final String ETXTHOME = "etxtHome";
    private static final String USERAGENT = "lstUserAgent";
    // private static final String PURCHASE = "prefPurchase";
    // private static final String USER = "prefUser";

    private SBrowserData sBrowserData = null;
    private SharedPreferences prefs = null;

    private String[] lstFlashArray;
    private String[] lstuserAgentArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        L.d(TAG, "Prefs onCreate");

        sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
        lstFlashArray = getResources().getStringArray(R.array.prefs_flash_human_value);
        lstuserAgentArray = getResources().getStringArray(R.array.prefs_user_agent_human_value);

        if (sBrowserData.isChkFullscreen()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setTitle(R.string.preference_activity);
        setContentView(R.layout.preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPreferencesFromResource(R.xml.preferences);

        EditTextPreference etxtPrefHome = (EditTextPreference) getPreferenceScreen()
                .findPreference(ETXTHOME);
        ListPreference lstFlash = (ListPreference) getPreferenceScreen().findPreference(LSTFLASH);
        ListPreference lstUserAgent = (ListPreference) getPreferenceScreen().findPreference(
                USERAGENT);
        // getPreferenceScreen().findPreference(PURCHASE).setOnPreferenceClickListener(this);
        // getPreferenceScreen().findPreference(USER).setOnPreferenceClickListener(this);
        Preference prefVersion = getPreferenceScreen().findPreference("prefs_version");

        etxtPrefHome.setSummary(sBrowserData.getetxtHome());
        lstFlash.setSummary(lstFlashArray[sBrowserData.getLstflash()]);
        lstUserAgent.setSummary(lstuserAgentArray[sBrowserData.getUserAgent()]);
        prefVersion.setTitle(getString(R.string.sBrowserTitle, BuildConfig.VERSION_NAME));

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*-
        if (prefs.getBoolean(Cons.hasPro, false)) {
            getPreferenceScreen().findPreference(PURCHASE).setEnabled(false);
            getPreferenceScreen().findPreference(USER).setEnabled(true);
        } else {
            getPreferenceScreen().findPreference(PURCHASE).setEnabled(true);
            getPreferenceScreen().findPreference(USER).setEnabled(false);
        }
        if (ParseUser.getCurrentUser() != null) {
            ParseUser user = ParseUser.getCurrentUser();
            getPreferenceScreen().findPreference(USER).setSummary(user.getUsername());

        } else getPreferenceScreen().findPreference(USER).setSummary(R.string.sign_up);
        */

        // getPreferenceScreen().findPreference(USER).setEnabled(true);
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
        L.d(TAG, "Prefs Changed");

        if (key.equals(CHKFULLSCREEN)) {
            L.d(TAG, "Prefs fullscreen Changed");

            boolean chkFullscreen = prefs.getBoolean(CHKFULLSCREEN, false);
            sBrowserData.setChkFullscreen(chkFullscreen);
            sBrowserData.setInvalidate(true);

            L.d(TAG, key + ": " + sBrowserData.isChkFullscreen());

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

            L.d(TAG, "prefs user agent: " + sBrowserData.getUserAgent());
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        /*-
        if (preference.getKey().equals(PURCHASE)) {
            startActivity(new Intent(this, PurchaseActivity.class));
            return true;
        } else if (preference.getKey().endsWith(USER)) {
            if (ParseUser.getCurrentUser() != null) {
                startActivity(new Intent(this, PrefsUserActivity.class));
            } else {
                startActivity(new Intent(this, DispatchActivity.class));
            }
            return true;
        }*/
        return false;
    }
}
