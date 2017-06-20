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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class WordsCatalogActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_catalog);

        ArrayList<Word> exampleListOfWords = new ArrayList<>();
        exampleListOfWords.add(new Word("fragile", "kruchy"));
        exampleListOfWords.add(new Word("emphatic", "dobitny"));
        exampleListOfWords.add(new Word("setbacks", "niepowodzenia"));
        exampleListOfWords.add(new Word("underlying", "zasadniczy"));
        exampleListOfWords.add(new Word("assume", "przyjąć"));
        exampleListOfWords.add(new Word("discrepancy", "rozbieżność"));
        exampleListOfWords.add(new Word("entrench", "obwarować"));
        exampleListOfWords.add(new Word("outrageous", "oburzający"));
        exampleListOfWords.add(new Word("condemned", "potępiony"));
        exampleListOfWords.add(new Word("scrutiny", "przestudiowanie"));
        exampleListOfWords.add(new Word("unspoilt", "nieskażony"));
        exampleListOfWords.add(new Word("gorgeus", "wspaniały"));
        exampleListOfWords.add(new Word("reluctant", "niechętny"));

        final String[] list2 = {"Kamil", "Komnacki", "Legionowo", "Polska", "Mazowieckie", "Europa", "Ziemia", "uniwersytet", "Informatyka", "Projekt", "Praca"};


        ListView list = (ListView) findViewById(R.id.listOfWords);


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list2);
        list.setAdapter(adapter);


    }

}
