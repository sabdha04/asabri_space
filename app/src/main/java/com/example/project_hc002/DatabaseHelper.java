package com.example.project_hc002;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "medsos.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    public static final String TABLE_POSTS = "posts";

    // Table columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_KTPA = "ktpa";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_IMAGE_URL = "image_url";

    // Create table query
    private static final String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_KTPA + " TEXT, "
            + COLUMN_CONTENT + " TEXT NOT NULL, "
            + COLUMN_CREATED_AT + " DATETIME, "
            + COLUMN_UPDATED_AT + " DATETIME, "
            + COLUMN_IMAGE_URL + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        // Create tables again
        onCreate(db);
    }
}