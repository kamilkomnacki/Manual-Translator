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

package com.komnacki.manualtranslator.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;

public class WordProvider extends ContentProvider{


    public static final String LOG_TAG = WordProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the WORDS table.*/
    private static final int WORDS = 100;

    /** URI matcher code for the content URI for the SINGLE word in the table.*/
    private static final int WORD_ID = 101;



    /**
     * Database helper object
     */
    private WordDbHelper mDbHelper;

    /**
     * UriMatcher object to match a content URI to corresponding code.
     * The input passed into the constructor of UriMatcher
     * represents the code to return for the URI root.
     * On the beginning UriMatcher use NO_MATCH input.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        /**
         * When method addUri() is called then go here, for all of the content URI patterns that
         * the provider should recognize. All paths added to the UriMatcher have corresponding code
         * to return when a match is found.
         */

        /**
         * The content URI of the form "content://com.komnacki.manualtranslator/words"
         * will map to the integer code {@link #WORDS}. This URI is used to provide
         * access to MULTIPLE rows of the words table.
         */
       sUriMatcher.addURI(WordDbContract.CONTENT_AUTHORITY, WordDbContract.PATH, WORDS);

        /**
         * The content URI of the form "content://com.komnacki.manualtranslator/words/# will map
         * to the integer code {@link #WORD_ID}. This URI is used to provide acces to one single row.
         *
         * The "#" (wildcard) is used and it can be substituted for integer.
         */
        sUriMatcher.addURI(WordDbContract.CONTENT_AUTHORITY, WordDbContract.PATH + "/#", WORD_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     * Refer to {@link WordDbHelper} constructor.
     * @return
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new WordDbHelper(getContext());
        return false;
    }



    /**
     * Perform the query for the given URI.
     * Use the given projection, selection, selection args and sort order.
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArguments
     * @param sortOrder
     * @return cursor
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArguments, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case WORDS:
                cursor = database.query(
                        WordDbContract.WordDbEntry.TABLE_NAME,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case WORD_ID:
                selection = WordDbContract.WordDbEntry._ID + "=?";
                selectionArguments = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(
                        WordDbContract.WordDbEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    /**
     * Returns the MIME type of data for the content URI.
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case WORDS:
                return WordDbContract.WordDbEntry.CONTENT_LIST_TYPE;
            case WORD_ID:
                return WordDbContract.WordDbEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    /**
     * Insert new data into the provider with the give ContentValues.
     * @param uri
     * @param contentValues
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case WORDS:
                return insertWord(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }



    /**
     * Insert a word into the database with the given content values.
     * Return the new content URI for that specyfic row in the database.
     * @param uri
     * @param contentValues
     * @return uri
     */
    private Uri insertWord(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //String name = contentValues.getAsString(WordDbContract.WordDbEntry.COLUMN_WORD_NAME);
        String name = contentValues.getAsString(WordDbContract.WordDbEntry.COLUMN_WORD_NAME);
        String translation = contentValues.getAsString(WordDbContract.WordDbEntry.COLUMN_WORD_TRANSLATION);
        String category = contentValues.getAsString(WordDbContract.WordDbEntry.COLUMN_WORD_CATEGORY);
        String language = contentValues.getAsString(WordDbContract.WordDbEntry.COLUMN_WORD_LANGUAGE);

        long id = db.insert(WordDbContract.WordDbEntry.TABLE_NAME, null, contentValues);
        if(id == -1){
            return null;
        } else{
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id);
        }
    }


    /**
     * Delete the data at the given selection and selection atguments.
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match){
            case WORDS:
                selection = selection + " IN (?";
                for(int i=1; i<selectionArgs.length; i++){
                    selection += ",?";
                }
                selection +=")";

                getContext().getContentResolver().notifyChange(uri, null);
                Log.d(LOG_TAG, "Delete method data: " + selection + " / " + Arrays.toString(selectionArgs));
                return db.delete(WordDbContract.WordDbEntry.TABLE_NAME, selection, selectionArgs);
            case WORD_ID:
                selection = WordDbContract.WordDbEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return db.delete(WordDbContract.WordDbEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for: " + uri);
        }

    }


    /**
     * Updates the data at given selection and selection arguments, with the new ContentValues.
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case WORDS:
                getContext().getContentResolver().notifyChange(uri, null);
                return updateWord(uri, contentValues, selection, selectionArgs);
            case WORD_ID:
                getContext().getContentResolver().notifyChange(uri, null);
                selection = WordDbContract.WordDbEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateWord(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for: "+ uri);
        }
    }


    private int updateWord(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if(values.size() == 0)
            return 0;

        if(values.containsKey(WordDbContract.WordDbEntry.COLUMN_WORD_NAME)){
            String name = values.getAsString(WordDbContract.WordDbEntry.COLUMN_WORD_NAME);
            if ((name == null) || (name.length() == 0)){
                throw new IllegalArgumentException("Update name is invalid.");
            }
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.update(WordDbContract.WordDbEntry.TABLE_NAME, values, selection, selectionArgs);
    }


}
