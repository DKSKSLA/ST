package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {// 데이터 베이스 설정

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="WORKLIST.db";

    //데이터베이스 생성자
    public static final String SQL_CREATE_TABLE=
            "CREATE TABLE IF NOT EXISTS "+ TableInfo.TABLE_NAME+" ("+
                    TableInfo.COLUMN_NAME_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    TableInfo.COLUMN_NAME_TIME+" DATETIME,"+
                    TableInfo.COLUMN_NAME_MEMO+" TEXT,"+
                    TableInfo.COLUMN_NAME_TITLE+" TEXT,"+
                    TableInfo.COLUMN_NAME_SPACE+" TEXT)";

    public static final String SQL_CREATE_TABLE2=
            "CREATE TABLE IF NOT EXISTS "+ TableInfo.TABLE_NAME2+" ("+
                    TableInfo.COLUMN_NAME_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    TableInfo.COLUMN_NAME_REPEAT+" INTEGER,"+
                    TableInfo.COLUMN_NAME_TIME+" DATETIME,"+
                    TableInfo.COLUMN_NAME_MEMO+" TEXT,"+
                    TableInfo.COLUMN_NAME_TITLE+" TEXT,"+
                    TableInfo.COLUMN_NAME_SPACE+" TEXT)";

    public static final String SQL_CREATE_TABLE3=
            "CREATE TABLE IF NOT EXISTS "+ TableInfo.TABLE_NAME3+" ("+
                    TableInfo.COLUMN_NAME_NAME+" TEXT PRIMARY KEY,"+
                    TableInfo.COLUMN_NAME_SPACEX+" TEXT,"+
                    TableInfo.COLUMN_NAME_SPACEY+" TEXT)";


    public static final String SQL_DELETE_TABLE1=
            "DROP TABLE IF EXISTS " + TableInfo.TABLE_NAME;
    public static final String SQL_DELETE_TABLE2=
            "DROP TABLE IF EXISTS " + TableInfo.TABLE_NAME2;
    public static final String SQL_DELETE_TABLE3=
            "DROP TABLE IF EXISTS " + TableInfo.TABLE_NAME3;

    public DBHelper(Context context) {
        super(context, "groupDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //생성문 실행
        db.execSQL(SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_TABLE2);
        db.execSQL(SQL_CREATE_TABLE3);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //드랍
        db.execSQL(SQL_DELETE_TABLE1);
        db.execSQL(SQL_DELETE_TABLE2);
        db.execSQL(SQL_DELETE_TABLE3);
        onCreate(db);

    }
}
