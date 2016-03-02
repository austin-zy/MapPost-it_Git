package com.downloader.austin.mappost_it;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Austin on 30/9/2015.
 */
public class PostsDataSource {

    private MySQLiteHelper dbHelper;

    private String[] POSTS_TABLE_COLUMNS = {
            MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_LONGITUDE,
            MySQLiteHelper.COLUMN_CONTENT,MySQLiteHelper.COLUMN_ENABLED
    };
    private SQLiteDatabase database;

    public PostsDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException{
        database =dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Posts addPost(String message, double longitude, double latitude, Context context){
        SQLiteDatabase db= new MySQLiteHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
        values.put(MySQLiteHelper.COLUMN_LONGITUDE,longitude);
        values.put(MySQLiteHelper.COLUMN_CONTENT, message);

        long id = database.insert(MySQLiteHelper.TABLE_POSTS, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_POSTS,
                POSTS_TABLE_COLUMNS,MySQLiteHelper.COLUMN_ID + " = " + id
        ,null,null,null,null);

        cursor.moveToFirst();

        Posts newPost = parsePost(cursor);
        cursor.close();

        return newPost;
    }

    public int deletePost(Context context,String message){
        SQLiteDatabase db = new MySQLiteHelper(context).getWritableDatabase();
        return db.delete(MySQLiteHelper.TABLE_POSTS,MySQLiteHelper.COLUMN_CONTENT+"='"+message+"'",null);
    }

    public int editPost(Context context, String message, double longitude, double latitude){
        SQLiteDatabase db = new MySQLiteHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
        values.put(MySQLiteHelper.COLUMN_LONGITUDE,longitude);
        values.put(MySQLiteHelper.COLUMN_CONTENT, message);
        return db.update(MySQLiteHelper.TABLE_POSTS,values,MySQLiteHelper.COLUMN_CONTENT+"='"+message+"'",null);
    }

    public List getAllPosts(Context context){
        SQLiteDatabase db= new MySQLiteHelper(context).getReadableDatabase();
        List<Posts> posts = new ArrayList<Posts>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_POSTS, POSTS_TABLE_COLUMNS,
                null, null, null, null, null);
        if (cursor.getCount()>=1) {
            while (cursor.moveToNext()) {
                Posts post = parsePost(cursor);
                posts.add(post);
            }
        }
        cursor.close();
        return posts;
    }

    private Posts parsePost(Cursor cursor){
        Posts post = new Posts();
        post.setId(cursor.getLong(0));
        post.setLatitude(cursor.getDouble(1));
        post.setLongitude(cursor.getDouble(2));
        post.setContent(cursor.getString(3));
        return post;

    }
}
