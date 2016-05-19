package com.tyomsky.moviedb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {


    private static final int VERSION = 1;
    private static final String TABLE_NAME = "moviedb.db";

    public MovieDbHelper(Context context) {
        super(context, TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.Movies.TABLE_NAME + " (" +
                MovieContract.Movies._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.Movies.COL_TITLE + " TEXT NOT NULL, " +
                MovieContract.Movies.COL_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.Movies.COL_POSTER_PATH + " TEXT, " +
                MovieContract.Movies.COL_RELEASE_DATE + " TEXT, " +
                MovieContract.Movies.COL_VOTE_AVERAGE + " REAL NOT NULL)";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //do nothing
    }
}
