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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListBookmarkAdapter extends BaseAdapter {
	private static final String TAG = "sBrowser";

	private Context context;
	private LayoutInflater mInflater = null;
	private List<BookmarkItem> mItems = new ArrayList<BookmarkItem>();

	public ListBookmarkAdapter(Context context) {
		Log.d(TAG, "Constructor");
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	public void addItem(BookmarkItem it) {
		mItems.add(it);
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

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder = null;

		if (convertView != null) vHolder = (ViewHolder) convertView.getTag();
		else {
			convertView = (View) mInflater.inflate(R.layout.lst_bookmark_item, null);

			vHolder = new ViewHolder();
			vHolder.imageView = ((ImageView) convertView.findViewById(R.id.listImage));
			vHolder.textView = ((TextView) convertView.findViewById(R.id.lstText));

			convertView.setTag(vHolder);
		}

		vHolder.imageView.setId(position);
		vHolder.textView.setId(position);

		if (position == 0) {
			vHolder.textView.setText(context.getResources().getString(R.string.addBookmark));
			vHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.add));
		} else {
			BookmarkItem b = mItems.get(position);
			vHolder.textView.setText(b.getName());
			if (b.getImage() != null) {
				Bitmap bm = BitmapFactory.decodeByteArray(b.getImage(), 0, b.getImage().length);
				if (bm != null) vHolder.imageView.setImageBitmap(bm);
				else vHolder.imageView.setImageDrawable(context.getResources().getDrawable(
						R.drawable.bookmark_empty));
				bm.isRecycled();
			} else vHolder.imageView.setImageDrawable(context.getResources().getDrawable(
					R.drawable.bookmark_empty));
		}

		return convertView;
	}

	public static class ViewHolder {
		ImageView imageView;
		TextView textView;
	}
}