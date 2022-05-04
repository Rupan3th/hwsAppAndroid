package com.hws.hwsappandroid.model;

import android.graphics.Bitmap;

public class CourseModel {
    private String course_name;
    private Bitmap imgid;
    private String product_info;
    private String price;

    public CourseModel(String course_name, Bitmap imgid, String product_info, String price) {
        this.course_name = course_name;
        this.imgid = imgid;
        this.product_info = product_info;
        this.price = price;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public Bitmap getImgid() {
        return imgid;
    }

    public void setImgid(Bitmap  imgid) {
        this.imgid = imgid;
    }

    public String getProductInfo() {
        return product_info;
    }

    public void setProductInfo(String product_info) { this.product_info = product_info; }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) { this.price = price; }
}

