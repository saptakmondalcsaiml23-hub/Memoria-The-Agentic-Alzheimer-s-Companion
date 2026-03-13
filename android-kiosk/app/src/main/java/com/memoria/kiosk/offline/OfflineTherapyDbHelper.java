package com.memoria.kiosk.offline;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OfflineTherapyDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "memoria_offline.db";
    public static final int DB_VERSION = 1;

    public OfflineTherapyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE therapy_pack (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, instruction TEXT NOT NULL)");
        db.execSQL("INSERT INTO therapy_pack(title, instruction) VALUES " +
                "('Calm Breathing', 'Take 5 gentle breaths while listening to soft music')," +
                "('Memory Spark', 'Name three familiar places from your hometown')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS therapy_pack");
        onCreate(db);
    }
}
