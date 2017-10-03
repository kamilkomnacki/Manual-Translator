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


import android.database.Cursor;

import com.komnacki.manualtranslator.data.WordDbContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WordList
 * This class is Singleton.
 * The function of this class is hold the list of wordModels {@link WordModel} from database in
 * Map<Integer, WordModel> form
 * .
 * The list of wordModels is used in {@link com.komnacki.manualtranslator.data.WordProvider}
 * and {@link WordsCatalogActivity} in order to delete proper data from database.
 */

public class WordList {

    public static final String LOG_TAG = "WORD_LIST";

    /**
     * The list of all {@link WordModel} from database in Map<Integer, WordModel> form.
     * Used to contain data by _ID from database. Items ID don't repeat.
     */
    private Map<Integer, WordModel> listOfWordItems;


    /** Flag to represent if new word in listOfWordItems should be selected or not from beginning.*/
    private boolean isDefaultSelected;


    //----------------------------------------------------------------------------------------------
    //----------------------------SINGLETON CONSTRUCTORS--------------------------------------------
    private WordList(){}


    private static class WordListSingletonHolder{
        private static final WordList INSTANCE = new WordList();
    }

    public static WordList getInstance(){
        return WordListSingletonHolder.INSTANCE;
    }
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------


    public void setDefaultSelectMode(boolean isDefaultSelected) {
        this.isDefaultSelected = isDefaultSelected;
    }

    public int getCount(){
        return listOfWordItems.size();
    }



    public void init(Cursor cursor){
        listOfWordItems = new HashMap<>();
        fillListOfWordItems(cursor);
//        Log.d(LOG_TAG, "size of cursor : " + listOfWordItems.size());
//        Log.d(LOG_TAG, "items in cursor: " + listOfWordItems.keySet());
    }

    private void fillListOfWordItems(Cursor cursor) {
        if(cursor.moveToFirst()) {
            listOfWordItems.clear();
            do {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(WordDbContract.WordDbEntry._ID)));
                if(!listOfWordItems.containsKey(id)){
                    listOfWordItems.put(id, new WordModel(id, isDefaultSelected));
                    //Log.d(LOG_TAG, " Add id to list: " + String.valueOf(id));
                }
            } while(cursor.moveToNext());
        }
    }



    public void selectItemById(int id){
        if(!(listOfWordItems == null)) {
            for (Map.Entry<Integer, WordModel> entry : listOfWordItems.entrySet()) {
                if (entry.getKey().equals(id)) {
                    entry.getValue().setSelected(true);
                }
            }
        }else
        {}//Log.e(LOG_TAG, "Unable to select item: " + id + ". listOfWordItem is null!");
    }

    public void unselectItemById(int id){
        if(!(listOfWordItems == null)) {
            for (Map.Entry<Integer, WordModel> entry : listOfWordItems.entrySet()) {
                if (entry.getKey().equals(id)) {
                    entry.getValue().setSelected(false);
                }
            }
        }else
        {}//Log.e(LOG_TAG, "Unable to unselect item: " + id + ". listOfWordItem is null!");
    }


    

    protected void selectAll(){
        for (Map.Entry<Integer, WordModel> entry : listOfWordItems.entrySet()){
            entry.getValue().setSelected(true);
        }
    }

    protected void unselectAll(){
        for (Map.Entry<Integer, WordModel> entry : listOfWordItems.entrySet()){
            entry.getValue().setSelected(false);
        }
    }




    public List<String> getSelectedItemsID(){
        List<String> selectedItems = new ArrayList<>();
        for (Map.Entry<Integer, WordModel> entry : listOfWordItems.entrySet()){
            if(entry.getValue().isSelected()){
                selectedItems.add(String.valueOf(entry.getKey()));
            }
        }
        return selectedItems;
    }




}
