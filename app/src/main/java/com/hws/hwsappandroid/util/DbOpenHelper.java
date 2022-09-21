package com.hws.hwsappandroid.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.ResultSet;

public class DbOpenHelper {
    private static final String DATABASE_NAME = "HwsAppDB";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DataBases.CreateDB._CREATE0);
            db.execSQL(DataBases.ChattingHistory._CREATE_chatHistory);
            db.execSQL(DataBases.SearchHistory._CREATE_searchHistory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    // Insert DB
    public long insertColumn(String partner_id, String name, String pic, String message, long time, long num ){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.partner_id, partner_id);
        values.put(DataBases.CreateDB.partner_name, name);
        values.put(DataBases.CreateDB.partner_pic, pic);
        values.put(DataBases.CreateDB.message, message);
        values.put(DataBases.CreateDB.receive_time, time);
        values.put(DataBases.CreateDB.msg_num, num);
        return mDB.insert(DataBases.CreateDB._TABLENAME0, null, values);
    }

    public long insertChatHistory(String Avatar, String From, String To, String SoR, String MSG, String time, String Reading ){
        ContentValues values = new ContentValues();
        values.put(DataBases.ChattingHistory.COLUMN_Avatar, Avatar);
        values.put(DataBases.ChattingHistory.COLUMN_From, From);
        values.put(DataBases.ChattingHistory.COLUMN_To, To);
        values.put(DataBases.ChattingHistory.COLUMN_SoR, SoR);
        values.put(DataBases.ChattingHistory.COLUMN_MSG, MSG);
        values.put(DataBases.ChattingHistory.COLUMN_Time, time);
        values.put(DataBases.ChattingHistory.COLUMN_Reading, Reading);

        return mDB.insert(DataBases.ChattingHistory.TABLE_NAME, null, values);
    }

    public long insertSearchHistory(String keyword){
        ContentValues values = new ContentValues();
        values.put(DataBases.SearchHistory.COLUMN_Key, keyword);

        return mDB.insert(DataBases.SearchHistory.TABLE_NAME, null, values);
    }

    // Update DB
    public boolean updateColumn(String partner_id, String name, String pic, String message, long time, long num){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.partner_id, partner_id);
        if (name != null)
            values.put(DataBases.CreateDB.partner_name, name);
        if (pic != null)
            values.put(DataBases.CreateDB.partner_pic, pic);
        if (message != null)
            values.put(DataBases.CreateDB.message, message);
        if (time > 0)
            values.put(DataBases.CreateDB.receive_time, time);
        if (num >= 0)
            values.put(DataBases.CreateDB.msg_num, num);
        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "partner_id = ?", new String[]{partner_id}) > 0;
    }

    //Update chat history
    public boolean updateChatHistory(String From, String Avatar, String To, String SoR, String MSG, String time, String Reading){
        ContentValues values = new ContentValues();
        values.put(DataBases.ChattingHistory.COLUMN_From, From);
        if (Avatar != null)
            values.put(DataBases.ChattingHistory.COLUMN_Avatar, Avatar);
        if (To != null)
            values.put(DataBases.ChattingHistory.COLUMN_To, To);
        if (SoR != null)
            values.put(DataBases.ChattingHistory.COLUMN_SoR, SoR);
        if (MSG != null)
            values.put(DataBases.ChattingHistory.COLUMN_MSG, MSG);
        if (time != null)
            values.put(DataBases.ChattingHistory.COLUMN_Time, time);
        if (Reading != null)
        values.put(DataBases.ChattingHistory.COLUMN_Reading, Reading);

        return mDB.update(DataBases.ChattingHistory.TABLE_NAME, values, "from_id = ?", new String[]{From}) > 0;
    }

    // Delete All
    public void deleteAllColumns() {
        mDB.delete(DataBases.CreateDB._TABLENAME0, null, null);
    }

    public void deleteAllChatHistory() {
        mDB.delete(DataBases.ChattingHistory.TABLE_NAME, null, null);
    }

    public void deleteAllSearchHistory() { mDB.delete(DataBases.SearchHistory.TABLE_NAME, null, null);  }

    // Delete DB
    public boolean deleteColumn(long id){
        return mDB.delete(DataBases.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }

    public boolean deleteColumnSearchHistory(String keyword){
        return mDB.delete(DataBases.SearchHistory.TABLE_NAME, "keyword='"+keyword +"'", null) > 0;
    }

    // Select DB
    public Cursor selectColumns(){
        return mDB.query(DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    public Cursor selectColumn(String partner_id){
        Cursor c = mDB.rawQuery( "SELECT * FROM usertable Where partner_id='" + partner_id + "';", null);
        return c;
    }

    // sort by column
    public Cursor sortColumn(String sort){
        Cursor c = mDB.rawQuery( "SELECT * FROM usertable ORDER BY " + sort + " DESC;", null);
        return c;
    }

    public Cursor sortColumnWording(String sort, String word){
        Cursor c = mDB.rawQuery( "SELECT * FROM usertable Where name LIKE '"+"%"+word+"%"+"' ORDER BY " + sort + ";", null);
        return c;
    }

    public Cursor getChatHistory(String userId){
        Cursor c = mDB.rawQuery( "SELECT * FROM chat_history Where from_id='"+userId+"' Or to_id='"+userId+"' ORDER BY time DESC;", null);
        return c;
    }

    public Cursor getSearchHistory(){
        Cursor c = mDB.rawQuery( "SELECT * FROM search_history ;", null);
        return c;
    }

    public int getUnreadMessageCount () {
        Cursor c = mDB.rawQuery( "SELECT * FROM chat_history Where SoR='2' And reading='0';", null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public int getUnreadMsgPerShop (String partnerId) {
        Cursor c = mDB.rawQuery( "SELECT * FROM chat_history Where SoR='2' And reading='0' And from_id='"+ partnerId +"';", null);
        int count = c.getCount();
        c.close();
        return count;
    }
}
