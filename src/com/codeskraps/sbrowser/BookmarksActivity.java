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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

			TextView txtName = new TextView(this);
		    txtName.setText(getResources().getString(R.string.dialog_name));
		    LayoutParams txtLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		    txtName.setLayoutParams(txtLayoutParams);
		    
		    final EditText edtName = new EditText(this);
		    LayoutParams edtLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		    edtName.setLayoutParams(edtLayoutParams);
		    
		    TextView txtUrl = new TextView(this);
		    txtUrl.setText(getResources().getString(R.string.dialog_location));
		    txtName.setLayoutParams(txtLayoutParams);
		    
		    final EditText edtUrl = new EditText(this);
		    edtUrl.setLayoutParams(edtLayoutParams);
		    
		    LinearLayout dialogLayout = new LinearLayout(this);
		    dialogLayout.setOrientation(LinearLayout.VERTICAL);
		    dialogLayout.addView(txtName);
		    dialogLayout.addView(edtName);
		    dialogLayout.addView(txtUrl);
		    dialogLayout.addView(edtUrl);
			
			final AlertDialog.Builder alertSearch = new AlertDialog.Builder(this);
			alertSearch.setTitle(getResources().getString(R.string.dialog_title));
			alertSearch.setView(dialogLayout);
			alertSearch.setPositiveButton("Ok",	new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
					BookmarkItem newBookmark = new BookmarkItem(edtName.getText().toString(), edtUrl.getText().toString());
					dataBaseData.insert(newBookmark);
					Log.d(TAG, "saved: "
							+ newBookmark.getName());
					
					BookmarksActivity.this.startActivity(new Intent(
							BookmarksActivity.this, BookmarksActivity.class));
					BookmarksActivity.this.finish();
					
					dialog.dismiss();

						}
					});

			alertSearch.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
						}
					});
			alertSearch.show();

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