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
         * When method addUri() is called then go here, for all of th content URI patterns that
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
        sUriMatcher.addURI(WordDbContract.CONTENT_AUTHORITY, WordDbContract.PATH + "#", WORD_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     * Refer to {@link WordDbHelper} constructor.
     * @return
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new WordDbHelper(getContext());
        return true;
    }



    /**
     * Perform the query for the given URI.
     * Use the given projection, selection, selection args and sort order.
     * @param uri
     * @param strings
     * @param s
     * @param strings1
     * @param s1
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }


    /**
     * Returns the MIME type of data for the content URI.
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
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
     * @param s
     * @param strings
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }


    /**
     * Updates the data at given selection and selection arguments, with the new ContentValues.
     * @param uri
     * @param contentValues
     * @param s
     * @param strings
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
