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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import static com.komnacki.manualtranslator.data.WordDbContract.WordDbEntry;


/**
 * Database helper for WordModel app.
 * Manages database creation and version management.
 */
public class WordDbHelper extends SQLiteOpenHelper {

    /**
     * The name of class to mark logs.
     */
    public static final String LOG_TAG = WordDbHelper.class.getSimpleName();


    /**
     * Name of the database file.
     * This file will be put in internal storage in private disk space.
     * /data/data/com.komnacki.manualTranslator/databases/
     */
    private static final String DATABASE_NAME = "dictionary.db";


    /**
     * If you change the database schema, you must increment the DATABASE_VERSION.
     * The onUpgrade() or onDowngrade() method will execute after change version.
     */
    private static final int DATABASE_VERSION = 1;


    /**
     * Constructs a new instance of {@link WordDbHelper}.
     * @param context of the app
     */
    public WordDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    /**
     * This method is called when the database is created for the first time.
     * @param sqLiteDatabase - instance of database use to execute sql statement create.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_WORDS_TABLE = "CREATE TABLE " + WordDbEntry.TABLE_NAME + " ("
                + WordDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WordDbEntry.COLUMN_WORD_NAME + " TEXT NOT NULL, "
                + WordDbEntry.COLUMN_WORD_TRANSLATION + " TEXT, "
                + WordDbEntry.COLUMN_WORD_CATEGORY + " TEXT, " //wprowadz DEFAULT
                + WordDbEntry.COLUMN_WORD_LANGUAGE + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_WORDS_TABLE);
        Log.v(LOG_TAG, "Tables have been created successfully.");
    }


    /**
     * This method is called when the database is upgraded.
     * Firstly, delete old table. After that create new table.
     * ALL DATA FROM OLD TABLE WILL BE LOST!
     * @param sqLiteDatabase - instance of database
     * @param oldVersion - not using
     * @param newVersion - not using
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String SQL_DELETE_WORDS_TABLE = "DROP TABLE IF EXISTS " + WordDbEntry.TABLE_NAME;

        sqLiteDatabase.execSQL(SQL_DELETE_WORDS_TABLE);
        onCreate(sqLiteDatabase);
        Log.d(LOG_TAG, "Tables have been upgraded succesfully.");
    }



    /**
     * This method is called when database version is downgraded.
     * @param db - instance of database
     * @param oldVersion - not using
     * @param newVersion - not using
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
        Log.d(LOG_TAG, "Tables have been downgraded succesfully.");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Database reopen.");
        super.onOpen(db);
        Log.d(LOG_TAG, "Tables have been opened succesfully.");
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


}
