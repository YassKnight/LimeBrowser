package com.snxun.browser.module.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * @ProjectName : LimeBrowser
 * @Author : Yangjw
 * @Time : 2021/1/7
 * @Description :
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    //书签表
    public static final String CREATE_BOOKMARKDB = "create table " + DatabaseConfig.BOOKMARK_TABLE_NAME + "(" +
            "id integer primary key autoincrement," +
            "title text ," +
            "url text)";

    //历史记录表
    public static final String CREATE_HISTORYDB = "create table " + DatabaseConfig.HISTORY_TABLE_NAME + "(" +
            "id integer primary key autoincrement," +
            "title text," +
            "url text)";

    private Context mContext;


    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HISTORYDB);
        db.execSQL(CREATE_BOOKMARKDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //更新表
        db.execSQL("drop table if exists " + DatabaseConfig.BOOKMARK_TABLE_NAME);
        db.execSQL("drop table if exists " + DatabaseConfig.HISTORY_TABLE_NAME);
        onCreate(db);
    }
}
