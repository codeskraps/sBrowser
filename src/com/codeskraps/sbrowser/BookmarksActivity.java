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
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BookmarksActivity extends Activity implements OnItemClickListener, OnClickListener {
	private static final String TAG = "sBrowser";
	private static final int ADD = 1;
	private static final int EDIT = 2;

	private SBrowserData sBrowserData = null;
	private DataBaseData dataBaseData = null;
	private ListBookmarkAdapter listItemAdapter = null;
	private GridView gridview = null;
	private TextView txtIcon = null;
	private ImageView imgIcon = null;

	private Cursor cursor = null;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.lst_bookmarks);

		sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
		dataBaseData = ((SBrowserApplication) getApplication()).getDataBaseData();

		listItemAdapter = new ListBookmarkAdapter(this);

		txtIcon = (TextView) findViewById(R.id.txtIcon);
		imgIcon = (ImageView) findViewById(R.id.imgIcon);

		txtIcon.setOnClickListener(this);
		imgIcon.setOnClickListener(this);

		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(listItemAdapter);
		gridview.setOnItemClickListener(this);

		registerForContextMenu(gridview);

		listItemAdapter.addItem(sBrowserData.getBookmarkItem());

		cursor = dataBaseData.query(DataBaseData.DB_TABLE_BOOKMARK);
		startManagingCursor(cursor);

		final int idColumnIndex = cursor.getColumnIndex(DataBaseData.C_ID);
		final int userColumnIndex = cursor.getColumnIndex(DataBaseData.C_BOOK_NAME);
		final int textColumnIndex = cursor.getColumnIndex(DataBaseData.C_BOOK_URL);
		final int imageColumnIndex = cursor.getColumnIndex(DataBaseData.C_BOOK_IMAGE);

		Log.d(TAG, ("Got cursor with records: " + cursor.getCount()));

		int id;
		String name, url;
		byte[] image;
		while (cursor.moveToNext()) {
			id = cursor.getInt(idColumnIndex);
			name = cursor.getString(userColumnIndex);
			url = cursor.getString(textColumnIndex);
			image = cursor.getBlob(imageColumnIndex);
			BookmarkItem b = new BookmarkItem(name, url);
			b.setId(id);
			if (image != null) b.setImage(image);
			listItemAdapter.addItem(b);
			Log.d(TAG, String.format("\n%s: %s: %s", id, name, url));
			Log.d(TAG, "Image: " + image);
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

		if (id > 0) {
			final BookmarkItem b = (BookmarkItem) listItemAdapter.getItem(id);

			switch (item.getItemId()) {
			case R.id.itemOpen:
				sBrowserData.setSelected(true);
				sBrowserData.setSaveState(b.getUrl());
				finish();
				break;

			case R.id.itemEdit:
				showMyDialog(EDIT, b);
				break;
			case R.id.itemDelete:
				final AlertDialog.Builder alertSearch = new AlertDialog.Builder(this);
				alertSearch.setTitle(getResources().getString(R.string.dialog_title_delete));
				alertSearch.setMessage(getResources().getString(R.string.dialog_message_delete));
				alertSearch.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						dataBaseData.delete(DataBaseData.DB_TABLE_BOOKMARK, b.getId());
						Log.d(TAG, "delete: " + b.getName());

						BookmarksActivity.this.startActivity(new Intent(BookmarksActivity.this,
								BookmarksActivity.class));
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
				break;
			}
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return;
		}
		int id = (int) listItemAdapter.getItemId(info.position);

		if (id > 0) {
			BookmarkItem b = (BookmarkItem) listItemAdapter.getItem(id);
			menu.setHeaderTitle(b.getName());

			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.bookmarkcontextmenu, menu);
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Log.d(TAG, "onListItemClick: " + position);

		if (position == 0) {

			showMyDialog(ADD, sBrowserData.getBookmarkItem());

		} else {
			sBrowserData.setSelected(true);
			BookmarkItem b = (BookmarkItem) listItemAdapter.getItem(position);
			sBrowserData.setSaveState(b.getUrl());
			finish();
		}
	}

	public void showMyDialog(final int thisDialog, final BookmarkItem b) {

		TextView txtName = new TextView(this);
		txtName.setText(getResources().getString(R.string.dialog_name));
		LayoutParams txtLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		txtName.setLayoutParams(txtLayoutParams);

		final EditText edtName = new EditText(this);
		edtName.setText(b.getName());
		LayoutParams edtLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		edtName.setLayoutParams(edtLayoutParams);

		TextView txtUrl = new TextView(this);
		txtUrl.setText(getResources().getString(R.string.dialog_location));
		txtName.setLayoutParams(txtLayoutParams);

		final EditText edtUrl = new EditText(this);
		edtUrl.setText(b.getUrl());
		edtUrl.setLayoutParams(edtLayoutParams);

		LinearLayout dialogLayout = new LinearLayout(this);
		dialogLayout.setOrientation(LinearLayout.VERTICAL);
		dialogLayout.addView(txtName);
		dialogLayout.addView(edtName);
		dialogLayout.addView(txtUrl);
		dialogLayout.addView(edtUrl);

		final AlertDialog.Builder alertSearch = new AlertDialog.Builder(this);
		if (thisDialog == ADD) alertSearch.setTitle(getResources().getString(
				R.string.dialog_title_add));
		else alertSearch.setTitle(getResources().getString(R.string.dialog_title_edit));
		alertSearch.setView(dialogLayout);
		alertSearch.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				BookmarkItem newBookmark = new BookmarkItem(edtName.getText().toString(), edtUrl
						.getText().toString());
				newBookmark.setId(b.getId());
				newBookmark.setImage(b.getImage());

				if (thisDialog == ADD) dataBaseData.insert(DataBaseData.DB_TABLE_BOOKMARK,
						newBookmark);
				else dataBaseData.update(DataBaseData.DB_TABLE_BOOKMARK, newBookmark);

				Log.d(TAG, "saved: " + newBookmark.getName());

				Intent i = new Intent(BookmarksActivity.this, BookmarksActivity.class);
				BookmarksActivity.this.startActivity(i);
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
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	public void onClick(View arg0) {
		this.finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}