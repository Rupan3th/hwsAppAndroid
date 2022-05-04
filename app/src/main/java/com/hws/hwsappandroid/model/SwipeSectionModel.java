package com.hws.hwsappandroid.model;

import java.util.ArrayList;

public class SwipeSectionModel {
    private boolean selected;
    private String sectionLabel;
    private ArrayList<SwipeSectionItemModel> itemArrayList;

    public SwipeSectionModel(boolean selected, String sectionLabel, ArrayList<SwipeSectionItemModel> itemArrayList) {
        this.sectionLabel = sectionLabel;
        this.itemArrayList = itemArrayList;
        this.selected = selected;
    }
    public boolean getSelectedState() {return  selected;}
    public void setSelectedState(boolean selected) {
        this.selected = selected;
    }

    public String getSectionLabel() {
        return sectionLabel;
    }

    public ArrayList<SwipeSectionItemModel> getItemArrayList() {
        return itemArrayList;
    }
}