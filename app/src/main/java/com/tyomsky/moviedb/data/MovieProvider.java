package com.tyomsky.moviedb.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = getUriMatcher();
    private static final int MOVIES = 100;
    private static final int MOVIE = 101;
    private MovieDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch (match) {
            case MOVIES:
                cursor = db.query(MovieContract.Movies.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case MOVIE:
                long id = ContentUris.parseId(uri);
                selectionArgs = new String[]{String.valueOf(id)};
                cursor = db.query(MovieContract.Movies.TABLE_NAME, projection,
                        MovieContract.Movies._ID + "=?", selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return MovieContract.Movies.CONTENT_TYPE_ITEM;
            case MOVIES:
                return MovieContract.Movies.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIES:
                long _id = db.insert(MovieContract.Movies.TABLE_NAME, null, values);
                returnUri = ContentUris.withAppendedId(uri, _id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsAffected;
        switch (match) {
            case MOVIE:
                long _id = ContentUris.parseId(uri);
                selection = MovieContract.Movies._ID + "=?";
                selectionArgs = new String[]{String.valueOf(_id)};
                rowsAffected = db.delete(MovieContract.Movies.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES:
                rowsAffected = db.delete(MovieContract.Movies.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowsAffected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsAffected;
        switch (match) {
            case MOVIE:
                long _id = ContentUris.parseId(uri);
                selection = MovieContract.Movies._ID + "=?";
                selectionArgs = new String[]{String.valueOf(_id)};
                rowsAffected = db.update(
                        MovieContract.Movies.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case MOVIES:
                rowsAffected = db.update(
                        MovieContract.Movies.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowsAffected;
    }

    public static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIE, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE);
        return uriMatcher;
    }
}
