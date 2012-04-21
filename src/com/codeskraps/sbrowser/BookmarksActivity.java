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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class BookmarksActivity extends Activity implements OnItemClickListener {
	private static final String TAG = BookmarksActivity.class.getSimpleName();
	private SBrowserData sBrowserData = null;
	private DataBaseData dataBaseData = null;
	private ListItemAdapter listItemAdapter = null;
	private GridView gridview = null;

	private Cursor cursor = null;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.list_activity);

		sBrowserData = ((SBrowserApplication) getApplication())
				.getsBrowserData();
		dataBaseData = ((SBrowserApplication) getApplication())
				.getDataBaseData();

		/*
		 * Set and fill the Adapter
		 */
		listItemAdapter = new ListItemAdapter(this);
		// setListAdapter(listItemAdapter);

		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(listItemAdapter);
		gridview.setOnItemClickListener(this);

		registerForContextMenu(gridview);

		listItemAdapter.addItem(sBrowserData.getBookmarkItem());

		// Get the data
		cursor = dataBaseData.query();
		startManagingCursor(cursor);
		final int userColumnIndex = cursor
				.getColumnIndex(DataBaseData.C_BOOK_NAME);
		final int textColumnIndex = cursor
				.getColumnIndex(DataBaseData.C_BOOK_URL);

		Log.d(TAG, ("Got cursor with records: " + cursor.getCount()));

		// Output it
		String name, url;
		while (cursor.moveToNext()) {
			name = cursor.getString(userColumnIndex);
			url = cursor.getString(textColumnIndex);
			BookmarkItem b = new BookmarkItem(name, url);
			listItemAdapter.addItem(b);
			Log.d(TAG, String.format("\n%s: %s", name, url));
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info;

		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return false;
		}
		int id = (int) listItemAdapter.getItemId(info.position);

		switch (item.getItemId()) {
		case R.id.itemOpen:
			sBrowserData.setSelected(true);
			BookmarkItem b = (BookmarkItem) listItemAdapter.getItem(id);
			sBrowserData.setSaveState(b.getUrl());
			finish();
			break;
		// case R.id.itemEdit:
		// break;
		// case R.id.itemDelete:
		// break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return;
		}
		int id = (int) listItemAdapter.getItemId(info.position);

		BookmarkItem b = (BookmarkItem) listItemAdapter.getItem(id);
		menu.setHeaderTitle(b.getName());

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bookmarkcontextmenu, menu);
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Log.d(TAG, "onListItemClick: " + position);

		if (position == 0) {

			final Dialog dialog = new Dialog(this);

			dialog.setContentView(R.layout.bookmark_dialog);
			dialog.setTitle(getResources().getString(R.string.dialog_title));

			final EditText dtxtName = (EditText) dialog.findViewById(R.id.dtxttitle);
			dtxtName.setText(sBrowserData.getBookmarkItem().getName());

			final EditText dtxtURL = (EditText) dialog.findViewById(R.id.dtxturl);
			dtxtURL.setText(sBrowserData.getBookmarkItem().getUrl());

			Button btnok = (Button) dialog.findViewById(R.id.btnok);
			btnok.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					Toast.makeText(BookmarksActivity.this, "clicked",
							Toast.LENGTH_SHORT);
					Log.d(TAG, "ok clicked");

					BookmarkItem newBookmark = new BookmarkItem(dtxtName.getText().toString(), dtxtURL.getText().toString());
					dataBaseData.insert(newBookmark);
					Log.d(TAG, "saved: "
							+ newBookmark.getName());
					
					BookmarksActivity.this.startActivity(new Intent(
							BookmarksActivity.this, BookmarksActivity.class));
					BookmarksActivity.this.finish();
					
					dialog.dismiss();
				}
			});

			Button btnCancel = (Button) dialog.findViewById(R.id.btncancel);
			btnCancel.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					Toast.makeText(BookmarksActivity.this, "clicked",
							Toast.LENGTH_SHORT);
					Log.d(TAG, "Cancel clicked");
					dialog.dismiss();
				}
			});

			dialog.show();

		} else {
			sBrowserData.setSelected(true);
			BookmarkItem b = (BookmarkItem) listItemAdapter.getItem(position);
			sBrowserData.setSaveState(b.getUrl());
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}