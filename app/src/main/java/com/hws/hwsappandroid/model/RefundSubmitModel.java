package com.hws.hwsappandroid.model;

import java.util.ArrayList;

public class RefundSubmitModel {
    public String detailDescription;
    public ArrayList<String> files;
    public String goodsId;
    public String goodsSpecId;
    public String orderId;
    public String phone;
    public int receivingStatus;
    public String refundMoney;
    public int refundReason;
    public int refundType;

}

/*
"detailDescription": "退货详情描述",
        "files": "退货图片",
        "goodsId": "退货商品ID",
        "goodsSpecId": "退货商品规格ID",
        "orderId": "订单ID",
        "phone": "手机号",
        "receivingStatus": "收货状态（0：未收货 1：已收货）",
        "refundMoney": "退款金额",
        "refundReason": "申请原因 1:拍错/不喜欢/效果不好 2: 材质与商品描叙不符 3: 大小尺寸与商品描叙不符 4: 卖家发错货 5: 假冒品牌 6: 收到商品少件/破损或污渍 7: 做工粗糙/有瑕疵 8: 生产日期/保质期描叙不符 9: 颜色/款式/描叙不符）'",
        "refundType": "退货类型 0 退货退款, 1 仅退款"
        }

 */