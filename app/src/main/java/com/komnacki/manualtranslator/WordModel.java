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


public class WordModel {
    private int id;
    private String word;
    private String translation;
    private boolean isSelected;
    private boolean hasPhoto;

    public WordModel(int id, boolean isSelected){
        this.isSelected = isSelected;
        this.id = id;
    }

    public WordModel(int id, String word, String translation){
        this.id = id;
        this.word = word;
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }


    public String getTranslation() {return translation;}


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected){
        this.isSelected = isSelected;
    }

    public boolean isHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }
}
