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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemAdapter extends BaseAdapter {
	private static final String TAG = ListItemAdapter.class.getSimpleName();

	private Context context;
	private BookmarkItem bookmarkItem = null;
	private LayoutInflater mInflater = null;
	private List<BookmarkItem> mItems = new ArrayList<BookmarkItem>(); 

	public ListItemAdapter(Context context, BookmarkItem bookmarkItem) {
		Log.d(TAG, "Constructor");
		this.context = context;
		this.bookmarkItem = bookmarkItem;
		mInflater = LayoutInflater.from(context);
	}

	public void addItem(BookmarkItem it) {
		mItems.add(it); // Adding Items to the list
	}

	public void setListItems(List<BookmarkItem> lit) { 
		mItems = lit;
	}

	public int getCount() {
		return mItems.size();
	}

	public Object getItem(int position) {
		return mItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/*
	 * This gets called every time ListView needs a new Row Item position holds
	 * the position on the row in the ListView convertView is the new view we
	 * have to filled with our custom --> list_item.xml
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder = null;

		if (convertView != null)
			vHolder = (ViewHolder) convertView.getTag(); // convertView is been
															// recycled
		else {
			convertView = (View) mInflater.inflate(R.layout.list_item, null); 

			vHolder = new ViewHolder();
			vHolder.imageView = ((ImageView) convertView
					.findViewById(R.id.listImage));
			vHolder.textView = ((TextView) convertView
					.findViewById(R.id.lstText));

			convertView.setTag(vHolder);
		}

		vHolder.imageView.setId(position);
		vHolder.textView.setId(position); // Do not delete !!!

		if (position == 0) {
			vHolder.textView.setText(context.getResources().getString(R.string.addBookmark));
			vHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.add));
		} else {
			vHolder.textView.setText(mItems.get(position).getName());
			//vHolder.imageView.setImageBitmap(mItems.get(position).getImage());
			vHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_empty));
		}
		
		return convertView;
	}

	public static class ViewHolder {
		ImageView imageView;
		TextView textView;
	}
}