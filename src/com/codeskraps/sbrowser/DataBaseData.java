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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DataBaseData {
	private static final String TAG = DataBaseData.class.getSimpleName();
	
	public static final String C_ID = BaseColumns._ID;
	public static final String C_BOOK_IMAGE = "sbrowser_image";
	public static final String C_BOOK_NAME = "sbrowser_name";
	public static final String C_BOOK_URL = "sbrowser_url";
	
	private Context context;
	private DbHelper dbHelper;
	
	public DataBaseData(Context context){
		this.context = context;
		dbHelper = new DbHelper();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public void insert(BookmarkItem bookmarkItem){
		ContentValues values = new ContentValues();
		values.put(C_BOOK_NAME, bookmarkItem.getName());
		values.put(C_BOOK_URL, bookmarkItem.getUrl());
		values.put(C_BOOK_IMAGE, bookmarkItem.getImage());
		
		long r;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		r = db.insertWithOnConflict(DbHelper.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
		Log.d(TAG, "inserting: " + r);
	}
		
	public void update(BookmarkItem bookmarkItem){
		
		ContentValues values = new ContentValues();
		values.put(C_ID, bookmarkItem.getId());
		values.put(C_BOOK_NAME, bookmarkItem.getName());
		values.put(C_BOOK_URL, bookmarkItem.getUrl());
		values.put(C_BOOK_IMAGE, bookmarkItem.getImage());
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String whereClause = new String(C_ID + " = ?");
		String whereArgs = new String(Integer.toString(bookmarkItem.getId()));
		db.updateWithOnConflict(DbHelper.TABLE, values, whereClause, new String[] { whereArgs },
				SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
	}
	
	public void delete(int id){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String whereClause = new String(C_ID + " = ?");
		String whereArgs = new String(Integer.toString(id));
		db.delete(DbHelper.TABLE, whereClause, new String[] { whereArgs });
		db.close();
	}
	
	public Cursor query(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		return db.query(DbHelper.TABLE, null, null, null, null, null, C_ID + " ASC");
	}
	
	private class DbHelper extends SQLiteOpenHelper {		
		public static final String DB_NAME = "sBrowserDB.db";
		public static final int DB_VERSION = 1;
		public static final String TABLE = "bookmarks";
		
		public DbHelper() {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = String.format("create table %s (%s INTEGER primary key AUTOINCREMENT, %s text, %s text, %s BLOB)", 
					TABLE, C_ID, C_BOOK_NAME, C_BOOK_URL, C_BOOK_IMAGE);
		
			Log.d(TAG, "onCreate sql: " + sql);
			
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists " + TABLE);
			Log.d(TAG, "onUpdate dropped table " + TABLE);
			this.onCreate(db);
		}
	}
}
