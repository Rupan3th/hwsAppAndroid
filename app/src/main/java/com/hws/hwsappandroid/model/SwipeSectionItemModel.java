package com.hws.hwsappandroid.model;

import android.graphics.Bitmap;

public class SwipeSectionItemModel {
    private boolean selected;
    private Bitmap product_image;
    private String itemLabel;
    private String specification;
    private String price;
    private int amount;

    public SwipeSectionItemModel(boolean selected, Bitmap product_image, String itemLabel, String specification, String price, int amount) {
        this.selected = selected;
        this.product_image = product_image;
        this.itemLabel = itemLabel;
        this.specification = specification;
        this.price = price;
        this.amount = amount;

    }
    public boolean getSelectedState() {return  selected;}
    public void setSelectedState(boolean selected) {
        this.selected = selected;
    }

    public Bitmap getProductImage() {
        return product_image;
    }
    public void setProductImage(Bitmap product_image) {
        this.product_image = product_image;
    }

    public String getItemLabel() {
        return itemLabel;
    }
    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    public String getSpecification() { return specification;   }
    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getPrice() { return price; }
    public void setPrice(String price) {
        this.price = price;
    }

    public int getAmount() { return amount;}
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
