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
	
	public static final String C_ID = BaseColumns._ID; //Special for id
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
	
	/**
	 * Insert into database
	 * @param values Name/value pairs data
	 */
	public void insert(ContentValues values){
		// Open Database
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		// Insert into database
		db.insertWithOnConflict(DbHelper.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		
		// Close Database
		db.close();
	}
	
	/**
	 * Insert into database
	 * @param status Status data as provided by online service
	 */
	public void insert(BookmarkItem bookmarkItem){
		ContentValues values = new ContentValues();
		//values.put(C_ID, bookmarkItem.getId());
		//values.put(C_BOOK_IMAGE, EntityUtils.toByteArray(bookmarkItem.getImage());
		values.put(C_BOOK_NAME, bookmarkItem.getName());
		values.put(C_BOOK_URL, bookmarkItem.getUrl());
		this.insert(values);
	}
	
	/**
	 * Deletes all data
	 */
	public void delete(){
		// Open Database
		SQLiteDatabase db = dbHelper.getWritableDatabase();
	
		// Delete the data
		db.delete(DbHelper.TABLE, null, null);
		
		// Close Database
		db.close();
	}
	
	public Cursor query(){
		// Open Database
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		// Get the data
		// SELECT * FROM statuses ORDER BY yamba_createdAt DESC;
		return db.query(DbHelper.TABLE, null, null, null, null, null, C_ID + " ASC");
	}
	
	/**
	 * Class to help open/create/upgrade database
	 */
	private class DbHelper extends SQLiteOpenHelper {		
		public static final String DB_NAME = "sBrowserDB.db";
		public static final int DB_VERSION = 1;
		public static final String TABLE = "bookmarks";
		
		public DbHelper() {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = String.format("create table %s (%s INTEGER primary key AUTOINCREMENT, %s text, %s text)", 
					TABLE, C_ID, C_BOOK_NAME, C_BOOK_URL);
		
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
