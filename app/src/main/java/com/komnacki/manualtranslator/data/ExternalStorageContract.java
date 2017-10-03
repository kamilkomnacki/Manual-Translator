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


/**
 * Contract class for external storage.
 * This class is a containter for constants that define names for directories, permissions.
 * This class allow using the same constants across the other classes.
 */
public final class ExternalStorageContract {

    /**
     * To prevent someone from instantiating the contract class,
     * Set constructor as private.
     */
    private ExternalStorageContract() {}


    /**
     * Name used as directory name for pictures created in default system localisation.
     * In this directory ManualTranslator will save all photos made by user.
     */
    public static final String DIRECTORY_PICTURES_NAME = "ManualTranslator";


    /** Identifier for the permission to write in external storage*/
    public static final int REQUEST_PERMISSION_WRITE = 1001;


    /** Identifier for send data through intent */
    public static final String EXTERNAL_STORAGE_STATE = "EXTERNAL STORAGE STATE";

}
