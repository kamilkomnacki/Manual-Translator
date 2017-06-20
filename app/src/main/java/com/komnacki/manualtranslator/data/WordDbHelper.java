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


/**
 * Database helper for Word app.
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
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link WordDbHelper}.
     *
     * @param context of the app
     */
    public WordDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * This method is called when the database is created for the first time.
     * @param sqLiteDatabase - instance of database
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }


    /**
     * This method is called when the database is upgraded.
     * @param sqLiteDatabase - instance of database
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //For now this database is at verion 1, so there's nothing to do.
    }
}
