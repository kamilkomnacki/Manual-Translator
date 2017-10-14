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
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.komnacki.manualtranslator.data.WordDbContract.WordDbEntry;

//TO DO:
//1. android developers - query free space!
public class WordActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = WordActivity.class.getSimpleName();


    /**
     * Unique identifier for word loader. Helps identify loader if there is many.*/
    private static final int WORD_LOADER = 1;


    /**
     *  Unique constant value to recognize camera intent in onActivityResult() method.*/
    static final int REQUEST_TAKE_PHOTO = 1000;


    /**
     * EditText fields to enter word name and translation*/
    private EditText mNameEditText;
    private EditText mTranslationEditText;


    /**
     * ImageButton field to show current word image (and zoom in to full screen in the future).*/
    private ImageButton imgBtn_picture;


    /**
     * Boolean flag that keeps track of whether the word has been edited (true) or not (false).*/
    private boolean flag_dataHasChanged = false;


    private Uri currentWordUri;
    private String mImageFileLocation = "";


    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the flag_dataHasChanged boolean to true.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
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
                    imageClickHandler();
            }

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);


        /**
         * Get intent with data from WordsCatalogActivity.
         * Data from WordsCatalogActivity is used to manage directories for pictures, sounds ect.*/
        Intent intent = getIntent();
        currentWordUri = intent.getData();


        /**
         * Check if external storage is accessible.
         * if no, warn user that is not possible to use functions related with multimedia data.*/
        if(!isExternalStorageAccessible(savedInstanceState, intent))
            showAlertDialogExternalStorage();


        /**
         * If new word is created and this activity is used to fill data, the title is "Add new word"
         * If word is editing and this activity is used to edit data, the title is "Edit word"*/
        setTitleForActivity();


        /**
         * Bind java objects with XML elements.*/
        mNameEditText = (EditText) findViewById(R.id.et_word_name);
        mTranslationEditText = (EditText) findViewById(R.id.et_word_translation);
        imgBtn_picture = (ImageButton) findViewById(R.id.word_imgBtn_picture);


        /**
         * Set listeners for objects.*/
        mNameEditText.setOnTouchListener(touchListener);
        mTranslationEditText.setOnTouchListener(touchListener);
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


    private void showAlertDialogExternalStorage() {
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


    private void showAlertDialogCamera() {
        AlertDialog alertDialog = new AlertDialog.Builder(WordActivity.this).create();
        alertDialog.setTitle("Camera is not accessible!");
        alertDialog.setMessage("You cannot take a picture, because your device not support this feature or there is no application to take a picture.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }


    private Boolean isExternalStorageAccessible(Bundle savedInstanceState, Intent intent) {
        String externalStorageState = ExternalStorageContract.EXTERNAL_STORAGE_STATE;
        Boolean isExternalStorageAccessible;
        if (savedInstanceState == null) {
            Bundle extras = intent.getExtras();
            isExternalStorageAccessible = (extras != null) && (extras.getBoolean(externalStorageState));
        } else
            isExternalStorageAccessible = (Boolean) savedInstanceState.getSerializable(externalStorageState);


        return isExternalStorageAccessible;
    }


    //----------------------------------------------------------------------------------------------
    //------------------------------------WORD IMAGE-------------------------------------------------
    //----------------------------------------------------------------------------------------------
    private void imageClickHandler() {
        //Save other data in this activity before go to camera application.
        if(flag_dataHasChanged)
            saveWord();



        Intent takePhotoIntent = new Intent();
        takePhotoIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);


        // Ensure that there's a camera activity to handle the intent
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFIle();
            } catch (IOException e) {
                e.printStackTrace();
            }
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
        }else{
            showAlertDialogCamera();
        }
    }


    /**
     * Method invoked after external application finish tasks.
     * @param requestCode   - unique code to recognize intent.
     * @param resultCode    - code used to check if intent finish correctly.
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    rotateImage(reduceImageSize());
                    updateImageInDatabase();
                    break;
            }
        }
    }


    /**
     * New name for file example:       MT_20171005_131005_1234567890.jpg
     * Directory for photos:            /Pictures/ManualTranslator/
     * Image type:                      *.jpg
     *
     * @return File image - temporary file with image data.
     * @throws IOException
     */
    File createImageFIle() throws IOException {
        String imageFileName = getPictureName();
        File storageDirectory = ExternalStorageContract.getPicturesStorageDir();

        File image = File.createTempFile(
                imageFileName,          /** prefix */
                ".jpg",                 /** suffix */
                storageDirectory);      /** directory */

        mImageFileLocation = image.getAbsolutePath();
        return image;
    }


    /**
     * Generate name for image file.
     * Name syntax:     MT_20171005_131005_1234567890
     * @return
     */
    private String getPictureName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = simpleDateFormat.format(new Date());
        return "MT_" + timeStamp;
    }


    /**
     * Reduce image size in order to save memory and show in activity. This method set image
     * proportionally to original dimensions.
     * @return
     */
    private Bitmap reduceImageSize() {
        int targetImageViewWidth = imgBtn_picture.getWidth();
        int targetImageViewHeight = imgBtn_picture.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions);

        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth / targetImageViewWidth, cameraImageHeight / targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
    }


    /**
     * Rotate image to correct position.
     * Some devices rotate photos automatically, and setting in not correct orientation.
     *
     * @param bitmap reduced image from location (external storage).
     */
    private void rotateImage(Bitmap bitmap){
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(mImageFileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        imgBtn_picture.setImageBitmap(rotatedBitmap);

    }



    /**
     * Set picture to local ImageView object, by path to image in storage.
     * @param picturePath
     */
    private void setPictureWithPath(String picturePath) {
        if (picturePath == null) {
            Toast.makeText(this, "picture path is null", Toast.LENGTH_SHORT).show();
        } else {
            mImageFileLocation = picturePath;
            rotateImage(reduceImageSize());

            Toast.makeText(this, picturePath, Toast.LENGTH_SHORT).show();
        }
    }


    private void updateImageInDatabase() {
        Log.d(LOG_TAG, "Photo path: " + mImageFileLocation);

        if (mImageFileLocation != null) {
            ContentValues values = new ContentValues();
            values.put(WordDbEntry.COLUMN_WORD_PICTURE_PATH, mImageFileLocation);

            if (currentWordUri == null) {
                Uri newUri = getContentResolver().insert(WordDbEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, "Error with saving picture! Please, try again.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Insert successful!", Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowAffected = getContentResolver().update(currentWordUri, values, null, null);
                if (rowAffected == 0) {
                    Toast.makeText(this, "Error with updating picture! Please, try again.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Update successful!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void showImageOnFullScreen() {
        Toast.makeText(getApplicationContext(), "Image button clicked!", Toast.LENGTH_SHORT).show();
    }



    //----------------------------------------------------------------------------------------------
    //-------------------SAVE WORD------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    private void backwardToParentActivity() {
        if (flag_dataHasChanged) {
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NavUtils.navigateUpFromSameTask(WordActivity.this);
                        }
                    };
            showUnsavedChangesDialog(discardButtonClickListener);
        } else {
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
                if (dialogInterface != null) {
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

        if (currentWordUri == null) {
            Uri newUri = getContentResolver().insert(WordDbEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Error with saving word! Please, try again.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Insert successful!", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowAffected = getContentResolver().update(currentWordUri, values, null, null);
            if (rowAffected == 0) {
                Toast.makeText(this, "Error with updating word! Please, try again.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Update successful!", Toast.LENGTH_SHORT).show();
            }
        }
    }




    //---------------------------------------------------------------
    //-------------------DELETE WORD------------------------------------
    //---------------------------------------------------------------
    private void deleteWord() {


        if (currentWordUri != null) {
            int rowsDeleted = getContentResolver().delete(currentWordUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error with deleting word.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Delete successful!", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


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
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //---------------------------------------------------------------
    //-------------------LOADER--------------------------------------
    //---------------------------------------------------------------
    /**
     * Create a projection and a new loader and send it to run on background thread.
     * After it finish in background thread, onLoadFinished method start working.
     *
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
                WordDbEntry.COLUMN_WORD_PICTURE_PATH};

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
     *
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {

            //Find the columns of word attributes that we're intrested in.
            int idColumnIndex = data.getColumnIndex(WordDbEntry._ID);
            int nameColumnIndex = data.getColumnIndex(WordDbEntry.COLUMN_WORD_NAME);
            int translationColumnIndex = data.getColumnIndex(WordDbEntry.COLUMN_WORD_TRANSLATION);
            int pictureTitleColumnIndex = data.getColumnIndex(WordDbEntry.COLUMN_WORD_PICTURE_PATH);

            //Extract out the value from the Cursor for rhe given column index.
            String id = data.getString(idColumnIndex);
            String name = data.getString(nameColumnIndex);
            String translation = data.getString(translationColumnIndex);
            String picturePath = data.getString(pictureTitleColumnIndex);

            //Update the views on the screen with the values from database.
            mNameEditText.setText(name);
            mTranslationEditText.setText(translation);
            setPictureWithPath(picturePath);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}




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
        switch (item.getItemId()) {
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
}
