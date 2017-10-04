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
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.komnacki.manualtranslator.data.ExternalStorageContract;

import java.io.File;

import static com.komnacki.manualtranslator.data.WordDbContract.WordDbEntry;

//TO DO:
//1. android developers - query free space!
public class WordActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = WordActivity.class.getSimpleName();
    private static final int WORD_LOADER = 1;
    private Uri currentWordUri;


    /** EditText fields to enter word name and translation*/
    private EditText mNameEditText;
    private EditText mTranslationEditText;
    private ImageButton imgBtn_picture;


    /** Boolean flag that keeps track of whether the word has been edited (true) or not (false).*/
    private boolean flag_dataHasChanged = false;


    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the flag_dataHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            flag_dataHasChanged = true;
            return false;
        }
    };



    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.word_imgBtn_picture:
                    showImageOnFullScreen();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);


        /**
         * Get intent with data from WordsCatalogActivity.
         * Data from WordsCatalogActivity is used to manage directories for pictures, sounds ect.
         */
        Intent intent = getIntent();
        currentWordUri = intent.getData();



        /**
         * Check if external storage is accessible.
         * If yes, go on and create directory for multimedia data.
         * if no, warn user that is not possible to use functions related with multimedia data.
         */
        if(isExternalStorageAccessible(savedInstanceState, intent))
            createDirectoryForPictures();
        else
            showExternalStorageAlertDialog();



        /**
         * If new word is created and this activity is used to fill data, the title is "Add new word"
         * If word is editing and this activity is used to edit data, the title is "Edit word"
         */
        setTitleForActivity();



        /**
         * Bind java objects with XML elements.
         */
        mNameEditText = (EditText) findViewById(R.id.et_word_name);
        mTranslationEditText = (EditText) findViewById(R.id.et_word_translation);
        imgBtn_picture = (ImageButton) findViewById(R.id.word_imgBtn_picture);



        /**
         * Set listeners for objects.
         */
        mNameEditText.setOnTouchListener(mTouchListener);
        mTranslationEditText.setOnTouchListener(mTouchListener);
        imgBtn_picture.setOnClickListener(clickListener);
    }

    private void setTitleForActivity() {
        if (currentWordUri == null) {
            setTitle("Add new word");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit word");
            getLoaderManager().initLoader(WORD_LOADER, null, this);
        }
    }

    private void showExternalStorageAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(WordActivity.this).create();
        alertDialog.setTitle("External storage not accessible!");
        alertDialog.setMessage("You cannot use picture and sound functions, because the problem with external storage occur. Please check if you have enough memory space.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void createDirectoryForPictures() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ExternalStorageContract.DIRECTORY_PICTURES_NAME);
        if(file.mkdirs()){
            Log.e(LOG_TAG, "Directory not created");
        }
        if(file.isDirectory()){
            Log.d(LOG_TAG, "Directory for photos already exist.");
        }
    }

    private Boolean isExternalStorageAccessible(Bundle savedInstanceState, Intent intent) {
        String externalStorageState = ExternalStorageContract.EXTERNAL_STORAGE_STATE;
        Boolean isExternalStorageAccessible;
        if(savedInstanceState == null){
            Bundle extras = intent.getExtras();
            isExternalStorageAccessible = (extras != null) && (extras.getBoolean(externalStorageState));
        }else
            isExternalStorageAccessible = (Boolean) savedInstanceState.getSerializable(externalStorageState);


        return isExternalStorageAccessible;
    }

    private void showImageOnFullScreen() {
        Toast.makeText(getApplicationContext(), "Image button clicked!",Toast.LENGTH_SHORT).show();
    }





    //---------------------------------------------------------------
    //-------------------OPTION MENU---------------------------------
    //---------------------------------------------------------------
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
                finish();
                return true;
            case R.id.action_delete_word:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                backwardToParentActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }






    //---------------------------------------------------------------
    //-------------------SAVE WORD------------------------------------
    //---------------------------------------------------------------
    private void backwardToParentActivity() {
        if(flag_dataHasChanged){
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NavUtils.navigateUpFromSameTask(WordActivity.this);
                        }
                    };
            showUnsavedChangesDialog(discardButtonClickListener);
        }else{
            NavUtils.navigateUpFromSameTask(WordActivity.this);
        }
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
        flag_dataHasChanged = false;
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







    //---------------------------------------------------------------
    //-------------------DELETE WORD------------------------------------
    //---------------------------------------------------------------
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this word?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteWord();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteWord() {


        if (currentWordUri!=null){
            int rowsDeleted = getContentResolver().delete(currentWordUri, null, null);
            if(rowsDeleted == 0){
                Toast.makeText(this, "Error with deleting word.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Delete successful!", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }








    //---------------------------------------------------------------
    //-------------------LOADER--------------------------------------
    //---------------------------------------------------------------


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
                WordDbEntry.COLUMN_WORD_TRANSLATION,
                WordDbEntry.COLUMN_WORD_PICTURE_TITLE};

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
            int pictureTitleColumnIndex = data.getColumnIndex(WordDbEntry.COLUMN_WORD_PICTURE_TITLE);

            //Extract out the value from the Cursor for rhe given column index.
            String id = data.getString(idColumnIndex);
            String name = data.getString(nameColumnIndex);
            String translation = data.getString(translationColumnIndex);
            String pictureTitle = data.getString(pictureTitleColumnIndex);

            //Update the views on the screen with the values from database.
            mNameEditText.setText(name);
            mTranslationEditText.setText(translation);


            if(pictureTitle == null){
                Toast.makeText(this, "picture title is null", Toast.LENGTH_SHORT).show();
            }else{
                File file = ExternalStorageContract.getPicturesStorageDir();
                StringBuilder picturePath = new StringBuilder(file.getPath()).append("/pictureTest.jpg");
                Bitmap picture = BitmapFactory.decodeFile(picturePath.toString());
                imgBtn_picture.setImageBitmap(picture);
                Toast.makeText(this, picturePath, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
