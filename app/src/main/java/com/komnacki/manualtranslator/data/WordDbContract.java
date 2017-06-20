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


import android.provider.BaseColumns;

/**
 * Contract class for Words Database.
 * This class is a container for constants that define names for URIs, tables, columns.
 * This class allow using the same constants across the other classes.
 */
public final class WordDbContract {




    /**
     * To prevent someone from instantiating the contract class,
     * I set constructor private
     */
    private WordDbContract() {}


    /**
     * Inner class to define table contents.
     * Each entry in the table represents a single word.
     */
    public static class WordDbEntry implements BaseColumns{

        /**
         * Name of database table for words.
         */
        public static final String TABLE_NAME = "words";


        /**
         * Unique ID number for the word.
         * Only for use in the database table.
         *
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;


        /**
         * The name of word.
         *
         * Type: TEXT
         */
        public static final String COLUMN_WORD_NAME = "word";


        /**
         * The translation of word.
         *
         * Type: TEXT
         */
        public static final String COLUMN_WORD_TRANSLATION = "translation";


        /**
         * The category of word.
         *
         * Type: TEXT
         */
        public static final String COLUMN_WORD_CATEGORY = "category";


        /**
         * The language of word/translation.
         *
         * Type: TEXT
         */
        public static final String COLUMN_WORD_LANGUAGE = "language";


    }

}
