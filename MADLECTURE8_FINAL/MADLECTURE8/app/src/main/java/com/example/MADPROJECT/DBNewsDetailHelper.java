package com.example.MADPROJECT;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBNewsDetailHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DBnewsdetail.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "news";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_HEADLINE = "headline";
    private static final String COLUMN_PUB_DATE = "pubDate";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_LINK = "link";

    public DBNewsDetailHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HEADLINE + " TEXT,"
                + COLUMN_PUB_DATE + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_LINK + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addNews(String headline, String pubDate, String content, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HEADLINE, headline);
        values.put(COLUMN_PUB_DATE, pubDate);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_LINK, link);

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Cursor getAllNews() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
