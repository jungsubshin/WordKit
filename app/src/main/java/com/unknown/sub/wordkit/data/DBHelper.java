package com.unknown.sub.wordkit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.unknown.sub.wordkit.data.DBInfo.DBEntry;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "vocabulary.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void create(SQLiteDatabase db){
        String createTable = "create table if not exists " + DBEntry.TABLE_NAME + " ("
                + DBEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBEntry.COLUMN_WORD_WORD + " text not null UNIQUE, "
                + DBEntry.COLUMN_WORD_WORDMEAN + " text, "
                + DBEntry.COLUMN_WORD_EXAMPLE1 + " text, "
                + DBEntry.COLUMN_WORD_EXAMPLEMEAN1 + " text, "
                + DBEntry.COLUMN_WORD_EXAMPLE2 + " text, "
                + DBEntry.COLUMN_WORD_EXAMPLEMEAN2 + " text, "
                + DBEntry.COLUMN_WORD_ISREVIEW + " integer  DEFAULT 0, "
                + DBEntry.COLUMN_WORD_DELAYTIME + " integer DEFAULT 0, "
                + DBEntry.COLUMN_WORD_POPUPDATE + " text , "
                + DBEntry.COLUMN_WORD_CORRECT + " integer DEFAULT 0);";

        db.execSQL(createTable);
    }


}
