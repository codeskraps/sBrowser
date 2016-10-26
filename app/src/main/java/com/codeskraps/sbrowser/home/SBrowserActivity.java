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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.text.InputType;
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

import com.codeskraps.sbrowser.R;
import com.codeskraps.sbrowser.misc.BookmarkItem;
import com.codeskraps.sbrowser.misc.L;
import com.codeskraps.sbrowser.misc.SBrowserData;

import java.io.ByteArrayOutputStream;

@SuppressWarnings("deprecation")
public class SBrowserActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = SBrowserActivity.class.getSimpleName();

    private SBrowserData sBrowserData = null;
    // private DataBaseData dataBaseData = null;

    private WebViewFragment wF = null;
    private WebView webView = null;
    private View horizontalBar;
    private View verticalBar;
    private View menuView;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
        // dataBaseData = ((SBrowserApplication) getApplication()).getDataBaseData();

        if (sBrowserData.isChkFullscreen()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.sbrowser);

        horizontalBar = findViewById(R.id.horizontalbar_land);
        verticalBar = findViewById(R.id.horizontalbar_vert);

        findViewById(R.id.btnWww).setOnClickListener(this);
        findViewById(R.id.btnHome).setOnClickListener(this);
        findViewById(R.id.btnRight).setOnClickListener(this);
        findViewById(R.id.btnRefresh).setOnClickListener(this);
        findViewById(R.id.btnSearch).setOnClickListener(this);
        findViewById(R.id.btnMenu).setOnClickListener(this);

        findViewById(R.id.btnWww_land).setOnClickListener(this);
        findViewById(R.id.btnHome_land).setOnClickListener(this);
        findViewById(R.id.btnRight_land).setOnClickListener(this);
        findViewById(R.id.btnRefresh_land).setOnClickListener(this);
        findViewById(R.id.btnSearch_land).setOnClickListener(this);
        findViewById(R.id.btnMenu_land).setOnClickListener(this);

        menuView = findViewById(R.id.ll_menu);
        menuView.setOnClickListener(this);
        menuView.bringToFront();

        findViewById(R.id.txt_bookmarks).setOnClickListener(this);
        findViewById(R.id.txt_prefs).setOnClickListener(this);

		/*-
        Resources res = getResources();
		if (res.getBoolean(R.bool.isTablet)) {
			findViewById(R.id.btnMenu).setVisibility(View.VISIBLE);
			findViewById(R.id.btnSearch).setVisibility(View.VISIBLE);

		} else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1
				|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && ViewConfiguration
						.get(this).hasPermanentMenuKey())) {
			findViewById(R.id.btnMenu).setVisibility(View.GONE);
			findViewById(R.id.btnSearch).setVisibility(View.VISIBLE);

		} else {
			findViewById(R.id.btnMenu).setVisibility(View.VISIBLE);
			findViewById(R.id.btnSearch).setVisibility(View.GONE);
		}*/

        // findViewById(R.id.btnMenu).setVisibility(View.GONE);
        // findViewById(R.id.btnMenu_land).setVisibility(View.GONE);
        // findViewById(R.id.btnSearch).setVisibility(View.VISIBLE);

        FrameLayout fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        if (fragmentContainer != null) {
            L.v(TAG, "bundle:" + savedInstanceState);
            if (savedInstanceState != null) {
                return;
            }
            wF = WebViewFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, wF)
                    .commit();
        }
    }

    public void setWebView(WebViewFragment wF, WebView webView) {
        this.wF = wF;
        this.webView = webView;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TAG, webView.getUrl());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (menuView.getVisibility() == View.VISIBLE) {
                menuView.setVisibility(View.GONE);
                return true;
            }
            if (webView.canGoBack()) webView.goBack();
            else {
                if (webView != null) {
                    webView.clearCache(true);
                    webView.clearHistory();
                }

                sBrowserData.setSelected(false);
                sBrowserData.setSaveState(sBrowserData.getetxtHome());

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
        L.v(TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        L.v(TAG, "onCreateContextMenu");
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v instanceof WebView) {
            WebView.HitTestResult result = ((WebView) v).getHitTestResult();

            if (result.getType() == HitTestResult.IMAGE_TYPE
                    || result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                L.d(TAG, "onCreateContextMenu - SRC_IMAGE_ANCHOR_TYPE");
                menu.setHeaderTitle(result.getExtra());

                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.contextmenuimage, menu);

            } else if (result.getType() == HitTestResult.ANCHOR_TYPE
                    || result.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
                L.d(TAG, "onCreateContextMenu - SRC_ANCHOR_TYPE");
                menu.setHeaderTitle(result.getExtra());

                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.contextmenulink, menu);

            } else if (result.getType() == HitTestResult.UNKNOWN_TYPE) {
                L.d(TAG, "onCreateContextMenu - Unknown_type");
            }
        } else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*-
        if (item.getItemId() == R.id.itemQuit) {

            try {
                dataBaseData.deleteTable(DataBaseData.DB_TABLE_TABS);
            } catch (Exception e) {
                L.e(TAG, "deleteTable: " + e.getMessage());
            }
            this.finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        } else*/
        /*-
        if (item.getItemId() == R.id.itemFeedback) {

            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

            String aEmailList[] = {"codeskraps@gmail.com"};

            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "sBrowser - Feedback");
            emailIntent.setType("plain/text");

            startActivity(Intent.createChooser(emailIntent, "Send your feedback in:"));*/

			/*-
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
			 */
        // } else {

        saveBookmarkItem();

        SBrowserApplication sBrwoserApp = (SBrowserApplication) getApplication();
        SBrowserActivity.this.startActivity(sBrwoserApp.getMenuIntent(item,
                SBrowserActivity.this));
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        // }

        return super.onOptionsItemSelected(item);
    }

    private void saveBookmarkItem(){
        try {
            Picture picture = webView.capturePicture();
            PictureDrawable pictureDrawable = new PictureDrawable(picture);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawPicture(pictureDrawable.getPicture());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bos);
            bitmap.isRecycled();

            BookmarkItem bookmarkItem = new BookmarkItem(webView.getTitle(), webView.getUrl());
            bookmarkItem.setImage(bos.toByteArray());
            sBrowserData.setBookmarkItem(bookmarkItem);
            bos.close();

        } catch (Exception e) {
            L.e(TAG, "Picture:" + e.getMessage());
            BookmarkItem bookmarkItem = new BookmarkItem("Set title", "Set url");
            bookmarkItem.setImage(null);
            sBrowserData.setBookmarkItem(bookmarkItem);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        L.v(TAG, "onContextItemSelected");

        WebView.HitTestResult result = webView.getHitTestResult();
        L.d(TAG, "result: " + result.getExtra());
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
                    L.d(TAG, "Erro Downloading: " + e);
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
                    L.d(TAG, "Erro Sharing link: " + e, e);
                }

                break;

            case R.id.itemShareImage:

                try {
                    sharingIntent.setType("image/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, result.getExtra());
                    startActivity(Intent.createChooser(sharingIntent, "Share image using..."));
                } catch (Exception e) {
                    L.d(TAG, "Erro Sharing Image: " + e, e);
                }

                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        L.v(TAG, "onBackPressed v:" + menuView.getVisibility());
        if (menuView.getVisibility() == View.VISIBLE) {
            menuView.setVisibility(View.GONE);

        } else super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView(getResources().getConfiguration().orientation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (menuView.getVisibility() == View.VISIBLE) {
            menuView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateView(newConfig.orientation);
    }

    private void updateView(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            verticalBar.setVisibility(View.GONE);
            horizontalBar.setVisibility(View.VISIBLE);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            verticalBar.setVisibility(View.VISIBLE);
            horizontalBar.setVisibility(View.GONE);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWww:
            case R.id.btnWww_land:
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
            case R.id.btnHome_land:
                webView.loadUrl(sBrowserData.getetxtHome());
                break;

            case R.id.btnRight:
            case R.id.btnRight_land:
                webView.goForward();
                break;

            case R.id.btnRefresh:
            case R.id.btnRefresh_land:
                if (wF != null && !wF.isReloading()) {
                    wF.reload();
                } else {
                    webView.stopLoading();
                }
                break;

            case R.id.btnSearch:
            case R.id.btnSearch_land:
                doSearch();
                break;

            case R.id.btnMenu:
            case R.id.btnMenu_land:
                L.v(TAG, "Menu pressed");
                menuView.setVisibility(View.VISIBLE);
                break;

            case R.id.ll_menu:
                menuView.setVisibility(View.GONE);
                break;

            case R.id.txt_bookmarks:
                saveBookmarkItem();
                Intent iBookmark = new Intent(this, BookmarksActivity.class);
                startActivity(iBookmark);
                menuView.setVisibility(View.GONE);
                break;

            case R.id.txt_prefs:
                Intent iPrefs = new Intent(this, PreferenceActivity.class);
                startActivity(iPrefs);
                menuView.setVisibility(View.GONE);
                break;
        }
    }

    public void setStopButton() {
        ((ImageView) findViewById(R.id.btnRefresh)).setImageResource(R.drawable.webview_stop);
        ((ImageView) findViewById(R.id.btnRefresh_land)).setImageResource(R.drawable.webview_stop);
    }

    public void setBackForwardButtons() {
        ((ImageView) findViewById(R.id.btnRefresh)).setImageResource(R.drawable.webview_refresh);
        ((ImageView) findViewById(R.id.btnRefresh_land)).setImageResource(R.drawable.webview_refresh);

        if (webView.canGoForward()) {
            ((ImageView) findViewById(R.id.btnRight)).setImageResource(R.drawable.webview_right);
        } else {
            ((ImageView) findViewById(R.id.btnRight)).setImageResource(R.drawable.webview_right_bw);
        }
    }

    private void doSearch() {
        final AlertDialog.Builder alertSearch = new AlertDialog.Builder(this);
        alertSearch.setTitle(getResources().getString(R.string.alertSearchTitle));
        alertSearch.setMessage(getResources().getString(R.string.alertSearchSummary));
        final EditText inputSearch = new EditText(this);
        inputSearch.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                | InputType.TYPE_TEXT_VARIATION_URI);
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
}