package com.uboss.godcodecamera.app.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.uboss.godcodecamera.AppConstants;

/**
 * Created by cuiyan on 16/3/3.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = AppConstants.DB_MyGodCode; //数据库名称
    private static final int version = AppConstants.DB_MyGodCode_Version; //数据库版本
    public static final String TABLE_NAME = "godcode";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String sql = "create table godcode(username varchar(20) not null , password varchar(60) not null );";
        db.execSQL("CREATE TABLE IF NOT EXISTS godcode" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, filename VARCHAR, date VARCHAR, count INTEGER, content TEXT, url VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("ALTER TABLE godcode ADD COLUMN other STRING");
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

}
