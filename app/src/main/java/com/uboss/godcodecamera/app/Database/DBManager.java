package com.uboss.godcodecamera.app.Database;

/**
 * Created by cuiyan on 16/3/3.
 */


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uboss.godcodecamera.AppConstants;
import com.uboss.godcodecamera.base.GodeCode;

//参考：http://blog.csdn.net/liuhe688/article/details/6715983
public class DBManager {
    private static final String LOG_TAG = "DBManager";
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context)
    {
        Log.i(LOG_TAG, "DBManager --> Constructor");
        helper = new DatabaseHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add
     *
     * @param listgodcodes
     */
    public void add(List<GodeCode> listgodcodes)
    {
        Log.i(LOG_TAG, "DBManager --> add");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (GodeCode godcode : listgodcodes)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME
                        + " VALUES(null, ?, ?, ?, ?, ?)", new Object[] { godcode.getFilename(),
                        godcode.getDate(), godcode.getCount(),godcode.getContent(),godcode.getUrl() });
                //id设置为null，自增
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }

    /**
     * 插入一个数据
     * @param godcode
     */
    public void add(GodeCode godcode)
    {
        Log.i(LOG_TAG, "DBManager --> add");
        Log.i(LOG_TAG,"filename=="+godcode.getFilename());
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {

                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME
                        + " VALUES(null, ?, ?, ?, ?, ?)", new Object[] { godcode.getFilename(),
                        godcode.getDate(), godcode.getCount(),godcode.getContent(),godcode.getUrl() });
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }


    /**
     * update person's age
     *
     * @param godcode
     */
//    public void updateAge(GodeCode godcode)
//    {
//        Log.d(LOG_TAG, "DBManager --> updateAge");
//        ContentValues cv = new ContentValues();
//        cv.put("age", godcode.age);
//        db.update(DatabaseHelper.TABLE_NAME, cv, "name = ?",
//                new String[] { person.name });
//    }

    /**
     * delete old person
     *
     * @param person
     */
//    public void deleteOldPerson(Person person)
//    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> deleteOldPerson");
//        db.delete(DatabaseHelper.TABLE_NAME, "age >= ?",
//                new String[] { String.valueOf(person.age) });
//    }

    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public List<GodeCode> query()
    {
        Log.i(LOG_TAG, "DBManager --> query");
        ArrayList<GodeCode> godcodes = new ArrayList<GodeCode>();
        Cursor c = queryTheCursor();
        while (c.moveToNext())
        {
            Log.i(LOG_TAG, "DBManager --> moveToNext()");
            GodeCode godcode = new GodeCode();
            godcode.setFilename( c.getString(c.getColumnIndex("filename")));
            godcode.setDate(c.getString(c.getColumnIndex("date")));
            godcode.setCount(c.getInt(c.getColumnIndex("count")));
            godcode.setContent(c.getString(c.getColumnIndex("content")));
            godcode.setUrl(c.getString(c.getColumnIndex("url")));

            godcodes.add(godcode);

        }
        c.close();
        return godcodes;
    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor()
    {
        Log.i(LOG_TAG, "DBManager --> queryTheCursor");
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME,
                null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB()
    {
        Log.i(LOG_TAG, "DBManager --> closeDB");
        // 释放数据库资源
        db.close();
    }

}
