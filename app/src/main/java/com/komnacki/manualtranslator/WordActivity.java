/*Manual Translator Android Aplication

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
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import static com.komnacki.manualtranslator.data.WordDbContract.WordDbEntry;


public class WordActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = WordActivity.class.getSimpleName();

    private Uri currentWordUri;

    private static final int WORD_LOADER = 1;

    /** EditText fields to enter word name and translation*/
    private EditText mNameEditText;
    private EditText mTranslationEditText;
    private ImageButton imgBtn_picture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        Intent intent = getIntent();
        currentWordUri = intent.getData();


        /** Choose the title of activity. */
        if (currentWordUri == null) {
            setTitle("Add new word");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit word");
            getLoaderManager().initLoader(WORD_LOADER, null, this);
        }



        mNameEditText = (EditText) findViewById(R.id.et_word_name);
        mTranslationEditText = (EditText) findViewById(R.id.et_word_translation);
        imgBtn_picture = (ImageButton) findViewById(R.id.word_imgBtn_picture);


        imgBtn_picture.setOnClickListener(clickListener);

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.word_imgBtn_picture:
                    showImageOnFullScreen();
            }

        }
    };

    private void showImageOnFullScreen() {
        Toast.makeText(getApplicationContext(), "Image button clicked!",Toast.LENGTH_SHORT).show();
    }






    //-------------------OPTION MENU--------------------------------


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_single_word_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save_word:
                saveWord();
                return true;
            case R.id.action_delete_all_data:
                return true;
            case android.R.id.home:
                //Dodać flage zmiany inputu. Jesli input sie nie zmienil nie warto pytac o zmiany.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(WordActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }



        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void saveWord() {
        String nameOfWord = mNameEditText.getText().toString().trim();
        String translationOfWord = mTranslationEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameOfWord)) {
            Toast.makeText(this, "Name of word cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }


        ContentValues values = new ContentValues();
        values.put(WordDbEntry.COLUMN_WORD_NAME, nameOfWord);
        values.put(WordDbEntry.COLUMN_WORD_TRANSLATION, translationOfWord);

        if(currentWordUri == null){
            Uri newUri = getContentResolver().insert(WordDbEntry.CONTENT_URI, values);
            if(newUri == null){
                Toast.makeText(this, "Error with saving word! Please, try again.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Insert successful!", Toast.LENGTH_SHORT).show();
            }
        }else{
            int rowAffected = getContentResolver().update(currentWordUri, values, null, null);
            if(rowAffected == 0){
                Toast.makeText(this, "Error with updating word! Please, try again.", Toast.LENGTH_LONG). show();
            }else{
                Toast.makeText(this, "Update successful!", Toast.LENGTH_SHORT).show();
            }
        }


    }

    //-------------------LOADER--------------------------------------
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

    }

    /** Create a projection and a new loader and send it to run on background thread.
     * After it finish in background thread, onLoadFinished method start working.
     * @param i
     * @param args
     * @return CursorLoader for onLoadFinished.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {

        //Specify what column we're interested in.
        String[] projection = {
                WordDbEntry._ID,
                WordDbEntry.COLUMN_WORD_NAME,
                WordDbEntry.COLUMN_WORD_TRANSLATION};

        return new CursorLoader(
                this,
                currentWordUri,
                projection,
                null,
                null,
                null);
    }



    /**
     * Start working after onCreateLoader finish run on background thread.
     * Assign data to xml-fields.
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){

            //Find the columns of word attributes that we're intrested in.
            int idColumnIndex = data.getColumnIndex(WordDbEntry._ID);
            int nameColumnIndex = data.getColumnIndex(WordDbEntry.COLUMN_WORD_NAME);
            int translationColumnIndex = data.getColumnIndex(WordDbEntry.COLUMN_WORD_TRANSLATION);

            //Extract out the value from the Cursor for rhe given column index.
            String id = data.getString(idColumnIndex);
            String name = data.getString(nameColumnIndex);
            String translation = data.getString(translationColumnIndex);

            //Update the views on the screen with the values from database.
            mNameEditText.setText(name);
            mTranslationEditText.setText(translation);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
