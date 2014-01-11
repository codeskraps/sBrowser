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

public class BookmarkItem {

	private int id;
	private byte[] image = null;
	private byte[] favIcon = null;
	private String title;
	private String url;
	
	public BookmarkItem(String title, String url) {
		setName(title);
		setUrl(url);
	}

	public int getId()					{ return id; }
	public void setId(int id)			{ this.id = id; }
	
	public String getName() 			{ return title;	}
	public void setName(String name) 	{ this.title = name; }

	public String getUrl() 				{ return url; }
	public void setUrl(String url) 		{ this.url = url; }

	public byte[] getImage() 			{ return image; }
	public void setImage(byte[] image) 	{ this.image = image; }
	
	public byte[] getFavIcon() 			{ return favIcon; }
	public void setFavIcon(byte[] favIcon) 	{ this.favIcon = favIcon; }
}