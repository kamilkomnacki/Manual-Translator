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

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static com.komnacki.manualtranslator.data.WordDbContract.WordDbEntry;


public class WordCursorAdapter extends CursorAdapter implements Serializable{

    int i = 0;

    WordList list = WordList.getInstance();

    /** To store all selected items positions*/
    Set<Integer> setOfSelectedItemsPositions;

    /** To store all items ID is Already checked*/
    Set<Integer> setOfItemsPosition;

    private int checkBoxVisibility;


    public WordCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        setOfSelectedItemsPositions = new HashSet<>();
        setOfItemsPosition = new HashSet<>();
        checkBoxVisibility = View.GONE;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     * @param context
     * @param cursor
     * @param parent
     * @return - newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.words_catalog_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);
        return view;
    }




    /**
     * Bind an existing view to the data pointed to the cursor.
     * @param view - existing view, returned by newView() method.
     * @param context
     * @param cursor - cursor with the data. Cursor is already moved to the correct position.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.wordsCatalog_listItem_word);
        TextView translationTextView = (TextView) view.findViewById(R.id.wordsCatalog_listItem_translate);

        int nameColumnIndex = cursor.getColumnIndex(WordDbEntry.COLUMN_WORD_NAME);
        int translationColumnIndex = cursor.getColumnIndex(WordDbEntry.COLUMN_WORD_TRANSLATION);
        int idColumnIndex = cursor.getColumnIndex(WordDbEntry._ID);

        int itemID = Integer.parseInt(cursor.getString(idColumnIndex));
        String wordName = itemID + " " + cursor.getString(nameColumnIndex);
        String wordTranslation = cursor.getString(translationColumnIndex);

        nameTextView.setText(wordName);
        translationTextView.setText(wordTranslation);








        //-------------------------------CheckBox Service-------------------------------------------
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int position = cursor.getPosition();

        viewHolder.checkBox.setTag(position);

        viewHolder.checkBox.setVisibility(checkBoxVisibility);

        selectItems(viewHolder, position, itemID);

//        if(viewHolder.checkBox.getVisibility() == View.VISIBLE) {
//            if (viewHolder.checkBox.isChecked()) {
//                //Toast.makeText(context, "Selected item: " + itemID, Toast.LENGTH_SHORT).show();
//                list.selectItem(itemID);
//            } else {
//                //Toast.makeText(context, "Unselected item: " + itemID, Toast.LENGTH_SHORT).show();
//                list.unselectItem(itemID);
//            }
//        }

    }

    private void selectItems(ViewHolder view, int position, int id) {
        if(setOfSelectedItemsPositions.contains(position)){
            view.checkBox.setChecked(true);
            list.selectItem(id);
        } else{
            view.checkBox.setChecked(false);
            list.unselectItem(id);
        }

    }

    public void selectAll(){
        Cursor cursor = getCursor();
        int i=0;
        if(cursor.moveToFirst()){
            do{
                setOfSelectedItemsPositions.add(cursor.getPosition());
                list.selectAll();
            }while(cursor.moveToNext());
        }
        Log.d("WORD", "how many times cursor moved? " + i);
        Log.d("WORD", "selectAll: SETofSelectedPositions " + setOfSelectedItemsPositions);
    }

    public void unselectAll(){
        setOfSelectedItemsPositions.clear();
        list.unselectAll();
    }



    public void setCheckboxesVisible(boolean isVisible) {
        checkBoxVisibility = isVisible ? View.VISIBLE : View.GONE;
    }




    /**
     * Class to hold a view of current row that newView() and bindView() method currently work.
     */
    public class ViewHolder{
        CheckBox checkBox;



        public ViewHolder(View view){
            checkBox = (CheckBox) view.findViewById(R.id.chbox_wordsCatalog_listItem);
            setOnCheckedChangeListener();
        }



        private void setOnCheckedChangeListener() {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean selected) {
                    int position = (int) compoundButton.getTag();
                    if(selected){
                        if(!setOfSelectedItemsPositions.contains(position)){
                            setOfSelectedItemsPositions.add(position);
                            Toast.makeText(compoundButton.getContext(), "selectedpositions: " + setOfSelectedItemsPositions, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        setOfSelectedItemsPositions.remove((Object) position);}

                    notifyDataSetChanged();
                }
            });
        }

    }



}
