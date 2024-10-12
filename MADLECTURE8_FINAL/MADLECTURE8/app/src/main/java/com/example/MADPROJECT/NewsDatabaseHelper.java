package com.example.MADPROJECT;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NewsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "news";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_HEADLINE = "headline";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_DATE = "date";

    public NewsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HEADLINE + " TEXT, " +
                COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "+
                COLUMN_LINK + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertNews(String headline, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HEADLINE, headline);
        values.put(COLUMN_LINK, link);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result;
    }

    public Cursor getAllNews() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public Cursor getRecentHeadlines() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " >= datetime('now', '-1 day')";
        return db.rawQuery(query, null);
    }
}
