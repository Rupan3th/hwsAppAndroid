package com.hws.hwsappandroid.util;

import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.model.OrderInfoVO;
import com.hws.hwsappandroid.model.UserCartItem;

import java.util.ArrayList;

public class MyGlobals {
    public int select_CategoryIdx = 0;
    public String select_CategoryName = "";
    public ArrayList<UserCartItem> myShoppingCart;
    public float total_price = 0.0f;
    public int total_num = 0;
    public int notify_cart = 0;
    public OrderInfoVO orderInfoVO;

    ///select_CategoryIdx
    public int getSelect_CategoryIdx(){
        return this.select_CategoryIdx;
    }

    public void setSelect_CategoryIdx(int select_CategoryIdx){
        this.select_CategoryIdx = select_CategoryIdx;
    }

    ///select_CategoryName
    public String getSelect_CategoryName(){
        return this.select_CategoryName;
    }

    public void setSelect_CategoryName(String select_CategoryName){
        this.select_CategoryName = select_CategoryName;
    }


    /// UserCartItem
    public ArrayList<UserCartItem> getMyShoppingCart(){
        return this.myShoppingCart;
    }

    public void setMyShoppingCart(ArrayList<UserCartItem> myShoppingCart){
        this.myShoppingCart = myShoppingCart;
    }

    public void setMySelected(ArrayList<GoodOfShoppingCart> mySelected, int index){
        this.myShoppingCart.get(index).goods = mySelected;
    }

    public void delMyShoppingCartItem(int sectionPos, int itemPos){
        try{    this.myShoppingCart.get(sectionPos).goods.remove(itemPos);  }
        catch (Exception e){}
    }

    public void delMyShoppingCartSection(int sectionPoss){
        try{    this.myShoppingCart.remove(sectionPoss);  }
        catch (Exception e){}
    }

    /// total_price
    public float getTotal_price(){
        return this.total_price;
    }

    public void addTotal_price(float item_price){
        this.total_price = this.total_price + item_price;
    }
    public void minusTotal_price(float item_price){
        this.total_price = this.total_price - item_price;
    }

    public void set_Total_price(float total_price){
        this.total_price = total_price;
    }

    public void set_format_Total_price(){
        this.total_price = 0.0f;
    }

    /// total_num
    public int getTotal_num(){
        return this.total_num;
    }

    public void addTotal_num(int item_num){
        this.total_num = this.total_num + item_num;
    }
    public void minusTotal_num(int item_num){
        this.total_num = this.total_num - item_num;
    }

    public void set_Total_num(int total_num){
        this.total_num = total_num;
    }

    public void set_format_Total_num(){
        this.total_num = 0;
    }

    /// notify_cart
    public int getNotify_cart(){
        return this.notify_cart;
    }

    public void setNotify_cart(int notify_cart){
        this.notify_cart = notify_cart;
    }

    public void set_formatNotify_cart(){
        this.notify_cart = 0;
    }


    private static MyGlobals instance = null;

    public static synchronized MyGlobals getInstance(){
        if(null == instance) {
            instance = new MyGlobals();
        }
        return instance;
    }

    ///// OrderInfoVO
    public OrderInfoVO getOrderInfoVO(){    return this.orderInfoVO;    }

    public void setOrderInfoVO(OrderInfoVO orderInfoVO){
        this.orderInfoVO = orderInfoVO;
    }

}
