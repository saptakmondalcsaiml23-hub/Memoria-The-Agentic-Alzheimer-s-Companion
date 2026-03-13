package com.memoria.kiosk.offline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OfflineTherapyRepository {

    private final OfflineTherapyDbHelper dbHelper;

    public OfflineTherapyRepository(Context context) {
        this.dbHelper = new OfflineTherapyDbHelper(context);
    }

    public String getFirstTherapy() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT title, instruction FROM therapy_pack ORDER BY id LIMIT 1", null)) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0) + " - " + cursor.getString(1);
            }
            return null;
        }
    }
}
