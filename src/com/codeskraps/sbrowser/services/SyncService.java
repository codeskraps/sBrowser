package com.codeskraps.sbrowser.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.codeskraps.sbrowser.home.SBrowserApplication;
import com.codeskraps.sbrowser.misc.BookmarkItem;
import com.codeskraps.sbrowser.misc.Cons;
import com.codeskraps.sbrowser.misc.DataBaseData;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class SyncService extends Service {
	private static final String TAG = SyncService.class.getSimpleName();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "Sync started", Toast.LENGTH_SHORT).show();

		DataBaseData dataBaseData = ((SBrowserApplication) getApplication()).getDataBaseData();
		Map<String, BookmarkItem> localB = getLocalBookmarks();
		List<BookmarkItem> parseB = getParseBookmarks();
		int countLocal = 0;

		for (BookmarkItem item : parseB) {
			String url = item.getUrl();
			if (localB.containsKey(url)) {
				localB.remove(url);
			} else {
				dataBaseData.insert(DataBaseData.DB_TABLE_BOOKMARK, item);
			}
		}

		if (localB.size() > 0) {
			try {
				Log.v(TAG, "Saving to parse:" + localB.size());
				List<BookmarkItem> list = new ArrayList<BookmarkItem>(localB.values());
				ParseUser user = ParseUser.getCurrentUser();
				user.add("bookamarks", list);
				user.save();
			} catch (ParseException e) {
				Log.i(TAG, "Unable to save bookmarks to parse", e);
			}
		}

		Toast.makeText(
				this,
				"Sync ended\nSsaved " + countLocal + " on device\nSaved " + localB.size()
						+ " server", Toast.LENGTH_LONG).show();

		return super.onStartCommand(intent, flags, startId);
	}

	public Map<String, BookmarkItem> getLocalBookmarks() {
		Map<String, BookmarkItem> object = new HashMap<String, BookmarkItem>();
		DataBaseData dataBaseData = ((SBrowserApplication) getApplication()).getDataBaseData();
		Cursor cursor = dataBaseData.query(DataBaseData.DB_TABLE_BOOKMARK);
		String userName = ParseUser.getCurrentUser().getUsername();

		final int idColumnIndex = cursor.getColumnIndex(Cons.C_ID);
		final int userColumnIndex = cursor.getColumnIndex(Cons.C_BOOK_NAME);
		final int textColumnIndex = cursor.getColumnIndex(Cons.C_BOOK_URL);
		final int imageColumnIndex = cursor.getColumnIndex(Cons.C_BOOK_IMAGE);

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
			b.setUser(userName);
			object.put(url, b);
			Log.d(TAG, String.format("\n%s: %s: %s", id, name, url));
		}

		cursor.close();

		return object;
	}

	public List<BookmarkItem> getParseBookmarks() {
		ParseQuery<BookmarkItem> bookmarkQuery = BookmarkItem.getQuery();
		bookmarkQuery.whereContains(Cons.C_USER, ParseUser.getCurrentUser().getUsername());
		bookmarkQuery.orderByAscending(BookmarkItem.ID);
		List<BookmarkItem> object = new ArrayList<BookmarkItem>();

		try {
			object = bookmarkQuery.find();
		} catch (ParseException e) {
			Log.i(TAG, "Unable to get Bookmarks", e);
		}

		Log.d(TAG, ("Got parse records: " + object.size()));
		return object;
	}
}
