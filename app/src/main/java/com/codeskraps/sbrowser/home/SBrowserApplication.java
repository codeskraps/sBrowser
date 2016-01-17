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

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.codeskraps.sbrowser.R;
import com.codeskraps.sbrowser.misc.BookmarkItem;
import com.codeskraps.sbrowser.misc.Cons;
import com.codeskraps.sbrowser.misc.DataBaseData;
import com.codeskraps.sbrowser.misc.L;
import com.codeskraps.sbrowser.misc.SBrowserData;
import com.codeskraps.sbrowser.services.ProAccoundVerificationService;
import com.parse.Parse;
import com.parse.ParseObject;

public class SBrowserApplication extends Application {
    private static final String TAG = SBrowserApplication.class.getSimpleName();
    private static final String CHKFULLSCREEN = "ckbfullscreen";
    private static final String CHKJAVASCRIPT = "ckbjavascript";
    private static final String LSTFLASH = "lstflash";
    private static final String ETXTHOME = "etxtHome";
    private static final String USERAGENT = "lstUserAgent";

    private SBrowserData sBrowserData = null;
    private DataBaseData dataBaseData = null;

    @Override
    public void onCreate() {
        super.onCreate();

        L.d(TAG, "onCreate started");

        ParseObject.registerSubclass(BookmarkItem.class);
        Parse.initialize(this, Cons.APLICATION_ID, Cons.CLIENT_KEY);

        setsBrowserData(new SBrowserData());
        setDataBaseData(new DataBaseData(this));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean chkFullscreen = prefs.getBoolean(CHKFULLSCREEN, false);
        boolean chkJavascript = prefs.getBoolean(CHKJAVASCRIPT, true);
        String lstFlash = prefs.getString(LSTFLASH, "0");
        String etxtHome = prefs.getString(ETXTHOME,
                getResources().getString(R.string.pref_home_summary));
        String lstUserAgent = prefs.getString(USERAGENT, "0");

        sBrowserData.setChkFullscreen(chkFullscreen);
        sBrowserData.setChkJavascript(chkJavascript);
        sBrowserData.setLstflash(Integer.parseInt(lstFlash));
        sBrowserData.setetxtHome(etxtHome);
        sBrowserData.setUserAgent(Integer.parseInt(lstUserAgent));

        startService(new Intent(this, ProAccoundVerificationService.class));
    }

    public Intent getMenuIntent(MenuItem item, Context context) {

        switch (item.getItemId()) {
            // case R.id.itemTabs: return new Intent(context, TabsActivity.class);
            case R.id.itemBookmarks:
                return new Intent(context, BookmarksActivity.class);
            case R.id.itemDownloads:
                Intent i = new Intent();
                i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
                return i;
            case R.id.itemPreference:
                return new Intent(context, PreferenceActivity.class);
        }
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
