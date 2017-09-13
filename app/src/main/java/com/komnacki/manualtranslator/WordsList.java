package com.komnacki.manualtranslator;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.komnacki.manualtranslator.data.WordDbContract;

import java.util.HashMap;
import java.util.Map;

public class WordsList {

    public static final String LOG_TAG = "WORD_LIST";
    private Map<Integer, WordModel> listOfWordItems;
    private boolean isDefaultSelectedItem;



    public WordsList(Context context, Cursor cursor) {
        listOfWordItems = new HashMap<>();
        int i=0;
        fillListOfWordItems(cursor);

        Toast.makeText(context ,"The cursor has: " + listOfWordItems.size() + " items: " + listOfWordItems , Toast.LENGTH_LONG).show();
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


}
