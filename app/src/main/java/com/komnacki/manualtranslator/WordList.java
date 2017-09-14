package com.komnacki.manualtranslator;


import android.database.Cursor;
import android.util.Log;

import com.komnacki.manualtranslator.data.WordDbContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordList {

    public static final String LOG_TAG = "WORD_LIST";
    private Map<Integer, WordModel> listOfWordItems;
    private boolean isDefaultSelectedItem;


    private WordList(){}

    private static class WordListSingletonHolder{
        private static final WordList INSTANCE = new WordList();
    }

    public static WordList getInstance(){
        return WordListSingletonHolder.INSTANCE;
    }


    public void init(Cursor cursor){
        listOfWordItems = new HashMap<>();

        if(cursor.moveToFirst()){}
            fillListOfWordItems(cursor);

        Log.d(LOG_TAG, "size of cursor : " + listOfWordItems.size());
        Log.d(LOG_TAG, "items in cursor: " + listOfWordItems);
    }


    public void setDefaultSelectedItem(boolean defaultSelectedItem) {
        isDefaultSelectedItem = defaultSelectedItem;
    }



    private void fillListOfWordItems(Cursor cursor) {
        if(cursor.moveToFirst()) {
            listOfWordItems.clear();
            do {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(WordDbContract.WordDbEntry._ID)));
                if(!listOfWordItems.containsKey(id)){
                    listOfWordItems.put(id, new WordModel(id, isDefaultSelectedItem));
                    Log.d(LOG_TAG, " Add id to list: " + String.valueOf(id));
                }
            } while(cursor.moveToNext());
        }
    }

    public void refreshListOfWordItems(Cursor cursor){
        fillListOfWordItems(cursor);
        Log.d(LOG_TAG, "Size of list: " + listOfWordItems.size());
    }

    public void selectAll(){
        for (Map.Entry<Integer, WordModel> entry : listOfWordItems.entrySet()){
            entry.getValue().setSelected(true);
        }
    }

    public List<Integer> getSelectedItemsID(){
        List<Integer> selectedItems = new ArrayList<>();
        for (Map.Entry<Integer, WordModel> entry : listOfWordItems.entrySet()){
            if(entry.getValue().isSelected()){
                selectedItems.add(entry.getKey());
            }
        }
        return selectedItems;
    }


}
