package com.codeskraps.sbrowser.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.codeskraps.sbrowser.R;
import com.codeskraps.sbrowser.loginsignup.DispatchActivity;
import com.codeskraps.sbrowser.misc.SBrowserData;
import com.codeskraps.sbrowser.services.SyncService;
import com.parse.ParseUser;

public class PrefsUserActivity extends android.preference.PreferenceActivity implements
		OnClickListener, OnPreferenceClickListener {
	private static final String TAG = PrefsUserActivity.class.getSimpleName();
	private static final int REQ_CREATE_PATTERN = 1;
	private static final String SYNC_NOW = "prefSyncNow";
	private static final String SIGN_OUT = "prefSignOut";
	private static final String CHK_LOCK = "prefchkbookmark";
	private static final String CREATE = "prefCreateLock";

	private SBrowserData sBrowserData = null;
	private SharedPreferences prefs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (sBrowserData.isChkFullscreen()) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setTitle(R.string.preference_activity);
		setContentView(R.layout.preference);
		addPreferencesFromResource(R.xml.user_pref);

		findViewById(R.id.txtIcon).setOnClickListener(this);
		findViewById(R.id.imgIcon).setOnClickListener(this);
		getPreferenceScreen().findPreference(SYNC_NOW).setOnPreferenceClickListener(this);
		getPreferenceScreen().findPreference(SIGN_OUT).setOnPreferenceClickListener(this);
		/*-
		getPreferenceScreen().findPreference(CREATE).setOnPreferenceClickListener(this);
		((CheckBoxPreference) getPreferenceScreen().findPreference(CHK_LOCK))
				.setOnPreferenceChangeListener(this);*/
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*-
		boolean chk = prefs.getBoolean(CHK_LOCK, false);
		getPreferenceScreen().findPreference(CREATE).setEnabled(chk);*/
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtIcon:
		case R.id.imgIcon:
			finish();
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(SYNC_NOW)) {
			startService(new Intent(this, SyncService.class));
			return true;

		} else if (preference.getKey().equals(SIGN_OUT)) {
			ParseUser.logOut();
			startActivity(new Intent(this, DispatchActivity.class));
			finish();
			return true;

		} else if (preference.getKey().equals(CREATE)) {
			/*-
			Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null, this,
					LockPatternActivity.class);
			startActivityForResult(intent, REQ_CREATE_PATTERN);
			return true;*/
		}
		return false;
	}

	/*-
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(CHK_LOCK)) {
			boolean chk = (Boolean) newValue;
			getPreferenceScreen().findPreference(CREATE).setEnabled(chk);
			return true;
		}
		return false;
	}*/

	/*-
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_CREATE_PATTERN: {
			if (resultCode == RESULT_OK) {
				char[] pattern = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
				Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
			}
			break;
		}// REQ_CREATE_PATTERN
		}
	}*/
}
