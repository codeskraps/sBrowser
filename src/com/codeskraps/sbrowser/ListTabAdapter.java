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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ListTabAdapter extends BaseAdapter {
	private static final String TAG = "sBrowser";

	private DataBaseData dataBaseData = null;
	private Context context;
	private LayoutInflater mInflater = null;
	private List<BookmarkItem> mItems = new ArrayList<BookmarkItem>(); 

	public ListTabAdapter(Context context) {
		Log.d(TAG, "Constructor");
		this.context = context;
		dataBaseData = ((SBrowserApplication) context.getApplicationContext()).getDataBaseData();
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

		if (convertView != null)
			vHolder = (ViewHolder) convertView.getTag();
		else {
			convertView = (View) mInflater.inflate(R.layout.lst_tab_item, null); 

			vHolder = new ViewHolder();
			vHolder.imgTab = ((ImageView) convertView.findViewById(R.id.lstTabImage));
			vHolder.titleTab = ((TextView) convertView.findViewById(R.id.txtTabTitle));
			vHolder.urlTab = ((TextView) convertView.findViewById(R.id.txtTabURL));
			vHolder.btnTab = ((ImageButton) convertView.findViewById(R.id.btnTabRemove));

			convertView.setTag(vHolder);
		}

		vHolder.imgTab.setId(position);
		vHolder.titleTab.setId(position);
		vHolder.urlTab.setId(position);
		vHolder.btnTab.setId(position);
		
		//vHolder.btnTab.setOnClickListener(this);

		BookmarkItem b = mItems.get(position);
		vHolder.titleTab.setText(b.getName());
		vHolder.urlTab.setText(b.getUrl());
		if (position == 0) vHolder.imgTab.setBackgroundResource(R.drawable.add);
		else vHolder.imgTab.setBackgroundResource(R.drawable.default_favicon);
//		if (b.getImage() != null){
//			Bitmap bm = BitmapFactory.decodeByteArray(b.getImage(), 0, b.getImage().length);
//			if (bm != null) vHolder.imgTab.setImageBitmap(bm);
//			else vHolder.imgTab.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_empty));
//			bm.isRecycled();
//		}else vHolder.imgTab.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_empty));
		
		return convertView;
	}

	public static class ViewHolder {
		ImageView imgTab;
		TextView titleTab;
		TextView urlTab;
		ImageButton btnTab;
	}
}