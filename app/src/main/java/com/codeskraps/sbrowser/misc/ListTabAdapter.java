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

package com.codeskraps.sbrowser.misc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codeskraps.sbrowser.R;
import com.codeskraps.sbrowser.home.SBrowserApplication;
import com.codeskraps.sbrowser.home.TabsActivity;

public class ListTabAdapter extends BaseAdapter {
	private static final String TAG = "sBrowser";

	private DataBaseData dataBaseData = null;
	private Context context;
	private LayoutInflater mInflater = null;
	private List<BookmarkItem> mItems = new ArrayList<BookmarkItem>();

	public ListTabAdapter(Context context) {
		L.d(TAG, "Constructor");
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

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder = null;

		if (convertView != null) vHolder = (ViewHolder) convertView.getTag();
		else {
			convertView = (View) mInflater.inflate(R.layout.lst_tab_item, null);
			// parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

			vHolder = new ViewHolder();
			vHolder.imgTab = ((ImageView) convertView.findViewById(R.id.lstTabImage));
			vHolder.titleTab = ((TextView) convertView.findViewById(R.id.txtTabTitle));
			vHolder.urlTab = ((TextView) convertView.findViewById(R.id.txtTabURL));
			vHolder.llTabLine = ((LinearLayout) convertView.findViewById(R.id.llTabLine));
			vHolder.llTabRemove = ((LinearLayout) convertView.findViewById(R.id.llTabRemove));
			vHolder.btnTab = ((ImageView) convertView.findViewById(R.id.btnTabRemove));

			vHolder.llTabRemove.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					BookmarkItem b = (BookmarkItem) getItem(position);
					dataBaseData.delete(DataBaseData.DB_TABLE_TABS, b.getId());
					L.d(TAG, "deleted: " + b.getId());
					((TabsActivity) context).updateView();
				}
			});

			convertView.setTag(vHolder);
		}

		vHolder.imgTab.setId(position);
		vHolder.titleTab.setId(position);
		vHolder.urlTab.setId(position);
		vHolder.btnTab.setId(position);

		// vHolder.btnTab.setOnClickListener(this);

		BookmarkItem b = mItems.get(position);
		vHolder.titleTab.setText(b.getName());
		vHolder.urlTab.setText(b.getUrl());
		if (position == 0) {

			vHolder.urlTab.setVisibility(View.GONE);
			vHolder.imgTab.setBackgroundResource(R.drawable.add_tab);
			vHolder.llTabLine.setVisibility(View.GONE);
			vHolder.llTabRemove.setVisibility(View.GONE);
			vHolder.btnTab.setVisibility(View.GONE);

		} else {
			setImage(vHolder, b);
			vHolder.btnTab.setImageResource(R.drawable.remove_tab);
		}

		return convertView;
	}

	private void setImage(ViewHolder vHolder, BookmarkItem b) {
		if (b.getFavIcon() != null) {
			Bitmap bm = BitmapFactory.decodeByteArray(b.getFavIcon(), 0, b.getFavIcon().length);
			if (bm != null) vHolder.imgTab.setImageBitmap(bm);
			else vHolder.imgTab.setImageResource(R.drawable.fav_icon);
			bm.isRecycled();
		} else vHolder.imgTab.setImageResource(R.drawable.fav_icon);
	}

	public static class ViewHolder {
		ImageView imgTab;
		TextView titleTab;
		TextView urlTab;
		LinearLayout llTabLine;
		LinearLayout llTabRemove;
		ImageView btnTab;
	}
}