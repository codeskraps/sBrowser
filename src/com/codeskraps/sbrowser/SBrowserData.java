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

import android.webkit.WebView.HitTestResult;

public class SBrowserData {
	private boolean invalidate;
	private boolean selected;
	private boolean chkFullscreen;
	private boolean chkLandscape;
	private boolean chkJavascript;
	private boolean tabbed;
	private int lstflash;
	private String etxtHome;
	private String saveState;
	private int userAgent;
	
	private HitTestResult result;
	private BookmarkItem bookmarkItem;
	
	public SBrowserData() {
		setInvalidate(false);
		setChkFullscreen(false);
		setSelected(false);
		setChkLandscape(false);
		setChkJavascript(true);
		setTabbed(false);
		setLstflash(0);
		setetxtHome(new String());
		setSaveState(new String());
		setUserAgent(0);
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

	public int getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(int userAgent) {
		this.userAgent = userAgent;
	}

	public boolean isTabbed() {
		return tabbed;
	}

	public void setTabbed(boolean tabbed) {
		this.tabbed = tabbed;
	}
}
