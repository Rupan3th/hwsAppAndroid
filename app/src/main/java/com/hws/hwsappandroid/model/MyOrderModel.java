package com.hws.hwsappandroid.model;

import java.util.ArrayList;

public class MyOrderModel {
    public int isChargeback;     // "0 不可以 1 可以 ",
    public String bizClientId;
    public String bizUserId;
    public ArrayList<GoodOfShoppingCart> myGoodsList;
    public String orderCode;
    public int orderStatus;     // "未支付(0), 代发货(1), 待收货(3)  已完成(5), 已取消(99) ",
    public String orderTime;
    public String shippingFee;
    public String pkId;         //"订单Id ",
    public String shopId;       // "店铺Id",
    public String shopName;     //"店铺名称",
    public String totalMoney;       // "订单的总共价格 "
}
