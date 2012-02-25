package com.codeskraps.sbrowser;

import android.graphics.Bitmap;


public class BookmarkItem {

	private Bitmap image;
	private String title;
	private String url;
	
	public BookmarkItem(String title, String url) {
		setImage(image);
		setName(title);
		setUrl(url);
	}

	public String getName() {
		return title;
	}

	public void setName(String name) {
		this.title = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}
}
