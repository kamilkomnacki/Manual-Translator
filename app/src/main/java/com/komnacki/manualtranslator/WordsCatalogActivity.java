/*
        Manual Translator Android Aplication

        Copyright (C) 2017 Kamil Komnacki

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
        */

package com.komnacki.manualtranslator;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.komnacki.manualtranslator.data.WordDbHelper;

import static com.komnacki.manualtranslator.data.WordDbContract.WordDbEntry;

public class WordsCatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{




    /** Identifier for the word data loader */
    private static final int WORD_LOADER = 0;



    private WordDbHelper dbHelper = new WordDbHelper(this);
    private ListView listViewOfWords;
    WordCursorAdapter cursorAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_catalog);




        listViewOfWords = (ListView) findViewById(R.id.listOfWords);


        cursorAdapter = new WordCursorAdapter(this, null);
        listViewOfWords.setAdapter(cursorAdapter);
        listViewOfWords.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listViewOfWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                runWordActivityClassForItem(id);
            }
            private void runWordActivityClassForItem(long id) {
                Intent intent = new Intent(WordsCatalogActivity.this, WordActivity.class);
                Uri currentWordUri = ContentUris.withAppendedId(WordDbEntry.CONTENT_URI, id);

                intent.setData(currentWordUri);
                startActivity(intent);
            }
        });



        getLoaderManager().initLoader(WORD_LOADER,null, this);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_words_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_insert_dummy_data:
                insertWord();
                //displayDatabaseInfo();
                return true;
            case R.id.action_delete_words_from_catalogActivity:
                deleteListItemsMode(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void deleteListItemsMode(final MenuItem btn_onOptionMenu_delete) {
        final LinearLayout deletePanel = (LinearLayout) findViewById(R.id.linearL_wordCatalog_bottomDeletePanel);
        final Button btn_selectAll = (Button) findViewById(R.id.btn_wordCatalog_deletePanel_selectAll);
        final Button btn_cancelDelete = (Button) findViewById(R.id.btn_wordCatalog_deletePanel_cancelDelete);
        final FloatingActionButton floatingBTN_delete = (FloatingActionButton) findViewById(R.id.floatingButton_catalogActivity_delete);

        btn_onOptionMenu_delete.setVisible(false);
        deletePanel.setVisibility(View.VISIBLE);
        floatingBTN_delete.setVisibility(View.VISIBLE);
        cursorAdapter.setCheckboxesVisible(true);
        cursorAdapter.list.init(cursorAdapter.getCursor());


        //////////////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////////////////////////////////

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_wordCatalog_deletePanel_selectAll:
                        cursorAdapter.selectAll();
                        cursorAdapter.notifyDataSetChanged();
                        break;
                    case R.id.btn_wordCatalog_deletePanel_cancelDelete:
                        btn_onOptionMenu_delete.setVisible(true);
                        deletePanel.setVisibility(View.GONE);
                        floatingBTN_delete.setVisibility(View.GONE);
                        cursorAdapter.setCheckboxesVisible(false);
                        cursorAdapter.unselectAll();
                        cursorAdapter.notifyDataSetChanged();
                        break;
                    case R.id.floatingButton_catalogActivity_delete:
                        int size = cursorAdapter.list.getSelectedItemsID().size();
                        String[] itemsToDelete = cursorAdapter.list.getSelectedItemsID().toArray(new String[size]);

                        getContentResolver().delete(WordDbEntry.CONTENT_URI, WordDbEntry._ID, itemsToDelete);
                        //Toast.makeText(getApplicationContext(), "Selected items: " + itemsToDelete., Toast.LENGTH_LONG).show();
                        break;
                }

            }
        };


        floatingBTN_delete.setOnClickListener(clickListener);
        btn_cancelDelete.setOnClickListener(clickListener);
        btn_selectAll.setOnClickListener(clickListener);
    }







    private void insertWord() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WordDbEntry.COLUMN_WORD_NAME, "gorgeus");
        contentValues.put(WordDbEntry.COLUMN_WORD_TRANSLATION, "wspaniały");
        contentValues.put(WordDbEntry.COLUMN_WORD_CATEGORY, "category 1");
        contentValues.put(WordDbEntry.COLUMN_WORD_LANGUAGE, "language 1");

        Uri newUri = getContentResolver().insert(WordDbEntry.CONTENT_URI, contentValues);
        Toast.makeText(getApplicationContext(), "new row: " + newUri, Toast.LENGTH_SHORT).show();
    }


    //----------------------------------------------------------------------------------------------
    //--------------------------------LOADER--------------------------------------------------------
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                WordDbEntry._ID,
                WordDbEntry.COLUMN_WORD_NAME,
                WordDbEntry.COLUMN_WORD_TRANSLATION};


        return new CursorLoader(
                this,
                WordDbEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


}
