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
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.komnacki.manualtranslator.data.WordDbContract.WordDbEntry;


public class WordCursorAdapter extends CursorAdapter {

    public WordCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.words_catalog_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.wordsCatalog_listItem_word);
        TextView translationTextView = (TextView) view.findViewById(R.id.wordsCatalog_listItem_translate);

        int nameColumnIndex = cursor.getColumnIndex(WordDbEntry.COLUMN_WORD_NAME);
        int translationColumnIndex = cursor.getColumnIndex(WordDbEntry.COLUMN_WORD_TRANSLATION);
        int idColumnIndex = cursor.getColumnIndex(WordDbEntry._ID);

        String wordName = cursor.getString(nameColumnIndex) + cursor.getString(idColumnIndex);
        String wordTranslation = cursor.getString(translationColumnIndex);



        nameTextView.setText(wordName);
        translationTextView.setText(wordTranslation);

    }
}
