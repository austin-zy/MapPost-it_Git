package com.downloader.austin.mappost_it;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Austin on 30/9/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_POSTS = "Message";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_ENABLED = "enabled";

    public static final String DATABASE_NAME = "post.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_POSTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_LONGITUDE
            + " double not null, " + COLUMN_LATITUDE
            + " double not null, " + COLUMN_ENABLED
            + " boolean not null default 1, "+ COLUMN_CONTENT
            + " text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        onCreate(db);
    }

}

