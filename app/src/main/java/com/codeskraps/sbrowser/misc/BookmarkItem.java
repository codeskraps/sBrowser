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

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("BookmarkItem")
public class BookmarkItem extends ParseObject {
	public static final String ID = "id_data";

	public BookmarkItem() {}

	public BookmarkItem(String title, String url) {
		setName(title);
		setUrl(url);
	}

	public String getUser() {
		return getString(Cons.C_USER);
	}

	public void setUser(String user) {
		put(Cons.C_USER, user);
	}

	public int getId() {
		return getInt(ID);
	}

	public void setId(int id) {
		put(ID, id);
	}

	public String getName() {
		return getString(Cons.C_BOOK_NAME);
	}

	public void setName(String name) {
		put(Cons.C_BOOK_NAME, name);
	}

	public String getUrl() {
		return getString(Cons.C_BOOK_URL);
	}

	public void setUrl(String url) {
		put(Cons.C_BOOK_URL, url);
	}

	public byte[] getImage() {
		return getBytes(Cons.C_BOOK_IMAGE);
	}

	public void setImage(byte[] image) {
		if (image == null) return;
		put(Cons.C_BOOK_IMAGE, image);
	}

	public byte[] getFavIcon() {
		return getBytes(Cons.C_BOOK_IMAGE);
	}

	public void setFavIcon(byte[] favIcon) {
		if (favIcon == null) return;
		put(Cons.C_BOOK_IMAGE, favIcon);
	}

	public static ParseQuery<BookmarkItem> getQuery() {
		return ParseQuery.getQuery(BookmarkItem.class);
	}
}