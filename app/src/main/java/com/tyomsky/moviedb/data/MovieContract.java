package com.tyomsky.moviedb.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY = "com.tyomsky.moviedb.provider";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static String PATH_MOVIE = "movies";

    public static class Movies implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COL_TITLE = "title";
        public static final String COL_OVERVIEW = "overview";
        public static final String COL_RELEASE_DATE = "release_date";
        public static final String COL_POSTER_PATH = "poster_path";
        public static final String COL_VOTE_AVERAGE = "vote_average";

        public Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
