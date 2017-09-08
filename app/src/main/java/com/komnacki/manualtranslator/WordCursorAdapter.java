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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.komnacki.manualtranslator.data.WordDbContract.WordDbEntry;


public class WordCursorAdapter extends CursorAdapter implements Serializable{


    /** To store all selected items positions*/
    List<Integer> listOfSelectedItemsPositions;

    /** To store all items ID is Already checked*/
    Set<Integer> setOfItemsID_isAlreadyChecked;

    public boolean isItemsCheckboxVisible;
    public boolean isUnselectMode;
    public boolean isPressed_SelectOrUnselectAll_Button;
    public boolean isPressed_SingleCheckBox;
    public boolean unselectAllSelected;
    public boolean isDeleteCheckedItems;


    public WordCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        listOfSelectedItemsPositions = new ArrayList<>();
        setOfItemsID_isAlreadyChecked = new HashSet<>();
        isPressed_SelectOrUnselectAll_Button = false;
        isPressed_SingleCheckBox = false;
        isDeleteCheckedItems = false;
        isUnselectMode = true;
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
        setVisibilityOfCheckbox(viewHolder);

        viewHolder.checkBox.setTag(cursor.getPosition());

        handleWhenSelectOrUnselectButtonPressed(cursor, itemID);
        handleWhenSingleCheckBoxPressed(cursor, itemID);


        checkIfClearListOfCheckedItems();
        checkAllPositions_fromSelectedItemPositions(cursor, viewHolder);


        deleteCheckedItems();

    }

    private void deleteCheckedItems() {
    }

    private void handleWhenSelectOrUnselectButtonPressed(Cursor cursor, int itemID) {
        if(isPressed_SelectOrUnselectAll_Button) {
            if (isUnselectMode) {
                unselectAllSelected = true;
            } else {
                setCheckboxItemChecked(cursor, itemID);
            }
        }
    }

    private void handleWhenSingleCheckBoxPressed(Cursor cursor, int itemID) {
        if(isPressed_SingleCheckBox){
            if (!isUnselectMode){
                if(!setOfItemsID_isAlreadyChecked.contains(itemID)){
                    setCheckboxItemChecked(cursor, itemID);
                }
            }
        }
    }

    /**
     * Add item position to list of items going to be checked.
     * Also add id Of item to set of already checked item.
     * @param cursor
     * @param itemID - unique id of item from SQL database
     */
    private void setCheckboxItemChecked(Cursor cursor, int itemID) {
        setOfItemsID_isAlreadyChecked.add(itemID);
        if (!listOfSelectedItemsPositions.contains(cursor.getPosition())) {
            listOfSelectedItemsPositions.add(cursor.getPosition());
        }
    }




    /**
     * Check all ArrayList in order to find if checkBox of this item should be checked
     * and check it (if yes).
     */
    private void checkAllPositions_fromSelectedItemPositions(Cursor cursor, ViewHolder viewHolder) {
        if(listOfSelectedItemsPositions.contains(cursor.getPosition()))
            viewHolder.checkBox.setChecked(true);
        else
            viewHolder.checkBox.setChecked(false);
    }


    /**
     * In the case when CANCEL DELETE button has been pressed or is necessary to clear
     * all checked items.
     * Then clear entire ArrayList with checked checkBox positions and clear Set of already
     * checked items and reset flag of isPressed_events.
     */
    private void checkIfClearListOfCheckedItems() {
        if (unselectAllSelected){
            listOfSelectedItemsPositions.clear();
            setOfItemsID_isAlreadyChecked.clear();
            isPressed_SelectOrUnselectAll_Button=false;
            isPressed_SingleCheckBox=false;
            unselectAllSelected = false;
        }
    }



    private void setVisibilityOfCheckbox(ViewHolder viewHolder) {
        if (isItemsCheckboxVisible)
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        else
            viewHolder.checkBox.setVisibility(View.GONE);
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
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int position = (int) compoundButton.getTag();
                    if(b){
                        if(!listOfSelectedItemsPositions.contains(position))
                            listOfSelectedItemsPositions.add(position);
                    }else{
                        listOfSelectedItemsPositions.remove((Object) position);
                    }
                }
            });

            checkBox.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    isPressed_SingleCheckBox = true;
                    isPressed_SelectOrUnselectAll_Button = false;
                    return false;
                }
            });
        }

    }



}
