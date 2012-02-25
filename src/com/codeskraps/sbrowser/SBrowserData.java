package com.codeskraps.sbrowser;

import android.content.Context;
import android.webkit.WebView.HitTestResult;

public class SBrowserData {
	private Context context = null;
	private boolean invalidate;
	private boolean selected;
	private boolean chkFullscreen;
	private boolean chkLandscape;
	private boolean chkJavascript;
	private int lstflash;
	private String etxtHome;
	private String saveState;
	
	private HitTestResult result;
	private BookmarkItem bookmarkItem;
	
	public SBrowserData(Context context) {
		setContext(context);
		setInvalidate(false);
		setChkFullscreen(false);
		setSelected(false);
		setChkLandscape(false);
		setChkJavascript(true);
		setLstflash(0);
		setetxtHome(new String());
		setSaveState(new String());
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public boolean isInvalidate() {
		return invalidate;
	}

	public void setInvalidate(boolean invalidate) {
		this.invalidate = invalidate;
	}

	public boolean isChkFullscreen() {
		return chkFullscreen;
	}
	
	public void setChkFullscreen(boolean chkFullscreen) {
		this.chkFullscreen = chkFullscreen;
	}
	
	public boolean isChkLandscape() {
		return chkLandscape;
	}

	public void setChkLandscape(boolean chkLandscape) {
		this.chkLandscape = chkLandscape;
	}

	public boolean isChkJavascript() {
		return chkJavascript;
	}

	public void setChkJavascript(boolean chkJavascript) {
		this.chkJavascript = chkJavascript;
	}

	public int getLstflash() {
		return lstflash;
	}

	public void setLstflash(int i) {
		this.lstflash = i;
	}

	public String getetxtHome () {
		return etxtHome;
	}
	
	public void setetxtHome(String etxtHome) {
		this.etxtHome = etxtHome;
	}

	public String getSaveState() {
		return saveState;
	}

	public void setSaveState(String saveState) {
		this.saveState = saveState;
	}

	public HitTestResult getResult() {
		return result;
	}

	public void setResult(HitTestResult result) {
		this.result = result;
	}

	public BookmarkItem getBookmarkItem() {
		return bookmarkItem;
	}

	public void setBookmarkItem(BookmarkItem bookmarkItem) {
		this.bookmarkItem = bookmarkItem;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
