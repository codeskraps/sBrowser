package com.codeskraps.sbrowser;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TabsActivity extends Activity implements OnClickListener, OnItemClickListener {

	private static final String TAG = "sBrowser";
	private static final int ADD = 1;
	private static final int EDIT = 2;
	
	private SBrowserData sBrowserData = null;
	private DataBaseData dataBaseData = null;
	private ListTabAdapter lstTabAdapter = null;
	private ListView lstTab = null;
	private TextView txtIcon = null;
	private ImageView imgIcon = null;

	private Cursor cursor = null;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.lst_tab);

		sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
		dataBaseData = ((SBrowserApplication) getApplication()).getDataBaseData();

		lstTabAdapter = new ListTabAdapter(this);
		
		Log.d(TAG, "I got here");
		
		lstTab = (ListView) findViewById(R.id.lstTab);
		txtIcon = (TextView) findViewById(R.id.txtTabIcon);
		imgIcon = (ImageView) findViewById(R.id.imgTabIcon);
		
		lstTab.setOnItemClickListener(this);
		txtIcon.setOnClickListener(this);
		imgIcon.setOnClickListener(this);
		
		lstTab.setAdapter(lstTabAdapter);

		cursor = dataBaseData.query(DataBaseData.DB_TABLE_TABS);
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
			lstTabAdapter.addItem(b);
			Log.d(TAG, String.format("\n%s: %s: %s", id, name, url));
			Log.d(TAG, "Image: " + image);
		}

	}
	
	public void onClick(View v) {
		finish();
	}

	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		Log.d(TAG, "itemClick");
		switch(v.getId()){
		case R.id.btnTabRemove:
			Log.d(TAG, "btnTabRemoveClicked");
			BookmarkItem b = (BookmarkItem) lstTabAdapter.getItem(position);
			dataBaseData.delete(DataBaseData.DB_TABLE_TABS, b.getId());
			lstTabAdapter.notifyDataSetChanged();
		}
	}
}
