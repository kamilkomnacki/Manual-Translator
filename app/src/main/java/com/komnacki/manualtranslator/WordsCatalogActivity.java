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

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import static com.komnacki.manualtranslator.data.ExternalStorageContract.EXTERNAL_STORAGE_STATE;
import static com.komnacki.manualtranslator.data.ExternalStorageContract.REQUEST_PERMISSION_WRITE;
import static com.komnacki.manualtranslator.data.WordDbContract.WordDbEntry;

public class WordsCatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{


    String LOG_TAG = getClass().getName();


    /** Identifier for the word data loader */
    private static final int WORD_LOADER = 0;




    private WordDbHelper dbHelper = new WordDbHelper(this);
    private ListView listViewOfWords;
    WordCursorAdapter cursorAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_catalog);

        getPermissionToWriteExternalStorage();

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
                intent.putExtra(EXTERNAL_STORAGE_STATE, (isExternalStorageWritable()));
                startActivity(intent);
            }
        });



        getLoaderManager().initLoader(WORD_LOADER,null, this);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_words_catalog, menu);
        cursorAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_insert_dummy_data:
                insertWord();
                cursorAdapter.notifyDataSetChanged();
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
        cursorAdapter.notifyDataSetChanged();


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

                        showDeleteConfirmationDialog(cursorAdapter.list.getSelectedItemsID().size());

                        //Toast.makeText(getApplicationContext(), "Selected items: " + itemsToDelete., Toast.LENGTH_LONG).show();



                        break;
                }
                //Tu mozesz sprobowac dac notifyDataSetChanged() + przetestowac!

            }
        };


        floatingBTN_delete.setOnClickListener(clickListener);
        btn_cancelDelete.setOnClickListener(clickListener);
        btn_selectAll.setOnClickListener(clickListener);
    }

    private void showDeleteConfirmationDialog(int amountOfWordsToDelete) {
        if(amountOfWordsToDelete > 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //--------------------------------------------------------------------------------------
            //------------------------MESSAGE----------------------------------------------------
            String message = (amountOfWordsToDelete > 1) ? "Delete " + amountOfWordsToDelete + " words?" : "Delete this word?";
            builder.setMessage(message);
            //------------------------DELETE--------------------------------------------------------
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    delete();
                }
            });
            //------------------------CANCEL--------------------------------------------------------
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    if(dialog != null){
                        dialog.dismiss();
                    }
                }
            });
            //--------------------------------------------------------------------------------------
            //--------------------------------------------------------------------------------------

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }else
            Toast.makeText(getApplicationContext(), "Select items to delete", Toast.LENGTH_LONG).show();
    }

    private void delete(){
        int size = cursorAdapter.list.getSelectedItemsID().size();
        String[] itemsToDelete = cursorAdapter.list.getSelectedItemsID().toArray(new String[size]);
        int result = getContentResolver().delete(WordDbEntry.CONTENT_URI, WordDbEntry._ID, itemsToDelete);

        if(result <= 0)
            Toast.makeText(getApplicationContext(), "Error occurred with deleting items", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), "Delete successful!", Toast.LENGTH_SHORT).show();

        cursorAdapter.unselectAll();
        cursorAdapter.notifyDataSetChanged();
    }

    private void insertWord() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WordDbEntry.COLUMN_WORD_NAME, "gorgeus");
        contentValues.put(WordDbEntry.COLUMN_WORD_TRANSLATION, "wspaniały");
        contentValues.put(WordDbEntry.COLUMN_WORD_CATEGORY, "category 1");
        contentValues.put(WordDbEntry.COLUMN_WORD_LANGUAGE, "language 1");
        contentValues.put(WordDbEntry.COLUMN_WORD_PICTURE_TITLE, "pictureTest");

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






    //----------------------------------------------------------------------------------------------
    //---------------------------------PERMISSIONS--------------------------------------------------
    /** Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    /** Initiate request for permissions. */
    private void getPermissionToWriteExternalStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_WRITE);

                // REQUEST_PERMISSION_WRITE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    /** Handle permissions result */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,
                            "External storage permission granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
