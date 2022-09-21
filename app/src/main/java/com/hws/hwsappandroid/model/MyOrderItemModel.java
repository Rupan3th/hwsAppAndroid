package com.hws.hwsappandroid.model;

import java.util.ArrayList;

public class MyOrderItemModel {
    public String product_image;
    public String product_info;
    public String price;
    public String product_option;
    public int amount;

    public MyOrderItemModel(String product_image, String product_info, String price, String product_option, int amount) {
        this.product_image = product_image;
        this.product_info = product_info;
        this.price = price;
        this.product_option = product_option;
        this.amount = amount;
    }

    public String get_product_image() {
        return product_image;
    }
    public void set_product_image(String product_image) {
        this.product_image = product_image;
    }

    public String get_product_info() {
        return product_info;
    }
    public void set_product_info(String product_info) {
        this.product_info = product_info;
    }

    public String get_price() {
        return price;
    }
    public void set_price(String price) {
        this.price = price;
    }

    public String get_product_option() {
        return product_option;
    }
    public void set_product_option(String product_option) {
        this.product_option = product_option;
    }

    public int get_amount() {
        return amount;
    }
    public void set_amount(int amount) {
        this.amount = amount;
    }
}
