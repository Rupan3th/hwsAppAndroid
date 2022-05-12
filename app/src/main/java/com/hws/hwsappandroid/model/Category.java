package com.hws.hwsappandroid.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class Category implements Parcelable {
    public int sortValue; // ex: 11729
    public String gmtModified; // ex: "2022-04-16T15:27:44"
    public String pkId; // ex: "11729"
    public int level; // ex: 1
    public int isDelete; // ex: 0
    public String categoryCode; // ex: "11729"
    public String gmtCreate; // ex: "2022-04-16T15:27:45"
    public String categoryImg; // ex:  "https://www.baidu.com/img/flexible/logo/pc/result.png"
    public String categoryName; // ex: "鞋靴"
    public String operatorId; // ex: "sys"
    public int parentId; // ex: 0
    public ArrayList<Children_level_1> childrenList;

    public Category(){}

    public Category(Parcel src){
        sortValue = src.readInt();
        gmtModified = src.readString();
        pkId = src.readString();
        level = src.readInt();
        isDelete = src.readInt();
        categoryCode = src.readString();
        gmtCreate = src.readString();
        categoryImg= src.readString();
        categoryName = src.readString();
        operatorId= src.readString();
        parentId= src.readInt();
        childrenList = new ArrayList<>();
        src.readTypedList(childrenList, Children_level_1.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sortValue);
        dest.writeString(gmtModified);
        dest.writeString(pkId);
        dest.writeInt(level);
        dest.writeInt(isDelete);
        dest.writeString(categoryCode);
        dest.writeString(gmtCreate);
        dest.writeString(categoryImg);
        dest.writeString(categoryName);
        dest.writeString(operatorId);
        dest.writeInt(parentId);
        dest.writeTypedList(childrenList);
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };


}



