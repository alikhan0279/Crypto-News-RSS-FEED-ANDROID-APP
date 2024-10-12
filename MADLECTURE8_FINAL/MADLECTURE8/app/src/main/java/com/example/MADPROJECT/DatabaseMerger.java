package com.example.MADPROJECT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseMerger {

    private static final String TAG = "DatabaseMerger";

    public static void mergeDatabases(Context context) {
        DBHelper db1Helper = new DBHelper(context);
        NewsDatabaseHelper db2Helper = new NewsDatabaseHelper(context);

        SQLiteDatabase db1 = db1Helper.getWritableDatabase();
        SQLiteDatabase db2 = db2Helper.getReadableDatabase();

        db1.beginTransaction();
        try {
            Cursor cursor = db2.query(NewsDatabaseHelper.TABLE_NAME, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(NewsDatabaseHelper.TABLE_NAME));
                    db1.execSQL("INSERT OR IGNORE INTO " + DBHelper.TABLE_NAME + " (" +
                            DBHelper.TABLE_NAME + ") VALUES (?);", new Object[]{name});
                } while (cursor.moveToNext());
            }

            cursor.close();
            db1.setTransactionSuccessful();
            Log.d(TAG, "Database merged successfully");
        } finally {
            db1.endTransaction();
        }

        db1.close();
        db2.close();
    }
}
