package com.hws.hwsappandroid.model;

import java.util.ArrayList;

public class LogisticsModel {
    public String consignee;
    public ArrayList<DeliveryResultListModel> deliveryResultList;
    public String phone;
}

/*
    "consignee": "收货人",
    "deliveryResultList": [
      {
        "courier": "string",
        "courierPhone": "string",
        "deliverystatus": "string",
        "expName": "string",
        "expPhone": "string",
        "expSite": "string",
        "goodsInfoVO": [
          {
            "goodsId": "goods ID",
            "goodsName": "商品名称",
            "goodsNum": "商品数量",
            "goodsPic": "商品图片",
            "goodsPrice": "商品价格",
            "goodsSpec": "商品规格",
            "isChargeback": false,
            "orderGoodsId": "string",
            "pkId": "goodsSpec ID",
            "shopId": "店铺ID",
            "shopName": "店铺名称"
          }
        ],
        "issign": "string",
        "list": [
          {
            "status": "string",
            "time": "string"
          }
        ],
        "logo": "string",
        "number": "string",
        "takeTime": "string",
        "type": "string",
        "updateTime": "string"
      }
    ],
    "phone": "收货人电话"

 */