package com.hws.hwsappandroid.model;

import java.util.ArrayList;

public class OrderDetailModel {
    public String address;
    public String bizUserId;
    public String cancelTime;
    public String consignee;
    public ArrayList<OrderGoods> goodsInfoList;
    public String orderCode;
    public int orderStatus;     // "未支付(0), 代发货(1), 待收货(3)  已完成(5), 已取消(99) ",
    public int orderSurplusTime;
    public String orderTime;
    public String payTime;
    public int payType;
    public String phone;
    public String pkId;         //"订单Id ",
    public String shippingFee;
    public String shopId;       // "店铺Id",
    public String shopName;     //"店铺名称",
    public String totalMoney;       // "订单的总共价格 "
}
