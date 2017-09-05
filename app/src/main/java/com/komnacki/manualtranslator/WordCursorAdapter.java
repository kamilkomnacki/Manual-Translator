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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.komnacki.manualtranslator.data.WordDbContract.WordDbEntry;


public class WordCursorAdapter extends CursorAdapter implements Serializable{

    /** To store all selected items positions*/
    List<Integer> selectedItemsPositions;

    public boolean isAllItemsCheckBoxVisible;
    public boolean isSelectMode;
    public boolean isPressed_SelectOrUnselectAll_Button;
    public boolean cancelAllSelected;


    public WordCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        selectedItemsPositions = new ArrayList<>();
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
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        setVisibilityOfCheckBoxes(viewHolder);


        viewHolder.checkBox.setTag(cursor.getPosition());
//
//        //setVisibilityOfAllCheckboxes()
//        if (isAllItemsCheckBoxVisible)
//            checkBox.setVisibility(View.VISIBLE);
//        else
//            checkBox.setVisibility(View.GONE);

        //setCheckboxSelectedOrUnselected()
//        if(isSelectMode)
//            checkBox.setChecked(true);
//        else
//            checkBox.setChecked(false);
        selectOrUnselectAllItems_ifSelectOrUnselectPressed(cursor);
        clearListOfCheckedItems_ifCancelButtonPressed();

        checkAllPositions_fromSelectedItemPositions(cursor, viewHolder);



        TextView nameTextView = (TextView) view.findViewById(R.id.wordsCatalog_listItem_word);
        TextView translationTextView = (TextView) view.findViewById(R.id.wordsCatalog_listItem_translate);

        int nameColumnIndex = cursor.getColumnIndex(WordDbEntry.COLUMN_WORD_NAME);
        int translationColumnIndex = cursor.getColumnIndex(WordDbEntry.COLUMN_WORD_TRANSLATION);
        int idColumnIndex = cursor.getColumnIndex(WordDbEntry._ID);

        String wordName = cursor.getString(nameColumnIndex);
        String wordTranslation = cursor.getString(translationColumnIndex);

        nameTextView.setText(wordName);
        translationTextView.setText(wordTranslation);

    }

    private void selectOrUnselectAllItems_ifSelectOrUnselectPressed(Cursor cursor) {
        if(isPressed_SelectOrUnselectAll_Button){
            if(isSelectMode){
                selectedItemsPositions.clear();
            }else{
                if(!selectedItemsPositions.contains(cursor.getPosition()))
                    selectedItemsPositions.add(cursor.getPosition());
            }
        }
    }


    /**
     * Check all ArrayList in order to find if checkBox of this item should be checked
     * and check it (if yes).
     */
    private void checkAllPositions_fromSelectedItemPositions(Cursor cursor, ViewHolder viewHolder) {
        if(selectedItemsPositions.contains(cursor.getPosition()))
            viewHolder.checkBox.setChecked(true);
        else
            viewHolder.checkBox.setChecked(false);
    }


    /**
     * In the case when CANCEL DELETE button has been pressed.
     * Then clear all ArrayList with checked checkBox positions.
     */
    private void clearListOfCheckedItems_ifCancelButtonPressed() {
        if (cancelAllSelected){
            selectedItemsPositions.clear();
        }
    }



    private void setVisibilityOfCheckBoxes(ViewHolder viewHolder) {
        if (isAllItemsCheckBoxVisible)
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        else
            viewHolder.checkBox.setVisibility(View.GONE);
    }



    public class ViewHolder{
        CheckBox checkBox;


        public ViewHolder(View view){
            checkBox = (CheckBox) view.findViewById(R.id.chbox_wordsCatalog_listItem);
            setOnCheckedChangeListener();
        }

        private void setOnCheckedChangeListener() {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int position = (int) compoundButton.getTag();
                    if(b){
                        if(!selectedItemsPositions.contains(position))
                            selectedItemsPositions.add(position);
                    }else{
                        selectedItemsPositions.remove((Object) position);
                    }
                }
            });
        }

    }



}
