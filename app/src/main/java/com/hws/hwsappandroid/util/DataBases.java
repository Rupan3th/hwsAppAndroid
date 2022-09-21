package com.hws.hwsappandroid.util;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns {
        public static final String partner_id = "partner_id";
        public static final String partner_name = "name";
        public static final String partner_pic = "pic";
        public static final String message = "message";
        public static final String receive_time = "time";
        public static final String msg_num = "num";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +partner_id+" text not null , "
                +partner_name+" text not null , "
                +partner_pic+" text not null , "
                +message+" text not null , "
                +receive_time+" integer not null , "
                +msg_num+" integer not null );";
        public static final String _DELETE0 =
                "DROP TABLE IF EXISTS " + _TABLENAME0;
    }

    public static final class ChattingHistory implements BaseColumns {
        public static final String TABLE_NAME = "chat_history";
        public static final String COLUMN_Avatar = "avatar_img";
        public static final String COLUMN_From = "from_id";
        public static final String COLUMN_To = "to_id";
        public static final String COLUMN_SoR = "SoR";
        public static final String COLUMN_MSG = "msg";
        public static final String COLUMN_Time = "time";
        public static final String COLUMN_Reading = "reading";
        public static final String _CREATE_chatHistory = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                +_ID + " INTEGER PRIMARY KEY,"
                +COLUMN_Avatar+" text not null , "
                +COLUMN_From+" text not null , "
                +COLUMN_To+" text not null , "
                +COLUMN_SoR+" text not null , "
                +COLUMN_MSG+" text not null , "
                +COLUMN_Time+" text not null , "
                +COLUMN_Reading+" integer not null );";

        public static final String _DELETE_chatHistory =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static final class SearchHistory implements BaseColumns {
        public static final String TABLE_NAME = "search_history";
        public static final String COLUMN_Key = "keyword";

        public static final String _CREATE_searchHistory = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                +_ID + " INTEGER PRIMARY KEY,"
                +COLUMN_Key+" integer not null );";

        public static final String _DELETE_searchHistory =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}

