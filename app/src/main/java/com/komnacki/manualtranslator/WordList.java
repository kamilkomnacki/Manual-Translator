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
    private boolean isDefaultSelected;


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
        Log.d(LOG_TAG, "items in cursor: " + listOfWordItems.keySet());
    }


    public void setDefaultSelectMode(boolean isDefaultSelected) {
        this.isDefaultSelected = isDefaultSelected;
    }

    public void selectItem(int id){
        if(!(listOfWordItems == null)) {
            Log.d(LOG_TAG, "sellectItem/ID: " + id);
            for (Map.Entry<Integer, WordModel> entry : listOfWordItems.entrySet()) {
                if (entry.getKey().equals(id)) {
                    entry.getValue().setSelected(true);
                }
            }
        }else
            Log.e(LOG_TAG, "Unable to select item: " + id + ". listOfWordItem is null!");
    }

    public void unselectItem(int id){
        if(!(listOfWordItems == null)) {
            for (Map.Entry<Integer, WordModel> entry : listOfWordItems.entrySet()) {
                if (entry.getKey().equals(id)) {
                    entry.getValue().setSelected(false);
                }
            }
        }else
            Log.e(LOG_TAG, "Unable to unselect item: " + id + ". listOfWordItem is null!");
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

    public void refreshListOfWordItems(Cursor cursor){
        fillListOfWordItems(cursor);
        Log.d(LOG_TAG, "Size of list: " + listOfWordItems.size());
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

    public int getCount(){
        return listOfWordItems.size();
    }


}
