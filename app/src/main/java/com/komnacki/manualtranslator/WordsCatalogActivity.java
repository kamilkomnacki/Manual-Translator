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
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;

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


        ArrayList<Word> exampleListOfWords = new ArrayList<>();
        exampleListOfWords.add(new Word("fragile", "kruchy"));
        exampleListOfWords.add(new Word("emphatic", "dobitny"));
        exampleListOfWords.add(new Word("setbacks", "niepowodzenia"));
        exampleListOfWords.add(new Word("underlying", "zasadniczy"));
        exampleListOfWords.add(new Word("assume", "przyjąć"));
        exampleListOfWords.add(new Word("discrepancy", "rozbieżność"));
        exampleListOfWords.add(new Word("entrench", "obwarować"));
        exampleListOfWords.add(new Word("outrageous", "oburzający"));
        exampleListOfWords.add(new Word("condemned", "potępiony"));
        exampleListOfWords.add(new Word("scrutiny", "przestudiowanie"));
        exampleListOfWords.add(new Word("unspoilt", "nieskażony"));
        exampleListOfWords.add(new Word("gorgeus", "wspaniały"));
        exampleListOfWords.add(new Word("reluctant", "niechętny"));


        listViewOfWords = (ListView) findViewById(R.id.listOfWords);

        cursorAdapter = new WordCursorAdapter(this, null);
        listViewOfWords.setAdapter(cursorAdapter);

        listViewOfWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(WordsCatalogActivity.this, WordActivity.class);
                Uri currentWordUri = ContentUris.withAppendedId(WordDbEntry.CONTENT_URI, id);

                intent.setData(currentWordUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(WORD_LOADER,null, this);

    }



    @Override
    protected void onStart() {
        super.onStart();
        //displayDatabaseInfo();
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


    private void deleteListItemsMode(final MenuItem deleteButton) {
        final LinearLayout bottomDeletePanel = (LinearLayout) findViewById(R.id.linearL_wordCatalog_bottomDeletePanel);

        bottomDeletePanel.setVisibility(View.VISIBLE);
        deleteButton.setVisible(false);
        enable_checkboxForEachItem(true);

        Button btn_selectAll = (Button) findViewById(R.id.btn_wordCatalog_bottomPanel_selectAll);
        Button btn_cancelDelete = (Button) findViewById(R.id.btn_wordCatalog_bottomPanel_cancelDelete);


        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_wordCatalog_bottomPanel_selectAll:
                        //sellectAllItemsInListView();
                        break;
                    case R.id.btn_wordCatalog_bottomPanel_cancelDelete:
                        enable_checkboxForEachItem(false);
                        bottomDeletePanel.setVisibility(View.GONE);
                        deleteButton.setVisible(true);
                        break;
                }
            }
        };

        btn_selectAll.setOnClickListener(clickListener);
        btn_cancelDelete.setOnClickListener(clickListener);

    }

    private void sellectAllItemsInListView() {
//        cursorAdapter.isAllItemsCheckBoxVisible = true;
//        cursorAdapter.notifyDataSetChanged();
    }


    private void enable_checkboxForEachItem(boolean isEnable) {
        cursorAdapter.isAllItemsCheckBoxVisible = isEnable;
        cursorAdapter.notifyDataSetChanged();
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

    private void displayDatabaseInfo(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();


        String[] projection = {
                WordDbEntry._ID,
                WordDbEntry.COLUMN_WORD_NAME,
                WordDbEntry.COLUMN_WORD_TRANSLATION
        };

        Cursor cursor = database.query(
                WordDbEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );




    }

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
