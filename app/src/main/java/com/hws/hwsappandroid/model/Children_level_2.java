package com.hws.hwsappandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Children_level_2 implements Parcelable {
    public int sortValue; // ex: 12066
    public String gmtModified; // ex: "2022-04-16T15:27:44"
    public String pkId; // ex: "12066"
    public int level; // ex: 3
    public int isDelete; // ex: 0
    public String categoryCode; // ex: "12066"
    public String gmtCreate; // ex: "2022-04-16T15:27:45"
    public String categoryImg; // ex:  "https://www.baidu.com/img/flexible/logo/pc/result.png"
    public String categoryName; // ex: "增高鞋"
    public String operatorId; // ex: "sys"
    public int parentId; // ex: 11730

    public Children_level_2(){}

    public Children_level_2(Parcel src){
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
    }

    public static final Creator<Children_level_2> CREATOR = new Creator<Children_level_2>() {
        @Override
        public Children_level_2 createFromParcel(Parcel in) {
            return new Children_level_2(in);
        }

        @Override
        public Children_level_2[] newArray(int size) {
            return new Children_level_2[size];
        }
    };
}