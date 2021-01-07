package com.snxun.browser.module.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @ProjectName : LimeBrowser
 * @Author : Yangjw
 * @Time : 2021/1/7
 * @Description :
 */
public class DaoManager {
    private static DaoManager instance = null;
    DataBaseHelper helper;
    private static SQLiteDatabase db;
    private static final String TAG = "DaoManager";

    /**
     * 单例
     *
     * @return
     */
    public static DaoManager getInstance(Context context) {
        if (instance == null) {
            instance = new DaoManager(new DataBaseHelper(context, DatabaseConfig.DATABASE_NAME, null, 1));
        }
        return instance;
    }

    public DaoManager(DataBaseHelper helper) {
        this.helper = helper;
    }

    ///浏览记录相关--------------------------------------------

    /**
     * 插入浏览记录
     */
    public boolean insertHistory(String title, String url) {
        boolean result = false;
        try {
            db = this.helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("url", url);

            result = db.insert(DatabaseConfig.HISTORY_TABLE_NAME, null, values) != -1;
        } catch (SQLException e) {
            Log.e(TAG, "insertHistory: SQLException=" + e);
        } finally {
            db.close();
            return result;
        }
    }

    /**
     * 清空浏览记录
     */
    public void clearHistory() {

    }

    /**
     * 删除一条浏览记录
     */
    public void deleteHistory() {

    }

    /**
     * 查询浏览记录
     */
    public void queryHistory() {

    }


    ///书签相关--------------------------------------------

    /**
     * 删除一条书签
     */
    public void deleteBookMark() {

    }

    /**
     * 新增一条书签
     */
    public void insertBookMark() {

    }

    /**
     * 查询书签
     */
    public void queryBookMark() {

    }

}
