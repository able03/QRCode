package com.example.qrcode;

import android.content.ContentValues;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper
{
    private Context context;
    private static final String DB_NAME = "QRCode.db";
    private static final int DB_VER = 1;

    private static final String TABLE_NAME = "qrcode",
                                COLUMN_ID = "id",
                                COLUMN_NAME = "name",
                                COLUMN_IMAGE = "image";



    private static final String CREATE_TABLE_QRCODE = "CREATE TABLE "+TABLE_NAME+"(" +
            COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME+" VARCHAR UNIQUE," +
            COLUMN_IMAGE+" BLOB)";
    private static final String DROP_TABLE_QRCODE = "DROP TABLE IF EXISTS "+TABLE_NAME;




    public DBHelper(@Nullable Context context)
    {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_QRCODE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(DROP_TABLE_QRCODE);
        onCreate(db);
    }


    public boolean addData(String name, byte[] img)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_IMAGE, img);

        long rowAffected = db.insert(TABLE_NAME, null, values);

        if(rowAffected != -1)
        {
            return true;
        }
        return false;

    }

    public Cursor findQRByName(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_NAME+" = '"+name+"'", null);
    }

}
