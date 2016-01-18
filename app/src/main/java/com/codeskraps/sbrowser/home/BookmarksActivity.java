/**
 * sBrowser
 * Copyright (C) Carles Sentis 2011 <codeskraps@gmail.com>
 * <p/>
 * sBrowser is free software: you can
 * redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later
 * version.
 * <p/>
 * sBrowser is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU
 * General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.codeskraps.sbrowser.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codeskraps.sbrowser.R;
import com.codeskraps.sbrowser.misc.BookmarkItem;
import com.codeskraps.sbrowser.misc.Cons;
import com.codeskraps.sbrowser.misc.DataBaseData;
import com.codeskraps.sbrowser.misc.L;
import com.codeskraps.sbrowser.misc.ListBookmarkAdapter;
import com.codeskraps.sbrowser.misc.SBrowserData;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity implements OnItemClickListener {
    private static final String TAG = BookmarksActivity.class.getSimpleName();
    private static final int ADD = 1;
    private static final int EDIT = 2;

    private SBrowserData sBrowserData = null;
    private DataBaseData dataBaseData = null;
    private ListBookmarkAdapter listItemAdapter = null;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.lst_bookmarks);

        Toolbar toolbar = (Toolbar) findViewById(
                R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
        dataBaseData = ((SBrowserApplication) getApplication()).getDataBaseData();

        listItemAdapter = new ListBookmarkAdapter(this);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(listItemAdapter);
        gridview.setOnItemClickListener(this);

        registerForContextMenu(gridview);

        new GetBookmarks().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetBookmarks extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            listItemAdapter.addItem(sBrowserData.getBookmarkItem());

            Cursor cursor = dataBaseData.query(DataBaseData.DB_TABLE_BOOKMARK);
            startManagingCursor(cursor);

            final int idColumnIndex = cursor.getColumnIndex(Cons.C_ID);
            final int userColumnIndex = cursor.getColumnIndex(Cons.C_BOOK_NAME);
            final int textColumnIndex = cursor.getColumnIndex(Cons.C_BOOK_URL);
            final int imageColumnIndex = cursor.getColumnIndex(Cons.C_BOOK_IMAGE);

            L.d(TAG, ("Got cursor with records: " + cursor.getCount()));

            int id;
            String name, url;
            byte[] image;
            while (cursor.moveToNext()) {
                id = cursor.getInt(idColumnIndex);
                name = cursor.getString(userColumnIndex);
                url = cursor.getString(textColumnIndex);
                image = cursor.getBlob(imageColumnIndex);
                BookmarkItem b = new BookmarkItem(name, url);
                b.setId(id);
                if (image != null) b.setImage(image);
                listItemAdapter.addItem(b);
                L.d(TAG, String.format("\n%s: %s: %s", id, name, url));
            }

            cursor.close();

            stopManagingCursor(cursor);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            listItemAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            L.e(TAG, "bad menuInfo", e);
            return false;
        }
        int id = (int) listItemAdapter.getItemId(info.position);

        if (id > 0) {
            final BookmarkItem b = (BookmarkItem) listItemAdapter.getItem(id);

            switch (item.getItemId()) {
                case R.id.itemOpen:
                    sBrowserData.setSelected(true);
                    sBrowserData.setSaveState(b.getUrl());
                    finish();
                    break;

                case R.id.itemEdit:
                    showMyDialog(EDIT, b);
                    break;

                case R.id.itemDelete:
                    final AlertDialog.Builder alertSearch = new AlertDialog.Builder(this);
                    alertSearch.setTitle(getResources().getString(R.string.dialog_title_delete));
                    alertSearch.setMessage(getResources().getString(R.string.dialog_message_delete));
                    alertSearch.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            dataBaseData.delete(DataBaseData.DB_TABLE_BOOKMARK, b.getId());
                            L.d(TAG, "delete: " + b.getName());

                            /*-
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    List<BookmarkItem> parseBook = getParseBookmarks();
                                    for (BookmarkItem item : parseBook) {
                                        if (item.getUrl().equals(b.getUrl())) {
                                            item.deleteInBackground();
                                            break;
                                        }
                                    }
                                }
                            }).start();*/

                            BookmarksActivity.this.startActivity(new Intent(BookmarksActivity.this,
                                    BookmarksActivity.class));
                            BookmarksActivity.this.finish();

                            dialog.dismiss();
                        }
                    });
                    alertSearch.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    alertSearch.show();
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            L.e(TAG, "bad menuInfo", e);
            return;
        }
        int id = (int) listItemAdapter.getItemId(info.position);

        if (id > 0) {
            BookmarkItem b = (BookmarkItem) listItemAdapter.getItem(id);
            menu.setHeaderTitle(b.getName());

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.bookmarkcontextmenu, menu);
        }
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        L.d(TAG, "onListItemClick: " + position);

        if (position == 0) {

            showMyDialog(ADD, sBrowserData.getBookmarkItem());

        } else {
            sBrowserData.setSelected(true);
            BookmarkItem b = (BookmarkItem) listItemAdapter.getItem(position);
            sBrowserData.setSaveState(b.getUrl());
            finish();
        }
    }

    public void showMyDialog(final int thisDialog, final BookmarkItem b) {

        TextView txtName = new TextView(this);
        txtName.setText(getResources().getString(R.string.dialog_name));
        LayoutParams txtLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        txtName.setLayoutParams(txtLayoutParams);

        final EditText edtName = new EditText(this);
        edtName.setText(b.getName());
        LayoutParams edtLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        edtName.setLayoutParams(edtLayoutParams);

        TextView txtUrl = new TextView(this);
        txtUrl.setText(getResources().getString(R.string.dialog_location));
        txtName.setLayoutParams(txtLayoutParams);

        final EditText edtUrl = new EditText(this);
        edtUrl.setText(b.getUrl());
        edtUrl.setLayoutParams(edtLayoutParams);

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.addView(txtName);
        dialogLayout.addView(edtName);
        dialogLayout.addView(txtUrl);
        dialogLayout.addView(edtUrl);

        final AlertDialog.Builder alertSearch = new AlertDialog.Builder(this);
        if (thisDialog == ADD) alertSearch.setTitle(getResources().getString(
                R.string.dialog_title_add));
        else alertSearch.setTitle(getResources().getString(R.string.dialog_title_edit));
        alertSearch.setView(dialogLayout);
        alertSearch.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                BookmarkItem newBookmark = new BookmarkItem(edtName.getText().toString(), edtUrl
                        .getText().toString());
                newBookmark.setId(b.getId());
                newBookmark.setImage(b.getImage());

                if (thisDialog == ADD) dataBaseData.insert(DataBaseData.DB_TABLE_BOOKMARK,
                        newBookmark);
                else dataBaseData.update(DataBaseData.DB_TABLE_BOOKMARK, newBookmark);

                ParseUser user = ParseUser.getCurrentUser();
                if (user != null) {
                    newBookmark.setUser(user.getUsername());
                    newBookmark.saveInBackground();
                }

                L.d(TAG, "saved: " + newBookmark.getName());

                Intent i = new Intent(BookmarksActivity.this, BookmarksActivity.class);
                BookmarksActivity.this.startActivity(i);
                BookmarksActivity.this.finish();

                dialog.dismiss();
            }
        });
        alertSearch.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alertSearch.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    /*-
    public List<BookmarkItem> getParseBookmarks() {
        ParseQuery<BookmarkItem> bookmarkQuery = BookmarkItem.getQuery();
        bookmarkQuery.whereContains(Cons.C_USER, ParseUser.getCurrentUser().getUsername());
        bookmarkQuery.orderByAscending(BookmarkItem.ID);
        List<BookmarkItem> object = new ArrayList<>();

        try {
            object = bookmarkQuery.find();
        } catch (ParseException e) {
            L.i(TAG, "Unable to get Bookmarks", e);
        }

        L.d(TAG, ("Got parse records: " + object.size()));
        return object;
    }*/
}