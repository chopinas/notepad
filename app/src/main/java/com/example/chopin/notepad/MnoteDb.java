package com.example.chopin.notepad;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Chopin on 2017/7/10.
 */

public class MnoteDb extends SQLiteOpenHelper {

    public  static  final  String TBALE_NAME_NOTES="notes";
    public  static  final  String TBALE_NAME_MEDIA="media";
    public  static  final  String COLUMN_NAME_ID="_id";
    public  static  final  String COLUMN_NAME_NOTE_NAME="name";
    public  static  final  String COLUMN_NAME_NOTE_CONTENT="content";
    public  static  final  String COLUMN_NAME_NOTE_DATE="date";
    public  static  final  String COLUMN_NAME_MEDIA_PATH="path";
    public  static  final  String COLUMN_NAME_MEDIA_OWNER_NOTE_ID="note_id";

    private static  final  String NOTE_sql="CREATE TABLE "+TBALE_NAME_NOTES+"(" +
                                       COLUMN_NAME_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                       COLUMN_NAME_NOTE_NAME+" TEXT NOT NULL DEFAULT NONE,"+
                                       COLUMN_NAME_NOTE_CONTENT+" TEXT NOT NULL DEFAULT NONE,"+
                                       COLUMN_NAME_NOTE_DATE+" TEXT NOT NULL DEFAULT NONE"+
            ")";

    private static  final  String MEDIA_sql="CREATE TABLE "+TBALE_NAME_MEDIA+"(" +
            COLUMN_NAME_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            COLUMN_NAME_MEDIA_PATH+" TEXT NOT NULL DEFAULT NONE,"+
            COLUMN_NAME_MEDIA_OWNER_NOTE_ID+" INTEGER NOT NULL DEFAULT 0"+
            ")";



    public MnoteDb(Context context) {
        super(context, "notes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(NOTE_sql);
        sqLiteDatabase.execSQL(MEDIA_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
